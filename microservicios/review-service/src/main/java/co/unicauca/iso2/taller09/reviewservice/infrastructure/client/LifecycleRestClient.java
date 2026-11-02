package co.unicauca.iso2.taller09.reviewservice.infrastructure.client;

import co.unicauca.iso2.taller09.common.dto.QuestionStateDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.port.QuestionLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
public class LifecycleRestClient implements QuestionLifecycle {
    private static final Logger log = LoggerFactory.getLogger(LifecycleRestClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public LifecycleRestClient(RestTemplate restTemplate, @Value("${services.lifecycle.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<QuestionStatus> currentState(Long questionId) {
        try {
            QuestionStateDTO dto = restTemplate.getForObject(baseUrl + "/lifecycle/{id}", QuestionStateDTO.class, questionId);
            return Optional.ofNullable(dto).map(QuestionStateDTO::getState);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Servicio de ciclo de vida no disponible para la pregunta {}: {}", questionId, e.getMessage());
            throw ReviewException.unavailable("El servicio de ciclo de vida no está disponible.");
        }
    }

    @Override
    public void transition(Long questionId, QuestionStatus target, String changedBy, String message) {
        Map<String, String> body = Map.of(
                "targetState", target.name(),
                "changedBy", changedBy,
                "message", message);
        try {
            restTemplate.postForEntity(baseUrl + "/lifecycle/{id}/transition", body, Map.class, questionId);
        } catch (HttpClientErrorException e) {
            log.warn("Transición a {} rechazada para la pregunta {}: {}", target, questionId, e.getResponseBodyAsString());
            throw ReviewException.conflict("El servicio de ciclo de vida rechazó la transición a " + target + ".");
        } catch (RestClientException e) {
            log.error("Servicio de ciclo de vida no disponible al pasar la pregunta {} a {}: {}", questionId, target, e.getMessage());
            throw ReviewException.unavailable("El servicio de ciclo de vida no está disponible.");
        }
    }
}

package co.unicauca.iso2.taller09.questionservice.client;

import co.unicauca.iso2.taller09.common.dto.OperationResultDTO;
import co.unicauca.iso2.taller09.common.dto.QuestionStateDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionException;
import co.unicauca.iso2.taller09.questionservice.domain.port.QuestionLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Patrón GoF <b>Adapter</b>: traduce la API REST de lifecycle-service
 * ({@code GET /lifecycle/{id}} y {@code GET /lifecycle?ids=}) al puerto {@link QuestionLifecycle}.
 */
@Component
public class LifecycleClient implements QuestionLifecycle {
    private static final Logger log = LoggerFactory.getLogger(LifecycleClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public LifecycleClient(RestTemplate restTemplate, @Value("${services.lifecycle.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public QuestionStatus stateOf(Long questionId) {
        try {
            QuestionStateDTO dto = restTemplate.getForObject(baseUrl + "/lifecycle/{id}", QuestionStateDTO.class, questionId);
            return dto == null || dto.getState() == null ? QuestionStatus.DRAFT : dto.getState();
        } catch (RestClientException e) {
            log.error("Servicio de ciclo de vida no disponible al consultar la pregunta {}: {}", questionId, e.getMessage());
            throw QuestionException.unavailable("El servicio de ciclo de vida no está disponible.");
        }
    }

    /** Resultado de una operación asíncrona (envío a revisión) registrado por lifecycle-service. */
    public Optional<OperationResultDTO> operation(String operationId) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(baseUrl + "/operations/{id}", OperationResultDTO.class, operationId));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("No se pudo obtener el estado de la operación {}: {}", operationId, e.getMessage());
            throw QuestionException.unavailable("El servicio de ciclo de vida no está disponible.");
        }
    }

    @Override
    public Map<Long, QuestionStatus> statesOf(Collection<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Map.of();
        }
        String ids = questionIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        try {
            QuestionStateDTO[] states = restTemplate.getForObject(baseUrl + "/lifecycle?ids={ids}", QuestionStateDTO[].class, ids);
            Map<Long, QuestionStatus> result = new HashMap<>();
            if (states != null) {
                Arrays.stream(states).forEach(s -> result.put(s.getQuestionId(), s.getState()));
            }
            return result;
        } catch (RestClientException e) {
            log.error("Servicio de ciclo de vida no disponible al listar estados: {}", e.getMessage());
            throw QuestionException.unavailable("El servicio de ciclo de vida no está disponible.");
        }
    }
}

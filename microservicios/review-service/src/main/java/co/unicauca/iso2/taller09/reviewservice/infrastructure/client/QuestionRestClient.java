package co.unicauca.iso2.taller09.reviewservice.infrastructure.client;

import co.unicauca.iso2.taller09.common.dto.QuestionDTO;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.QuestionSummary;
import co.unicauca.iso2.taller09.reviewservice.domain.port.QuestionCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class QuestionRestClient implements QuestionCatalog {
    private static final Logger log = LoggerFactory.getLogger(QuestionRestClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public QuestionRestClient(RestTemplate restTemplate, @Value("${services.question.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<QuestionSummary> findById(Long questionId) {
        try {
            QuestionDTO dto = restTemplate.getForObject(baseUrl + "/questions/{id}", QuestionDTO.class, questionId);
            return Optional.ofNullable(dto).map(q -> new QuestionSummary(q.getId(), q.getText(), q.getAuthorId()));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Servicio de preguntas no disponible al consultar la pregunta {}: {}", questionId, e.getMessage());
            throw ReviewException.unavailable("El servicio de preguntas no está disponible.");
        }
    }
}

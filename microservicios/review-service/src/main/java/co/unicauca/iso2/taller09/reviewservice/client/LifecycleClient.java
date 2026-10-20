package co.unicauca.iso2.taller09.reviewservice.client;

import co.unicauca.iso2.taller09.common.dto.QuestionStateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
public class LifecycleClient {
    private static final Logger log = LoggerFactory.getLogger(LifecycleClient.class);

    private final RestTemplate restTemplate;
    private final String lifecycleServiceUrl;

    public LifecycleClient(RestTemplate restTemplate,
                           @Value("${services.lifecycle.url}") String lifecycleServiceUrl) {
        this.restTemplate = restTemplate;
        this.lifecycleServiceUrl = lifecycleServiceUrl;
    }

    public Optional<QuestionStateDTO> getQuestionState(Long questionId) {
        try {
            String url = lifecycleServiceUrl + "/lifecycle/" + questionId;
            log.info("Consultando síncronamente estado de pregunta en Lifecycle Service: {}", url);
            ResponseEntity<QuestionStateDTO> response = restTemplate.getForEntity(url, QuestionStateDTO.class);
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.error("Error al consultar estado en Lifecycle Service para questionId={}: {}", questionId, e.getMessage());
            return Optional.empty();
        }
    }

    public boolean requestTransition(Long questionId, String targetState, String changedBy, String message) {
        try {
            String url = lifecycleServiceUrl + "/lifecycle/" + questionId + "/transition";
            log.info("Solicitando transición síncrona a Lifecycle Service [{}]: targetState={}", url, targetState);
            Map<String, String> body = Map.of(
                    "targetState", targetState,
                    "changedBy", changedBy,
                    "message", message
            );
            ResponseEntity<?> response = restTemplate.postForEntity(url, body, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Fallo al solicitar transición a Lifecycle Service para questionId={}: {}", questionId, e.getMessage());
            return false;
        }
    }
}

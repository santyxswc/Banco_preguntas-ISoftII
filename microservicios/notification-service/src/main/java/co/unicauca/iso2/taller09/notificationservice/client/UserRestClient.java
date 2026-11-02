package co.unicauca.iso2.taller09.notificationservice.client;

import co.unicauca.iso2.taller09.common.dto.UserDTO;
import co.unicauca.iso2.taller09.notificationservice.application.UserDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Patrón GoF <b>Adapter</b>: adapta {@code GET /users/{id}} de user-service al puerto
 * {@link UserDirectory}. Si user-service no responde se usa un correo institucional derivado
 * del id, para que la notificación no se pierda.
 */
@Component
public class UserRestClient implements UserDirectory {
    private static final Logger log = LoggerFactory.getLogger(UserRestClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserRestClient(RestTemplate restTemplate, @Value("${services.user.url:http://localhost:8083}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public String emailOf(Long userId) {
        try {
            UserDTO user = restTemplate.getForObject(baseUrl + "/users/{id}", UserDTO.class, userId);
            if (user != null && user.getEmail() != null && !user.getEmail().isBlank()) {
                return user.getEmail();
            }
        } catch (RestClientException e) {
            log.warn("user-service no disponible para el usuario {}: {}", userId, e.getMessage());
        }
        return fallbackEmail(userId);
    }

    public static String fallbackEmail(Long userId) {
        return "usuario" + userId + "@unicauca.edu.co";
    }
}

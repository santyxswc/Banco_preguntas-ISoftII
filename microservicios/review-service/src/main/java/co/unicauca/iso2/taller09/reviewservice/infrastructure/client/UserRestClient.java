package co.unicauca.iso2.taller09.reviewservice.infrastructure.client;

import co.unicauca.iso2.taller09.common.dto.UserDTO;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewerProfile;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewerDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class UserRestClient implements ReviewerDirectory {
    private static final Logger log = LoggerFactory.getLogger(UserRestClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserRestClient(RestTemplate restTemplate, @Value("${services.user.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<ReviewerProfile> findById(Long userId) {
        try {
            UserDTO dto = restTemplate.getForObject(baseUrl + "/users/{id}", UserDTO.class, userId);
            return Optional.ofNullable(dto)
                    .map(u -> new ReviewerProfile(u.getId(), u.getFullName(), u.getEmail(), u.getRoles()));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Servicio de usuarios no disponible al consultar el usuario {}: {}", userId, e.getMessage());
            throw ReviewException.unavailable("El servicio de usuarios no está disponible.");
        }
    }
}

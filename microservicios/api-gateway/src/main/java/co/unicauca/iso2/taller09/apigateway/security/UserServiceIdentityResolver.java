package co.unicauca.iso2.taller09.apigateway.security;

import co.unicauca.iso2.taller09.common.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador del puerto {@link UserIdentityResolver}: sincroniza la identidad con user-service
 * ({@code POST /internal/users/sync}) y guarda el resultado en una caché de corta duración.
 * <p>
 * La caché evita una llamada a user-service por cada petición del frontend. La clave incluye
 * nombre, correo y roles del token: si el administrador cambia un rol en Keycloak, el siguiente
 * token produce otra clave y el perfil local se actualiza de inmediato.
 */
@Component
public class UserServiceIdentityResolver implements UserIdentityResolver {
    private static final Logger log = LoggerFactory.getLogger(UserServiceIdentityResolver.class);
    private static final int MAX_ENTRIES = 5_000;

    private final WebClient client;
    private final Duration ttl;
    private final Clock clock;
    private final Map<String, CachedUser> cache = new ConcurrentHashMap<>();

    private record CachedUser(UserDTO user, Instant expiresAt) {
    }

    private record SyncRequest(String username, String fullName, String email, List<String> roles) {
    }

    @Autowired
    public UserServiceIdentityResolver(WebClient.Builder builder,
                                       @Value("${identity.user-service-url}") String userServiceUrl,
                                       @Value("${identity.cache-ttl-seconds:60}") long ttlSeconds) {
        this(builder.baseUrl(userServiceUrl).build(), Duration.ofSeconds(ttlSeconds), Clock.systemUTC());
    }

    UserServiceIdentityResolver(WebClient client, Duration ttl, Clock clock) {
        this.client = client;
        this.ttl = ttl;
        this.clock = clock;
    }

    @Override
    public Mono<UserDTO> resolve(KeycloakIdentity identity) {
        String key = String.join("|", identity.username(), String.valueOf(identity.fullName()),
                String.valueOf(identity.email()), String.join(",", identity.roles()));
        CachedUser cached = cache.get(key);
        if (cached != null && clock.instant().isBefore(cached.expiresAt())) {
            return Mono.just(cached.user());
        }
        return client.post().uri("/internal/users/sync")
                .bodyValue(new SyncRequest(identity.username(), identity.fullName(), identity.email(), identity.roles()))
                .retrieve()
                .bodyToMono(UserDTO.class)
                .doOnNext(user -> remember(key, user))
                .doOnError(e -> log.error("No se pudo sincronizar la identidad de {} con user-service: {}",
                        identity.username(), e.getMessage()));
    }

    private void remember(String key, UserDTO user) {
        if (cache.size() >= MAX_ENTRIES) {
            cache.clear();
        }
        cache.put(key, new CachedUser(user, clock.instant().plus(ttl)));
    }
}

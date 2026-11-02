package co.unicauca.iso2.taller09.common.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public final class AuthTokenCodec {
    private static final String ALGORITHM = "HmacSHA256";
    private static final String FIELD_SEPARATOR = "|";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final byte[] secret;
    private final Duration ttl;
    private final Clock clock;

    public AuthTokenCodec(String secret, Duration ttl, Clock clock) {
        if (secret == null || secret.length() < 16) {
            throw new IllegalArgumentException("El secreto del token debe tener al menos 16 caracteres");
        }
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("La vigencia del token debe ser positiva");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.ttl = ttl;
        this.clock = clock;
    }

    public AuthTokenCodec(String secret, Duration ttl) {
        this(secret, ttl, Clock.systemUTC());
    }

    public String issue(Long userId, String username, List<String> roles) {
        if (userId == null || username == null) {
            throw new IllegalArgumentException("El token requiere el id y el nombre de usuario");
        }
        Instant expiresAt = clock.instant().plus(ttl);
        String payload = String.join(FIELD_SEPARATOR,
                String.valueOf(userId),
                URLEncoder.encode(username, StandardCharsets.UTF_8),
                String.join(",", roles == null ? List.of() : roles),
                String.valueOf(expiresAt.getEpochSecond()));
        String encodedPayload = ENCODER.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encodedPayload + "." + ENCODER.encodeToString(sign(encodedPayload));
    }

    public Optional<AuthenticatedUser> verify(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        int dot = token.indexOf('.');
        if (dot <= 0 || dot == token.length() - 1) {
            return Optional.empty();
        }
        String encodedPayload = token.substring(0, dot);
        try {
            byte[] signature = DECODER.decode(token.substring(dot + 1));
            if (!MessageDigest.isEqual(sign(encodedPayload), signature)) {
                return Optional.empty();
            }
            String[] fields = new String(DECODER.decode(encodedPayload), StandardCharsets.UTF_8).split("\\|", -1);
            if (fields.length != 4) {
                return Optional.empty();
            }
            Instant expiresAt = Instant.ofEpochSecond(Long.parseLong(fields[3]));
            if (!clock.instant().isBefore(expiresAt)) {
                return Optional.empty();
            }
            List<String> roles = fields[2].isEmpty() ? List.of() : Arrays.asList(fields[2].split(","));
            return Optional.of(new AuthenticatedUser(Long.valueOf(fields[0]),
                    URLDecoder.decode(fields[1], StandardCharsets.UTF_8), roles, expiresAt));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private byte[] sign(String data) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret, ALGORITHM));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }
}

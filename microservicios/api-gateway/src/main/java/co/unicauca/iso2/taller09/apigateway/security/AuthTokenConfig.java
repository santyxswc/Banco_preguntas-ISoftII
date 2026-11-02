package co.unicauca.iso2.taller09.apigateway.security;

import co.unicauca.iso2.taller09.common.security.AuthTokenCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AuthTokenConfig {

    @Bean
    public AuthTokenCodec authTokenCodec(@Value("${auth.token.secret}") String secret) {
        return new AuthTokenCodec(secret, Duration.ofMinutes(1));
    }
}

package co.unicauca.iso2.taller09.reviewservice.infrastructure.config;

import co.unicauca.iso2.taller09.reviewservice.application.ReviewService;
import co.unicauca.iso2.taller09.reviewservice.domain.policy.UnanimousApprovalPolicy;
import co.unicauca.iso2.taller09.reviewservice.domain.policy.VerdictPolicy;
import co.unicauca.iso2.taller09.reviewservice.domain.port.QuestionCatalog;
import co.unicauca.iso2.taller09.reviewservice.domain.port.QuestionLifecycle;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewEventPublisher;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewRepository;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewerDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ReviewServiceConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,
                                     @Value("${services.http.connect-timeout-ms:2000}") long connectTimeout,
                                     @Value("${services.http.read-timeout-ms:3000}") long readTimeout) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    @Bean
    public VerdictPolicy verdictPolicy() {
        return new UnanimousApprovalPolicy();
    }

    @Bean
    public ReviewService reviewService(ReviewRepository repository, QuestionCatalog questions,
                                       ReviewerDirectory reviewers, QuestionLifecycle lifecycle,
                                       ReviewEventPublisher events, VerdictPolicy verdictPolicy) {
        return new ReviewService(repository, questions, reviewers, lifecycle, events, verdictPolicy);
    }
}

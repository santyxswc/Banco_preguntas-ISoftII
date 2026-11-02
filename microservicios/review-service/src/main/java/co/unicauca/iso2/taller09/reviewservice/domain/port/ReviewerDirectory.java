package co.unicauca.iso2.taller09.reviewservice.domain.port;

import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewerProfile;

import java.util.Optional;

public interface ReviewerDirectory {

    Optional<ReviewerProfile> findById(Long userId);
}

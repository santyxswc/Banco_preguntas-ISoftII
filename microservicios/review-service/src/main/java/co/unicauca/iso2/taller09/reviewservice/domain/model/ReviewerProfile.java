package co.unicauca.iso2.taller09.reviewservice.domain.model;

import java.util.List;
import java.util.Set;

public record ReviewerProfile(Long id, String fullName, String email, List<String> roles) {

    private static final Set<String> ROLES_REVISORES = Set.of("REVISOR", "DOCENTE");

    public ReviewerProfile {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }

    public boolean canReview() {
        return roles.stream().map(String::toUpperCase).anyMatch(ROLES_REVISORES::contains);
    }
}

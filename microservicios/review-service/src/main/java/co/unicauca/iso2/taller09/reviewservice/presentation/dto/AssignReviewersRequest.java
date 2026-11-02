package co.unicauca.iso2.taller09.reviewservice.presentation.dto;

import java.util.List;

public record AssignReviewersRequest(Long questionId, List<Long> reviewerIds, Long assignedBy) {
}

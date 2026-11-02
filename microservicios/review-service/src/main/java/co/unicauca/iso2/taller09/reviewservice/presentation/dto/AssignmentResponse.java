package co.unicauca.iso2.taller09.reviewservice.presentation.dto;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;

import java.time.LocalDateTime;

public record AssignmentResponse(Long id, Long questionId, Long reviewerId, Long assignedBy, int round,
                                 LocalDateTime assignedAt, AssignmentStatus status) {

    public static AssignmentResponse from(ReviewAssignment a) {
        return new AssignmentResponse(a.getId(), a.getQuestionId(), a.getReviewerId(), a.getAssignedBy(), a.getRound(),
                a.getAssignedAt(), a.getStatus());
    }
}

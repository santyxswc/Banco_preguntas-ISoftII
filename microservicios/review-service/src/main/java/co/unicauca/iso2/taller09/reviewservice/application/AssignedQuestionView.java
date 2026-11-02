package co.unicauca.iso2.taller09.reviewservice.application;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;

import java.time.LocalDateTime;

public record AssignedQuestionView(Long assignmentId, Long questionId, String questionText,
                                   Long authorId, int round, LocalDateTime assignedAt, AssignmentStatus status) {
}

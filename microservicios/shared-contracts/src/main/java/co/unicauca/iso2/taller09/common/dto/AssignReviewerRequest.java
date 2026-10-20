package co.unicauca.iso2.taller09.common.dto;

import java.io.Serializable;

public class AssignReviewerRequest implements Serializable {
    private Long questionId;
    private Long reviewerId;
    private Long assignedBy;

    public AssignReviewerRequest() {}

    public AssignReviewerRequest(Long questionId, Long reviewerId, Long assignedBy) {
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.assignedBy = assignedBy;
    }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }
}

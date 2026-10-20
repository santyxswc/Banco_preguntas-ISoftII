package co.unicauca.iso2.taller09.common.dto;

import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import java.io.Serializable;

public class ReviewEvaluationRequest implements Serializable {
    private Long questionId;
    private Long reviewerId;
    private ReviewResult result; // APPROVED or REJECTED
    private String comments;

    public ReviewEvaluationRequest() {}

    public ReviewEvaluationRequest(Long questionId, Long reviewerId, ReviewResult result, String comments) {
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.result = result;
        this.comments = comments;
    }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public ReviewResult getResult() { return result; }
    public void setResult(ReviewResult result) { this.result = result; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}

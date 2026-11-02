package co.unicauca.iso2.taller09.common.messages;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReviewCompleted implements Serializable {
    private String operationId;
    private Long questionId;
    private Long reviewerId;
    private Long authorId;
    private ReviewResult result;
    private String comments;
    private QuestionStatus questionFinalState;
    private LocalDateTime occurredAt;

    public ReviewCompleted() {}

    public ReviewCompleted(String operationId, Long questionId, Long reviewerId, Long authorId,
                           ReviewResult result, String comments, QuestionStatus questionFinalState,
                           LocalDateTime occurredAt) {
        this.operationId = operationId;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.authorId = authorId;
        this.result = result;
        this.comments = comments;
        this.questionFinalState = questionFinalState;
        this.occurredAt = occurredAt;
    }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public ReviewResult getResult() { return result; }
    public void setResult(ReviewResult result) { this.result = result; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public QuestionStatus getQuestionFinalState() { return questionFinalState; }
    public void setQuestionFinalState(QuestionStatus questionFinalState) { this.questionFinalState = questionFinalState; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}

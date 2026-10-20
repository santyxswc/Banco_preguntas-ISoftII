package co.unicauca.iso2.taller09.common.dto;

import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ReviewDTO implements Serializable {
    private Long id;
    private Long questionId;
    private Long reviewerId;
    private ReviewResult result;
    private String comments;
    private LocalDateTime createdAt;

    public ReviewDTO() {}

    public ReviewDTO(Long id, Long questionId, Long reviewerId, ReviewResult result, String comments, LocalDateTime createdAt) {
        this.id = id;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.result = result;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public ReviewResult getResult() { return result; }
    public void setResult(ReviewResult result) { this.result = result; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

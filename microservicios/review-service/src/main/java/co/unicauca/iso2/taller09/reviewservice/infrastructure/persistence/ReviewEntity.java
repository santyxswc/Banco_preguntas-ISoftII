package co.unicauca.iso2.taller09.reviewservice.infrastructure.persistence;

import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "revision")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "ronda", nullable = false)
    private int round;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewResult result;

    @Column(length = 2000)
    private String comments;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ReviewEntity() {
    }

    public ReviewEntity(Long id, Long questionId, Long reviewerId, int round, ReviewResult result,
                        String comments, LocalDateTime createdAt) {
        this.id = id;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.round = round;
        this.result = result;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public Long getReviewerId() { return reviewerId; }
    public int getRound() { return round; }
    public ReviewResult getResult() { return result; }
    public String getComments() { return comments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

/**
 * @file BaseQuestionRecord.java
 * @brief JPA base record exhibiting InheritanceType.JOINED strategy (Taller 10 - JPA).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * @brief Base entity in the JPA persistence hierarchy.
 *
 * Stored in table 'jpa_base_questions'. Subclasses define specialized attributes
 * stored in separate tables joined by primary key.
 */
@Entity
@Table(name = "jpa_base_questions")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseQuestionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id", nullable = false)
    private String authorId;

    @Column(name = "stem", nullable = false, length = 1000)
    private String stem;

    @Column(name = "context_text", length = 2000)
    private String context;

    @Column(name = "justification_text", length = 2000)
    private String justification;

    @Column(name = "competence", length = 100)
    private String competence;

    @Column(name = "topic", length = 100)
    private String topic;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    public BaseQuestionRecord() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

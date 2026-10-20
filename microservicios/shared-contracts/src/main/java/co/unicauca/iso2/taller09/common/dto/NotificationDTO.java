package co.unicauca.iso2.taller09.common.dto;

import co.unicauca.iso2.taller09.common.enums.NotificationStatus;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import java.io.Serializable;
import java.time.LocalDateTime;

public class NotificationDTO implements Serializable {
    private Long id;
    private Long recipientId;
    private NotificationType type;
    private String subject;
    private String message;
    private NotificationStatus status;
    private LocalDateTime sentAt;

    public NotificationDTO() {}

    public NotificationDTO(Long id, Long recipientId, NotificationType type, String subject, String message, NotificationStatus status, LocalDateTime sentAt) {
        this.id = id;
        this.recipientId = recipientId;
        this.type = type;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.sentAt = sentAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}

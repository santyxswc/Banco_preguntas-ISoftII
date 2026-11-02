package co.unicauca.iso2.taller09.notificationservice.application;

import co.unicauca.iso2.taller09.common.enums.NotificationStatus;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.email.EmailMessage;
import co.unicauca.iso2.taller09.notificationservice.email.EmailSender;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

/**
 * Registra la notificación y la entrega por correo (HU-04 y HU-05: "el sistema enviará un
 * email"). Si el correo falla la notificación queda en FAILED en lugar de perderse.
 */
@Service
public class NotificationDispatcher {

    private final NotificationRepository repository;
    private final EmailSender emailSender;
    private final UserDirectory userDirectory;

    public NotificationDispatcher(NotificationRepository repository, EmailSender emailSender, UserDirectory userDirectory) {
        this.repository = repository;
        this.emailSender = emailSender;
        this.userDirectory = userDirectory;
    }

    public Notification notify(Long recipientId, NotificationType type, String subject, String body) {
        Notification notification = repository.save(
                new Notification(null, recipientId, type, subject, body, NotificationStatus.PENDING));
        String email = userDirectory.emailOf(recipientId);
        notification.setRecipientEmail(email);
        try {
            emailSender.send(new EmailMessage(email, subject, body));
            notification.setStatus(NotificationStatus.SENT);
        } catch (RuntimeException e) {
            notification.setStatus(NotificationStatus.FAILED);
        }
        return repository.save(notification);
    }
}

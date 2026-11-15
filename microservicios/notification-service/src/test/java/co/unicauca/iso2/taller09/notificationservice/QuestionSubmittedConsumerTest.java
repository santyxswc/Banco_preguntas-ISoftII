package co.unicauca.iso2.taller09.notificationservice;

import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.messages.QuestionSubmittedForReview;
import co.unicauca.iso2.taller09.notificationservice.application.NotificationDispatcher;
import co.unicauca.iso2.taller09.notificationservice.client.UserRestClient;
import co.unicauca.iso2.taller09.notificationservice.consumer.QuestionSubmittedConsumer;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.email.ConsoleEmailSender;
import co.unicauca.iso2.taller09.notificationservice.idempotency.ProcessedEventRegistry;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionSubmittedConsumerTest {

    private NotificationRepository repository;
    private QuestionSubmittedConsumer consumer;

    @BeforeEach
    void setUp() {
        repository = new NotificationRepository();
        NotificationDispatcher dispatcher = new NotificationDispatcher(repository, new ConsoleEmailSender(),
                UserRestClient::fallbackEmail);
        consumer = new QuestionSubmittedConsumer(dispatcher, new ProcessedEventRegistry(), 1L);
    }

    @Test
    @DisplayName("PreguntaEnviadaARevision avisa una sola vez al administrador que debe asignar revisores")
    void avisaAlAdministrador() {
        QuestionSubmittedForReview event = new QuestionSubmittedForReview("op-9", 120L, QuestionStatus.PENDING_REVIEW,
                LocalDateTime.now());

        consumer.handleQuestionSubmitted(event);
        consumer.handleQuestionSubmitted(event);

        List<Notification> admin = repository.findByRecipientId(1L);
        assertEquals(1, admin.size());
        assertEquals(NotificationType.QUESTION_SUBMITTED, admin.get(0).getType());
        assertTrue(admin.get(0).getSubject().contains("#120"));
    }

    @Test
    @DisplayName("HU-06: al reenviar una versión mejorada se notifica al administrador y al autor con la versión")
    void versionMejoradaNotificaAlAdministradorYAlAutor() {
        QuestionSubmittedForReview event = new QuestionSubmittedForReview("op-10", 120L, QuestionStatus.PENDING_REVIEW,
                2L, 2, LocalDateTime.now());

        consumer.handleQuestionSubmitted(event);
        consumer.handleQuestionSubmitted(event);

        List<Notification> admin = repository.findByRecipientId(1L);
        List<Notification> autor = repository.findByRecipientId(2L);
        assertEquals(1, admin.size());
        assertEquals(1, autor.size());
        assertTrue(admin.get(0).getSubject().contains("Versión mejorada 2"));
        assertTrue(autor.get(0).getSubject().contains("versión mejorada 2"));
        assertEquals(NotificationType.QUESTION_SUBMITTED, autor.get(0).getType());
    }

    @Test
    @DisplayName("En el primer envío el autor recibe la confirmación sin mención de versión mejorada")
    void primerEnvioConfirmaAlAutor() {
        consumer.handleQuestionSubmitted(new QuestionSubmittedForReview("op-11", 121L, QuestionStatus.PENDING_REVIEW,
                2L, 1, LocalDateTime.now()));

        List<Notification> autor = repository.findByRecipientId(2L);
        assertEquals(1, autor.size());
        assertEquals("Su pregunta #121 fue enviada a revisión", autor.get(0).getSubject());
    }
}

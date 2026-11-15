package co.unicauca.iso2.taller09.notificationservice;

import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.common.messages.ReviewCompleted;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;
import co.unicauca.iso2.taller09.notificationservice.application.NotificationDispatcher;
import co.unicauca.iso2.taller09.notificationservice.client.UserRestClient;
import co.unicauca.iso2.taller09.notificationservice.consumer.ReviewCompletedConsumer;
import co.unicauca.iso2.taller09.notificationservice.email.ConsoleEmailSender;
import co.unicauca.iso2.taller09.notificationservice.consumer.ReviewerAssignedConsumer;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.idempotency.ProcessedEventRegistry;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationIdempotencyTest {

    private NotificationRepository repository;
    private ReviewerAssignedConsumer assignedConsumer;
    private ReviewCompletedConsumer completedConsumer;

    @BeforeEach
    void setUp() {
        repository = new NotificationRepository();
        ProcessedEventRegistry registry = new ProcessedEventRegistry();
        NotificationDispatcher dispatcher = new NotificationDispatcher(repository, new ConsoleEmailSender(),
                UserRestClient::fallbackEmail);
        assignedConsumer = new ReviewerAssignedConsumer(dispatcher, registry);
        completedConsumer = new ReviewCompletedConsumer(dispatcher, registry, 1L);
    }

    @Test
    @DisplayName("Un RevisorAsignado entregado dos veces genera una sola notificación")
    void revisorAsignadoIdempotente() {
        ReviewerAssigned event = new ReviewerAssigned("op-1", 100L, 4L, 1L, LocalDateTime.now());

        assignedConsumer.handleReviewerAssigned(event);
        assignedConsumer.handleReviewerAssigned(event);

        assertEquals(1, repository.findByRecipientId(4L).size());
    }

    @Test
    @DisplayName("RevisionCompletada notifica al autor y al administrador una sola vez")
    void revisionCompletadaNotificaAutorYAdmin() {
        ReviewCompleted event = new ReviewCompleted("op-2", 100L, 4L, 2L, ReviewResult.REJECTED,
                "Falta bibliografía", QuestionStatus.REJECTED, LocalDateTime.now());

        completedConsumer.handleReviewCompleted(event);
        completedConsumer.handleReviewCompleted(event);

        List<Notification> autor = repository.findByRecipientId(2L);
        assertEquals(1, autor.size());
        assertEquals(NotificationType.QUESTION_REJECTED, autor.get(0).getType());
        assertTrue(autor.get(0).getMessage().contains("Falta bibliografía"));
        assertEquals(1, repository.findByRecipientId(1L).size());
    }

    @Test
    @DisplayName("Una evaluación parcial se notifica como REVIEW_COMPLETED")
    void evaluacionParcial() {
        completedConsumer.handleReviewCompleted(new ReviewCompleted("op-3", 100L, 4L, 2L,
                ReviewResult.APPROVED, null, null, LocalDateTime.now()));

        assertEquals(NotificationType.REVIEW_COMPLETED, repository.findByRecipientId(2L).get(0).getType());
    }

    @Test
    @DisplayName("Eventos distintos con el mismo operationId de otro tipo no se bloquean entre sí")
    void tiposDistintos() {
        assignedConsumer.handleReviewerAssigned(new ReviewerAssigned("op-4", 100L, 4L, 1L, LocalDateTime.now()));
        completedConsumer.handleReviewCompleted(new ReviewCompleted("op-4", 100L, 4L, 2L,
                ReviewResult.APPROVED, null, QuestionStatus.APPROVED, LocalDateTime.now()));

        assertEquals(3, repository.findAll().size());
    }

    @Test
    @DisplayName("HU-06: la asignación de una ronda posterior avisa al revisor que es una versión mejorada")
    void asignacionDeUnaNuevaRonda() {
        assignedConsumer.handleReviewerAssigned(new ReviewerAssigned("op-5", 100L, 5L, 1L, 2, LocalDateTime.now()));

        Notification aviso = repository.findByRecipientId(5L).get(0);
        assertEquals(NotificationType.REVIEWER_ASSIGNED, aviso.getType());
        assertTrue(aviso.getSubject().contains("Versión mejorada"));
        assertTrue(aviso.getSubject().contains("ronda 2"));
    }
}

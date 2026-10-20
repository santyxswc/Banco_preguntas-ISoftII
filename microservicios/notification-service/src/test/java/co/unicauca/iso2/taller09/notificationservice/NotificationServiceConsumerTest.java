package co.unicauca.iso2.taller09.notificationservice;

import co.unicauca.iso2.taller09.common.enums.NotificationStatus;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;
import co.unicauca.iso2.taller09.notificationservice.consumer.ReviewerAssignedConsumer;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationServiceConsumerTest {

    @Autowired
    private ReviewerAssignedConsumer consumer;

    @Autowired
    private NotificationRepository repository;

    @Test
    void testConsumidorGeneraNotificacionAsincrona() {
        ReviewerAssigned event = new ReviewerAssigned(
                "op-notif-1",
                501L,
                4L,
                1L,
                LocalDateTime.now()
        );

        consumer.handleReviewerAssigned(event);

        List<Notification> notifs = repository.findByRecipientId(4L);
        assertFalse(notifs.isEmpty());
        Notification n = notifs.get(0);
        assertEquals(NotificationType.REVIEWER_ASSIGNED, n.getType());
        assertEquals(NotificationStatus.SENT, n.getStatus());
        assertTrue(n.getSubject().contains("501"));
    }
}

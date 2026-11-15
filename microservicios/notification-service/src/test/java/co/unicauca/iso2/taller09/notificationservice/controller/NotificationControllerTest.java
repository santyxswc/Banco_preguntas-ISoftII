package co.unicauca.iso2.taller09.notificationservice.controller;

import co.unicauca.iso2.taller09.common.dto.NotificationDTO;
import co.unicauca.iso2.taller09.common.enums.NotificationStatus;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Autorización por dato de las notificaciones (RNF #1): cada usuario lee solo las suyas. */
class NotificationControllerTest {

    private NotificationController controller;

    @BeforeEach
    void setUp() {
        NotificationRepository repository = new NotificationRepository();
        repository.save(new Notification(null, 2L, NotificationType.QUESTION_REJECTED, "Rechazada", "Detalle", NotificationStatus.SENT));
        repository.save(new Notification(null, 4L, NotificationType.REVIEWER_ASSIGNED, "Asignada", "Detalle", NotificationStatus.SENT));
        controller = new NotificationController(repository);
    }

    @Test
    @DisplayName("Un usuario lee sus notificaciones y recibe 403 al pedir las de otro")
    void cadaUsuarioLeeLasSuyas() {
        ResponseEntity<List<NotificationDTO>> propias = controller.listByRecipient(2L, 2L, "AUTOR,DOCENTE");
        assertEquals(HttpStatus.OK, propias.getStatusCode());
        assertEquals(1, propias.getBody().size());

        assertEquals(HttpStatus.FORBIDDEN, controller.listByRecipient(4L, 2L, "AUTOR,DOCENTE").getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, controller.listAll(2L, "AUTOR").getStatusCode());
    }

    @Test
    @DisplayName("El administrador y las llamadas internas ven las notificaciones de cualquiera")
    void administradorYLlamadasInternas() {
        assertEquals(1, controller.listByRecipient(4L, 1L, "ADMINISTRADOR").getBody().size());
        assertEquals(2, controller.listAll(1L, "ADMINISTRADOR").getBody().size());
        assertEquals(2, controller.listAll(null, null).getBody().size());
        assertEquals(1, controller.listByRecipient(2L, null, null).getBody().size());
    }
}

package co.unicauca.iso2.taller09.notificationservice.controller;

import co.unicauca.iso2.taller09.common.dto.NotificationDTO;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
@Tag(name = "Notificaciones", description = "Endpoints para consultar las notificaciones enviadas asíncronamente")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todas las notificaciones generadas", description = "Permite a Postman verificar las notificaciones enviadas de forma asíncrona.")
    public ResponseEntity<List<NotificationDTO>> listAll() {
        List<NotificationDTO> dtos = notificationRepository.findAll().stream()
                .map(Notification::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Listar notificaciones por destinatario (Revisor)")
    public ResponseEntity<List<NotificationDTO>> listByRecipient(@PathVariable Long recipientId) {
        List<NotificationDTO> dtos = notificationRepository.findByRecipientId(recipientId).stream()
                .map(Notification::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}

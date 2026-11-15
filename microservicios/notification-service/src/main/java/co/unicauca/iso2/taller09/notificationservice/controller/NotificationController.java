package co.unicauca.iso2.taller09.notificationservice.controller;

import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import co.unicauca.iso2.taller09.common.security.RequestIdentity;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import co.unicauca.iso2.taller09.common.dto.NotificationDTO;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Consulta de las notificaciones registradas por notification-service. Aplica la autorización
 * por dato del RNF #1: un usuario solo lee sus notificaciones, salvo el administrador.
 */
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
    @Operation(summary = "Listar todas las notificaciones generadas",
            description = "Solo el administrador (o una llamada interna) puede ver las notificaciones de todos los usuarios.")
    @ApiResponse(responseCode = "403", description = "Quien consulta no es administrador")
    public ResponseEntity<List<NotificationDTO>> listAll(
            @RequestHeader(value = IdentityHeaders.USER_ID, required = false) Long userId,
            @RequestHeader(value = IdentityHeaders.USER_ROLES, required = false) String roles) {
        RequestIdentity identity = RequestIdentity.of(userId, roles);
        if (!identity.isAnonymous() && !identity.isAdministrator()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<NotificationDTO> dtos = notificationRepository.findAll().stream()
                .map(Notification::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Listar las notificaciones de un destinatario",
            description = "Cada usuario solo puede leer sus propias notificaciones; el administrador, las de cualquiera.")
    @ApiResponse(responseCode = "403", description = "Las notificaciones son de otro usuario")
    public ResponseEntity<List<NotificationDTO>> listByRecipient(
            @PathVariable Long recipientId,
            @RequestHeader(value = IdentityHeaders.USER_ID, required = false) Long userId,
            @RequestHeader(value = IdentityHeaders.USER_ROLES, required = false) String roles) {
        if (!RequestIdentity.of(userId, roles).canAccessDataOf(recipientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<NotificationDTO> dtos = notificationRepository.findByRecipientId(recipientId).stream()
                .map(Notification::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}

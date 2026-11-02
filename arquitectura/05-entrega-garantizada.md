# Prueba de entrega garantizada de eventos

Historia técnica **HU-T9.3** (SCRUM-92): comprobar que los eventos pendientes se procesan al reiniciar el servicio consumidor.

## Qué se comprueba

1. Con notification-service detenido, review-service publica `RevisorAsignado` y el mensaje queda en `reviewer.assigned.queue` (no se pierde).
2. Al arrancar notification-service, el mensaje se consume y se crea **una** notificación para el revisor.
3. La cola queda vacía.

## Ejecución automática

Con RabbitMQ, el gateway, user, question, validation, lifecycle y review en marcha, y **notification-service detenido**:

```powershell
cd microservicios\pruebas
.\verificar-entrega-garantizada.ps1
```

El script crea una pregunta, la envía a revisión, asigna al revisor 4 y muestra cuántos mensajes hay en la cola. Después pide arrancar notification-service y comprueba la cola y las notificaciones. Termina con `RESULTADO: OK` o `RESULTADO: FALLO`.

## Ejecución manual (para el video)

1. Abra http://localhost:15672 (guest/guest) → **Queues**.
2. Detenga notification-service y asigne un revisor con `POST http://localhost:8080/api/v1/asignaciones`.
3. Muestre que `reviewer.assigned.queue` tiene 1 mensaje en *Ready*.
4. Arranque notification-service y muestre que la cola baja a 0 y que `GET http://localhost:8080/api/v1/notificaciones/recipient/4` devuelve la notificación.

## Pruebas automatizadas relacionadas

- `notification-service/.../NotificationIdempotencyTest`: un mismo evento entregado dos veces genera una sola notificación, y `RevisionCompletada` notifica al autor y al administrador.
- `review-service/.../ReviewServiceTest`: se publica un `RevisorAsignado` por revisor y un `RevisionCompletada` por evaluación; si la transición falla, no se publica nada.

## Nota sobre colas creadas antes de este cambio

Las colas ahora se declaran con *dead-letter exchange*. Si RabbitMQ ya tenía `reviewer.assigned.queue` del Taller 09 sin esos argumentos, Spring falla al arrancar con `PRECONDITION_FAILED - inequivalent arg 'x-dead-letter-exchange'`. Para resolverlo, borre la cola desde la consola de RabbitMQ o recree el contenedor:

```powershell
cd microservicios
docker compose down
docker compose up -d
```

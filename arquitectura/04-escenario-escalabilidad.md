# Escenario de calidad: escalabilidad

Historia técnica **HU-T13** (SCRUM-55). La métrica de carga es la misma en el README, en el documento de arquitectura y en la prueba: **500 usuarios concurrentes**.

## 1. Escenario

| Elemento | Especificación |
|---|---|
| **Atributo** | Escalabilidad (ISO/IEC 25010, capacidad) |
| **Fuente del estímulo** | Docentes autores y revisores durante la convocatoria Saber PRO |
| **Estímulo** | 500 usuarios concurrentes. Cada uno hace una petición por segundo: el 90 % crea preguntas (`POST /api/v1/preguntas`) y el 10 % consulta sus preguntas asignadas (`GET /api/v1/revisiones/asignadas`) |
| **Artefacto** | API Gateway, question-service (con validation-service) y review-service |
| **Entorno** | Operación normal. Primero con 1 instancia de question-service y después con 2 instancias detrás del gateway (perfil `escalado`) |
| **Respuesta** | El gateway reparte la carga entre las instancias. Las operaciones lentas (correos) salen por RabbitMQ y no retienen la petición REST |
| **Medición** | Latencia p95 y tasa de errores medidas con k6 durante 2 minutos de carga sostenida |
| **Resultado esperado** | Con 500 usuarios: p95 < 500 ms y errores < 1 %. Al pasar de 1 a 2 instancias, la p95 se mantiene o baja sin cambiar código, solo configuración |

## 2. Cómo se ejecuta la prueba

Archivos en `microservicios/pruebas-carga/`:

| Archivo | Uso |
|---|---|
| `escenario-escalabilidad.js` | Script k6: rampa de 30 s hasta 500 usuarios, 2 min sostenidos, umbrales p95 < 500 ms y errores < 1 % |
| `ejecutar-escenario.ps1` | Ejecuta k6 (local o con Docker `grafana/k6`) y guarda `resultados/resumen-<n>-instancia(s).json` |
| `comparar-resultados.ps1` | Genera `resultados/comparacion.md` con la tabla 1 instancia vs 2 instancias |

### Medición con 1 instancia

```powershell
cd microservicios
.\start-all.ps1
cd pruebas-carga
.\ejecutar-escenario.ps1 -Instancias 1
```

### Medición con 2 instancias

1. Detenga el gateway y vuelva a iniciarlo con el perfil de escalado:
   ```powershell
   cd microservicios\api-gateway
   $env:SPRING_PROFILES_ACTIVE = "escalado"; ..\..\mvnw.cmd spring-boot:run
   ```
2. Inicie una segunda instancia de question-service:
   ```powershell
   cd microservicios\question-service
   $env:QUESTION_SERVICE_PORT = "8091"; ..\..\mvnw.cmd spring-boot:run
   ```
3. Ejecute y compare:
   ```powershell
   cd microservicios\pruebas-carga
   .\ejecutar-escenario.ps1 -Instancias 2
   .\comparar-resultados.ps1
   ```

Los JSON y la tabla de `resultados/` se suben al repositorio como evidencia (CA2).

## 3. Resultados

Pendiente de ejecutar en el equipo del grupo. Al correr `comparar-resultados.ps1` se genera la tabla en `microservicios/pruebas-carga/resultados/comparacion.md`. Hay que copiarla aquí y en el documento de arquitectura.

| Métrica | 1 instancia | 2 instancias |
|---|---|---|
| Peticiones por segundo | — | — |
| Tasa de errores | — | — |
| Latencia p95 | — | — |
| ¿Cumple p95 < 500 ms y errores < 1 %? | — | — |

## 4. Limitaciones conocidas

- Las dos instancias corren en el mismo equipo, así que comparten CPU. La mejora que se mida es menor que la que habría en máquinas separadas.
- question-service guarda las preguntas en memoria hasta que tenga su base de datos (HU-T3.3). Por eso la prueba solo crea preguntas y no consulta una pregunta por id: cada instancia tiene sus propios datos.
- Si RabbitMQ está caído en el momento de publicar, review-service registra el error y no reintenta. Para cubrir ese caso haría falta un *outbox* transaccional.

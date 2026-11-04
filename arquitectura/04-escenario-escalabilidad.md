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

Medición del 4 de noviembre de 2026 (500 usuarios, 30 s de rampa, 2 min sostenidos, 15 s de bajada). Equipo: AMD Ryzen 7 5700G (16 hilos), 40 GB de RAM, Linux, Java 21, RabbitMQ 3.13 en Docker, k6 0.54.0 en Docker (`grafana/k6`). Los archivos de evidencia están en `microservicios/pruebas-carga/resultados/` (`resumen-*.json`, `k6-completo-*.json` y `comparacion.md`).

| Métrica | 1 instancia | 2 instancias |
|---|---|---|
| Peticiones totales | 71 287 | 71 278 |
| Peticiones por segundo | 429,9 | 429,8 |
| Tasa de errores | 0,0 % | 0,0 % |
| Latencia p95 | 3,9 ms | 3,8 ms |
| Latencia p99 | 5,8 ms | 5,9 ms |
| p95 crear pregunta / listar asignadas | 4,0 ms / 3,3 ms | 3,8 ms / 1,5 ms |
| ¿Cumple p95 < 500 ms y errores < 1 %? | Sí | Sí |

**Lectura.** Con 500 usuarios concurrentes la arquitectura cumple los dos umbrales con amplio margen: la p95 queda unas 125 veces por debajo del límite de 500 ms y no hubo errores. Al pasar a 2 instancias, sin tocar código (solo el perfil `escalado` del gateway y una segunda instancia en el puerto 8091), la p95 se mantuvo y bajó un 4,1 %, que es el resultado esperado. Durante la prueba se contaron las conexiones abiertas del gateway hacia cada instancia: estuvieron repartidas por igual (por ejemplo, 8 hacia el 8081 y 8 hacia el 8091), lo que confirma el balanceo. El rendimiento es igual en ambos casos porque cada usuario virtual hace una petición por segundo: la carga la fija el escenario, no la capacidad del sistema.

### Cómo se ejecutó en Linux (sin PowerShell)

Los scripts `.ps1` son para Windows. En Linux se hizo lo mismo con estos comandos:

```bash
cd microservicios
mvn -DskipTests package
docker compose up -d                                   # RabbitMQ
for s in user validation lifecycle question review notification; do
  (cd $s-service && nohup java -jar target/$s-service-1.0.0-SNAPSHOT.jar > /tmp/$s.log 2>&1 &)
done
(cd api-gateway && GATEWAY_PORT=8090 nohup java -jar target/api-gateway-1.0.0-SNAPSHOT.jar > /tmp/gw.log 2>&1 &)

cd pruebas-carga
docker run --rm --network host --user "$(id -u):$(id -g)" -v "$PWD:/scripts" -w /scripts grafana/k6:0.54.0 \
  run -e BASE_URL=http://localhost:8090 -e ETIQUETA=1-instancia -e USUARIOS=500 -e MANTENER=2m escenario-escalabilidad.js

# 2 instancias: segunda question-service en 8091 y gateway reiniciado con el perfil escalado
(cd ../question-service && QUESTION_SERVICE_PORT=8091 nohup java -jar target/question-service-1.0.0-SNAPSHOT.jar > /tmp/q2.log 2>&1 &)
# (detener el gateway anterior y lanzarlo con SPRING_PROFILES_ACTIVE=escalado GATEWAY_PORT=8090)
docker run --rm --network host --user "$(id -u):$(id -g)" -v "$PWD:/scripts" -w /scripts grafana/k6:0.54.0 \
  run -e BASE_URL=http://localhost:8090 -e ETIQUETA=2-instancias -e USUARIOS=500 -e MANTENER=2m escenario-escalabilidad.js
```

El gateway se levantó en el puerto 8090 porque en el equipo de la medición el 8080 estaba ocupado por otra aplicación; el puerto no cambia el resultado.

## 4. Limitaciones conocidas

- Las dos instancias corren en el mismo equipo, así que comparten CPU. La mejora que se mida es menor que la que habría en máquinas separadas.
- Con 500 usuarios el sistema está lejos de saturarse (p95 de unos 4 ms), así que la segunda instancia apenas cambia la latencia. La prueba demuestra que el escenario se cumple y que escalar no exige cambiar código; no mide la capacidad máxima. Para verla habría que subir `USUARIOS` hasta que la p95 de 1 instancia se acerque a 500 ms y repetir con 2.
- question-service guarda las preguntas en su propia base de datos (H2 en archivo por defecto). Con dos instancias en el mismo equipo ambas usan el mismo archivo gracias a `AUTO_SERVER=TRUE`; en producción se apuntan a un PostgreSQL con `QUESTION_DB_URL`.
- Si RabbitMQ está caído en el momento de publicar, review-service registra el error y no reintenta. Para cubrir ese caso haría falta un *outbox* transaccional.

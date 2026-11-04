# Resultados del escenario de escalabilidad

| Métrica | 1 instancia | 2 instancias |
|---|---|---|
| Usuarios concurrentes | 500 | 500 |
| Peticiones totales | 71287 | 71278 |
| Peticiones por segundo | 429,9 | 429,8 |
| Tasa de errores (%) | 0,0 | 0,0 |
| Latencia promedio (ms) | 2,3 | 2,5 |
| Latencia p95 (ms) | 3,9 | 3,8 |
| Latencia p99 (ms) | 5,8 | 5,9 |
| p95 crear pregunta (ms) | 4,0 | 3,8 |
| p95 listar asignadas (ms) | 3,3 | 1,5 |
| Umbrales cumplidos (p95 < 500 ms y errores < 1 %) | Sí | Sí |

Variación de la p95 al pasar a 2 instancias: 4,1 % (positivo = mejora).

Ejecutado: 2026-11-04T18:55:31.602Z y 2026-11-04T18:59:18.155Z.

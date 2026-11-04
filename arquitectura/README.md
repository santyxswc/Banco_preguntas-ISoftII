# Arquitectura distribuida — Corte 2

| Documento | Historia | Contenido |
|---|---|---|
| [01-bounded-contexts.md](01-bounded-contexts.md) | HU-T1 | Contextos delimitados, servicio y base de datos de cada uno |
| [02-contratos.md](02-contratos.md) | HU-T1.2 | Eventos de dominio y endpoints REST por servicio |
| [03-c4-contenedores.md](03-c4-contenedores.md) | HU-T8.3 | Diagrama C4 nivel 2: frontend web, API Gateway, microservicios, RabbitMQ y Swagger unificado |
| [04-escenario-escalabilidad.md](04-escenario-escalabilidad.md) | HU-T13 | Escenario de calidad, prueba de carga k6 y resultados medidos (1 y 2 instancias) |
| [05-entrega-garantizada.md](05-entrega-garantizada.md) | HU-T9.3 | Prueba de eventos pendientes al reiniciar el consumidor |
| [06-guion-video.md](06-guion-video.md) | HU-T16.1 | Guion del video con tiempos, responsables y qué decir |

Los diagramas están en `diagramas/`: el `.png` sirve para el documento Word y el video, y el `.mmd` (Mermaid) es la fuente editable. Incluye C4 niveles 1 a 3 (`c4-contexto`, `c4-contenedores`, `c4-componentes`), UML de clases y de secuencia (`uml-clases`, `uml-secuencia`), contextos delimitados (`contextos`) y las capturas del Sprint 2 en Jira (`sprint2-jira-lista.png`, `sprint2-jira-resumen.png`).

El documento de entrega del corte 2, que reúne todo esto con las HU, el modelo C4 completo (niveles 1 a 3), los diagramas UML, los patrones GoF y las pruebas, es [`../DOCUMENTO_ARQUITECTURA_CORTE2.md`](../DOCUMENTO_ARQUITECTURA_CORTE2.md) (versión PDF para entregar: [`../DOCUMENTO_ARQUITECTURA_CORTE2.pdf`](../DOCUMENTO_ARQUITECTURA_CORTE2.pdf); fuente Word: [`../DOCUMENTO_ARQUITECTURA_CORTE2.docx`](../DOCUMENTO_ARQUITECTURA_CORTE2.docx)). Video de sustentación: https://youtu.be/B-chmKrIsbY.

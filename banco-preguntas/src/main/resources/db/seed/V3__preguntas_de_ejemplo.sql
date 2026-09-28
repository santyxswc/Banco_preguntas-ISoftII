-- Preguntas de ejemplo del Banco de Preguntas.
-- Son las mismas que carga QuestionImplRepository en memoria.
-- Solo la aplicación carga esta carpeta; las pruebas usan db/migration.

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-001',
    'Lectura crítica - Idea principal',
    'Lee el siguiente fragmento: "El texto argumentativo busca convencer al lector mediante el uso de razones, evidencias y ejemplos. El autor expone su punto de vista y lo defiende con argumentos."',
    '¿Cuál de las siguientes opciones expresa mejor la idea principal del texto anterior?',
    'A',
    'La idea principal de un texto argumentativo es la tesis central que el autor defiende con razones y evidencias, no un resumen ni información biográfica.',
    'Cassany, D. (2006). Tras las líneas. Anagrama.',
    'Lectura crítica',
    'Comprensión textual',
    'Tipos de texto',
    'BASICO',
    'BORRADOR',
    'U-002',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-001', 'A', 'La opinión del autor sobre un tema, sustentada con razones'),
    ('P-001', 'B', 'Un resumen de todos los párrafos del texto'),
    ('P-001', 'C', 'La biografía del autor del texto'),
    ('P-001', 'D', 'El título del texto');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-002',
    'Razonamiento cuantitativo - Porcentajes',
    'En un curso de 50 estudiantes, el 40% aprobó el examen final. Los demás reprobaron.',
    '¿Qué fracción de estudiantes NO aprobó?',
    'B',
    'Si el 40% aprobó, el 60% no aprobó. 60/100 = 3/5.',
    'Stewart, J. (2015). Cálculo. Cengage Learning.',
    'Razonamiento cuantitativo',
    'Proporciones y porcentajes',
    'Porcentajes',
    'BASICO',
    'PENDIENTE_REVISION',
    'U-002',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-002', 'A', '2/5'),
    ('P-002', 'B', '3/5'),
    ('P-002', 'C', '1/4'),
    ('P-002', 'D', '4/5');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-003',
    'Competencias ciudadanas - Participación',
    'En una democracia representativa, los ciudadanos cuentan con diversos mecanismos para incidir en las decisiones del Estado y ejercer control sobre sus gobernantes.',
    '¿Cuál de las siguientes acciones es un ejemplo de participación ciudadana?',
    'A',
    'Votar es el mecanismo más básico de participación ciudadana en una democracia representativa.',
    'Constitución Política de Colombia (1991), Art. 40.',
    'Competencias ciudadanas',
    'Participación y responsabilidad democrática',
    'Mecanismos de participación',
    'INTERMEDIO',
    'EN_REVISION',
    'U-002',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-003', 'A', 'Votar en elecciones populares'),
    ('P-003', 'B', 'Ignorar las decisiones del gobierno local'),
    ('P-003', 'C', 'No informarse sobre asuntos públicos'),
    ('P-003', 'D', 'Evitar el diálogo con otros ciudadanos');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-004',
    'Comunicación escrita - Cohesión textual',
    'Los conectores lógicos son elementos lingüísticos que permiten unir ideas y establecer relaciones semánticas entre oraciones y párrafos.',
    '¿Qué conector es más adecuado para expresar una relación de causa-consecuencia?',
    'B',
    '"Por lo tanto" introduce una conclusión que se deriva lógicamente de lo afirmado anteriormente, expresando consecuencia.',
    'Halliday, M. A. K. (1976). Cohesion in English. Longman.',
    'Comunicación escrita',
    'Cohesión textual',
    'Conectores lógicos',
    'INTERMEDIO',
    'BORRADOR',
    'U-003',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-004', 'A', 'Sin embargo'),
    ('P-004', 'B', 'Por lo tanto'),
    ('P-004', 'C', 'Aunque'),
    ('P-004', 'D', 'Por ejemplo');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-005',
    'Inglés - Comprensión de lectura',
    'Read the following sentence carefully and choose the correct verb form to complete it: "She ___ to the office every day."',
    'Choose the option that best completes the sentence.',
    'B',
    'With a third-person singular subject (she/he/it) in simple present tense, the verb takes the -s/-es form.',
    'Murphy, R. (2019). English Grammar in Use. Cambridge.',
    'Inglés',
    'Gramática',
    'Presente simple',
    'BASICO',
    'BORRADOR',
    'U-003',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-005', 'A', 'go'),
    ('P-005', 'B', 'goes'),
    ('P-005', 'C', 'going'),
    ('P-005', 'D', 'gone');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-006',
    'Ingeniería de software - Arquitectura en capas',
    'La arquitectura en capas es un patrón de diseño arquitectónico ampliamente usado en el desarrollo de software empresarial. Organiza el sistema en capas con responsabilidades bien definidas.',
    '¿Cuál es la principal ventaja de organizar una aplicación en capas?',
    'B',
    'La separación de responsabilidades (SoC) reduce el acoplamiento y facilita el mantenimiento y las pruebas.',
    'Fowler, M. (2002). Patterns of Enterprise Application Architecture.',
    'Ingeniería de software',
    'Arquitectura de software',
    'Patrones arquitectónicos',
    'AVANZADO',
    'PENDIENTE_REVISION',
    'U-003',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-006', 'A', 'Reducir el número de clases del sistema'),
    ('P-006', 'B', 'Separar responsabilidades y reducir el acoplamiento'),
    ('P-006', 'C', 'Evitar el uso de bases de datos'),
    ('P-006', 'D', 'Eliminar la necesidad de pruebas unitarias');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-007',
    'Razonamiento cuantitativo - Regla de tres',
    'Un taller produce 120 piezas en 8 horas trabajando siempre al mismo ritmo de producción.',
    '¿Cuántas piezas producirá el taller en 5 horas?',
    'B',
    'El ritmo es 120 / 8 = 15 piezas por hora; en 5 horas se producen 15 x 5 = 75 piezas.',
    'Baldor, A. (2007). Aritmética. Grupo Editorial Patria.',
    'Razonamiento cuantitativo',
    'Proporciones y porcentajes',
    'Regla de tres',
    'BASICO',
    'EN_REVISION',
    'U-002',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-007', 'A', '60 piezas'),
    ('P-007', 'B', '75 piezas'),
    ('P-007', 'C', '80 piezas'),
    ('P-007', 'D', '96 piezas');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-008',
    'Lectura crítica - Inferencia',
    '"Desde que la biblioteca amplió su horario hasta la medianoche, el número de préstamos creció un 30%, aunque la cantidad de libros disponibles no cambió."',
    '¿Qué se puede inferir a partir del fragmento?',
    'A',
    'El único cambio mencionado es el horario, por lo que el aumento de préstamos se asocia con él; las demás opciones contradicen o no se apoyan en el texto.',
    'Cassany, D. (2006). Tras las líneas. Anagrama.',
    'Lectura crítica',
    'Comprensión textual',
    'Inferencia',
    'INTERMEDIO',
    'PENDIENTE_REVISION',
    'U-002',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-008', 'A', 'Los usuarios aprovechan el horario extendido'),
    ('P-008', 'B', 'La biblioteca compró muchos libros nuevos'),
    ('P-008', 'C', 'Los libros antiguos dejaron de prestarse'),
    ('P-008', 'D', 'El personal de la biblioteca se redujo');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-009',
    'Razonamiento cuantitativo - Promedios',
    'Las notas de un estudiante en cuatro evaluaciones con el mismo peso fueron 3,0; 4,0; 3,5 y 4,5.',
    '¿Cuál es el promedio de las cuatro notas?',
    'B',
    'La suma de las notas es 15,0 y al dividir entre 4 se obtiene 3,75.',
    'Triola, M. (2018). Estadística. Pearson.',
    'Razonamiento cuantitativo',
    'Estadística descriptiva',
    'Medidas de tendencia central',
    'BASICO',
    'BORRADOR',
    'U-002',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-009', 'A', '3,50'),
    ('P-009', 'B', '3,75'),
    ('P-009', 'C', '4,00'),
    ('P-009', 'D', '4,25');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-010',
    'Competencias ciudadanas - Acción de tutela',
    'Una persona considera que una entidad pública le está vulnerando su derecho fundamental a la salud y necesita una protección inmediata.',
    '¿Qué mecanismo constitucional es el más adecuado?',
    'A',
    'La acción de tutela (Art. 86) protege de forma inmediata los derechos fundamentales; los demás son mecanismos de participación política.',
    'Constitución Política de Colombia (1991), Art. 86.',
    'Competencias ciudadanas',
    'Constitución y derechos',
    'Mecanismos de protección',
    'INTERMEDIO',
    'ARCHIVADA',
    'U-002',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-010', 'A', 'La acción de tutela'),
    ('P-010', 'B', 'El referendo'),
    ('P-010', 'C', 'La revocatoria del mandato'),
    ('P-010', 'D', 'La consulta popular');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-011',
    'Razonamiento cuantitativo - Probabilidad',
    'En una bolsa hay 3 balotas rojas, 5 azules y 2 verdes, todas del mismo tamaño y peso.',
    '¿Cuál es la probabilidad de sacar una balota azul al azar?',
    'C',
    'Hay 5 balotas azules de un total de 10, así que la probabilidad es 5/10 = 1/2.',
    'Walpole, R. (2012). Probabilidad y estadística. Pearson.',
    'Razonamiento cuantitativo',
    'Estadística descriptiva',
    'Probabilidad simple',
    'INTERMEDIO',
    'PENDIENTE_REVISION',
    'U-002',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-011', 'A', '1/5'),
    ('P-011', 'B', '3/10'),
    ('P-011', 'C', '1/2'),
    ('P-011', 'D', '2/3');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-012',
    'Comunicación escrita - Ortografía',
    'Las palabras agudas llevan tilde cuando terminan en vocal, en n o en s, según las reglas de acentuación de la lengua española.',
    '¿Cuál de las siguientes palabras está escrita correctamente?',
    'A',
    '"Canción" es aguda terminada en n y lleva tilde; árbol y lápiz son graves terminadas en consonante distinta de n o s, y camión también la requiere.',
    'Real Academia Española (2010). Ortografía de la lengua española.',
    'Comunicación escrita',
    'Normas de escritura',
    'Acentuación',
    'BASICO',
    'EN_REVISION',
    'U-003',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-012', 'A', 'Canción'),
    ('P-012', 'B', 'Arbol'),
    ('P-012', 'C', 'Lapiz'),
    ('P-012', 'D', 'Camion');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-013',
    'Inglés - Pasado simple',
    'Read the sentence and choose the correct form of the verb: "Yesterday, they ___ a movie at home."',
    'Which option correctly completes the sentence?',
    'C',
    '"Yesterday" indicates a finished action in the past, so the simple past form "watched" is required.',
    'Murphy, R. (2019). English Grammar in Use. Cambridge.',
    'Inglés',
    'Gramática',
    'Pasado simple',
    'BASICO',
    'PENDIENTE_REVISION',
    'U-003',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-013', 'A', 'watch'),
    ('P-013', 'B', 'watches'),
    ('P-013', 'C', 'watched'),
    ('P-013', 'D', 'watching');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-014',
    'Comunicación escrita - Coherencia',
    'Un estudiante escribe un ensayo en el que cada párrafo trata un tema distinto sin relación con la tesis que planteó en la introducción.',
    '¿Qué propiedad textual se ve afectada principalmente?',
    'A',
    'La coherencia garantiza que las ideas se relacionen con un tema central; al romperse la relación con la tesis el texto pierde coherencia global.',
    'Van Dijk, T. (1983). La ciencia del texto. Paidós.',
    'Comunicación escrita',
    'Cohesión textual',
    'Coherencia global',
    'INTERMEDIO',
    'BORRADOR',
    'U-003',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-014', 'A', 'La coherencia'),
    ('P-014', 'B', 'La ortografía'),
    ('P-014', 'C', 'La puntuación'),
    ('P-014', 'D', 'La caligrafía');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-015',
    'Inglés - Vocabulario en contexto',
    '"The company decided to postpone the meeting until next week because the manager was sick."',
    'What does the word "postpone" mean in this sentence?',
    'B',
    '"Postpone" means to delay an event to a later time; the context "until next week" confirms it.',
    'Cambridge Dictionary. (2024). Postpone. Cambridge University Press.',
    'Inglés',
    'Comprensión de lectura',
    'Vocabulario en contexto',
    'INTERMEDIO',
    'ARCHIVADA',
    'U-003',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-015', 'A', 'Cancel forever'),
    ('P-015', 'B', 'Move to a later time'),
    ('P-015', 'C', 'Start earlier'),
    ('P-015', 'D', 'Make shorter');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-016',
    'Lectura crítica - Propósito del autor',
    '"Reciclar no es una moda: cada botella que separamos evita que toneladas de plástico lleguen a los ríos. Empieza hoy en tu casa."',
    '¿Cuál es el propósito principal del fragmento?',
    'A',
    'El uso del imperativo ("Empieza hoy") y del argumento sobre los ríos muestra una intención persuasiva.',
    'Cassany, D. (2006). Tras las líneas. Anagrama.',
    'Lectura crítica',
    'Comprensión textual',
    'Intención comunicativa',
    'BASICO',
    'EN_REVISION',
    'U-003',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-016', 'A', 'Persuadir al lector para que recicle'),
    ('P-016', 'B', 'Describir el proceso industrial del reciclaje'),
    ('P-016', 'C', 'Narrar la historia de una fábrica de botellas'),
    ('P-016', 'D', 'Explicar la composición química del plástico');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-017',
    'Ingeniería de software - Principio SOLID',
    'Una clase Reporte se encarga de calcular estadísticas, darles formato en PDF y enviarlas por correo electrónico a los interesados.',
    '¿Qué principio SOLID se incumple de forma más directa?',
    'A',
    'La clase tiene tres razones para cambiar (cálculo, formato y envío), lo que viola el principio de responsabilidad única.',
    'Martin, R. C. (2017). Clean Architecture. Prentice Hall.',
    'Ingeniería de software',
    'Diseño orientado a objetos',
    'Principios SOLID',
    'INTERMEDIO',
    'BORRADOR',
    'U-006',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-017', 'A', 'Responsabilidad única (SRP)'),
    ('P-017', 'B', 'Sustitución de Liskov (LSP)'),
    ('P-017', 'C', 'Segregación de interfaces (ISP)'),
    ('P-017', 'D', 'Inversión de dependencias (DIP)');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-018',
    'Ingeniería de software - Patrón Observer',
    'En una aplicación de escritorio varias ventanas deben actualizarse automáticamente cada vez que cambia el estado de un mismo objeto del dominio.',
    '¿Qué patrón de diseño resuelve mejor esta necesidad?',
    'A',
    'Observer define una dependencia uno a muchos: cuando el sujeto cambia, notifica a todos sus observadores.',
    'Gamma, E. et al. (1994). Design Patterns. Addison-Wesley.',
    'Ingeniería de software',
    'Patrones de diseño',
    'Patrones de comportamiento',
    'INTERMEDIO',
    'PENDIENTE_REVISION',
    'U-006',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-018', 'A', 'Observer'),
    ('P-018', 'B', 'Singleton'),
    ('P-018', 'C', 'Builder'),
    ('P-018', 'D', 'Adapter');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-019',
    'Ingeniería de software - Pruebas unitarias',
    'Un equipo quiere verificar el comportamiento de un servicio sin depender de la base de datos real, que es lenta y compartida con otros equipos.',
    '¿Qué técnica es la más adecuada para lograrlo?',
    'A',
    'Los dobles de prueba (fakes o mocks) reemplazan la dependencia real y permiten pruebas rápidas y aisladas.',
    'Meszaros, G. (2007). xUnit Test Patterns. Addison-Wesley.',
    'Ingeniería de software',
    'Calidad de software',
    'Pruebas unitarias',
    'AVANZADO',
    'EN_REVISION',
    'U-006',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-019', 'A', 'Usar un doble de prueba del repositorio'),
    ('P-019', 'B', 'Ejecutar las pruebas solo en producción'),
    ('P-019', 'C', 'Eliminar las pruebas del servicio'),
    ('P-019', 'D', 'Probar manualmente cada funcionalidad');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-020',
    'Ingeniería de software - Requisitos',
    'Durante el levantamiento de requisitos, el cliente indica que el sistema debe responder cualquier consulta en menos de dos segundos.',
    '¿Qué tipo de requisito expresa el cliente?',
    'A',
    'El tiempo de respuesta describe una cualidad del sistema (rendimiento), no una función que deba realizar.',
    'Sommerville, I. (2016). Software Engineering. Pearson.',
    'Ingeniería de software',
    'Ingeniería de requisitos',
    'Requisitos no funcionales',
    'BASICO',
    'BORRADOR',
    'U-006',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-020', 'A', 'Un requisito no funcional de rendimiento'),
    ('P-020', 'B', 'Un requisito funcional de negocio'),
    ('P-020', 'C', 'Una regla de validación de datos'),
    ('P-020', 'D', 'Una historia de usuario de interfaz');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-021',
    'Ingeniería de software - Control de versiones',
    'Dos desarrolladores trabajan en funcionalidades distintas del mismo proyecto y no quieren afectar la rama principal hasta terminar su trabajo.',
    '¿Qué práctica de Git deben aplicar?',
    'A',
    'Las ramas aíslan el trabajo en curso y permiten integrarlo luego mediante merge o pull request.',
    'Chacon, S. y Straub, B. (2014). Pro Git. Apress.',
    'Ingeniería de software',
    'Gestión de la configuración',
    'Control de versiones',
    'BASICO',
    'ARCHIVADA',
    'U-006',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-021', 'A', 'Trabajar en ramas separadas'),
    ('P-021', 'B', 'Borrar el historial del repositorio'),
    ('P-021', 'C', 'Subir los cambios sin hacer commit'),
    ('P-021', 'D', 'Editar directamente la rama principal');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-022',
    'Competencias ciudadanas - Resolución de conflictos',
    'Dos vecinos discuten porque uno de ellos pone música a alto volumen en la noche y el otro no puede dormir.',
    '¿Cuál es la forma más adecuada de manejar el conflicto?',
    'A',
    'El diálogo y la concertación son las vías pacíficas de resolución de conflictos que promueve la convivencia.',
    'Ministerio de Educación Nacional (2004). Estándares básicos de competencias ciudadanas.',
    'Competencias ciudadanas',
    'Convivencia y paz',
    'Resolución de conflictos',
    'BASICO',
    'PENDIENTE_REVISION',
    'U-007',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-022', 'A', 'Dialogar y acordar horarios razonables'),
    ('P-022', 'B', 'Responder con música a mayor volumen'),
    ('P-022', 'C', 'Dañar el equipo de sonido del vecino'),
    ('P-022', 'D', 'Ignorar el problema de forma indefinida');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-023',
    'Razonamiento cuantitativo - Interpretación de gráficas',
    'Una tienda vendió 200 unidades en enero, 250 en febrero y 300 en marzo, según su gráfico de barras mensual.',
    '¿En qué porcentaje aumentaron las ventas de enero a marzo?',
    'C',
    'El aumento es de 100 unidades sobre 200 iniciales: 100 / 200 = 0,5, es decir, un 50%.',
    'Triola, M. (2018). Estadística. Pearson.',
    'Razonamiento cuantitativo',
    'Proporciones y porcentajes',
    'Variación porcentual',
    'INTERMEDIO',
    'BORRADOR',
    'U-007',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-023', 'A', '25%'),
    ('P-023', 'B', '33%'),
    ('P-023', 'C', '50%'),
    ('P-023', 'D', '100%');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-024',
    'Lectura crítica - Argumentos y falacias',
    '"No deberíamos aceptar la propuesta de reforma de Juan, porque él llegó tarde a la reunión de ayer."',
    '¿Qué tipo de falacia contiene el argumento?',
    'A',
    'Se descalifica la propuesta atacando una característica de quien la presenta, no su contenido: es ad hominem.',
    'Weston, A. (2006). Las claves de la argumentación. Ariel.',
    'Lectura crítica',
    'Argumentación',
    'Falacias',
    'AVANZADO',
    'EN_REVISION',
    'U-007',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-024', 'A', 'Ataque a la persona (ad hominem)'),
    ('P-024', 'B', 'Apelación a la mayoría'),
    ('P-024', 'C', 'Pendiente resbaladiza'),
    ('P-024', 'D', 'Falso dilema');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-025',
    'Inglés - Preposiciones de tiempo',
    'Complete the sentence with the correct preposition: "The class starts ___ 8 o''clock in the morning."',
    'Which preposition correctly completes the sentence?',
    'C',
    '"At" is used with specific clock times, while "in" and "on" are used with months and days.',
    'Murphy, R. (2019). English Grammar in Use. Cambridge.',
    'Inglés',
    'Gramática',
    'Preposiciones',
    'BASICO',
    'PENDIENTE_REVISION',
    'U-007',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-025', 'A', 'in'),
    ('P-025', 'B', 'on'),
    ('P-025', 'C', 'at'),
    ('P-025', 'D', 'by');

INSERT INTO preguntas (id, nombre, contexto, enunciado, respuesta_correcta, justificacion, bibliografia, competencia, tema, subtema, nivel_dificultad, estado, autor_id, fecha_creacion, fecha_modificacion) VALUES (
    'P-026',
    'Comunicación escrita - Signos de puntuación',
    'La coma se utiliza para separar los elementos de una enumeración, excepto el último cuando va precedido por las conjunciones y, e, o, u, ni.',
    '¿Cuál de las oraciones está puntuada correctamente?',
    'A',
    'Solo la opción A separa con coma los elementos de la enumeración y omite la coma antes de la conjunción y.',
    'Real Academia Española (2010). Ortografía de la lengua española.',
    'Comunicación escrita',
    'Normas de escritura',
    'Puntuación',
    'BASICO',
    'BORRADOR',
    'U-007',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);
INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) VALUES
    ('P-026', 'A', 'Compré pan, leche, huevos y queso.'),
    ('P-026', 'B', 'Compré pan leche, huevos, y queso.'),
    ('P-026', 'C', 'Compré, pan, leche huevos y queso.'),
    ('P-026', 'D', 'Compré pan, leche huevos, y, queso.');


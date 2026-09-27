/**
 * @file QuestionImplRepository.java
 * @brief Repositorio en memoria de preguntas.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.access;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.NivelDificultad;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionDistractors;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @brief Implementación en memoria de QuestionRepository.
 *
 * Guarda las preguntas en un LinkedHashMap y arranca con datos de
 * ejemplo de varios autores y estados para poder probar las vistas.
 */
public class QuestionImplRepository implements QuestionRepository {

    private final Map<String, Question> preguntas =
            new LinkedHashMap<>();

    public QuestionImplRepository() {
        cargarDatosDeEjemplo();
    }

    @Override
    public List<Question> list() {
        return new ArrayList<>(preguntas.values());
    }

    @Override
    public Question findById(String id) {
        if (id == null) {
            return null;
        }
        return preguntas.get(id);
    }

    @Override
    public List<Question> findByAutorId(String autorId) {
        List<Question> resultado = new ArrayList<>();
        for (Question q : preguntas.values()) {
            if (autorId != null && autorId.equals(q.getAutorId())) {
                resultado.add(q);
            }
        }
        return resultado;
    }

    @Override
    public List<Question> findByEstado(EstadoPregunta estado) {
        List<Question> resultado = new ArrayList<>();
        for (Question q : preguntas.values()) {
            if (estado != null && estado == q.getEstado()) {
                resultado.add(q);
            }
        }
        return resultado;
    }

    @Override
    public boolean save(Question question) {

        if (question == null
                || question.getId() == null
                || question.getId().isBlank()) {
            return false;
        }

        if (preguntas.containsKey(question.getId())) {
            return false;
        }

        preguntas.put(question.getId(), question);
        return true;
    }

    @Override
    public boolean update(Question question) {

        if (question == null || question.getId() == null) {
            return false;
        }

        if (!preguntas.containsKey(question.getId())) {
            return false;
        }

        preguntas.put(question.getId(), question);
        return true;
    }

    @Override
    public boolean updateEstado(String id, EstadoPregunta nuevoEstado) {

        if (id == null || nuevoEstado == null) {
            return false;
        }

        Question pregunta = preguntas.get(id);

        if (pregunta == null) {
            return false;
        }

        pregunta.setEstado(nuevoEstado);
        return true;
    }

    /** @brief Carga preguntas de ejemplo de varios autores y estados. */
    private void cargarDatosDeEjemplo() {

        // Autor: Carlos Morales (U-002)
        agregar("P-001", "Lectura crítica - Idea principal",
                "U-002",
                "Lee el siguiente fragmento: \"El texto argumentativo "
                + "busca convencer al lector mediante el uso de "
                + "razones, evidencias y ejemplos. El autor expone "
                + "su punto de vista y lo defiende con argumentos.\"",
                "¿Cuál de las siguientes opciones expresa mejor la "
                        + "idea principal del texto anterior?",
                new String[]{
                        "La opinión del autor sobre un tema, sustentada con razones",
                        "Un resumen de todos los párrafos del texto",
                        "La biografía del autor del texto",
                        "El título del texto"
                },
                "A",
                "La idea principal de un texto argumentativo es la "
                + "tesis central que el autor defiende con razones "
                + "y evidencias, no un resumen ni información biográfica.",
                "Cassany, D. (2006). Tras las líneas. Anagrama.",
                "Lectura crítica",
                "Comprensión textual",
                "Tipos de texto",
                NivelDificultad.BASICO,
                EstadoPregunta.BORRADOR);

        agregar("P-002", "Razonamiento cuantitativo - Porcentajes",
                "U-002",
                "En un curso de 50 estudiantes, el 40% aprobó el examen "
                + "final. Los demás reprobaron.",
                "¿Qué fracción de estudiantes NO aprobó?",
                new String[]{"2/5", "3/5", "1/4", "4/5"},
                "B",
                "Si el 40% aprobó, el 60% no aprobó. 60/100 = 3/5.",
                "Stewart, J. (2015). Cálculo. Cengage Learning.",
                "Razonamiento cuantitativo",
                "Proporciones y porcentajes",
                "Porcentajes",
                NivelDificultad.BASICO,
                EstadoPregunta.PENDIENTE_REVISION);

        agregar("P-003", "Competencias ciudadanas - Participación",
                "U-002",
                "En una democracia representativa, los ciudadanos "
                + "cuentan con diversos mecanismos para incidir en "
                + "las decisiones del Estado y ejercer control sobre "
                + "sus gobernantes.",
                "¿Cuál de las siguientes acciones es un ejemplo de "
                        + "participación ciudadana?",
                new String[]{
                        "Votar en elecciones populares",
                        "Ignorar las decisiones del gobierno local",
                        "No informarse sobre asuntos públicos",
                        "Evitar el diálogo con otros ciudadanos"
                },
                "A",
                "Votar es el mecanismo más básico de participación "
                + "ciudadana en una democracia representativa.",
                "Constitución Política de Colombia (1991), Art. 40.",
                "Competencias ciudadanas",
                "Participación y responsabilidad democrática",
                "Mecanismos de participación",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.EN_REVISION);

        // Autor: Laura Pérez (U-003)
        agregar("P-004", "Comunicación escrita - Cohesión textual",
                "U-003",
                "Los conectores lógicos son elementos lingüísticos que "
                + "permiten unir ideas y establecer relaciones "
                + "semánticas entre oraciones y párrafos.",
                "¿Qué conector es más adecuado para expresar una "
                        + "relación de causa-consecuencia?",
                new String[]{
                        "Sin embargo", "Por lo tanto",
                        "Aunque", "Por ejemplo"
                },
                "B",
                "\"Por lo tanto\" introduce una conclusión que se "
                + "deriva lógicamente de lo afirmado anteriormente, "
                + "expresando consecuencia.",
                "Halliday, M. A. K. (1976). Cohesion in English. Longman.",
                "Comunicación escrita",
                "Cohesión textual",
                "Conectores lógicos",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.BORRADOR);

        agregar("P-005", "Inglés - Comprensión de lectura",
                "U-003",
                "Read the following sentence carefully and choose the "
                + "correct verb form to complete it: "
                + "\"She ___ to the office every day.\"",
                "Choose the option that best completes the sentence.",
                new String[]{"go", "goes", "going", "gone"},
                "B",
                "With a third-person singular subject (she/he/it) in "
                + "simple present tense, the verb takes the -s/-es form.",
                "Murphy, R. (2019). English Grammar in Use. Cambridge.",
                "Inglés",
                "Gramática",
                "Presente simple",
                NivelDificultad.BASICO,
                EstadoPregunta.BORRADOR);

        agregar("P-006",
                "Ingeniería de software - Arquitectura en capas",
                "U-003",
                "La arquitectura en capas es un patrón de diseño "
                + "arquitectónico ampliamente usado en el desarrollo "
                + "de software empresarial. Organiza el sistema en "
                + "capas con responsabilidades bien definidas.",
                "¿Cuál es la principal ventaja de organizar una "
                        + "aplicación en capas?",
                new String[]{
                        "Reducir el número de clases del sistema",
                        "Separar responsabilidades y reducir el acoplamiento",
                        "Evitar el uso de bases de datos",
                        "Eliminar la necesidad de pruebas unitarias"
                },
                "B",
                "La separación de responsabilidades (SoC) reduce el "
                + "acoplamiento y facilita el mantenimiento y las pruebas.",
                "Fowler, M. (2002). Patterns of Enterprise Application Architecture.",
                "Ingeniería de software",
                "Arquitectura de software",
                "Patrones arquitectónicos",
                NivelDificultad.AVANZADO,
                EstadoPregunta.PENDIENTE_REVISION);

        // Autor: Carlos Morales (U-002)
        agregar("P-007", "Razonamiento cuantitativo - Regla de tres",
                "U-002",
                "Un taller produce 120 piezas en 8 horas trabajando "
                + "siempre al mismo ritmo de producción.",
                "¿Cuántas piezas producirá el taller en 5 horas?",
                new String[]{"60 piezas", "75 piezas", "80 piezas", "96 piezas"},
                "B",
                "El ritmo es 120 / 8 = 15 piezas por hora; en 5 horas "
                + "se producen 15 x 5 = 75 piezas.",
                "Baldor, A. (2007). Aritmética. Grupo Editorial Patria.",
                "Razonamiento cuantitativo",
                "Proporciones y porcentajes",
                "Regla de tres",
                NivelDificultad.BASICO,
                EstadoPregunta.EN_REVISION);

        agregar("P-008", "Lectura crítica - Inferencia",
                "U-002",
                "\"Desde que la biblioteca amplió su horario hasta la "
                + "medianoche, el número de préstamos creció un 30%, "
                + "aunque la cantidad de libros disponibles no cambió.\"",
                "¿Qué se puede inferir a partir del fragmento?",
                new String[]{
                        "Los usuarios aprovechan el horario extendido",
                        "La biblioteca compró muchos libros nuevos",
                        "Los libros antiguos dejaron de prestarse",
                        "El personal de la biblioteca se redujo"
                },
                "A",
                "El único cambio mencionado es el horario, por lo que el "
                + "aumento de préstamos se asocia con él; las demás "
                + "opciones contradicen o no se apoyan en el texto.",
                "Cassany, D. (2006). Tras las líneas. Anagrama.",
                "Lectura crítica",
                "Comprensión textual",
                "Inferencia",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.PENDIENTE_REVISION);

        agregar("P-009", "Razonamiento cuantitativo - Promedios",
                "U-002",
                "Las notas de un estudiante en cuatro evaluaciones con "
                + "el mismo peso fueron 3,0; 4,0; 3,5 y 4,5.",
                "¿Cuál es el promedio de las cuatro notas?",
                new String[]{"3,50", "3,75", "4,00", "4,25"},
                "B",
                "La suma de las notas es 15,0 y al dividir entre 4 "
                + "se obtiene 3,75.",
                "Triola, M. (2018). Estadística. Pearson.",
                "Razonamiento cuantitativo",
                "Estadística descriptiva",
                "Medidas de tendencia central",
                NivelDificultad.BASICO,
                EstadoPregunta.BORRADOR);

        agregar("P-010", "Competencias ciudadanas - Acción de tutela",
                "U-002",
                "Una persona considera que una entidad pública le está "
                + "vulnerando su derecho fundamental a la salud y "
                + "necesita una protección inmediata.",
                "¿Qué mecanismo constitucional es el más adecuado?",
                new String[]{
                        "La acción de tutela",
                        "El referendo",
                        "La revocatoria del mandato",
                        "La consulta popular"
                },
                "A",
                "La acción de tutela (Art. 86) protege de forma inmediata "
                + "los derechos fundamentales; los demás son mecanismos "
                + "de participación política.",
                "Constitución Política de Colombia (1991), Art. 86.",
                "Competencias ciudadanas",
                "Constitución y derechos",
                "Mecanismos de protección",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.ARCHIVADA);

        agregar("P-011", "Razonamiento cuantitativo - Probabilidad",
                "U-002",
                "En una bolsa hay 3 balotas rojas, 5 azules y 2 verdes, "
                + "todas del mismo tamaño y peso.",
                "¿Cuál es la probabilidad de sacar una balota azul al azar?",
                new String[]{"1/5", "3/10", "1/2", "2/3"},
                "C",
                "Hay 5 balotas azules de un total de 10, así que la "
                + "probabilidad es 5/10 = 1/2.",
                "Walpole, R. (2012). Probabilidad y estadística. Pearson.",
                "Razonamiento cuantitativo",
                "Estadística descriptiva",
                "Probabilidad simple",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.PENDIENTE_REVISION);

        // Autor: Laura Pérez (U-003)
        agregar("P-012", "Comunicación escrita - Ortografía",
                "U-003",
                "Las palabras agudas llevan tilde cuando terminan en "
                + "vocal, en n o en s, según las reglas de acentuación "
                + "de la lengua española.",
                "¿Cuál de las siguientes palabras está escrita correctamente?",
                new String[]{"Canción", "Arbol", "Lapiz", "Camion"},
                "A",
                "\"Canción\" es aguda terminada en n y lleva tilde; "
                + "árbol y lápiz son graves terminadas en consonante "
                + "distinta de n o s, y camión también la requiere.",
                "Real Academia Española (2010). Ortografía de la lengua española.",
                "Comunicación escrita",
                "Normas de escritura",
                "Acentuación",
                NivelDificultad.BASICO,
                EstadoPregunta.EN_REVISION);

        agregar("P-013", "Inglés - Pasado simple",
                "U-003",
                "Read the sentence and choose the correct form of the "
                + "verb: \"Yesterday, they ___ a movie at home.\"",
                "Which option correctly completes the sentence?",
                new String[]{"watch", "watches", "watched", "watching"},
                "C",
                "\"Yesterday\" indicates a finished action in the past, "
                + "so the simple past form \"watched\" is required.",
                "Murphy, R. (2019). English Grammar in Use. Cambridge.",
                "Inglés",
                "Gramática",
                "Pasado simple",
                NivelDificultad.BASICO,
                EstadoPregunta.PENDIENTE_REVISION);

        agregar("P-014", "Comunicación escrita - Coherencia",
                "U-003",
                "Un estudiante escribe un ensayo en el que cada párrafo "
                + "trata un tema distinto sin relación con la tesis que "
                + "planteó en la introducción.",
                "¿Qué propiedad textual se ve afectada principalmente?",
                new String[]{"La coherencia", "La ortografía",
                        "La puntuación", "La caligrafía"},
                "A",
                "La coherencia garantiza que las ideas se relacionen con "
                + "un tema central; al romperse la relación con la tesis "
                + "el texto pierde coherencia global.",
                "Van Dijk, T. (1983). La ciencia del texto. Paidós.",
                "Comunicación escrita",
                "Cohesión textual",
                "Coherencia global",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.BORRADOR);

        agregar("P-015", "Inglés - Vocabulario en contexto",
                "U-003",
                "\"The company decided to postpone the meeting until "
                + "next week because the manager was sick.\"",
                "What does the word \"postpone\" mean in this sentence?",
                new String[]{"Cancel forever", "Move to a later time",
                        "Start earlier", "Make shorter"},
                "B",
                "\"Postpone\" means to delay an event to a later time; "
                + "the context \"until next week\" confirms it.",
                "Cambridge Dictionary. (2024). Postpone. Cambridge University Press.",
                "Inglés",
                "Comprensión de lectura",
                "Vocabulario en contexto",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.ARCHIVADA);

        agregar("P-016", "Lectura crítica - Propósito del autor",
                "U-003",
                "\"Reciclar no es una moda: cada botella que separamos "
                + "evita que toneladas de plástico lleguen a los ríos. "
                + "Empieza hoy en tu casa.\"",
                "¿Cuál es el propósito principal del fragmento?",
                new String[]{
                        "Persuadir al lector para que recicle",
                        "Describir el proceso industrial del reciclaje",
                        "Narrar la historia de una fábrica de botellas",
                        "Explicar la composición química del plástico"
                },
                "A",
                "El uso del imperativo (\"Empieza hoy\") y del argumento "
                + "sobre los ríos muestra una intención persuasiva.",
                "Cassany, D. (2006). Tras las líneas. Anagrama.",
                "Lectura crítica",
                "Comprensión textual",
                "Intención comunicativa",
                NivelDificultad.BASICO,
                EstadoPregunta.EN_REVISION);

        // Autor: Andrés Gómez (U-006)
        agregar("P-017", "Ingeniería de software - Principio SOLID",
                "U-006",
                "Una clase Reporte se encarga de calcular estadísticas, "
                + "darles formato en PDF y enviarlas por correo "
                + "electrónico a los interesados.",
                "¿Qué principio SOLID se incumple de forma más directa?",
                new String[]{
                        "Responsabilidad única (SRP)",
                        "Sustitución de Liskov (LSP)",
                        "Segregación de interfaces (ISP)",
                        "Inversión de dependencias (DIP)"
                },
                "A",
                "La clase tiene tres razones para cambiar (cálculo, "
                + "formato y envío), lo que viola el principio de "
                + "responsabilidad única.",
                "Martin, R. C. (2017). Clean Architecture. Prentice Hall.",
                "Ingeniería de software",
                "Diseño orientado a objetos",
                "Principios SOLID",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.BORRADOR);

        agregar("P-018", "Ingeniería de software - Patrón Observer",
                "U-006",
                "En una aplicación de escritorio varias ventanas deben "
                + "actualizarse automáticamente cada vez que cambia el "
                + "estado de un mismo objeto del dominio.",
                "¿Qué patrón de diseño resuelve mejor esta necesidad?",
                new String[]{"Observer", "Singleton", "Builder", "Adapter"},
                "A",
                "Observer define una dependencia uno a muchos: cuando el "
                + "sujeto cambia, notifica a todos sus observadores.",
                "Gamma, E. et al. (1994). Design Patterns. Addison-Wesley.",
                "Ingeniería de software",
                "Patrones de diseño",
                "Patrones de comportamiento",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.PENDIENTE_REVISION);

        agregar("P-019", "Ingeniería de software - Pruebas unitarias",
                "U-006",
                "Un equipo quiere verificar el comportamiento de un "
                + "servicio sin depender de la base de datos real, que "
                + "es lenta y compartida con otros equipos.",
                "¿Qué técnica es la más adecuada para lograrlo?",
                new String[]{
                        "Usar un doble de prueba del repositorio",
                        "Ejecutar las pruebas solo en producción",
                        "Eliminar las pruebas del servicio",
                        "Probar manualmente cada funcionalidad"
                },
                "A",
                "Los dobles de prueba (fakes o mocks) reemplazan la "
                + "dependencia real y permiten pruebas rápidas y aisladas.",
                "Meszaros, G. (2007). xUnit Test Patterns. Addison-Wesley.",
                "Ingeniería de software",
                "Calidad de software",
                "Pruebas unitarias",
                NivelDificultad.AVANZADO,
                EstadoPregunta.EN_REVISION);

        agregar("P-020", "Ingeniería de software - Requisitos",
                "U-006",
                "Durante el levantamiento de requisitos, el cliente "
                + "indica que el sistema debe responder cualquier "
                + "consulta en menos de dos segundos.",
                "¿Qué tipo de requisito expresa el cliente?",
                new String[]{
                        "Un requisito no funcional de rendimiento",
                        "Un requisito funcional de negocio",
                        "Una regla de validación de datos",
                        "Una historia de usuario de interfaz"
                },
                "A",
                "El tiempo de respuesta describe una cualidad del sistema "
                + "(rendimiento), no una función que deba realizar.",
                "Sommerville, I. (2016). Software Engineering. Pearson.",
                "Ingeniería de software",
                "Ingeniería de requisitos",
                "Requisitos no funcionales",
                NivelDificultad.BASICO,
                EstadoPregunta.BORRADOR);

        agregar("P-021", "Ingeniería de software - Control de versiones",
                "U-006",
                "Dos desarrolladores trabajan en funcionalidades "
                + "distintas del mismo proyecto y no quieren afectar la "
                + "rama principal hasta terminar su trabajo.",
                "¿Qué práctica de Git deben aplicar?",
                new String[]{
                        "Trabajar en ramas separadas",
                        "Borrar el historial del repositorio",
                        "Subir los cambios sin hacer commit",
                        "Editar directamente la rama principal"
                },
                "A",
                "Las ramas aíslan el trabajo en curso y permiten "
                + "integrarlo luego mediante merge o pull request.",
                "Chacon, S. y Straub, B. (2014). Pro Git. Apress.",
                "Ingeniería de software",
                "Gestión de la configuración",
                "Control de versiones",
                NivelDificultad.BASICO,
                EstadoPregunta.ARCHIVADA);

        // Autora: Sofía Martínez (U-007)
        agregar("P-022", "Competencias ciudadanas - Resolución de conflictos",
                "U-007",
                "Dos vecinos discuten porque uno de ellos pone música a "
                + "alto volumen en la noche y el otro no puede dormir.",
                "¿Cuál es la forma más adecuada de manejar el conflicto?",
                new String[]{
                        "Dialogar y acordar horarios razonables",
                        "Responder con música a mayor volumen",
                        "Dañar el equipo de sonido del vecino",
                        "Ignorar el problema de forma indefinida"
                },
                "A",
                "El diálogo y la concertación son las vías pacíficas de "
                + "resolución de conflictos que promueve la convivencia.",
                "Ministerio de Educación Nacional (2004). Estándares básicos de competencias ciudadanas.",
                "Competencias ciudadanas",
                "Convivencia y paz",
                "Resolución de conflictos",
                NivelDificultad.BASICO,
                EstadoPregunta.PENDIENTE_REVISION);

        agregar("P-023", "Razonamiento cuantitativo - Interpretación de gráficas",
                "U-007",
                "Una tienda vendió 200 unidades en enero, 250 en febrero "
                + "y 300 en marzo, según su gráfico de barras mensual.",
                "¿En qué porcentaje aumentaron las ventas de enero a marzo?",
                new String[]{"25%", "33%", "50%", "100%"},
                "C",
                "El aumento es de 100 unidades sobre 200 iniciales: "
                + "100 / 200 = 0,5, es decir, un 50%.",
                "Triola, M. (2018). Estadística. Pearson.",
                "Razonamiento cuantitativo",
                "Proporciones y porcentajes",
                "Variación porcentual",
                NivelDificultad.INTERMEDIO,
                EstadoPregunta.BORRADOR);

        agregar("P-024", "Lectura crítica - Argumentos y falacias",
                "U-007",
                "\"No deberíamos aceptar la propuesta de reforma de "
                + "Juan, porque él llegó tarde a la reunión de ayer.\"",
                "¿Qué tipo de falacia contiene el argumento?",
                new String[]{
                        "Ataque a la persona (ad hominem)",
                        "Apelación a la mayoría",
                        "Pendiente resbaladiza",
                        "Falso dilema"
                },
                "A",
                "Se descalifica la propuesta atacando una característica "
                + "de quien la presenta, no su contenido: es ad hominem.",
                "Weston, A. (2006). Las claves de la argumentación. Ariel.",
                "Lectura crítica",
                "Argumentación",
                "Falacias",
                NivelDificultad.AVANZADO,
                EstadoPregunta.EN_REVISION);

        agregar("P-025", "Inglés - Preposiciones de tiempo",
                "U-007",
                "Complete the sentence with the correct preposition: "
                + "\"The class starts ___ 8 o'clock in the morning.\"",
                "Which preposition correctly completes the sentence?",
                new String[]{"in", "on", "at", "by"},
                "C",
                "\"At\" is used with specific clock times, while \"in\" "
                + "and \"on\" are used with months and days.",
                "Murphy, R. (2019). English Grammar in Use. Cambridge.",
                "Inglés",
                "Gramática",
                "Preposiciones",
                NivelDificultad.BASICO,
                EstadoPregunta.PENDIENTE_REVISION);

        agregar("P-026", "Comunicación escrita - Signos de puntuación",
                "U-007",
                "La coma se utiliza para separar los elementos de una "
                + "enumeración, excepto el último cuando va precedido "
                + "por las conjunciones y, e, o, u, ni.",
                "¿Cuál de las oraciones está puntuada correctamente?",
                new String[]{
                        "Compré pan, leche, huevos y queso.",
                        "Compré pan leche, huevos, y queso.",
                        "Compré, pan, leche huevos y queso.",
                        "Compré pan, leche huevos, y, queso."
                },
                "A",
                "Solo la opción A separa con coma los elementos de la "
                + "enumeración y omite la coma antes de la conjunción y.",
                "Real Academia Española (2010). Ortografía de la lengua española.",
                "Comunicación escrita",
                "Normas de escritura",
                "Puntuación",
                NivelDificultad.BASICO,
                EstadoPregunta.BORRADOR);
    }

    /** @brief Construye y registra una pregunta completa de ejemplo. */
    private void agregar(String id, String nombre, String autorId,
                          String contexto, String enunciado,
                          String[] textosOpciones, String letraCorrecta,
                          String justificacion, String bibliografia,
                          String competencia, String tema, String subtema,
                          NivelDificultad nivelDificultad,
                          EstadoPregunta estado) {

        List<QuestionDistractors> opciones = new ArrayList<>();
        String[] letras = {"A", "B", "C", "D"};

        for (int i = 0; i < textosOpciones.length && i < letras.length; i++) {
            opciones.add(new QuestionDistractors(letras[i], textosOpciones[i]));
        }

        Question pregunta = new Question(id, nombre, enunciado, opciones,
                letraCorrecta, estado);
        pregunta.setContexto(contexto);
        pregunta.setJustificacion(justificacion);
        pregunta.setBibliografia(bibliografia);
        pregunta.setCompetencia(competencia);
        pregunta.setTema(tema);
        pregunta.setSubtema(subtema);
        pregunta.setNivelDificultad(nivelDificultad);
        pregunta.setAutorId(autorId);

        preguntas.put(id, pregunta);
    }
}

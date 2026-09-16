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
 * Implementación de {@link QuestionRepository}.
 *
 * Pertenece a la capa de acceso a datos. Usa un mapa en memoria como
 * estructura de almacenamiento. QuestionService no conoce esta clase
 * directamente (DIP).
 *
 * Los datos de ejemplo están repartidos entre varios autores y en
 * distintos estados para poder probar de inmediato todas las vistas.
 */
public class QuestionImplRepository implements QuestionRepository {

    private final Map<String, Question> preguntas =
            new LinkedHashMap<>();

    public QuestionImplRepository() {
        cargarDatosDeEjemplo();
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

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
            // El id ya existe; save() es solo para altas nuevas.
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

    // ----------------------------------------------------------------
    // Datos de ejemplo
    // ----------------------------------------------------------------

    /**
     * Carga un banco de preguntas de ejemplo con todos los campos
     * requeridos por RF01.1, distribuidas entre varios autores y
     * en distintos estados.
     */
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
    }

    /**
     * Método auxiliar para construir y registrar una pregunta completa
     * con todos los campos requeridos por RF01.1.
     */
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

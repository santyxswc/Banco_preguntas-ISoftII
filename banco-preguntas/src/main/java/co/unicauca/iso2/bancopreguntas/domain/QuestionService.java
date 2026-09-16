package co.unicauca.iso2.bancopreguntas.domain;

import co.unicauca.iso2.bancopreguntas.infra.Subject;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servicio encargado de la lógica de negocio relacionada con las
 * preguntas del banco.
 *
 * Aplica el Principio de Inversión de Dependencias (DIP): depende de
 * {@link QuestionRepository} y {@link UsuarioRepository}, no de sus
 * implementaciones concretas.
 *
 * Además, extiende {@link Subject} (patrón Observer): es el "sujeto"
 * del micro patrón MVC. Cada vez que cambia el estado de una
 * pregunta, notifica a las vistas registradas para que se rendericen.
 */
public class QuestionService extends Subject {

    // Longitudes mínimas/máximas para textos largos (RF01.2)
    private static final int MIN_TEXTO_LARGO = 20;
    private static final int MAX_TEXTO_LARGO = 2000;
    private static final int MIN_ENUNCIADO = 10;
    private static final int CANTIDAD_DISTRACTORES = 4;

    private final QuestionRepository questionRepository;
    private final UsuarioRepository usuarioRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this(questionRepository, null);
    }

    public QuestionService(QuestionRepository questionRepository,
            UsuarioRepository usuarioRepository) {
        this.questionRepository = questionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ----------------------------------------------------------------
    // Consultas
    // ----------------------------------------------------------------

    /**
     * Lista todas las preguntas del banco.
     *
     * @return lista de preguntas
     */
    public List<Question> listQuestions() {
        return questionRepository.list();
    }

    /**
     * Lista las preguntas que pertenecen a un autor específico (RF03.1).
     *
     * @param autorId identificador del autor
     * @return lista de preguntas del autor
     */
    public List<Question> listByAutor(String autorId) {
        if (autorId == null || autorId.isBlank()) {
            return new ArrayList<>();
        }
        return questionRepository.findByAutorId(autorId);
    }

    /**
     * Lista las preguntas en un estado específico.
     * Usada por el admin para ver las pendientes de revisión (RF04.1).
     *
     * @param estado estado a filtrar
     * @return lista de preguntas en ese estado
     */
    public List<Question> listByEstado(EstadoPregunta estado) {
        if (estado == null) {
            return new ArrayList<>();
        }
        return questionRepository.findByEstado(estado);
    }

    /**
     * Busca una pregunta por su identificador.
     *
     * @param id identificador de la pregunta
     * @return pregunta encontrada o null
     */
    public Question findQuestionById(String id) {

        if (id == null || id.isBlank()) {
            return null;
        }

        return questionRepository.findById(id);
    }

    /**
     * Obtiene los usuarios disponibles como revisores (RF04.2).
     * Retorna todos los usuarios con rol AUTOR (docentes).
     *
     * @return lista de posibles revisores
     */
    public List<Usuario> listarRevisoresDisponibles() {
        if (usuarioRepository == null) {
            return new ArrayList<>();
        }
        return usuarioRepository.findByRol(Rol.AUTOR);
    }

    // ----------------------------------------------------------------
    // Validación estructural (RF01.2)
    // ----------------------------------------------------------------

    /**
     * Valida la estructura de una pregunta según las reglas de RF01.2.
     *
     * Verifica:
     * - Campos obligatorios no vacíos.
     * - Longitud mínima/máxima de textos largos (contexto, justificación).
     * - Exactamente 4 distractores distintos entre sí y distintos de
     * la respuesta correcta.
     * - Nivel de dificultad dentro del catálogo válido.
     *
     * @param q pregunta a validar
     * @return lista de mensajes de error (vacía si es válida)
     */
    public List<String> validarEstructura(Question q) {

        List<String> errores = new ArrayList<>();

        if (q == null) {
            errores.add("La pregunta no puede ser nula.");
            return errores;
        }

        // Campo: enunciado (pregunta directa)
        if (esVacio(q.getEnunciado())) {
            errores.add("enunciado|El enunciado (pregunta directa) es obligatorio.");
        } else if (q.getEnunciado().trim().length() < MIN_ENUNCIADO) {
            errores.add("enunciado|El enunciado debe tener al menos "
                    + MIN_ENUNCIADO + " caracteres.");
        }

        // Campo: contexto
        if (esVacio(q.getContexto())) {
            errores.add("contexto|El contexto es obligatorio.");
        } else {
            int len = q.getContexto().trim().length();
            if (len < MIN_TEXTO_LARGO) {
                errores.add("contexto|El contexto debe tener al menos "
                        + MIN_TEXTO_LARGO + " caracteres.");
            } else if (len > MAX_TEXTO_LARGO) {
                errores.add("contexto|El contexto no puede superar "
                        + MAX_TEXTO_LARGO + " caracteres.");
            }
        }

        // Campo: justificación
        if (esVacio(q.getJustificacion())) {
            errores.add("justificacion|La justificación es obligatoria.");
        } else {
            int len = q.getJustificacion().trim().length();
            if (len < MIN_TEXTO_LARGO) {
                errores.add("justificacion|La justificación debe tener al menos "
                        + MIN_TEXTO_LARGO + " caracteres.");
            } else if (len > MAX_TEXTO_LARGO) {
                errores.add("justificacion|La justificación no puede superar "
                        + MAX_TEXTO_LARGO + " caracteres.");
            }
        }

        // Campos simples obligatorios
        if (esVacio(q.getBibliografia())) {
            errores.add("bibliografia|La bibliografía es obligatoria.");
        }
        if (esVacio(q.getCompetencia())) {
            errores.add("competencia|La competencia es obligatoria.");
        }
        if (esVacio(q.getTema())) {
            errores.add("tema|El tema es obligatorio.");
        }
        if (esVacio(q.getSubtema())) {
            errores.add("subtema|El subtema es obligatorio.");
        }
        if (q.getNivelDificultad() == null) {
            errores.add("nivelDificultad|El nivel de dificultad es obligatorio.");
        }
        if (esVacio(q.getRespuestaCorrecta())) {
            errores.add("respuestaCorrecta|La respuesta correcta es obligatoria.");
        }

        // Validar 4 distractores distintos entre sí y distintos de
        // la respuesta correcta (RF01.2)
        List<QuestionDistractors> opciones = q.getOpciones();

        if (opciones == null || opciones.size() != CANTIDAD_DISTRACTORES) {
            errores.add("opciones|Deben existir exactamente "
                    + CANTIDAD_DISTRACTORES + " distractores.");
        } else {
            // Verificar que los textos de los distractores no estén vacíos
            for (int i = 0; i < opciones.size(); i++) {
                String texto = opciones.get(i).getTexto();
                if (esVacio(texto)) {
                    errores.add("opcion_" + opciones.get(i).getId()
                            + "|El distractor "
                            + opciones.get(i).getId()
                            + " no puede estar vacío.");
                }
            }

            // Verificar unicidad entre distractores
            Set<String> textosDistinct = new HashSet<>();
            boolean hayDuplicados = false;
            for (QuestionDistractors op : opciones) {
                if (op.getTexto() != null) {
                    String key = op.getTexto().trim().toLowerCase();
                    if (!textosDistinct.add(key)) {
                        hayDuplicados = true;
                        break;
                    }
                }
            }
            if (hayDuplicados) {
                errores.add("opciones|Los distractores deben ser distintos entre sí.");
            }

            // Verificar que ningún distractor sea igual a la respuesta correcta
            if (!esVacio(q.getRespuestaCorrecta())) {
                String textoCorrecta = q.getTextoRespuestaCorrecta()
                        .trim().toLowerCase();
                for (QuestionDistractors op : opciones) {
                    if (op.getTexto() != null
                            && !op.getId().equalsIgnoreCase(q.getRespuestaCorrecta())
                            && op.getTexto().trim().toLowerCase()
                                    .equals(textoCorrecta)) {
                        errores.add("opciones|Un distractor no puede ser igual "
                                + "a la respuesta correcta.");
                        break;
                    }
                }
            }
        }

        return errores;
    }

    private boolean esVacio(String s) {
        return s == null || s.isBlank();
    }

    // ----------------------------------------------------------------
    // Operaciones de escritura
    // ----------------------------------------------------------------

    /**
     * Registra una nueva pregunta en el banco.
     * La pregunta queda en estado BORRADOR asociada al autor (RF01.4).
     *
     * @param question pregunta a registrar
     * @return true si fue guardada correctamente
     */
    public boolean saveQuestion(Question question) {

        if (question == null) {
            return false;
        }

        if (question.getId() == null || question.getId().isBlank()) {
            return false;
        }

        // Asignar estado BORRADOR (RF01.4)
        if (question.getEstado() == null) {
            question.setEstado(EstadoPregunta.BORRADOR);
        }

        boolean guardada = questionRepository.save(question);

        if (guardada) {
            notifyObservers();
        }

        return guardada;
    }

    /**
     * Actualiza los datos de una pregunta existente (edición).
     * Solo permitida si la pregunta está en estado BORRADOR.
     *
     * @param question pregunta con los datos actualizados
     * @return true si fue actualizada correctamente
     */
    public boolean updateQuestion(Question question) {

        if (question == null || esVacio(question.getId())) {
            return false;
        }

        Question existente = questionRepository.findById(question.getId());
        if (existente == null) {
            return false;
        }

        // Solo editable en BORRADOR (RF03.5)
        if (existente.getEstado() != EstadoPregunta.BORRADOR) {
            return false;
        }

        boolean actualizada = questionRepository.update(question);

        if (actualizada) {
            notifyObservers();
        }

        return actualizada;
    }

    /**
     * Cambia el estado de una pregunta de BORRADOR a
     * PENDIENTE_REVISION (RF02).
     *
     * Reglas:
     * - Solo el autor de la pregunta puede solicitarlo (RF02.1).
     * - La pregunta debe pasar la validación estructural (RF02.2).
     *
     * @param preguntaId identificador de la pregunta
     * @param autorId    identificador del autor que solicita el cambio
     * @return lista de errores (vacía si fue exitoso)
     */
    public List<String> enviarARevision(String preguntaId, String autorId) {

        List<String> errores = new ArrayList<>();

        Question pregunta = questionRepository.findById(preguntaId);

        if (pregunta == null) {
            errores.add("La pregunta no existe.");
            return errores;
        }

        // RF02.1 — solo el autor propio puede enviarla
        if (!autorId.equals(pregunta.getAutorId())) {
            errores.add("Solo el autor de la pregunta puede enviarla a revisión.");
            return errores;
        }

        // RF02.2 — validación estructural
        List<String> erroresEstructura = validarEstructura(pregunta);
        if (!erroresEstructura.isEmpty()) {
            errores.add("La pregunta no pasa la validación estructural. "
                    + "Completa todos los campos antes de enviarla.");
            return errores;
        }

        boolean actualizada = questionRepository.updateEstado(
                preguntaId, EstadoPregunta.PENDIENTE_REVISION);

        if (!actualizada) {
            errores.add("No fue posible actualizar el estado de la pregunta.");
        } else {
            notifyObservers();
        }

        return errores;
    }

    /**
     * Asigna revisores a una pregunta y la transiciona a EN_REVISION
     * (RF04.2, RF04.3).
     *
     * El envío de correo es simulado (RF04.4). Si falla, la asignación
     * no se revierte — el error queda registrado (RF04.5, falla no
     * bloqueante).
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorIds lista de ids de los revisores asignados
     * @return true si la transición fue exitosa
     */
    public boolean asignarRevisores(String preguntaId,
            List<String> revisorIds) {

        if (esVacio(preguntaId) || revisorIds == null
                || revisorIds.isEmpty()) {
            return false;
        }

        Question pregunta = questionRepository.findById(preguntaId);
        if (pregunta == null) {
            return false;
        }

        if (pregunta.getEstado() != EstadoPregunta.PENDIENTE_REVISION) {
            return false;
        }

        // Transición a EN_REVISION (RF04.3)
        boolean actualizada = questionRepository.updateEstado(
                preguntaId, EstadoPregunta.EN_REVISION);

        if (!actualizada) {
            return false;
        }

        // RF04.4 — envío simulado de correo
        for (String revisorId : revisorIds) {
            enviarCorreoSimulado(preguntaId, revisorId);
        }

        notifyObservers();
        return true;
    }

    /**
     * Simulación del envío de correo a un revisor (RF04.4, RF04.5).
     * En producción, aquí iría la llamada al servicio de correo real.
     * Si falla, se registra el error pero no se revierte la asignación.
     */
    private void enviarCorreoSimulado(String preguntaId, String revisorId) {
        try {
            // Simulamos que el 10% de los envíos falla (para probar RF04.5)
            if (Math.random() < 0.1) {
                throw new RuntimeException("Error de conexión SMTP simulado.");
            }
            System.out.println("[CORREO] Notificación enviada al revisor "
                    + revisorId + " para la pregunta " + preguntaId);
        } catch (Exception e) {
            // RF04.5 — falla no bloqueante: se registra el error
            System.err.println("[CORREO-ERROR] No se pudo notificar al revisor "
                    + revisorId + " (pregunta " + preguntaId + "): "
                    + e.getMessage());
        }
    }

    /**
     * Cambia el estado de una pregunta y notifica a las vistas
     * observadoras (GUIObserver1 y GUIObserver2) del cambio.
     *
     * @param id          identificador de la pregunta
     * @param nuevoEstado nuevo estado
     * @return true si la pregunta existía y fue actualizada
     */
    public boolean updateEstado(String id, EstadoPregunta nuevoEstado) {

        if (id == null || id.isBlank() || nuevoEstado == null) {
            return false;
        }

        boolean actualizada = questionRepository.updateEstado(id, nuevoEstado);

        if (actualizada) {
            notifyObservers();
        }

        return actualizada;
    }

    // ----------------------------------------------------------------
    // Estadísticas
    // ----------------------------------------------------------------

    /**
     * Cuenta cuántas preguntas hay por cada estado.
     * Utilizado por GUIObserver1 (vista de estadísticas).
     *
     * @return mapa estado -> cantidad de preguntas
     */
    public Map<EstadoPregunta, Long> contarPorEstado() {

        Map<EstadoPregunta, Long> conteo = new EnumMap<>(EstadoPregunta.class);

        for (EstadoPregunta estado : EstadoPregunta.values()) {
            conteo.put(estado, 0L);
        }

        for (Question pregunta : questionRepository.list()) {

            EstadoPregunta estado = pregunta.getEstado();

            if (estado != null) {
                conteo.merge(estado, 1L, Long::sum);
            }
        }

        return conteo;
    }

    /**
     * Calcula el porcentaje de preguntas en cada estado, respecto
     * del total de preguntas del banco.
     * Utilizado por GUIObserver2 (vista gráfica en pastel).
     *
     * @return mapa estado -> porcentaje (0 a 100)
     */
    public Map<EstadoPregunta, Double> porcentajePorEstado() {

        Map<EstadoPregunta, Long> conteo = contarPorEstado();

        long total = 0;
        for (long cantidad : conteo.values()) {
            total += cantidad;
        }

        Map<EstadoPregunta, Double> porcentajes = new EnumMap<>(EstadoPregunta.class);

        for (Map.Entry<EstadoPregunta, Long> entrada : conteo.entrySet()) {

            double porcentaje = total == 0
                    ? 0.0
                    : (entrada.getValue() * 100.0) / total;

            porcentajes.put(entrada.getKey(), porcentaje);
        }

        return porcentajes;
    }
}

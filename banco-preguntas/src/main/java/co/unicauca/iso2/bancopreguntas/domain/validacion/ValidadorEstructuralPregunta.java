/**
 * @file ValidadorEstructuralPregunta.java
 * @brief Reglas de validación estructural de una pregunta (HU03).
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain.validacion;

import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionDistractors;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @brief Implementación por defecto de ReglaValidacion.
 *
 * Verifica que:
 *  - exista contexto y una única pregunta directa,
 *  - haya exactamente cuatro opciones y una sola respuesta correcta,
 *  - las opciones no estén vacías, repetidas ni usen expresiones como
 *    "Todas las anteriores" o "Ninguna de las anteriores",
 *  - las opciones tengan longitudes parecidas, para que la clave no se
 *    delate por su extensión,
 *  - los metadatos obligatorios estén diligenciados.
 */
public class ValidadorEstructuralPregunta implements ReglaValidacion {

    private static final int MIN_TEXTO_LARGO = 20;
    private static final int MAX_TEXTO_LARGO = 2000;
    private static final int MIN_ENUNCIADO = 10;
    private static final int CANTIDAD_OPCIONES = 4;
    private static final int MAX_OPCION = 300;
    /** Veces que la opción más larga puede superar a la más corta. */
    private static final int PROPORCION_MAX_LONGITUD = 4;

    private static final List<String> EXPRESIONES_PROHIBIDAS = List.of(
            "todas las anteriores",
            "ninguna de las anteriores",
            "todas las opciones anteriores",
            "ninguna de las opciones anteriores");

    @Override
    public List<String> validar(Question q) {

        List<String> errores = new ArrayList<>();

        if (q == null) {
            errores.add("La pregunta no puede ser nula.");
            return errores;
        }

        validarEnunciado(q, errores);
        validarTextoLargo(q.getContexto(), "contexto", "El contexto", errores);
        validarTextoLargo(q.getJustificacion(), "justificacion",
                "La justificación", errores);

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

        validarOpciones(q, errores);

        return errores;
    }

    private void validarEnunciado(Question q, List<String> errores) {

        String enunciado = q.getEnunciado();

        if (esVacio(enunciado)) {
            errores.add("enunciado|El enunciado (pregunta directa) es obligatorio.");
            return;
        }
        if (enunciado.trim().length() < MIN_ENUNCIADO) {
            errores.add("enunciado|El enunciado debe tener al menos "
                    + MIN_ENUNCIADO + " caracteres.");
        }
        if (enunciado.chars().filter(c -> c == '?').count() > 1) {
            errores.add("enunciado|Debe haber una única pregunta directa.");
        }
    }

    private void validarTextoLargo(String texto, String campo, String nombre,
                                   List<String> errores) {
        if (esVacio(texto)) {
            errores.add(campo + "|" + nombre + " es un campo obligatorio.");
            return;
        }
        int len = texto.trim().length();
        if (len < MIN_TEXTO_LARGO) {
            errores.add(campo + "|" + nombre + " debe tener al menos "
                    + MIN_TEXTO_LARGO + " caracteres.");
        } else if (len > MAX_TEXTO_LARGO) {
            errores.add(campo + "|" + nombre + " no puede superar "
                    + MAX_TEXTO_LARGO + " caracteres.");
        }
    }

    private void validarOpciones(Question q, List<String> errores) {

        List<QuestionDistractors> opciones = q.getOpciones();

        if (opciones == null || opciones.size() != CANTIDAD_OPCIONES) {
            errores.add("opciones|Deben existir exactamente "
                    + CANTIDAD_OPCIONES + " opciones.");
            if (esVacio(q.getRespuestaCorrecta())) {
                errores.add("respuestaCorrecta|La respuesta correcta es obligatoria.");
            }
            return;
        }

        validarRespuestaCorrecta(q, opciones, errores);

        boolean todasConTexto = true;
        for (QuestionDistractors op : opciones) {
            String texto = op.getTexto();
            String campo = "opcion_" + op.getId();

            if (esVacio(texto)) {
                errores.add(campo + "|La opción " + op.getId()
                        + " no puede estar vacía.");
                todasConTexto = false;
                continue;
            }
            if (texto.trim().length() > MAX_OPCION) {
                errores.add(campo + "|La opción " + op.getId()
                        + " no puede superar " + MAX_OPCION + " caracteres.");
            }
            if (usaExpresionProhibida(texto)) {
                errores.add(campo + "|No se permiten expresiones como "
                        + "\"Todas las anteriores\" o \"Ninguna de las anteriores\".");
            }
        }

        if (hayDuplicadas(opciones)) {
            errores.add("opciones|Las opciones deben ser distintas entre sí.");
        }

        if (todasConTexto && !longitudesHomogeneas(opciones)) {
            errores.add("opciones|Las opciones deben tener una longitud similar "
                    + "(la más larga no puede superar " + PROPORCION_MAX_LONGITUD
                    + " veces a la más corta).");
        }
    }

    private void validarRespuestaCorrecta(Question q,
                                          List<QuestionDistractors> opciones,
                                          List<String> errores) {

        if (esVacio(q.getRespuestaCorrecta())) {
            errores.add("respuestaCorrecta|La respuesta correcta es obligatoria.");
            return;
        }

        long coincidencias = opciones.stream()
                .filter(op -> q.getRespuestaCorrecta().equalsIgnoreCase(op.getId()))
                .count();

        if (coincidencias != 1) {
            errores.add("respuestaCorrecta|Debe existir una única respuesta "
                    + "correcta entre las opciones.");
        }
    }

    private boolean hayDuplicadas(List<QuestionDistractors> opciones) {
        Set<String> textos = new HashSet<>();
        for (QuestionDistractors op : opciones) {
            if (op.getTexto() != null
                    && !textos.add(normalizar(op.getTexto()))) {
                return true;
            }
        }
        return false;
    }

    private boolean longitudesHomogeneas(List<QuestionDistractors> opciones) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (QuestionDistractors op : opciones) {
            int len = op.getTexto().trim().length();
            min = Math.min(min, len);
            max = Math.max(max, len);
        }
        return max <= min * PROPORCION_MAX_LONGITUD;
    }

    private boolean usaExpresionProhibida(String texto) {
        String normalizado = normalizar(texto);
        return EXPRESIONES_PROHIBIDAS.stream().anyMatch(normalizado::contains);
    }

    /** Pasa a minúsculas y quita tildes y espacios repetidos. */
    private String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}

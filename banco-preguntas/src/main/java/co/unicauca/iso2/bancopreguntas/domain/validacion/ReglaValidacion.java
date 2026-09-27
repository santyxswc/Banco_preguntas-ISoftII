/**
 * @file ReglaValidacion.java
 * @brief Estrategia de validación de preguntas (patrón Strategy).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain.validacion;

import co.unicauca.iso2.bancopreguntas.domain.Question;

import java.util.List;

/**
 * @brief Regla intercambiable para validar la estructura de una pregunta.
 *
 * QuestionService depende de esta interfaz, así que las reglas se
 * pueden cambiar sin modificar el servicio.
 */
public interface ReglaValidacion {

    /**
     * @brief Valida la estructura de una pregunta.
     * @param pregunta pregunta a validar
     * @return errores en formato "campo|mensaje" (vacía si es válida)
     */
    List<String> validar(Question pregunta);
}

package co.unicauca.iso2.taller09.validationservice.domain;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;

import java.util.List;

/**
 * Patrón GoF <b>Strategy</b> (comportamiento).
 * <p>
 * Cada regla estructural de una pregunta Saber Pro es una estrategia intercambiable.
 * {@link QuestionValidator} (el contexto) no conoce las reglas concretas: Spring le inyecta
 * todas las implementaciones y él las ejecuta en orden. Agregar una regla nueva es crear una
 * clase, sin tocar el validador (principio abierto/cerrado).
 */
public interface ValidationRule {

    /** Nombre corto de la regla, útil para trazas y para la documentación. */
    String name();

    /** Devuelve los errores encontrados; lista vacía si la pregunta cumple la regla. */
    List<String> check(CreateQuestionRequest question);

    static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

package co.unicauca.iso2.taller09.lifecycleservice.repository;

import co.unicauca.iso2.taller09.lifecycleservice.domain.OperationResult;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;

import java.util.Optional;

/**
 * Puerto de persistencia del contexto de Ciclo de vida: estado actual de cada pregunta y
 * resultado de las operaciones asíncronas. Lo implementan {@code JpaLifecycleRepository}
 * (base de datos lifecycle_db) e {@link InMemoryLifecycleRepository} (pruebas unitarias).
 */
public interface LifecycleRepository {

    /** Estado de la pregunta; si aún no tiene historial se registra en DRAFT. */
    QuestionState getOrCreateState(Long questionId);

    Optional<QuestionState> findStateByQuestionId(Long questionId);

    QuestionState saveState(QuestionState state);

    OperationResult saveOperation(OperationResult operation);

    Optional<OperationResult> findOperationById(String operationId);
}

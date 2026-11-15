package co.unicauca.iso2.taller09.lifecycleservice.repository;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.lifecycleservice.domain.OperationResult;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación en memoria de {@link LifecycleRepository}. Era el almacenamiento del servicio
 * hasta el corte 2; ahora se conserva como doble para las pruebas unitarias, que así no
 * necesitan levantar Spring ni una base de datos.
 */
public class InMemoryLifecycleRepository implements LifecycleRepository {
    private final Map<Long, QuestionState> statesByQuestionId = new ConcurrentHashMap<>();
    private final Map<String, OperationResult> operationsById = new ConcurrentHashMap<>();
    private final AtomicLong stateIdGen = new AtomicLong(1);

    @Override
    public QuestionState getOrCreateState(Long questionId) {
        return statesByQuestionId.computeIfAbsent(questionId, qId ->
                new QuestionState(stateIdGen.incrementAndGet(), qId, QuestionStatus.DRAFT, "SYSTEM", "Estado inicial DRAFT"));
    }

    @Override
    public Optional<QuestionState> findStateByQuestionId(Long questionId) {
        return Optional.ofNullable(statesByQuestionId.get(questionId));
    }

    @Override
    public QuestionState saveState(QuestionState state) {
        if (state.getId() == null) {
            state.setId(stateIdGen.incrementAndGet());
        }
        statesByQuestionId.put(state.getQuestionId(), state);
        return state;
    }

    @Override
    public OperationResult saveOperation(OperationResult operation) {
        operationsById.put(operation.getId(), operation);
        return operation;
    }

    @Override
    public Optional<OperationResult> findOperationById(String operationId) {
        return Optional.ofNullable(operationsById.get(operationId));
    }
}

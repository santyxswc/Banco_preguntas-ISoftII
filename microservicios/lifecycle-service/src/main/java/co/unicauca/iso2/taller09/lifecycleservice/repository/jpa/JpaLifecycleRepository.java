package co.unicauca.iso2.taller09.lifecycleservice.repository.jpa;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.lifecycleservice.domain.OperationResult;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;
import co.unicauca.iso2.taller09.lifecycleservice.repository.LifecycleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Patrón GoF <b>Adapter</b>: implementa el puerto {@link LifecycleRepository} con Spring Data
 * JPA sobre la base de datos propia del servicio (lifecycle_db). Traduce entre los objetos
 * del dominio ({@link QuestionState}, {@link OperationResult}) y las entidades JPA, de modo que
 * el dominio no tiene anotaciones de persistencia.
 * <p>
 * Con esta base de datos el estado de las preguntas y el resultado de los envíos a revisión
 * sobreviven al reinicio del contenedor (RNF #3: volúmenes para las bases de datos).
 */
@Repository
public class JpaLifecycleRepository implements LifecycleRepository {

    private final QuestionStateJpaRepository states;
    private final OperationResultJpaRepository operations;

    public JpaLifecycleRepository(QuestionStateJpaRepository states, OperationResultJpaRepository operations) {
        this.states = states;
        this.operations = operations;
    }

    @Override
    public QuestionState getOrCreateState(Long questionId) {
        Optional<QuestionState> existing = findStateByQuestionId(questionId);
        if (existing.isPresent()) {
            return existing.get();
        }
        try {
            return saveState(new QuestionState(null, questionId, QuestionStatus.DRAFT, "SYSTEM", "Estado inicial DRAFT"));
        } catch (DataIntegrityViolationException e) {
            // Otra petición registró el estado inicial al mismo tiempo: se usa el que ya quedó guardado
            return findStateByQuestionId(questionId).orElseThrow(() -> e);
        }
    }

    @Override
    public Optional<QuestionState> findStateByQuestionId(Long questionId) {
        return states.findByQuestionId(questionId).map(JpaLifecycleRepository::toDomain);
    }

    @Override
    public QuestionState saveState(QuestionState state) {
        QuestionStateEntity entity = states.findByQuestionId(state.getQuestionId())
                .orElseGet(() -> new QuestionStateEntity(state.getQuestionId()));
        entity.setState(state.getState());
        entity.setChangedAt(state.getChangedAt() == null ? LocalDateTime.now() : state.getChangedAt());
        entity.setChangedBy(state.getChangedBy());
        entity.setMessage(state.getMessage());
        state.setId(states.saveAndFlush(entity).getId());
        return state;
    }

    @Override
    public OperationResult saveOperation(OperationResult operation) {
        OperationResultEntity entity = operations.findById(operation.getId())
                .orElseGet(() -> new OperationResultEntity(operation.getId()));
        entity.setQuestionId(operation.getQuestionId());
        entity.setType(operation.getType());
        entity.setStatus(operation.getStatus());
        entity.setMessage(operation.getMessage());
        entity.setCreatedAt(operation.getCreatedAt() == null ? LocalDateTime.now() : operation.getCreatedAt());
        entity.setCompletedAt(operation.getCompletedAt());
        operations.saveAndFlush(entity);
        return operation;
    }

    @Override
    public Optional<OperationResult> findOperationById(String operationId) {
        return operations.findById(operationId).map(JpaLifecycleRepository::toDomain);
    }

    private static QuestionState toDomain(QuestionStateEntity entity) {
        QuestionState state = new QuestionState(entity.getId(), entity.getQuestionId(), entity.getState(),
                entity.getChangedBy(), entity.getMessage());
        state.setChangedAt(entity.getChangedAt());
        return state;
    }

    private static OperationResult toDomain(OperationResultEntity entity) {
        OperationResult operation = new OperationResult(entity.getId(), entity.getQuestionId(), entity.getType(),
                entity.getStatus(), entity.getMessage());
        operation.setCreatedAt(entity.getCreatedAt());
        operation.setCompletedAt(entity.getCompletedAt());
        return operation;
    }
}

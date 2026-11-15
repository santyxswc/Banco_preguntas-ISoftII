package co.unicauca.iso2.taller09.lifecycleservice.repository;

import co.unicauca.iso2.taller09.common.enums.OperationStatus;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.lifecycleservice.domain.OperationResult;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;
import co.unicauca.iso2.taller09.lifecycleservice.repository.jpa.JpaLifecycleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas del adaptador JPA del ciclo de vida sobre H2 en memoria con las migraciones de Flyway. */
@DataJpaTest
@Import(JpaLifecycleRepository.class)
class JpaLifecycleRepositoryTest {

    @Autowired
    private JpaLifecycleRepository repository;

    @Test
    @DisplayName("Una pregunta sin historial queda registrada en DRAFT y no se duplica")
    void estadoInicial() {
        QuestionState creado = repository.getOrCreateState(10L);
        QuestionState leido = repository.getOrCreateState(10L);

        assertEquals(QuestionStatus.DRAFT, creado.getState());
        assertNotNull(creado.getId());
        assertEquals(creado.getId(), leido.getId());
        assertTrue(repository.findStateByQuestionId(11L).isEmpty());
    }

    @Test
    @DisplayName("Guardar una transición actualiza la misma fila de la pregunta")
    void guardaLaTransicion() {
        QuestionState estado = repository.getOrCreateState(20L);
        estado.setState(QuestionStatus.PENDING_REVIEW);
        estado.setChangedBy("Autor 2");
        estado.setMessage("Enviada a revisión");
        repository.saveState(estado);

        QuestionState leido = repository.findStateByQuestionId(20L).orElseThrow();
        assertEquals(QuestionStatus.PENDING_REVIEW, leido.getState());
        assertEquals("Autor 2", leido.getChangedBy());
        assertEquals("Enviada a revisión", leido.getMessage());
        assertEquals(estado.getId(), leido.getId());
        assertNotNull(leido.getChangedAt());
        assertTrue(leido.canTransitionTo(QuestionStatus.IN_REVIEW));
    }

    @Test
    @DisplayName("El resultado de una operación asíncrona se guarda y se actualiza por su operationId")
    void guardaLaOperacion() {
        OperationResult operacion = new OperationResult("op-1", 20L, "SUBMIT_FOR_REVIEW", OperationStatus.PENDING, "Procesando");
        repository.saveOperation(operacion);
        assertNull(repository.findOperationById("op-1").orElseThrow().getCompletedAt());

        operacion.complete("Transición completada");
        repository.saveOperation(operacion);

        OperationResult leida = repository.findOperationById("op-1").orElseThrow();
        assertEquals(OperationStatus.COMPLETED, leida.getStatus());
        assertEquals("Transición completada", leida.getMessage());
        assertEquals(20L, leida.getQuestionId());
        assertNotNull(leida.getCompletedAt());
        assertTrue(repository.findOperationById("no-existe").isEmpty());
    }
}

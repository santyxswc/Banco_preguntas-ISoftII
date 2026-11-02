package co.unicauca.iso2.taller09.reviewservice.support;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.messages.ReviewCompleted;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.QuestionSummary;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewerProfile;
import co.unicauca.iso2.taller09.reviewservice.domain.port.QuestionCatalog;
import co.unicauca.iso2.taller09.reviewservice.domain.port.QuestionLifecycle;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewEventPublisher;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewerDirectory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FakeCollaborators {

    private FakeCollaborators() {
    }

    public static class Questions implements QuestionCatalog {
        private final Map<Long, QuestionSummary> data = new HashMap<>();
        private boolean down;

        public Questions add(Long id, Long authorId) {
            data.put(id, new QuestionSummary(id, "Pregunta " + id, authorId));
            return this;
        }

        public void setDown(boolean down) {
            this.down = down;
        }

        @Override
        public Optional<QuestionSummary> findById(Long questionId) {
            if (down) {
                throw ReviewException.unavailable("El servicio de preguntas no está disponible.");
            }
            return Optional.ofNullable(data.get(questionId));
        }
    }

    public static class Users implements ReviewerDirectory {
        private final Map<Long, ReviewerProfile> data = new HashMap<>();

        public Users add(Long id, String... roles) {
            data.put(id, new ReviewerProfile(id, "Usuario " + id, "u" + id + "@unicauca.edu.co", List.of(roles)));
            return this;
        }

        @Override
        public Optional<ReviewerProfile> findById(Long userId) {
            return Optional.ofNullable(data.get(userId));
        }
    }

    public static class Lifecycle implements QuestionLifecycle {
        private final Map<Long, QuestionStatus> states = new HashMap<>();
        public final List<String> transitions = new ArrayList<>();
        private boolean rejectTransitions;

        public Lifecycle set(Long questionId, QuestionStatus state) {
            states.put(questionId, state);
            return this;
        }

        public QuestionStatus stateOf(Long questionId) {
            return states.get(questionId);
        }

        public void setRejectTransitions(boolean rejectTransitions) {
            this.rejectTransitions = rejectTransitions;
        }

        @Override
        public Optional<QuestionStatus> currentState(Long questionId) {
            return Optional.ofNullable(states.get(questionId));
        }

        @Override
        public void transition(Long questionId, QuestionStatus target, String changedBy, String message) {
            if (rejectTransitions) {
                throw ReviewException.conflict("El servicio de ciclo de vida rechazó la transición a " + target + ".");
            }
            transitions.add(questionId + "->" + target);
            states.put(questionId, target);
        }
    }

    public static class Events implements ReviewEventPublisher {
        public final List<ReviewerAssigned> assigned = new ArrayList<>();
        public final List<ReviewCompleted> completed = new ArrayList<>();

        @Override
        public void reviewerAssigned(ReviewerAssigned event) {
            assigned.add(event);
        }

        @Override
        public void reviewCompleted(ReviewCompleted event) {
            completed.add(event);
        }
    }
}

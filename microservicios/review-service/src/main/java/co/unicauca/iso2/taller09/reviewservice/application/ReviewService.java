package co.unicauca.iso2.taller09.reviewservice.application;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.common.messages.ReviewCompleted;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.PageResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.QuestionSummary;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewerProfile;
import co.unicauca.iso2.taller09.reviewservice.domain.policy.VerdictPolicy;
import co.unicauca.iso2.taller09.reviewservice.domain.port.QuestionCatalog;
import co.unicauca.iso2.taller09.reviewservice.domain.port.QuestionLifecycle;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewEventPublisher;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewRepository;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewerDirectory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ReviewService {

    public static final int MIN_REVISORES = 1;
    public static final int MAX_REVISORES = 3;
    public static final int MAX_TAMANO_PAGINA = 50;
    public static final String MENSAJE_NO_ASIGNADA = "No tiene asignada esta pregunta";

    private final ReviewRepository repository;
    private final QuestionCatalog questions;
    private final ReviewerDirectory reviewers;
    private final QuestionLifecycle lifecycle;
    private final ReviewEventPublisher events;
    private final VerdictPolicy verdictPolicy;

    public ReviewService(ReviewRepository repository, QuestionCatalog questions, ReviewerDirectory reviewers,
                         QuestionLifecycle lifecycle, ReviewEventPublisher events, VerdictPolicy verdictPolicy) {
        this.repository = Objects.requireNonNull(repository);
        this.questions = Objects.requireNonNull(questions);
        this.reviewers = Objects.requireNonNull(reviewers);
        this.lifecycle = Objects.requireNonNull(lifecycle);
        this.events = Objects.requireNonNull(events);
        this.verdictPolicy = Objects.requireNonNull(verdictPolicy);
    }

    public List<ReviewAssignment> assignReviewers(Long questionId, List<Long> reviewerIds, Long assignedBy) {
        requireId(questionId, "questionId");
        requireId(assignedBy, "assignedBy");
        List<Long> requested = reviewerIds == null ? List.of() : reviewerIds.stream().filter(Objects::nonNull).toList();
        List<Long> distinct = requested.stream().distinct().toList();
        if (distinct.size() < MIN_REVISORES || distinct.size() > MAX_REVISORES) {
            throw ReviewException.validation("Debe asignar entre " + MIN_REVISORES + " y " + MAX_REVISORES + " revisores.");
        }
        if (distinct.size() != requested.size()) {
            throw ReviewException.validation("No se puede asignar el mismo revisor más de una vez.");
        }

        QuestionSummary question = findQuestion(questionId);
        QuestionStatus state = lifecycle.currentState(questionId)
                .orElseThrow(() -> ReviewException.notFound("No se encontró el estado de la pregunta " + questionId + "."));
        if (state != QuestionStatus.PENDING_REVIEW) {
            throw ReviewException.conflict("La pregunta está en estado " + state
                    + "; solo se asignan revisores a preguntas en PENDING_REVIEW.");
        }
        List<ReviewAssignment> previous = repository.findAssignmentsByQuestion(questionId);
        if (previous.stream().anyMatch(ReviewAssignment::isPending)) {
            throw ReviewException.conflict("La pregunta ya tiene revisores asignados pendientes de evaluar.");
        }
        for (Long reviewerId : distinct) {
            validateReviewer(reviewerId, question);
        }

        int round = previous.stream().mapToInt(ReviewAssignment::getRound).max().orElse(0) + 1;
        lifecycle.transition(questionId, QuestionStatus.IN_REVIEW, "Administrador " + assignedBy,
                "Asignación de revisores " + distinct + " (ronda " + round + ")");

        List<ReviewAssignment> saved = new ArrayList<>();
        for (Long reviewerId : distinct) {
            ReviewAssignment assignment = repository.saveAssignment(
                    ReviewAssignment.create(questionId, reviewerId, assignedBy, round));
            saved.add(assignment);
            events.reviewerAssigned(new ReviewerAssigned(UUID.randomUUID().toString(), questionId,
                    reviewerId, assignedBy, LocalDateTime.now()));
        }
        return saved;
    }

    public PageResult<AssignedQuestionView> listAssigned(Long reviewerId, int page, int size) {
        requireId(reviewerId, "reviewerId");
        if (page < 0) {
            throw ReviewException.validation("La página debe ser mayor o igual a 0.");
        }
        if (size < 1 || size > MAX_TAMANO_PAGINA) {
            throw ReviewException.validation("El tamaño de página debe estar entre 1 y " + MAX_TAMANO_PAGINA + ".");
        }
        return repository.findPendingByReviewer(reviewerId, page, size).map(this::toView);
    }

    public EvaluationOutcome evaluate(Long questionId, Long reviewerId, ReviewResult result, String comments) {
        requireId(questionId, "questionId");
        requireId(reviewerId, "reviewerId");

        ReviewAssignment assignment = repository.findLatestAssignment(questionId, reviewerId)
                .orElseThrow(() -> ReviewException.forbidden(MENSAJE_NO_ASIGNADA));
        if (!assignment.isPending()) {
            throw ReviewException.conflict("Ya registró su evaluación para esta pregunta.");
        }

        Review review = Review.register(questionId, reviewerId, assignment.getRound(), result, comments);
        QuestionSummary question = findQuestion(questionId);

        boolean lastPending = repository.findAssignmentsByQuestion(questionId).stream()
                .filter(other -> other.getRound() == assignment.getRound())
                .filter(other -> !Objects.equals(other.getId(), assignment.getId()))
                .noneMatch(ReviewAssignment::isPending);

        QuestionStatus finalState = null;
        if (lastPending) {
            List<Review> decisions = new ArrayList<>(repository.findReviewsByQuestion(questionId).stream()
                    .filter(previousReview -> previousReview.getRound() == assignment.getRound())
                    .toList());
            decisions.add(review);
            finalState = verdictPolicy.decide(decisions);
            lifecycle.transition(questionId, finalState, "Revisor " + reviewerId,
                    "Resultado de la revisión por pares: " + finalState);
        }

        assignment.complete();
        repository.saveAssignment(assignment);
        Review saved = repository.saveReview(review);

        events.reviewCompleted(new ReviewCompleted(UUID.randomUUID().toString(), questionId, reviewerId,
                question.authorId(), saved.getResult(), saved.getComments(), finalState, LocalDateTime.now()));

        return new EvaluationOutcome(saved, finalState);
    }

    public List<Review> observations(Long questionId) {
        requireId(questionId, "questionId");
        return repository.findReviewsByQuestion(questionId);
    }

    public List<ReviewAssignment> assignmentsOf(Long questionId) {
        requireId(questionId, "questionId");
        return repository.findAssignmentsByQuestion(questionId);
    }

    public List<ReviewAssignment> allAssignments() {
        return repository.findAllAssignments();
    }

    private void validateReviewer(Long reviewerId, QuestionSummary question) {
        ReviewerProfile profile = reviewers.findById(reviewerId)
                .orElseThrow(() -> ReviewException.notFound("No existe el usuario " + reviewerId + "."));
        if (!profile.canReview()) {
            throw ReviewException.validation("El usuario " + reviewerId + " no tiene rol de revisor.");
        }
        if (Objects.equals(reviewerId, question.authorId())) {
            throw ReviewException.validation("El autor de la pregunta no puede ser su revisor.");
        }
    }

    private QuestionSummary findQuestion(Long questionId) {
        return questions.findById(questionId)
                .orElseThrow(() -> ReviewException.notFound("No existe la pregunta " + questionId + "."));
    }

    private AssignedQuestionView toView(ReviewAssignment assignment) {
        Optional<QuestionSummary> question;
        try {
            question = questions.findById(assignment.getQuestionId());
        } catch (ReviewException e) {
            question = Optional.empty();
        }
        return new AssignedQuestionView(assignment.getId(), assignment.getQuestionId(),
                question.map(QuestionSummary::text).orElse(null),
                question.map(QuestionSummary::authorId).orElse(null),
                assignment.getRound(), assignment.getAssignedAt(), assignment.getStatus());
    }

    private static void requireId(Long id, String field) {
        if (id == null) {
            throw ReviewException.validation("El campo " + field + " es obligatorio.");
        }
    }
}

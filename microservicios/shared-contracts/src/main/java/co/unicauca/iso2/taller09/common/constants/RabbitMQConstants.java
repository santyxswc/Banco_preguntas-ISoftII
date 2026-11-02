package co.unicauca.iso2.taller09.common.constants;

public final class RabbitMQConstants {
    private RabbitMQConstants() {}

    public static final String EXCHANGE_NAME = "banco.exchange";

    // Comando SubmitQuestionForReview (Question Service -> Lifecycle Service)
    public static final String QUEUE_QUESTION_SUBMIT = "question.submit.queue";
    public static final String ROUTING_KEY_QUESTION_SUBMIT = "question.submit";

    // Evento ReviewerAssigned (Review Service -> Notification Service)
    public static final String QUEUE_REVIEWER_ASSIGNED = "reviewer.assigned.queue";
    public static final String ROUTING_KEY_REVIEWER_ASSIGNED = "reviewer.assigned";

    // Evento QuestionSubmittedForReview (Opcional / Notificación de estado)
    public static final String QUEUE_QUESTION_SUBMITTED_EVENT = "question.submitted.event.queue";
    public static final String ROUTING_KEY_QUESTION_SUBMITTED_EVENT = "question.submitted.event";

    public static final String QUEUE_REVIEW_COMPLETED = "review.completed.queue";
    public static final String ROUTING_KEY_REVIEW_COMPLETED = "review.completed";

    public static final String DEAD_LETTER_EXCHANGE = "banco.dlx";
    public static final String DEAD_LETTER_SUFFIX = ".dlq";
}

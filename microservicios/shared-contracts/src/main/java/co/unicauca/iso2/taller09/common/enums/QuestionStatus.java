package co.unicauca.iso2.taller09.common.enums;

public enum QuestionStatus {
    DRAFT,
    PENDING_REVIEW,
    IN_REVIEW,
    APPROVED,
    REJECTED,
    PUBLISHED;

    public boolean canTransitionTo(QuestionStatus nextState) {
        if (nextState == null) return false;
        return switch (this) {
            case DRAFT -> nextState == PENDING_REVIEW;
            case PENDING_REVIEW -> nextState == IN_REVIEW;
            case IN_REVIEW -> nextState == APPROVED || nextState == REJECTED;
            case REJECTED -> nextState == PENDING_REVIEW || nextState == DRAFT;
            case APPROVED -> nextState == PUBLISHED;
            case PUBLISHED -> false;
        };
    }
}

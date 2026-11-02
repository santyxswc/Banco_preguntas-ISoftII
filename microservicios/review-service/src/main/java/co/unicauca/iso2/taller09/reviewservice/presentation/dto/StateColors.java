package co.unicauca.iso2.taller09.reviewservice.presentation.dto;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;

import java.util.EnumMap;
import java.util.Map;

public final class StateColors {

    private static final Map<QuestionStatus, String> COLORES = new EnumMap<>(QuestionStatus.class);

    static {
        COLORES.put(QuestionStatus.DRAFT, "#969696");
        COLORES.put(QuestionStatus.PENDING_REVIEW, "#E6A014");
        COLORES.put(QuestionStatus.IN_REVIEW, "#1E78D2");
        COLORES.put(QuestionStatus.APPROVED, "#28A745");
        COLORES.put(QuestionStatus.REJECTED, "#DC3545");
        COLORES.put(QuestionStatus.PUBLISHED, "#6F42C1");
    }

    private StateColors() {
    }

    public static String of(QuestionStatus state) {
        return state == null ? null : COLORES.get(state);
    }
}

package co.unicauca.iso2.taller09.reviewservice.domain.exception;

public class ReviewException extends RuntimeException {

    public enum Type {
        VALIDATION,
        NOT_FOUND,
        FORBIDDEN,
        CONFLICT,
        UNAVAILABLE
    }

    private final Type type;

    public ReviewException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public static ReviewException validation(String message) {
        return new ReviewException(Type.VALIDATION, message);
    }

    public static ReviewException notFound(String message) {
        return new ReviewException(Type.NOT_FOUND, message);
    }

    public static ReviewException forbidden(String message) {
        return new ReviewException(Type.FORBIDDEN, message);
    }

    public static ReviewException conflict(String message) {
        return new ReviewException(Type.CONFLICT, message);
    }

    public static ReviewException unavailable(String message) {
        return new ReviewException(Type.UNAVAILABLE, message);
    }
}

package co.unicauca.iso2.taller09.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationResult implements Serializable {
    private boolean valid;
    private List<String> errors;

    public ValidationResult() {
        this.errors = new ArrayList<>();
    }

    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, new ArrayList<>());
    }

    public static ValidationResult fail(List<String> errors) {
        return new ValidationResult(false, errors);
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}

package co.unicauca.iso2.taller09.questionservice.repository.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OptionValue {

    @Column(name = "option_id", nullable = false, length = 5)
    private String optionId;

    @Column(name = "option_text", nullable = false, length = 1000)
    private String text;

    protected OptionValue() {
    }

    public OptionValue(String optionId, String text) {
        this.optionId = optionId;
        this.text = text;
    }

    public String getOptionId() { return optionId; }
    public String getText() { return text; }
}

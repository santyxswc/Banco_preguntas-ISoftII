package co.unicauca.iso2.taller09.common.dto;

import java.io.Serializable;

public class OptionDTO implements Serializable {
    private String id;   // e.g. "A", "B", "C", "D"
    private String text;

    public OptionDTO() {}

    public OptionDTO(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}

package co.unicauca.iso2.taller09.common.dto;

import java.io.Serializable;
import java.util.List;

public class CreateQuestionRequest implements Serializable {
    private String text;
    private List<OptionDTO> options;
    private String correctOptionId;
    private Long authorId;

    public CreateQuestionRequest() {}

    public CreateQuestionRequest(String text, List<OptionDTO> options, String correctOptionId, Long authorId) {
        this.text = text;
        this.options = options;
        this.correctOptionId = correctOptionId;
        this.authorId = authorId;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<OptionDTO> getOptions() { return options; }
    public void setOptions(List<OptionDTO> options) { this.options = options; }

    public String getCorrectOptionId() { return correctOptionId; }
    public void setCorrectOptionId(String correctOptionId) { this.correctOptionId = correctOptionId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}

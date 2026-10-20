package co.unicauca.iso2.taller09.common.dto;

import java.io.Serializable;

public class CreateQuestionResponse implements Serializable {
    private Long id;
    private String status;
    private String message;

    public CreateQuestionResponse() {}

    public CreateQuestionResponse(Long id, String status, String message) {
        this.id = id;
        this.status = status;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

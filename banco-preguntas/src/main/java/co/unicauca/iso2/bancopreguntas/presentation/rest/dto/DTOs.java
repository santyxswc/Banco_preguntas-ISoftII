/**
 * @file DTOs.java
 * @brief Objetos DTO para peticiones y respuestas de la API REST.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation.rest.dto;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.Revision;

import java.time.LocalDateTime;
import java.util.List;

public class DTOs {

    public static class CambioEstadoRequest {
        private EstadoPregunta nuevoEstado;
        public EstadoPregunta getNuevoEstado() { return nuevoEstado; }
        public void setNuevoEstado(EstadoPregunta nuevoEstado) { this.nuevoEstado = nuevoEstado; }
    }

    public static class AsignacionRequest {
        private String preguntaId;
        private List<String> revisorIds;

        public String getPreguntaId() { return preguntaId; }
        public void setPreguntaId(String preguntaId) { this.preguntaId = preguntaId; }
        public List<String> getRevisorIds() { return revisorIds; }
        public void setRevisorIds(List<String> revisorIds) { this.revisorIds = revisorIds; }
    }

    public static class EvaluacionRequest {
        private String preguntaId;
        private String revisorId;
        private EstadoPregunta veredicto;
        private String observaciones;

        public String getPreguntaId() { return preguntaId; }
        public void setPreguntaId(String preguntaId) { this.preguntaId = preguntaId; }
        public String getRevisorId() { return revisorId; }
        public void setRevisorId(String revisorId) { this.revisorId = revisorId; }
        public EstadoPregunta getVeredicto() { return veredicto; }
        public void setVeredicto(EstadoPregunta veredicto) { this.veredicto = veredicto; }
        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }

    public static class RevisionResponse {
        private String id;
        private String preguntaId;
        private String revisorId;
        private EstadoPregunta veredicto;
        private String observaciones;
        private LocalDateTime fecha;

        public static RevisionResponse fromEntity(Revision r) {
            if (r == null) return null;
            RevisionResponse dto = new RevisionResponse();
            dto.id = r.getId();
            dto.preguntaId = r.getPreguntaId();
            dto.revisorId = r.getRevisorId();
            dto.veredicto = r.getVeredicto();
            dto.observaciones = r.getObservaciones();
            dto.fecha = r.getFecha();
            return dto;
        }

        public String getId() { return id; }
        public String getPreguntaId() { return preguntaId; }
        public String getRevisorId() { return revisorId; }
        public EstadoPregunta getVeredicto() { return veredicto; }
        public String getObservaciones() { return observaciones; }
        public LocalDateTime getFecha() { return fecha; }
    }

    public static class ApiResponse<T> {
        private boolean exito;
        private String mensaje;
        private T datos;
        private List<String> errores;

        public ApiResponse() {}

        public static <T> ApiResponse<T> ok(T datos, String mensaje) {
            ApiResponse<T> resp = new ApiResponse<>();
            resp.exito = true;
            resp.mensaje = mensaje;
            resp.datos = datos;
            return resp;
        }

        public static <T> ApiResponse<T> error(List<String> errores, String mensaje) {
            ApiResponse<T> resp = new ApiResponse<>();
            resp.exito = false;
            resp.mensaje = mensaje;
            resp.errores = errores;
            return resp;
        }

        public boolean isExito() { return exito; }
        public String getMensaje() { return mensaje; }
        public T getDatos() { return datos; }
        public List<String> getErrores() { return errores; }
    }
}

/**
 * @file QuestionFilter.java
 * @brief Criterios de búsqueda, orden y paginación de preguntas.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

/**
 * @brief Filtros combinables para listar preguntas.
 *
 * Ejemplo:
 * @code
 *   new QuestionFilter()
 *       .conEstado(EstadoPregunta.BORRADOR)
 *       .conTextoLibre("lectura")
 *       .conPagina(0)
 *       .conTamPagina(10);
 * @endcode
 */
public class QuestionFilter {

    /** @brief Orden de los resultados. */
    public enum Orden {
        FECHA_CREACION_DESC,
        FECHA_CREACION_ASC,
        FECHA_MODIFICACION_DESC,
        FECHA_MODIFICACION_ASC
    }

    private String autorId;
    private EstadoPregunta estado;
    private String competencia;
    private String tema;
    private String subtema;
    private NivelDificultad nivelDificultad;
    private String textoLibre;
    private Orden orden = Orden.FECHA_CREACION_DESC;
    private int pagina = 0;
    private int tamPagina = 10;

    public String getAutorId() { return autorId; }
    public QuestionFilter conAutorId(String autorId) {
        this.autorId = autorId; return this;
    }

    public EstadoPregunta getEstado() { return estado; }
    public QuestionFilter conEstado(EstadoPregunta estado) {
        this.estado = estado; return this;
    }

    public String getCompetencia() { return competencia; }
    public QuestionFilter conCompetencia(String competencia) {
        this.competencia = competencia; return this;
    }

    public String getTema() { return tema; }
    public QuestionFilter conTema(String tema) {
        this.tema = tema; return this;
    }

    public String getSubtema() { return subtema; }
    public QuestionFilter conSubtema(String subtema) {
        this.subtema = subtema; return this;
    }

    public NivelDificultad getNivelDificultad() { return nivelDificultad; }
    public QuestionFilter conNivelDificultad(NivelDificultad nivel) {
        this.nivelDificultad = nivel; return this;
    }

    public String getTextoLibre() { return textoLibre; }
    public QuestionFilter conTextoLibre(String textoLibre) {
        this.textoLibre = textoLibre; return this;
    }

    public Orden getOrden() { return orden; }
    public QuestionFilter conOrden(Orden orden) {
        this.orden = orden != null ? orden : Orden.FECHA_CREACION_DESC;
        return this;
    }

    public int getPagina() { return pagina; }
    public QuestionFilter conPagina(int pagina) {
        this.pagina = Math.max(0, pagina); return this;
    }

    public int getTamPagina() { return tamPagina; }
    public QuestionFilter conTamPagina(int tamPagina) {
        this.tamPagina = tamPagina > 0 ? tamPagina : 10; return this;
    }
}

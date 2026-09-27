/**
 * @file PaginaResultado.java
 * @brief Resultado paginado de una consulta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.Collections;
import java.util.List;

/**
 * @brief Elementos de la página actual junto con el total de resultados.
 * @tparam T tipo de elemento
 */
public class PaginaResultado<T> {

    private final List<T> elementos;
    private final long totalElementos;
    private final int pagina;
    private final int tamPagina;

    /**
     * @param elementos      elementos de la página
     * @param totalElementos total de elementos que cumplen el filtro
     * @param pagina         índice de página (desde 0)
     * @param tamPagina      tamaño de página
     */
    public PaginaResultado(List<T> elementos, long totalElementos,
                            int pagina, int tamPagina) {
        this.elementos = elementos != null
                ? elementos : Collections.emptyList();
        this.totalElementos = totalElementos;
        this.pagina = Math.max(0, pagina);
        this.tamPagina = Math.max(1, tamPagina);
    }

    public List<T> getElementos() {
        return elementos;
    }

    public long getTotalElementos() {
        return totalElementos;
    }

    public int getPagina() {
        return pagina;
    }

    public int getTamPagina() {
        return tamPagina;
    }

    /** @return número de páginas (mínimo 1) */
    public int getTotalPaginas() {
        return (int) Math.max(1,
                Math.ceil((double) totalElementos / tamPagina));
    }
}

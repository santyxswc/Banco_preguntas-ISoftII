/**
 * @file PreguntaTableModel.java
 * @brief Modelo de tabla para el listado de preguntas.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.Question;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Traduce una lista de Question a las columnas de la tabla.
 *
 * La columna Acciones devuelve la pregunta completa para que los
 * botones sepan sobre cuál actuar.
 */
public class PreguntaTableModel extends AbstractTableModel {

    public static final int COL_ID          = 0;
    public static final int COL_ENUNCIADO   = 1;
    public static final int COL_COMPETENCIA = 2;
    public static final int COL_NIVEL       = 3;
    public static final int COL_ESTADO      = 4;
    public static final int COL_FECHA       = 5;
    public static final int COL_ACCIONES    = 6;

    private static final String[] COLUMNAS = {
            "ID", "Enunciado", "Competencia", "Nivel",
            "Estado", "F.Creación", "Acciones"
    };

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private List<Question> preguntas = new ArrayList<>();

    /** @param preguntas preguntas de la página actual */
    public void setPreguntas(List<Question> preguntas) {
        this.preguntas = preguntas != null ? preguntas : new ArrayList<>();
        fireTableDataChanged();
    }

    /**
     * @param fila índice de fila
     * @return la pregunta de esa fila o null
     */
    public Question preguntaEnFila(int fila) {
        return (fila >= 0 && fila < preguntas.size()) ? preguntas.get(fila) : null;
    }

    @Override
    public int getRowCount() {
        return preguntas.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int col) {
        return COLUMNAS[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == COL_ACCIONES;
    }

    @Override
    public Object getValueAt(int row, int col) {

        Question q = preguntas.get(row);

        return switch (col) {
            case COL_ID -> q.getId();
            case COL_ENUNCIADO -> truncar(q.getEnunciado(), 60);
            case COL_COMPETENCIA -> q.getCompetencia();
            case COL_NIVEL -> q.getNivelDificultad() != null
                    ? q.getNivelDificultad().getEtiqueta() : "-";
            case COL_ESTADO -> q.getEstado();
            case COL_FECHA -> q.getFechaCreacion() != null
                    ? q.getFechaCreacion().format(FMT) : "-";
            case COL_ACCIONES -> q;
            default -> null;
        };
    }

    private static String truncar(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}

/**
 * @file QuestionJdbcRepository.java
 * @brief Repositorio de preguntas sobre base de datos (JDBC).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.NivelDificultad;
import co.unicauca.iso2.bancopreguntas.domain.PaginaResultado;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionDistractors;
import co.unicauca.iso2.bancopreguntas.domain.QuestionFilter;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Implementación de QuestionRepository con JDBC.
 *
 * Solo depende de javax.sql.DataSource, así funciona igual con
 * PostgreSQL o con H2 en las pruebas. El esquema lo crea la migración
 * Flyway V1__init_schema.sql.
 */
public class QuestionJdbcRepository implements QuestionRepository {

    private final DataSource dataSource;

    public QuestionJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Question> list() {
        String sql = "SELECT * FROM preguntas ORDER BY fecha_creacion DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return mapearTodas(con, rs);

        } catch (SQLException e) {
            throw new RuntimeException("Error listando preguntas", e);
        }
    }

    @Override
    public Question findById(String id) {
        if (id == null) {
            return null;
        }
        String sql = "SELECT * FROM preguntas WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Question q = mapearFila(rs);
                q.setOpciones(cargarOpciones(con, id));
                return q;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando pregunta " + id, e);
        }
    }

    @Override
    public List<Question> findByAutorId(String autorId) {
        String sql = "SELECT * FROM preguntas WHERE autor_id = ? "
                + "ORDER BY fecha_creacion DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, autorId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapearTodas(con, rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error buscando preguntas del autor " + autorId, e);
        }
    }

    @Override
    public List<Question> findByEstado(EstadoPregunta estado) {
        String sql = "SELECT * FROM preguntas WHERE estado = ? "
                + "ORDER BY fecha_creacion DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado.name());
            try (ResultSet rs = ps.executeQuery()) {
                return mapearTodas(con, rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error buscando preguntas en estado " + estado, e);
        }
    }

    @Override
    public boolean save(Question question) {

        if (question == null || question.getId() == null
                || question.getId().isBlank()) {
            return false;
        }

        String sql = "INSERT INTO preguntas (id, nombre, contexto, "
                + "enunciado, respuesta_correcta, justificacion, "
                + "bibliografia, competencia, tema, subtema, "
                + "nivel_dificultad, estado, autor_id, fecha_creacion, "
                + "fecha_modificacion) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = dataSource.getConnection()) {

            if (existe(con, question.getId())) {
                return false;
            }

            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                bindCamposPregunta(ps, question);
                ps.executeUpdate();
                guardarOpciones(con, question);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Error guardando pregunta "
                    + question.getId(), e);
        }
    }

    @Override
    public boolean update(Question question) {

        if (question == null || question.getId() == null) {
            return false;
        }

        String sql = "UPDATE preguntas SET nombre=?, contexto=?, "
                + "enunciado=?, respuesta_correcta=?, justificacion=?, "
                + "bibliografia=?, competencia=?, tema=?, subtema=?, "
                + "nivel_dificultad=?, estado=?, autor_id=?, "
                + "fecha_modificacion=? WHERE id=?";

        try (Connection con = dataSource.getConnection()) {

            if (!existe(con, question.getId())) {
                return false;
            }

            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, question.getNombre());
                ps.setString(2, question.getContexto());
                ps.setString(3, question.getEnunciado());
                ps.setString(4, question.getRespuestaCorrecta());
                ps.setString(5, question.getJustificacion());
                ps.setString(6, question.getBibliografia());
                ps.setString(7, question.getCompetencia());
                ps.setString(8, question.getTema());
                ps.setString(9, question.getSubtema());
                ps.setString(10, question.getNivelDificultad() != null
                        ? question.getNivelDificultad().name() : null);
                ps.setString(11, question.getEstado() != null
                        ? question.getEstado().name() : null);
                ps.setString(12, question.getAutorId());
                ps.setTimestamp(13, Timestamp.valueOf(
                        question.getFechaModificacion() != null
                                ? question.getFechaModificacion()
                                : java.time.LocalDateTime.now()));
                ps.setString(14, question.getId());
                ps.executeUpdate();

                borrarOpciones(con, question.getId());
                guardarOpciones(con, question);

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error actualizando pregunta " + question.getId(), e);
        }
    }

    @Override
    public boolean updateEstado(String id, EstadoPregunta nuevoEstado) {

        if (id == null || nuevoEstado == null) {
            return false;
        }

        String sql = "UPDATE preguntas SET estado=?, fecha_modificacion=? "
                + "WHERE id=?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado.name());
            ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(3, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error actualizando estado de " + id, e);
        }
    }

    /**
     * @brief Búsqueda paginada resuelta en SQL con WHERE, ORDER BY y
     *        LIMIT/OFFSET.
     * @param filtro criterios de búsqueda
     * @return página de resultados
     */
    @Override
    public PaginaResultado<Question> buscar(QuestionFilter filtro) {

        QuestionFilter f = filtro != null ? filtro : new QuestionFilter();

        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (f.getAutorId() != null && !f.getAutorId().isBlank()) {
            where.append(" AND autor_id = ? ");
            params.add(f.getAutorId());
        }
        if (f.getEstado() != null) {
            where.append(" AND estado = ? ");
            params.add(f.getEstado().name());
        }
        if (f.getCompetencia() != null && !f.getCompetencia().isBlank()) {
            where.append(" AND LOWER(competencia) LIKE ? ");
            params.add("%" + f.getCompetencia().toLowerCase() + "%");
        }
        if (f.getTema() != null && !f.getTema().isBlank()) {
            where.append(" AND LOWER(tema) LIKE ? ");
            params.add("%" + f.getTema().toLowerCase() + "%");
        }
        if (f.getSubtema() != null && !f.getSubtema().isBlank()) {
            where.append(" AND LOWER(subtema) LIKE ? ");
            params.add("%" + f.getSubtema().toLowerCase() + "%");
        }
        if (f.getNivelDificultad() != null) {
            where.append(" AND nivel_dificultad = ? ");
            params.add(f.getNivelDificultad().name());
        }
        if (f.getTextoLibre() != null && !f.getTextoLibre().isBlank()) {
            where.append(" AND (LOWER(enunciado) LIKE ? OR LOWER(contexto) LIKE ?) ");
            String comodin = "%" + f.getTextoLibre().toLowerCase() + "%";
            params.add(comodin);
            params.add(comodin);
        }

        String orderBy = switch (f.getOrden()) {
            case FECHA_CREACION_ASC -> " ORDER BY fecha_creacion ASC ";
            case FECHA_CREACION_DESC -> " ORDER BY fecha_creacion DESC ";
            case FECHA_MODIFICACION_ASC -> " ORDER BY fecha_modificacion ASC ";
            case FECHA_MODIFICACION_DESC -> " ORDER BY fecha_modificacion DESC ";
        };

        int tam = Math.max(1, f.getTamPagina());
        int desde = Math.max(0, f.getPagina()) * tam;

        try (Connection con = dataSource.getConnection()) {

            long total = contar(con, where.toString(), params);

            String sql = "SELECT * FROM preguntas" + where + orderBy
                    + " LIMIT ? OFFSET ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int idx = 1;
                for (Object p : params) {
                    ps.setObject(idx++, p);
                }
                ps.setInt(idx++, tam);
                ps.setInt(idx, desde);

                try (ResultSet rs = ps.executeQuery()) {
                    List<Question> pagina = mapearTodas(con, rs);
                    return new PaginaResultado<>(pagina, total, f.getPagina(), tam);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando preguntas paginadas", e);
        }
    }

    private long contar(Connection con, String where, List<Object> params)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM preguntas" + where;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            for (Object p : params) {
                ps.setObject(idx++, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    private boolean existe(Connection con, String id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT 1 FROM preguntas WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void bindCamposPregunta(PreparedStatement ps, Question q)
            throws SQLException {
        ps.setString(1, q.getId());
        ps.setString(2, q.getNombre());
        ps.setString(3, q.getContexto());
        ps.setString(4, q.getEnunciado());
        ps.setString(5, q.getRespuestaCorrecta());
        ps.setString(6, q.getJustificacion());
        ps.setString(7, q.getBibliografia());
        ps.setString(8, q.getCompetencia());
        ps.setString(9, q.getTema());
        ps.setString(10, q.getSubtema());
        ps.setString(11, q.getNivelDificultad() != null
                ? q.getNivelDificultad().name() : null);
        ps.setString(12, q.getEstado() != null
                ? q.getEstado().name() : EstadoPregunta.BORRADOR.name());
        ps.setString(13, q.getAutorId());
        ps.setTimestamp(14, Timestamp.valueOf(q.getFechaCreacion() != null
                ? q.getFechaCreacion() : java.time.LocalDateTime.now()));
        ps.setTimestamp(15, Timestamp.valueOf(q.getFechaModificacion() != null
                ? q.getFechaModificacion() : java.time.LocalDateTime.now()));
    }

    private void guardarOpciones(Connection con, Question q) throws SQLException {
        if (q.getOpciones() == null) {
            return;
        }
        String sql = "INSERT INTO pregunta_opciones (pregunta_id, opcion_id, texto) "
                + "VALUES (?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (QuestionDistractors opcion : q.getOpciones()) {
                ps.setString(1, q.getId());
                ps.setString(2, opcion.getId());
                ps.setString(3, opcion.getTexto());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void borrarOpciones(Connection con, String preguntaId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM pregunta_opciones WHERE pregunta_id = ?")) {
            ps.setString(1, preguntaId);
            ps.executeUpdate();
        }
    }

    private List<QuestionDistractors> cargarOpciones(Connection con, String preguntaId)
            throws SQLException {
        List<QuestionDistractors> opciones = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT opcion_id, texto FROM pregunta_opciones "
                        + "WHERE pregunta_id = ? ORDER BY opcion_id")) {
            ps.setString(1, preguntaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    opciones.add(new QuestionDistractors(
                            rs.getString("opcion_id"), rs.getString("texto")));
                }
            }
        }
        return opciones;
    }

    private List<Question> mapearTodas(Connection con, ResultSet rs) throws SQLException {
        List<Question> resultado = new ArrayList<>();
        while (rs.next()) {
            Question q = mapearFila(rs);
            q.setOpciones(cargarOpciones(con, q.getId()));
            resultado.add(q);
        }
        return resultado;
    }

    private Question mapearFila(ResultSet rs) throws SQLException {

        Question q = new Question();
        q.setId(rs.getString("id"));
        q.setNombre(rs.getString("nombre"));
        q.setContexto(rs.getString("contexto"));
        q.setEnunciado(rs.getString("enunciado"));
        q.setRespuestaCorrecta(rs.getString("respuesta_correcta"));
        q.setJustificacion(rs.getString("justificacion"));
        q.setBibliografia(rs.getString("bibliografia"));
        q.setCompetencia(rs.getString("competencia"));
        q.setTema(rs.getString("tema"));
        q.setSubtema(rs.getString("subtema"));

        String nivel = rs.getString("nivel_dificultad");
        q.setNivelDificultad(nivel != null ? NivelDificultad.valueOf(nivel) : null);

        String estado = rs.getString("estado");
        q.setEstado(estado != null ? EstadoPregunta.valueOf(estado) : null);

        q.setAutorId(rs.getString("autor_id"));

        Timestamp creacion = rs.getTimestamp("fecha_creacion");
        q.setFechaCreacion(creacion != null ? creacion.toLocalDateTime() : null);

        Timestamp modificacion = rs.getTimestamp("fecha_modificacion");
        q.setFechaModificacion(modificacion != null ? modificacion.toLocalDateTime() : null);

        return q;
    }
}

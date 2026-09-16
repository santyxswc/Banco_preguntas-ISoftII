package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.*;
import co.unicauca.iso2.bancopreguntas.infra.Observer;
import co.unicauca.iso2.bancopreguntas.infra.Subject;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista de listado y filtrado de preguntas (RF03) con soporte para
 * cambio de estado BORRADOR → PENDIENTE_REVISION (RF02).
 *
 * RF03.1 — Muestra solo las preguntas del autor autenticado
 *          (si autorId no es null) o todas (admin).
 * RF03.2 — Paginación configurable 10/25/50.
 * RF03.3 — Filtros combinables: estado, competencia, texto libre.
 * RF03.4 — Ordenable por fecha creación o modificación.
 * RF03.5 — Edición habilitada solo en BORRADOR.
 * RF02.3 — Cada estado muestra su color distintivo en la celda.
 *
 * Implementa {@link Observer} para actualizarse automáticamente
 * cuando QuestionService notifica cambios de estado.
 */
public class GUIListarPreguntas extends JFrame implements Observer {

    // Paleta
    private static final Color BG_DARK      = new Color(18, 22, 36);
    private static final Color BG_PANEL     = new Color(28, 33, 52);
    private static final Color ACCENT       = new Color(82, 130, 255);
    private static final Color ACCENT_AMBER = new Color(230, 160, 20);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 255);
    private static final Color TEXT_MUTED   = new Color(130, 140, 175);
    private static final Color BORDER_COLOR = new Color(50, 58, 85);
    private static final Color TABLE_BG     = new Color(22, 28, 44);
    private static final Color TABLE_SEL    = new Color(40, 55, 95);

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final QuestionService questionService;
    /** null si es el admin (ve todas); no-null si es el autor. */
    private final String autorIdFiltro;

    // Datos
    private List<Question> todasLasPreguntas = new ArrayList<>();
    private List<Question> preguntasFiltradas  = new ArrayList<>();
    private int paginaActual = 0;
    private int tamPagina    = 10;

    // Filtros
    private JComboBox<String>        comboEstado;
    private JTextField               txtBusqueda;
    private JTextField               txtCompetencia;
    private JComboBox<String>        comboOrden;
    private JComboBox<Integer>       comboTamPagina;

    // Tabla
    private JTable            tabla;
    private DefaultTableModel modeloTabla;

    // Paginación
    private JLabel  lblInfoPagina;
    private JButton btnAnterior;
    private JButton btnSiguiente;

    public GUIListarPreguntas(QuestionService questionService,
                              String autorIdFiltro) {

        this.questionService = questionService;
        this.autorIdFiltro   = autorIdFiltro;

        questionService.attach(this); // observer (RF)

        inicializarVentana();
        construirUI();
        cargarPreguntas();
    }

    // ----------------------------------------------------------------
    // Observer
    // ----------------------------------------------------------------

    @Override
    public void actualizar(Subject sujeto) {
        SwingUtilities.invokeLater(this::cargarPreguntas);
    }

    // ----------------------------------------------------------------
    // Construcción de la interfaz
    // ----------------------------------------------------------------

    private void inicializarVentana() {
        String titulo = (autorIdFiltro != null)
                ? "Mis preguntas — Banco de Preguntas"
                : "Todas las preguntas — Banco de Preguntas (Admin)";
        setTitle(titulo);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                questionService.detach(GUIListarPreguntas.this);
            }
        });
        getContentPane().setBackground(BG_DARK);
    }

    private void construirUI() {

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        root.add(crearPanelFiltros(), BorderLayout.NORTH);
        root.add(crearPanelTabla(),   BorderLayout.CENTER);
        root.add(crearPanelPaginacion(), BorderLayout.SOUTH);

        setContentPane(root);
        setPreferredSize(new Dimension(900, 600));
        pack();
        setLocationRelativeTo(null);
    }

    // ---- Panel de filtros ------------------------------------------

    private JPanel crearPanelFiltros() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Fila 0 — estado, competencia, búsqueda libre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(etiqueta("Estado:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.3;
        String[] estados = {"(Todos)",
                EstadoPregunta.BORRADOR.getEtiqueta(),
                EstadoPregunta.PENDIENTE_REVISION.getEtiqueta(),
                EstadoPregunta.EN_REVISION.getEtiqueta(),
                EstadoPregunta.ELIMINADA.getEtiqueta()};
        comboEstado = new JComboBox<>(estados);
        estilizarCombo(comboEstado);
        panel.add(comboEstado, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(etiqueta("Competencia:"), gbc);

        gbc.gridx = 3; gbc.weightx = 0.4;
        txtCompetencia = campoCampo();
        panel.add(txtCompetencia, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        panel.add(etiqueta("Texto libre:"), gbc);

        gbc.gridx = 5; gbc.weightx = 0.4;
        txtBusqueda = campoCampo();
        panel.add(txtBusqueda, gbc);

        // Fila 1 — orden, tam. página, botón aplicar
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(etiqueta("Ordenar por:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.3;
        comboOrden = new JComboBox<>(new String[]{
                "Fecha creación (desc)", "Fecha creación (asc)",
                "Fecha modificación (desc)", "Fecha modificación (asc)"});
        estilizarCombo(comboOrden);
        panel.add(comboOrden, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(etiqueta("Por página:"), gbc);

        gbc.gridx = 3; gbc.weightx = 0.2;
        comboTamPagina = new JComboBox<>(new Integer[]{10, 25, 50});
        estilizarCombo(comboTamPagina);
        comboTamPagina.addActionListener(e -> {
            tamPagina = (Integer) comboTamPagina.getSelectedItem();
            paginaActual = 0;
            refrescarTabla();
        });
        panel.add(comboTamPagina, gbc);

        gbc.gridx = 5; gbc.weightx = 0;
        JButton btnFiltrar = new JButton("🔍  Filtrar");
        estilizarBoton(btnFiltrar, ACCENT, ACCENT.brighter());
        btnFiltrar.addActionListener(e -> {
            paginaActual = 0;
            aplicarFiltros();
        });
        panel.add(btnFiltrar, gbc);

        return panel;
    }

    // ---- Tabla -----------------------------------------------------

    private JPanel crearPanelTabla() {

        String[] columnas = {"ID", "Enunciado", "Competencia",
                "Nivel", "Estado", "F.Creación", "Acciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 6; // solo columna Acciones
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setBackground(TABLE_BG);
        tabla.setForeground(TEXT_PRIMARY);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabla.setRowHeight(32);
        tabla.setSelectionBackground(TABLE_SEL);
        tabla.setGridColor(BORDER_COLOR);
        tabla.setShowVerticalLines(false);
        tabla.getTableHeader().setBackground(BG_PANEL);
        tabla.getTableHeader().setForeground(TEXT_MUTED);
        tabla.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        // Renderer de colores para columna Estado
        tabla.getColumnModel().getColumn(4).setCellRenderer(
                new EstadoCellRenderer());

        // Renderer/editor de botones para columna Acciones
        tabla.getColumnModel().getColumn(6).setCellRenderer(
                new AccionesCellRenderer());
        tabla.getColumnModel().getColumn(6).setCellEditor(
                new AccionesCellEditor());

        // Anchos aproximados
        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(260);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(160);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(TABLE_BG);
        scroll.getViewport().setBackground(TABLE_BG);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ---- Paginación ------------------------------------------------

    private JPanel crearPanelPaginacion() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        panel.setBackground(BG_DARK);

        btnAnterior = new JButton("← Anterior");
        estilizarBoton(btnAnterior, new Color(45, 55, 85),
                new Color(60, 72, 108));
        btnAnterior.addActionListener(e -> {
            if (paginaActual > 0) {
                paginaActual--;
                refrescarTabla();
            }
        });

        lblInfoPagina = new JLabel("Página 1 de 1");
        lblInfoPagina.setForeground(TEXT_MUTED);
        lblInfoPagina.setFont(new Font("SansSerif", Font.PLAIN, 12));

        btnSiguiente = new JButton("Siguiente →");
        estilizarBoton(btnSiguiente, new Color(45, 55, 85),
                new Color(60, 72, 108));
        btnSiguiente.addActionListener(e -> {
            int totalPaginas = calcularTotalPaginas();
            if (paginaActual < totalPaginas - 1) {
                paginaActual++;
                refrescarTabla();
            }
        });

        panel.add(btnAnterior);
        panel.add(lblInfoPagina);
        panel.add(btnSiguiente);
        return panel;
    }

    // ----------------------------------------------------------------
    // Lógica de datos
    // ----------------------------------------------------------------

    private void cargarPreguntas() {
        if (autorIdFiltro != null) {
            todasLasPreguntas = questionService.listByAutor(autorIdFiltro);
        } else {
            todasLasPreguntas = questionService.listQuestions();
        }
        paginaActual = 0;
        aplicarFiltros();
    }

    private void aplicarFiltros() {

        String estadoSel = (String) comboEstado.getSelectedItem();
        String busqueda  = txtBusqueda.getText().trim().toLowerCase();
        String compFiltro = txtCompetencia.getText().trim().toLowerCase();
        String ordenSel  = (String) comboOrden.getSelectedItem();

        // Filtrar
        preguntasFiltradas = new ArrayList<>();
        for (Question q : todasLasPreguntas) {

            // Filtro estado — null-safe: si la pregunta no tiene estado se omite al filtrar
            if (!"(Todos)".equals(estadoSel)) {
                if (q.getEstado() == null
                        || !q.getEstado().getEtiqueta().equals(estadoSel)) {
                    continue;
                }
            }
            // Filtro competencia
            if (!compFiltro.isEmpty()
                    && (q.getCompetencia() == null
                        || !q.getCompetencia().toLowerCase()
                             .contains(compFiltro))) {
                continue;
            }
            // Filtro texto libre en enunciado
            if (!busqueda.isEmpty()
                    && (q.getEnunciado() == null
                        || !q.getEnunciado().toLowerCase()
                             .contains(busqueda))) {
                continue;
            }

            preguntasFiltradas.add(q);
        }

        // Ordenar (RF03.4) — null-safe para fechas
        preguntasFiltradas.sort((a, b) -> {
            switch (ordenSel) {
                case "Fecha creación (asc)":
                    if (a.getFechaCreacion() == null) return 1;
                    if (b.getFechaCreacion() == null) return -1;
                    return a.getFechaCreacion().compareTo(b.getFechaCreacion());
                case "Fecha modificación (desc)":
                    if (b.getFechaModificacion() == null) return 1;
                    if (a.getFechaModificacion() == null) return -1;
                    return b.getFechaModificacion().compareTo(a.getFechaModificacion());
                case "Fecha modificación (asc)":
                    if (a.getFechaModificacion() == null) return 1;
                    if (b.getFechaModificacion() == null) return -1;
                    return a.getFechaModificacion().compareTo(b.getFechaModificacion());
                default: // "Fecha creación (desc)"
                    if (b.getFechaCreacion() == null) return 1;
                    if (a.getFechaCreacion() == null) return -1;
                    return b.getFechaCreacion().compareTo(a.getFechaCreacion());
            }
        });

        refrescarTabla();
    }

    private void refrescarTabla() {

        modeloTabla.setRowCount(0);

        int totalPaginas = calcularTotalPaginas();
        int desde = paginaActual * tamPagina;
        int hasta = Math.min(desde + tamPagina, preguntasFiltradas.size());

        for (int i = desde; i < hasta; i++) {
            Question q = preguntasFiltradas.get(i);
            modeloTabla.addRow(new Object[]{
                    q.getId(),
                    truncar(q.getEnunciado(), 60),
                    q.getCompetencia(),
                    q.getNivelDificultad() != null
                            ? q.getNivelDificultad().getEtiqueta() : "-",
                    q.getEstado(),
                    q.getFechaCreacion() != null
                            ? q.getFechaCreacion().format(FMT) : "-",
                    q  // objeto completo para los botones
            });
        }

        // Actualizar info de paginación
        lblInfoPagina.setText("Página " + (paginaActual + 1)
                + " de " + Math.max(1, totalPaginas)
                + "  (" + preguntasFiltradas.size() + " resultados)");
        btnAnterior.setEnabled(paginaActual > 0);
        btnSiguiente.setEnabled(paginaActual < totalPaginas - 1);
    }

    private int calcularTotalPaginas() {
        return (int) Math.ceil(
                (double) preguntasFiltradas.size() / tamPagina);
    }

    // ----------------------------------------------------------------
    // Acciones sobre preguntas
    // ----------------------------------------------------------------

    /** Abre el formulario de edición (solo BORRADOR — RF03.5). */
    void editarPregunta(Question q) {
        if (q.getEstado() != EstadoPregunta.BORRADOR) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden editar preguntas en estado BORRADOR.",
                    "No editable",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new GUIEditarPregunta(questionService, q).setVisible(true);
    }

    /** Envía la pregunta a revisión (RF02). */
    void enviarARevision(Question q) {
        String autorId = (SessionContext.estaAutenticado())
                ? SessionContext.getUsuarioActual().getId() : "";

        List<String> errores = questionService.enviarARevision(
                q.getId(), autorId);

        if (!errores.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    String.join("\n", errores),
                    "No se pudo enviar a revisión",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "✅ Pregunta enviada a revisión.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ----------------------------------------------------------------
    // Renderers y editores de tabla
    // ----------------------------------------------------------------

    /** Colorea la celda de Estado según RF02.3. */
    private static class EstadoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int col) {

            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);

            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);

            if (value instanceof EstadoPregunta estado) {
                Color color = estado.getColor();
                lbl.setBackground(isSelected
                        ? color.darker().darker()
                        : color.darker().darker().darker());
                lbl.setForeground(color.brighter());
                lbl.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            } else {
                lbl.setBackground(new Color(22, 28, 44));
                lbl.setForeground(new Color(130, 140, 175));
            }

            return lbl;
        }
    }

    /** Botones Editar / Enviar en columna Acciones. */
    private class AccionesCellRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 4, 2));
        private final JButton btnEditar  = new JButton("✏ Editar");
        private final JButton btnEnviar  = new JButton("📤 Enviar");

        AccionesCellRenderer() {
            panel.setOpaque(true);
            estilizarBotonTabla(btnEditar, ACCENT, ACCENT.darker());
            estilizarBotonTabla(btnEnviar, ACCENT_AMBER, ACCENT_AMBER.darker());
            panel.add(btnEditar);
            panel.add(btnEnviar);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int col) {

            panel.setBackground(isSelected
                    ? new Color(40, 55, 95) : new Color(22, 28, 44));

            if (value instanceof Question q) {
                btnEditar.setEnabled(q.getEstado() == EstadoPregunta.BORRADOR);
                btnEnviar.setEnabled(q.getEstado() == EstadoPregunta.BORRADOR);
            }
            return panel;
        }
    }

    /** Editor que delega los clics en los botones a la lógica de negocio. */
    private class AccionesCellEditor extends AbstractCellEditor
            implements TableCellEditor {

        private final JPanel  panel    = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 4, 2));
        private final JButton btnEditar = new JButton("✏ Editar");
        private final JButton btnEnviar = new JButton("📤 Enviar");
        private Question pregunta;

        AccionesCellEditor() {
            panel.setOpaque(true);
            panel.setBackground(new Color(40, 55, 95));

            estilizarBotonTabla(btnEditar, ACCENT, ACCENT.darker());
            estilizarBotonTabla(btnEnviar, ACCENT_AMBER, ACCENT_AMBER.darker());

            btnEditar.addActionListener(e -> {
                fireEditingStopped();
                if (pregunta != null) editarPregunta(pregunta);
            });
            btnEnviar.addActionListener(e -> {
                fireEditingStopped();
                if (pregunta != null) enviarARevision(pregunta);
            });

            panel.add(btnEditar);
            panel.add(btnEnviar);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int col) {
            panel.setBackground(new Color(40, 55, 95));
            if (value instanceof Question q) {
                this.pregunta = q;
                btnEditar.setEnabled(q.getEstado() == EstadoPregunta.BORRADOR);
                btnEnviar.setEnabled(q.getEstado() == EstadoPregunta.BORRADOR);
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return pregunta;
        }
    }

    // ----------------------------------------------------------------
    // Utilidades
    // ----------------------------------------------------------------

    private static String truncar(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return lbl;
    }

    private JTextField campoCampo() {
        JTextField f = new JTextField(10);
        f.setBackground(new Color(38, 45, 68));
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return f;
    }

    private <T> void estilizarCombo(JComboBox<T> combo) {
        combo.setBackground(new Color(38, 45, 68));
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    private void estilizarBoton(JButton btn, Color base, Color hover) {
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hover);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(base);
            }
        });
    }

    private void estilizarBotonTabla(JButton btn, Color base, Color hover) {
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 10));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(2, 6, 2, 6));
    }
}

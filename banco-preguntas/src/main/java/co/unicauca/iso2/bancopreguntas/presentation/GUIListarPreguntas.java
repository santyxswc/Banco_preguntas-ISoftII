/**
 * @file GUIListarPreguntas.java
 * @brief Listado de preguntas con filtros y paginación.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.*;
import co.unicauca.iso2.bancopreguntas.infra.Observer;
import co.unicauca.iso2.bancopreguntas.infra.Subject;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * @brief Tabla de preguntas con filtros, orden, paginación y acciones.
 *
 * El autor ve solo sus preguntas; el administrador ve todas. El estado
 * se pinta con su color y los botones Editar/Enviar solo se habilitan
 * en BORRADOR. Se refresca sola al ser Observer de QuestionService.
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

    private final QuestionService questionService;
    /** Autor cuyas preguntas se listan; null para ver todas. */
    private final String autorIdFiltro;

    // Paginación actual
    private int paginaActual = 0;
    private int tamPagina    = 10;
    private int totalPaginas = 1;

    // Filtros
    private JComboBox<String>        comboEstado;
    private JTextField               txtBusqueda;
    private JTextField               txtCompetencia;
    private JComboBox<String>        comboOrden;
    private JComboBox<Integer>       comboTamPagina;

    // Tabla
    private JTable              tabla;
    private PreguntaTableModel  modeloTabla;

    // Paginación
    private JLabel  lblInfoPagina;
    private JButton btnAnterior;
    private JButton btnSiguiente;

    public GUIListarPreguntas(QuestionService questionService,
                              String autorIdFiltro) {

        this.questionService = questionService;
        this.autorIdFiltro   = autorIdFiltro;

        questionService.attach(this);

        inicializarVentana();
        construirUI();
        aplicarFiltros();
    }

    // ----------------------------------------------------------------
    // Observer
    // ----------------------------------------------------------------

    @Override
    public void actualizar(Subject sujeto) {
        SwingUtilities.invokeLater(this::aplicarFiltros);
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
                EstadoPregunta.ARCHIVADA.getEtiqueta()};
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
            aplicarFiltros();
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

        modeloTabla = new PreguntaTableModel();
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
        tabla.getColumnModel().getColumn(PreguntaTableModel.COL_ESTADO)
                .setCellRenderer(new EstadoCellRenderer());

        // Renderer/editor de botones para columna Acciones
        tabla.getColumnModel().getColumn(PreguntaTableModel.COL_ACCIONES)
                .setCellRenderer(new AccionesCellRenderer());
        tabla.getColumnModel().getColumn(PreguntaTableModel.COL_ACCIONES)
                .setCellEditor(new AccionesCellEditor());

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
                aplicarFiltros();
            }
        });

        lblInfoPagina = new JLabel("Página 1 de 1");
        lblInfoPagina.setForeground(TEXT_MUTED);
        lblInfoPagina.setFont(new Font("SansSerif", Font.PLAIN, 12));

        btnSiguiente = new JButton("Siguiente →");
        estilizarBoton(btnSiguiente, new Color(45, 55, 85),
                new Color(60, 72, 108));
        btnSiguiente.addActionListener(e -> {
            if (paginaActual < totalPaginas - 1) {
                paginaActual++;
                aplicarFiltros();
            }
        });

        panel.add(btnAnterior);
        panel.add(lblInfoPagina);
        panel.add(btnSiguiente);
        return panel;
    }

    // ----------------------------------------------------------------
    // Consulta
    // ----------------------------------------------------------------

    private void aplicarFiltros() {

        QuestionFilter filtro = new QuestionFilter()
                .conAutorId(autorIdFiltro)
                .conEstado(estadoDesdeEtiqueta((String) comboEstado.getSelectedItem()))
                .conCompetencia(vacioANull(txtCompetencia.getText()))
                .conTextoLibre(vacioANull(txtBusqueda.getText()))
                .conOrden(ordenDesdeEtiqueta((String) comboOrden.getSelectedItem()))
                .conPagina(paginaActual)
                .conTamPagina(tamPagina);

        PaginaResultado<Question> pagina = questionService.buscarPaginado(filtro);

        modeloTabla.setPreguntas(pagina.getElementos());
        totalPaginas = pagina.getTotalPaginas();
        paginaActual = pagina.getPagina();

        lblInfoPagina.setText("Página " + (paginaActual + 1)
                + " de " + Math.max(1, totalPaginas)
                + "  (" + pagina.getTotalElementos() + " resultados)");
        btnAnterior.setEnabled(paginaActual > 0);
        btnSiguiente.setEnabled(paginaActual < totalPaginas - 1);
    }

    private EstadoPregunta estadoDesdeEtiqueta(String etiqueta) {
        if (etiqueta == null || "(Todos)".equals(etiqueta)) {
            return null;
        }
        for (EstadoPregunta estado : EstadoPregunta.values()) {
            if (estado.getEtiqueta().equals(etiqueta)) {
                return estado;
            }
        }
        return null;
    }

    private QuestionFilter.Orden ordenDesdeEtiqueta(String etiqueta) {
        if (etiqueta == null) {
            return QuestionFilter.Orden.FECHA_CREACION_DESC;
        }
        return switch (etiqueta) {
            case "Fecha creación (asc)" -> QuestionFilter.Orden.FECHA_CREACION_ASC;
            case "Fecha modificación (desc)" -> QuestionFilter.Orden.FECHA_MODIFICACION_DESC;
            case "Fecha modificación (asc)" -> QuestionFilter.Orden.FECHA_MODIFICACION_ASC;
            default -> QuestionFilter.Orden.FECHA_CREACION_DESC;
        };
    }

    private String vacioANull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    // ----------------------------------------------------------------
    // Acciones sobre preguntas
    // ----------------------------------------------------------------

    /** @brief Abre el formulario de edición si la pregunta está en borrador. */
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

    /** @brief Pasa la pregunta a "Pendiente de revisión". */
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

    /** @brief Pinta la celda de estado con el color del estado. */
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
                Color color = estado.colorUI();
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

    /** @brief Dibuja los botones Editar / Enviar. */
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

    /** @brief Atiende los clics de los botones de la columna Acciones. */
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

package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.*;
import co.unicauca.iso2.bancopreguntas.infra.Observer;
import co.unicauca.iso2.bancopreguntas.infra.Subject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista para que el administrador asigne revisores a preguntas
 * en estado "Pendiente de revisión" (RF04).
 *
 * RF04.1 — Muestra las preguntas en PENDIENTE_REVISION.
 * RF04.2 — Permite seleccionar uno o más revisores (docentes).
 * RF04.3 — Al asignar, la pregunta pasa a EN_REVISION.
 * RF04.4 — El sistema envía (simula) correo a cada revisor.
 * RF04.5 — Si el correo falla, la asignación no se revierte.
 *
 * Implementa {@link Observer} para actualizarse automáticamente
 * cuando QuestionService notifica cambios.
 */
public class GUIAsignarRevisores extends JFrame implements Observer {

    private static final Color BG_DARK      = new Color(18, 22, 36);
    private static final Color BG_PANEL     = new Color(28, 33, 52);
    private static final Color ACCENT       = new Color(82, 130, 255);
    private static final Color ACCENT_AMBER = new Color(230, 160, 20);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 255);
    private static final Color TEXT_MUTED   = new Color(130, 140, 175);
    private static final Color BORDER_COLOR = new Color(50, 58, 85);

    private final QuestionService   questionService;
    private final UsuarioRepository usuarioRepository;

    private JList<Question> listaPendientes;
    private DefaultListModel<Question> modeloPendientes;

    private JPanel panelRevisores;
    private final List<JCheckBox> checkboxRevisores = new ArrayList<>();

    public GUIAsignarRevisores(QuestionService questionService,
                                UsuarioRepository usuarioRepository) {

        this.questionService   = questionService;
        this.usuarioRepository = usuarioRepository;

        questionService.attach(this);

        inicializarVentana();
        construirUI();
        cargarPreguntas();
    }

    @Override
    public void actualizar(Subject sujeto) {
        SwingUtilities.invokeLater(this::cargarPreguntas);
    }

    // ----------------------------------------------------------------
    // Construcción de la interfaz
    // ----------------------------------------------------------------

    private void inicializarVentana() {
        setTitle("Asignar revisores — Panel del Administrador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                questionService.detach(GUIAsignarRevisores.this);
            }
        });
        getContentPane().setBackground(BG_DARK);
    }

    private void construirUI() {

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        root.add(crearEncabezado(), BorderLayout.NORTH);
        root.add(crearPanelPrincipal(), BorderLayout.CENTER);
        root.add(crearPanelBotones(), BorderLayout.SOUTH);

        setContentPane(root);
        setPreferredSize(new Dimension(850, 560));
        pack();
        setLocationRelativeTo(null);
    }

    private JLabel crearEncabezado() {
        JLabel lbl = new JLabel(
                "👥  Asignar revisores a preguntas pendientes de revisión");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    private JPanel crearPanelPrincipal() {

        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setBackground(BG_DARK);

        panel.add(crearPanelPreguntas());
        panel.add(crearPanelListaRevisores());

        return panel;
    }

    // ---- Panel izquierdo: lista de preguntas pendientes ------------

    private JPanel crearPanelPreguntas() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)));

        JLabel titulo = header("📋  Preguntas pendientes de revisión");
        panel.add(titulo, BorderLayout.NORTH);

        modeloPendientes = new DefaultListModel<>();
        listaPendientes  = new JList<>(modeloPendientes);
        listaPendientes.setBackground(new Color(22, 28, 44));
        listaPendientes.setForeground(TEXT_PRIMARY);
        listaPendientes.setFont(new Font("SansSerif", Font.PLAIN, 12));
        listaPendientes.setSelectionBackground(new Color(40, 55, 95));
        listaPendientes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        listaPendientes.setCellRenderer(new PreguntaCellRenderer());

        JScrollPane scroll = new JScrollPane(listaPendientes);
        scroll.setBackground(new Color(22, 28, 44));
        scroll.getViewport().setBackground(new Color(22, 28, 44));
        scroll.setBorder(null);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ---- Panel derecho: lista de revisores con checkboxes ----------

    private JPanel crearPanelListaRevisores() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)));

        JLabel titulo = header("👤  Docentes / Revisores disponibles");
        panel.add(titulo, BorderLayout.NORTH);

        panelRevisores = new JPanel();
        panelRevisores.setLayout(new BoxLayout(panelRevisores,
                BoxLayout.Y_AXIS));
        panelRevisores.setBackground(new Color(22, 28, 44));
        panelRevisores.setBorder(
                BorderFactory.createEmptyBorder(8, 16, 8, 16));

        cargarRevisores();

        JScrollPane scroll = new JScrollPane(panelRevisores);
        scroll.getViewport().setBackground(new Color(22, 28, 44));
        scroll.setBorder(null);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void cargarRevisores() {
        panelRevisores.removeAll();
        checkboxRevisores.clear();

        List<Usuario> revisores =
                questionService.listarRevisoresDisponibles();

        if (revisores.isEmpty()) {
            JLabel lbl = new JLabel("No hay revisores disponibles.");
            lbl.setForeground(TEXT_MUTED);
            panelRevisores.add(lbl);
        } else {
            for (Usuario revisor : revisores) {
                JCheckBox cb = new JCheckBox(
                        revisor.getNombre()
                        + "  (" + revisor.getEmail() + ")");
                cb.setBackground(new Color(22, 28, 44));
                cb.setForeground(TEXT_PRIMARY);
                cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
                cb.putClientProperty("usuarioId", revisor.getId());
                cb.setAlignmentX(Component.LEFT_ALIGNMENT);
                checkboxRevisores.add(cb);
                panelRevisores.add(cb);
                panelRevisores.add(Box.createVerticalStrut(6));
            }
        }

        panelRevisores.revalidate();
        panelRevisores.repaint();
    }

    // ---- Botones ---------------------------------------------------

    private JPanel crearPanelBotones() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setBackground(BG_DARK);

        JButton btnCancelar = boton("Cancelar",
                new Color(60, 70, 95), new Color(75, 88, 120));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnAsignar = boton(
                "✅  Asignar revisores y enviar a revisión",
                ACCENT, ACCENT.brighter());
        btnAsignar.addActionListener(e -> asignarRevisores());

        panel.add(btnCancelar);
        panel.add(btnAsignar);
        return panel;
    }

    // ----------------------------------------------------------------
    // Lógica de negocio
    // ----------------------------------------------------------------

    private void cargarPreguntas() {
        modeloPendientes.clear();
        List<Question> pendientes = questionService.listByEstado(
                EstadoPregunta.PENDIENTE_REVISION);
        for (Question q : pendientes) {
            modeloPendientes.addElement(q);
        }
    }

    private void asignarRevisores() {

        Question seleccionada =
                listaPendientes.getSelectedValue();

        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una pregunta de la lista.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Recoger revisores seleccionados
        List<String> revisorIds = new ArrayList<>();
        for (JCheckBox cb : checkboxRevisores) {
            if (cb.isSelected()) {
                revisorIds.add((String) cb.getClientProperty("usuarioId"));
            }
        }

        if (revisorIds.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona al menos un revisor.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // RF04.2 / RF04.3 / RF04.4 / RF04.5
        boolean exito = questionService.asignarRevisores(
                seleccionada.getId(), revisorIds);

        if (!exito) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo asignar los revisores. "
                    + "Verifica que la pregunta esté en estado "
                    + "\"Pendiente de revisión\".",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Deseleccionar checkboxes
        checkboxRevisores.forEach(cb -> cb.setSelected(false));

        JOptionPane.showMessageDialog(this,
                "✅ Revisores asignados correctamente.\n"
                + "La pregunta pasó a estado \"En revisión\".\n"
                + "(Notificaciones enviadas — ver consola para errores)",
                "Asignación exitosa",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ----------------------------------------------------------------
    // Utilidades
    // ----------------------------------------------------------------

    private JLabel header(String texto) {
        JLabel lbl = new JLabel("  " + texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setBackground(new Color(35, 42, 64));
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        return lbl;
    }

    private JButton boton(String texto, Color base, Color hover) {
        JButton btn = new JButton(texto);
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hover);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(base);
            }
        });
        return btn;
    }

    /** Renderiza cada pregunta en la lista con su estado coloreado. */
    private static class PreguntaCellRenderer
            extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list,
                Object value, int index, boolean isSelected,
                boolean cellHasFocus) {

            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            if (value instanceof Question q) {
                String texto = "<html><b>" + q.getId() + "</b> — "
                        + q.getEnunciado()
                        .substring(0, Math.min(q.getEnunciado().length(), 60))
                        + (q.getEnunciado().length() > 60 ? "…" : "")
                        + "</html>";
                lbl.setText(texto);

                if (!isSelected) {
                    lbl.setBackground(new Color(22, 28, 44));
                    lbl.setForeground(new Color(230, 235, 255));
                }
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            }

            return lbl;
        }
    }
}

package co.unicauca.iso2.bancopreguntas.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.NivelDificultad;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionDistractors;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;

/**
 * Formulario para crear una nueva pregunta de selección múltiple
 * (RF01).
 *
 * RF01.1 — Incluye todos los campos requeridos: contexto, pregunta
 * directa, 4 distractores, respuesta correcta, justificación,
 * bibliografía, competencia, tema, subtema, nivel de dificultad.
 *
 * RF01.2 — Al guardar, ejecuta validación estructural mediante
 * {@link QuestionService#validarEstructura(Question)}.
 *
 * RF01.3 — Si hay errores, los muestra campo a campo en rojo sin
 * perder lo digitado.
 *
 * RF01.4 — Al guardar con éxito, la pregunta queda en BORRADOR
 * asociada al autor autenticado.
 */
public class GUICrearPregunta extends JFrame {

    // Paleta
    private static final Color BG_DARK    = new Color(18, 22, 36);
    private static final Color BG_SECTION = new Color(28, 33, 52);
    private static final Color ACCENT     = new Color(82, 130, 255);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 255);
    private static final Color TEXT_MUTED   = new Color(130, 140, 175);
    private static final Color ERROR_COLOR  = new Color(255, 85, 85);
    private static final Color BORDER_COLOR = new Color(50, 58, 85);
    private static final Color SUCCESS_COLOR = new Color(60, 200, 100);

    private final QuestionService questionService;

    // Campos del formulario
    private JTextArea   txtContexto;
    private JTextArea   txtEnunciado;
    private JTextField  txtOpcionA, txtOpcionB, txtOpcionC, txtOpcionD;
    private JComboBox<String> comboRespuesta;
    private JTextArea   txtJustificacion;
    private JTextField  txtBibliografia;
    private JTextField  txtCompetencia;
    private JTextField  txtTema;
    private JTextField  txtSubtema;
    private JComboBox<NivelDificultad> comboNivel;

    // Labels de error por campo (clave = nombre del campo)
    private final Map<String, JLabel> labelsError = new HashMap<>();

    public GUICrearPregunta(QuestionService questionService) {
        this.questionService = questionService;
        inicializarVentana();
        construirUI();
    }

    // ----------------------------------------------------------------
    // Construcción de la interfaz
    // ----------------------------------------------------------------

    private void inicializarVentana() {
        setTitle("Nueva pregunta — Banco de Preguntas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
    }

    private void construirUI() {

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        root.add(crearSeccion("📄 Contexto y Enunciado",
                crearPanelContextoEnunciado()));
        root.add(Box.createVerticalStrut(16));
        root.add(crearSeccion("🔤 Opciones (Distractores)",
                crearPanelOpciones()));
        root.add(Box.createVerticalStrut(16));
        root.add(crearSeccion("📚 Metadatos académicos",
                crearPanelMetadatos()));
        root.add(Box.createVerticalStrut(20));
        root.add(crearPanelBotones());

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        setContentPane(scroll);
        setPreferredSize(new Dimension(680, 720));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearSeccion(String titulo, JPanel contenido) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_SECTION);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(0, 0, 16, 0)));

        JLabel lblTitulo = new JLabel("  " + titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitulo.setForeground(TEXT_PRIMARY);
        lblTitulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        lblTitulo.setBackground(new Color(35, 42, 64));
        lblTitulo.setOpaque(true);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(contenido, BorderLayout.CENTER);

        return panel;
    }

    // ---- Sección Contexto + Enunciado ------------------------------

    private JPanel crearPanelContextoEnunciado() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SECTION);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        txtContexto  = new JTextArea(4, 40);
        txtEnunciado = new JTextArea(3, 40);

        panel.add(crearFilaCampo("contexto",
                "Contexto / situación problema *",
                new JScrollPane(txtContexto)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearFilaCampo("enunciado",
                "Pregunta directa *",
                new JScrollPane(txtEnunciado)));

        estilizarAreaTexto(txtContexto);
        estilizarAreaTexto(txtEnunciado);

        return panel;
    }

    // ---- Sección Opciones ------------------------------------------

    private JPanel crearPanelOpciones() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SECTION);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        txtOpcionA = crearTextField();
        txtOpcionB = crearTextField();
        txtOpcionC = crearTextField();
        txtOpcionD = crearTextField();

        panel.add(crearFilaCampo("opcion_A", "Opción A *", txtOpcionA));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearFilaCampo("opcion_B", "Opción B *", txtOpcionB));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearFilaCampo("opcion_C", "Opción C *", txtOpcionC));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearFilaCampo("opcion_D", "Opción D *", txtOpcionD));
        panel.add(Box.createVerticalStrut(10));

        // Respuesta correcta
        comboRespuesta = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        estilizarCombo(comboRespuesta);
        panel.add(crearFilaCampo("respuestaCorrecta",
                "Respuesta correcta *", comboRespuesta));

        return panel;
    }

    // ---- Sección Metadatos -----------------------------------------

    private JPanel crearPanelMetadatos() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SECTION);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        txtJustificacion = new JTextArea(3, 40);
        estilizarAreaTexto(txtJustificacion);

        txtBibliografia = crearTextField();
        txtCompetencia  = crearTextField();
        txtTema         = crearTextField();
        txtSubtema      = crearTextField();

        comboNivel = new JComboBox<>(NivelDificultad.values());
        estilizarCombo(comboNivel);

        panel.add(crearFilaCampo("justificacion",
                "Justificación *",
                new JScrollPane(txtJustificacion)));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearFilaCampo("bibliografia",
                "Bibliografía *", txtBibliografia));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearFilaCampo("competencia",
                "Competencia *", txtCompetencia));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearFilaCampo("tema",
                "Tema *", txtTema));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearFilaCampo("subtema",
                "Subtema *", txtSubtema));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearFilaCampo("nivelDificultad",
                "Nivel de dificultad *", comboNivel));

        return panel;
    }

    // ---- Botones ---------------------------------------------------

    private JPanel crearPanelBotones() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setBackground(BG_DARK);

        JButton btnCancelar = new JButton("Cancelar");
        estilizarBoton(btnCancelar, new Color(60, 70, 95),
                new Color(75, 88, 120));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("💾  Guardar como Borrador");
        estilizarBoton(btnGuardar, ACCENT, new Color(100, 150, 255));
        btnGuardar.addActionListener(e -> guardar());

        panel.add(btnCancelar);
        panel.add(btnGuardar);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    // ----------------------------------------------------------------
    // Helpers de construcción de componentes
    // ----------------------------------------------------------------

    /**
     * Crea una fila etiqueta + componente + label de error.
     */
    private JPanel crearFilaCampo(String clave, String etiqueta,
                                   JComponent componente) {

        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setBackground(BG_SECTION);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (componente instanceof JTextField) {
            componente.setMaximumSize(new Dimension(
                    Integer.MAX_VALUE,
                    componente.getPreferredSize().height));
        } else if (componente instanceof JScrollPane jsp) {
            jsp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        } else {
            componente.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                    componente.getPreferredSize().height));
        }

        JLabel lblErr = new JLabel(" ");
        lblErr.setForeground(ERROR_COLOR);
        lblErr.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblErr.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelsError.put(clave, lblErr);

        fila.add(lbl);
        fila.add(Box.createVerticalStrut(3));
        fila.add(componente);
        fila.add(lblErr);

        return fila;
    }

    private JTextField crearTextField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(38, 45, 68));
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private void estilizarAreaTexto(JTextArea area) {
        area.setBackground(new Color(38, 45, 68));
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(ACCENT);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    private <T> void estilizarCombo(JComboBox<T> combo) {
        combo.setBackground(new Color(38, 45, 68));
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private void estilizarBoton(JButton btn, Color base, Color hover) {
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
    }

    // ----------------------------------------------------------------
    // Lógica de guardado (RF01.2, RF01.3, RF01.4)
    // ----------------------------------------------------------------

    private void guardar() {

        // Limpiar errores anteriores
        limpiarErrores();

        // Construir la pregunta con los datos del formulario
        Question q = construirPreguntaDesdeFormulario();

        // RF01.2 — validación estructural
        List<String> errores = questionService.validarEstructura(q);

        if (!errores.isEmpty()) {
            // RF01.3 — mostrar errores campo a campo
            mostrarErroresCampo(errores);
            return;
        }

        // RF01.4 — guardar en BORRADOR asociada al autor
        boolean guardada = questionService.saveQuestion(q);

        if (!guardada) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la pregunta. "
                    + "Es posible que el ID ya exista.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "✅  Pregunta guardada correctamente en estado BORRADOR.",
                "Pregunta creada",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private Question construirPreguntaDesdeFormulario() {

        Question q = new Question();
        q.setId("P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        q.setNombre(txtEnunciado.getText().trim().length() > 50
                ? txtEnunciado.getText().trim().substring(0, 50) + "…"
                : txtEnunciado.getText().trim());
        q.setContexto(txtContexto.getText().trim());
        q.setEnunciado(txtEnunciado.getText().trim());

        // Opciones (A, B, C, D)
        List<QuestionDistractors> opciones = new ArrayList<>();
        opciones.add(new QuestionDistractors("A", txtOpcionA.getText().trim()));
        opciones.add(new QuestionDistractors("B", txtOpcionB.getText().trim()));
        opciones.add(new QuestionDistractors("C", txtOpcionC.getText().trim()));
        opciones.add(new QuestionDistractors("D", txtOpcionD.getText().trim()));
        q.setOpciones(opciones);

        q.setRespuestaCorrecta(
                (String) comboRespuesta.getSelectedItem());
        q.setJustificacion(txtJustificacion.getText().trim());
        q.setBibliografia(txtBibliografia.getText().trim());
        q.setCompetencia(txtCompetencia.getText().trim());
        q.setTema(txtTema.getText().trim());
        q.setSubtema(txtSubtema.getText().trim());
        q.setNivelDificultad(
                (NivelDificultad) comboNivel.getSelectedItem());
        q.setEstado(EstadoPregunta.BORRADOR);

        // RF01.4 — asociar al autor autenticado
        if (SessionContext.estaAutenticado()) {
            q.setAutorId(SessionContext.getUsuarioActual().getId());
        }

        return q;
    }

    private void limpiarErrores() {
        for (JLabel lbl : labelsError.values()) {
            lbl.setText(" ");
        }
    }

    /**
     * Muestra mensajes de error campo a campo (RF01.3).
     * Cada error tiene el formato "campo|mensaje".
     */
    private void mostrarErroresCampo(List<String> errores) {
        for (String error : errores) {
            String[] partes = error.split("\\|", 2);
            if (partes.length == 2) {
                String campo   = partes[0];
                String mensaje = partes[1];
                JLabel lbl = labelsError.get(campo);
                if (lbl != null) {
                    lbl.setText("⚠ " + mensaje);
                }
            }
        }
    }
}

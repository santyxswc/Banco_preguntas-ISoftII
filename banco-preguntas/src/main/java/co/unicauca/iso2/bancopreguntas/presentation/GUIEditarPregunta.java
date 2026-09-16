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
 * Formulario para editar una pregunta existente en estado BORRADOR.
 * Reutiliza la misma lógica de validación de {@link GUICrearPregunta}
 * pero pre-carga los datos de la pregunta seleccionada.
 */
public class GUIEditarPregunta extends JFrame {

    private static final Color BG_DARK    = new Color(18, 22, 36);
    private static final Color BG_SECTION = new Color(28, 33, 52);
    private static final Color ACCENT     = new Color(82, 130, 255);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 255);
    private static final Color TEXT_MUTED   = new Color(130, 140, 175);
    private static final Color ERROR_COLOR  = new Color(255, 85, 85);
    private static final Color BORDER_COLOR = new Color(50, 58, 85);

    private final QuestionService questionService;
    private final Question        preguntaOriginal;

    private JTextArea   txtContexto;
    private JTextArea   txtEnunciado;
    private JTextField  txtOpcionA, txtOpcionB, txtOpcionC, txtOpcionD;
    private JComboBox<String>          comboRespuesta;
    private JTextArea   txtJustificacion;
    private JTextField  txtBibliografia;
    private JTextField  txtCompetencia;
    private JTextField  txtTema;
    private JTextField  txtSubtema;
    private JComboBox<NivelDificultad> comboNivel;

    private final Map<String, JLabel> labelsError = new HashMap<>();

    public GUIEditarPregunta(QuestionService questionService,
                              Question pregunta) {

        this.questionService  = questionService;
        this.preguntaOriginal = pregunta;

        inicializarVentana();
        construirUI();
        precargarDatos();
    }

    private void inicializarVentana() {
        setTitle("Editar pregunta: " + preguntaOriginal.getId());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
    }

    private void construirUI() {

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Sección Contexto + Enunciado
        txtContexto  = areaTexto();
        txtEnunciado = areaTexto();

        JPanel secContexto = seccion("📄 Contexto y Enunciado");
        secContexto.add(fila("contexto",  "Contexto *",
                scrollArea(txtContexto)));
        secContexto.add(Box.createVerticalStrut(10));
        secContexto.add(fila("enunciado", "Pregunta directa *",
                scrollArea(txtEnunciado)));

        // Sección Opciones
        txtOpcionA = campo();
        txtOpcionB = campo();
        txtOpcionC = campo();
        txtOpcionD = campo();
        comboRespuesta = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        estilizarCombo(comboRespuesta);

        JPanel secOpciones = seccion("🔤 Opciones");
        secOpciones.add(fila("opcion_A", "Opción A *", txtOpcionA));
        secOpciones.add(Box.createVerticalStrut(8));
        secOpciones.add(fila("opcion_B", "Opción B *", txtOpcionB));
        secOpciones.add(Box.createVerticalStrut(8));
        secOpciones.add(fila("opcion_C", "Opción C *", txtOpcionC));
        secOpciones.add(Box.createVerticalStrut(8));
        secOpciones.add(fila("opcion_D", "Opción D *", txtOpcionD));
        secOpciones.add(Box.createVerticalStrut(8));
        secOpciones.add(fila("respuestaCorrecta", "Respuesta correcta *",
                comboRespuesta));

        // Sección Metadatos
        txtJustificacion = areaTexto();
        txtBibliografia  = campo();
        txtCompetencia   = campo();
        txtTema          = campo();
        txtSubtema       = campo();
        comboNivel = new JComboBox<>(NivelDificultad.values());
        estilizarCombo(comboNivel);

        JPanel secMeta = seccion("📚 Metadatos académicos");
        secMeta.add(fila("justificacion", "Justificación *",
                scrollArea(txtJustificacion)));
        secMeta.add(Box.createVerticalStrut(8));
        secMeta.add(fila("bibliografia", "Bibliografía *", txtBibliografia));
        secMeta.add(Box.createVerticalStrut(8));
        secMeta.add(fila("competencia",  "Competencia *",  txtCompetencia));
        secMeta.add(Box.createVerticalStrut(8));
        secMeta.add(fila("tema",         "Tema *",         txtTema));
        secMeta.add(Box.createVerticalStrut(8));
        secMeta.add(fila("subtema",      "Subtema *",      txtSubtema));
        secMeta.add(Box.createVerticalStrut(8));
        secMeta.add(fila("nivelDificultad", "Nivel de dificultad *",
                comboNivel));

        root.add(wrapSeccion("📄 Contexto y Enunciado", secContexto));
        root.add(Box.createVerticalStrut(16));
        root.add(wrapSeccion("🔤 Opciones", secOpciones));
        root.add(Box.createVerticalStrut(16));
        root.add(wrapSeccion("📚 Metadatos académicos", secMeta));
        root.add(Box.createVerticalStrut(20));
        root.add(panelBotones());

        JScrollPane scroll = new JScrollPane(root);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        setContentPane(scroll);
        setPreferredSize(new Dimension(680, 720));
        pack();
        setLocationRelativeTo(null);
    }

    private void precargarDatos() {
        txtContexto.setText(nvl(preguntaOriginal.getContexto()));
        txtEnunciado.setText(nvl(preguntaOriginal.getEnunciado()));

        List<QuestionDistractors> ops = preguntaOriginal.getOpciones();
        if (ops != null && ops.size() >= 4) {
            txtOpcionA.setText(nvl(ops.get(0).getTexto()));
            txtOpcionB.setText(nvl(ops.get(1).getTexto()));
            txtOpcionC.setText(nvl(ops.get(2).getTexto()));
            txtOpcionD.setText(nvl(ops.get(3).getTexto()));
        }
        comboRespuesta.setSelectedItem(
                nvl(preguntaOriginal.getRespuestaCorrecta()));
        txtJustificacion.setText(nvl(preguntaOriginal.getJustificacion()));
        txtBibliografia.setText(nvl(preguntaOriginal.getBibliografia()));
        txtCompetencia.setText(nvl(preguntaOriginal.getCompetencia()));
        txtTema.setText(nvl(preguntaOriginal.getTema()));
        txtSubtema.setText(nvl(preguntaOriginal.getSubtema()));
        if (preguntaOriginal.getNivelDificultad() != null) {
            comboNivel.setSelectedItem(preguntaOriginal.getNivelDificultad());
        }
    }

    private void guardar() {

        limpiarErrores();

        // Construir pregunta con datos del formulario
        Question q = new Question();
        q.setId(preguntaOriginal.getId());
        q.setNombre(preguntaOriginal.getNombre());
        q.setAutorId(preguntaOriginal.getAutorId());
        q.setEstado(EstadoPregunta.BORRADOR);
        q.setFechaCreacion(preguntaOriginal.getFechaCreacion());

        q.setContexto(txtContexto.getText().trim());
        q.setEnunciado(txtEnunciado.getText().trim());

        List<QuestionDistractors> opciones = new ArrayList<>();
        opciones.add(new QuestionDistractors("A", txtOpcionA.getText().trim()));
        opciones.add(new QuestionDistractors("B", txtOpcionB.getText().trim()));
        opciones.add(new QuestionDistractors("C", txtOpcionC.getText().trim()));
        opciones.add(new QuestionDistractors("D", txtOpcionD.getText().trim()));
        q.setOpciones(opciones);

        q.setRespuestaCorrecta((String) comboRespuesta.getSelectedItem());
        q.setJustificacion(txtJustificacion.getText().trim());
        q.setBibliografia(txtBibliografia.getText().trim());
        q.setCompetencia(txtCompetencia.getText().trim());
        q.setTema(txtTema.getText().trim());
        q.setSubtema(txtSubtema.getText().trim());
        q.setNivelDificultad((NivelDificultad) comboNivel.getSelectedItem());

        List<String> errores = questionService.validarEstructura(q);
        if (!errores.isEmpty()) {
            for (String error : errores) {
                String[] partes = error.split("\\|", 2);
                if (partes.length == 2) {
                    JLabel lbl = labelsError.get(partes[0]);
                    if (lbl != null) lbl.setText("⚠ " + partes[1]);
                }
            }
            return;
        }

        boolean actualizada = questionService.updateQuestion(q);
        if (!actualizada) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar la pregunta.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "✅ Pregunta actualizada correctamente.",
                "Guardado", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    // ---- Helpers de construcción -----------------------------------

    private JPanel seccion(String titulo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_SECTION);
        p.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return p;
    }

    private JPanel wrapSeccion(String titulo, JPanel contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_SECTION);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JLabel lbl = new JLabel("  " + titulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setBackground(new Color(35, 42, 64));
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    private JPanel fila(String clave, String etiqueta,
                         JComponent componente) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setBackground(BG_SECTION);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (!(componente instanceof JScrollPane)) {
            componente.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                    componente.getPreferredSize().height));
        } else {
            componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
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

    private JPanel panelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        p.setBackground(BG_DARK);

        JButton btnCancelar = boton("Cancelar",
                new Color(60, 70, 95), new Color(75, 88, 120));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = boton("💾  Guardar cambios",
                ACCENT, ACCENT.brighter());
        btnGuardar.addActionListener(e -> guardar());

        p.add(btnCancelar);
        p.add(btnGuardar);
        return p;
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

    private JTextArea areaTexto() {
        JTextArea a = new JTextArea(3, 40);
        a.setBackground(new Color(38, 45, 68));
        a.setForeground(TEXT_PRIMARY);
        a.setCaretColor(ACCENT);
        a.setFont(new Font("SansSerif", Font.PLAIN, 13));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return a;
    }

    private JScrollPane scrollArea(JTextArea area) {
        return new JScrollPane(area);
    }

    private JTextField campo() {
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

    private <T> void estilizarCombo(JComboBox<T> combo) {
        combo.setBackground(new Color(38, 45, 68));
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private void limpiarErrores() {
        labelsError.values().forEach(l -> l.setText(" "));
    }

    private static String nvl(String s) {
        return s != null ? s : "";
    }
}

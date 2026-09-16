package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionDistractors;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Ventana principal de la aplicación: "VENTANA PRINCIPAL - GESTIÓN DE
 * PREGUNTAS" del prototipo del taller.
 *
 * Es, a la vez, la vista y el controlador del micro patrón MVC:
 * permite elegir una pregunta del banco, ver su formulario completo y
 * cambiar su estado. No construye sus propias dependencias: recibe
 * QuestionService (el "modelo"/"sujeto") por constructor.
 *
 * No implementa Observer: es la vista activa (la que produce el
 * cambio), no una de las vistas pendientes del cambio de estado.
 */
public class GUIQuestions extends JFrame {

    private final QuestionService questionService;

    private JComboBox<Question> comboPreguntas;
    private JLabel lblId;
    private JLabel lblNombre;
    private JTextArea txtEnunciado;
    private JTextArea txtOpciones;
    private JLabel lblRespuestaCorrecta;
    private JLabel lblEstadoActual;
    private JComboBox<EstadoPregunta> comboNuevoEstado;

    private Question preguntaCargada;

    public GUIQuestions(QuestionService questionService) {

        this.questionService = questionService;

        inicializarVentana();
        crearComponentes();
        cargarComboPreguntas();
    }

    private void inicializarVentana() {

        setTitle("Banco de Preguntas Saber PRO - Gestión de preguntas");
        setSize(560, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(40, 40);
    }

    private void crearComponentes() {

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15));

        principal.add(crearPanelSeleccion(), BorderLayout.NORTH);
        principal.add(crearPanelFormulario(), BorderLayout.CENTER);

        setContentPane(principal);
    }

    /**
     * Panel "SELECCIONAR PREGUNTA" del prototipo: comboBox con las
     * preguntas del banco y botón "Cargar pregunta".
     */
    private JPanel crearPanelSeleccion() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                "Seleccionar pregunta"));

        comboPreguntas = new JComboBox<>();

        JButton btnCargar = new JButton("Cargar pregunta");
        btnCargar.addActionListener(e -> cargarPreguntaSeleccionada());

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila.add(new JLabel("Pregunta:"));
        fila.add(comboPreguntas);
        fila.add(btnCargar);

        panel.add(fila);

        return panel;
    }

    /**
     * Panel "FORMULARIO DE PREGUNTA" del prototipo: Id, Nombre,
     * Pregunta, Opciones, Respuesta Correcta, Estado.
     */
    private JPanel crearPanelFormulario() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                "Formulario de pregunta"));

        lblId = new JLabel("Id: -");
        lblNombre = new JLabel("Nombre: -");

        txtEnunciado = crearAreaSoloLectura(3);
        txtOpciones = crearAreaSoloLectura(4);

        lblRespuestaCorrecta = new JLabel("Respuesta correcta: -");
        lblEstadoActual = new JLabel("Estado actual: -");

        comboNuevoEstado =
                new JComboBox<>(EstadoPregunta.values());

        JButton btnActualizar = new JButton("Actualizar estado");
        btnActualizar.addActionListener(e -> actualizarEstado());

        panel.add(lblId);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblNombre);
        panel.add(Box.createVerticalStrut(10));

        panel.add(new JLabel("Pregunta:"));
        panel.add(new JScrollPane(txtEnunciado));
        panel.add(Box.createVerticalStrut(10));

        panel.add(new JLabel("Opciones:"));
        panel.add(new JScrollPane(txtOpciones));
        panel.add(Box.createVerticalStrut(10));

        panel.add(lblRespuestaCorrecta);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblEstadoActual);
        panel.add(Box.createVerticalStrut(15));

        JPanel filaEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaEstado.add(new JLabel("Nuevo estado:"));
        filaEstado.add(comboNuevoEstado);
        filaEstado.add(btnActualizar);

        panel.add(filaEstado);

        return panel;
    }

    private JTextArea crearAreaSoloLectura(int filas) {

        JTextArea area = new JTextArea(filas, 30);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    /**
     * Llena el comboBox con todas las preguntas del banco (una vez,
     * al iniciar la ventana, y cada vez que se refresca).
     */
    private void cargarComboPreguntas() {

        Question seleccionActual =
                (Question) comboPreguntas.getSelectedItem();

        comboPreguntas.removeAllItems();

        for (Question pregunta : questionService.listQuestions()) {
            comboPreguntas.addItem(pregunta);
        }

        if (seleccionActual != null) {
            comboPreguntas.setSelectedItem(
                    questionService.findQuestionById(
                            seleccionActual.getId()));
        }
    }

    /**
     * Carga en el formulario los datos de la pregunta elegida en el
     * comboBox.
     */
    private void cargarPreguntaSeleccionada() {

        Question seleccionada =
                (Question) comboPreguntas.getSelectedItem();

        if (seleccionada == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una pregunta del listado.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Releemos desde el servicio para mostrar siempre el estado
        // más reciente (por si otra vista/acción lo cambió).
        preguntaCargada =
                questionService.findQuestionById(seleccionada.getId());

        mostrarPreguntaEnFormulario(preguntaCargada);
    }

    private void mostrarPreguntaEnFormulario(Question pregunta) {

        if (pregunta == null) {
            return;
        }

        lblId.setText("Id: " + pregunta.getId());
        lblNombre.setText("Nombre: " + pregunta.getNombre());

        txtEnunciado.setText(pregunta.getEnunciado());
        txtEnunciado.setCaretPosition(0);

        txtOpciones.setText(formatearOpciones(pregunta.getOpciones()));
        txtOpciones.setCaretPosition(0);

        lblRespuestaCorrecta.setText(
                "Respuesta correcta: "
                        + pregunta.getRespuestaCorrecta());

        lblEstadoActual.setText(
                "Estado actual: " + pregunta.getEstado());

        comboNuevoEstado.setSelectedItem(pregunta.getEstado());
    }

    private String formatearOpciones(
            List<QuestionDistractors> opciones) {

        StringBuilder texto = new StringBuilder();

        for (QuestionDistractors opcion : opciones) {
            texto.append(opcion.getId())
                    .append(". ")
                    .append(opcion.getTexto())
                    .append(System.lineSeparator());
        }

        return texto.toString();
    }

    /**
     * Actúa como el "controlador": toma la acción del usuario,
     * delega la regla de negocio en QuestionService y refresca la
     * vista con el resultado.
     *
     * Al cambiar el estado, QuestionService (el Subject) notifica
     * automáticamente a las vistas GUIObserver1 y GUIObserver2, que
     * se renderizan de nuevo sin que esta clase las conozca.
     */
    private void actualizarEstado() {

        if (preguntaCargada == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero carga una pregunta.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        EstadoPregunta nuevoEstado =
                (EstadoPregunta) comboNuevoEstado.getSelectedItem();

        boolean actualizada = questionService.updateEstado(
                preguntaCargada.getId(), nuevoEstado);

        if (!actualizada) {
            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible actualizar el estado de la "
                            + "pregunta.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        preguntaCargada =
                questionService.findQuestionById(
                        preguntaCargada.getId());

        mostrarPreguntaEnFormulario(preguntaCargada);
        cargarComboPreguntas();

        JOptionPane.showMessageDialog(
                this,
                "Estado actualizado correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

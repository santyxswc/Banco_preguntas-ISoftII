package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;
import co.unicauca.iso2.bancopreguntas.infra.Observer;
import co.unicauca.iso2.bancopreguntas.infra.Subject;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Primera vista pendiente del cambio de estado de las preguntas:
 * "VISTA DE ESTADÍSTICAS" del prototipo del taller.
 *
 * Muestra cuántas preguntas hay por cada estado. Implementa
 * {@link Observer} y se registra ante QuestionService (el Subject)
 * en su constructor: no necesita que nadie más la mantenga
 * actualizada, se entera sola de los cambios.
 */
public class GUIObserver1 extends JFrame implements Observer {

    private final QuestionService questionService;

    private JLabel lblBorrador;
    private JLabel lblPendiente;
    private JLabel lblEnRevision;
    private JLabel lblEliminada;
    private JLabel lblTotal;

    public GUIObserver1(QuestionService questionService) {

        this.questionService = questionService;

        inicializarVentana();
        crearComponentes();

        // Nos registramos como observadores del sujeto: a partir de
        // aquí, cada vez que QuestionService.notifyObservers() se
        // ejecute, este objeto será notificado mediante actualizar().
        questionService.attach(this);

        renderizar();
    }

    private void inicializarVentana() {

        setTitle("Vista de Estadísticas - Preguntas por estado");
        setSize(320, 260);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(650, 40);
        // Bug 3 fix: desregistrar observer al cerrar para evitar
        // notificaciones sobre componentes ya destruidos.
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                questionService.detach(GUIObserver1.this);
            }
        });
    }

    private void crearComponentes() {

        JPanel principal = new JPanel();
        principal.setLayout(new BoxLayout(principal, BoxLayout.Y_AXIS));
        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("Preguntas por estado");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        lblBorrador   = new JLabel();
        lblPendiente  = new JLabel();
        lblEnRevision = new JLabel();
        lblEliminada  = new JLabel();
        lblTotal      = new JLabel();
        lblTotal.setFont(new Font("Arial", Font.BOLD, 13));

        principal.add(titulo);
        principal.add(Box.createVerticalStrut(15));
        principal.add(lblBorrador);
        principal.add(Box.createVerticalStrut(6));
        principal.add(lblPendiente);
        principal.add(Box.createVerticalStrut(6));
        principal.add(lblEnRevision);
        principal.add(Box.createVerticalStrut(6));
        principal.add(lblEliminada);
        principal.add(Box.createVerticalStrut(15));
        principal.add(new JSeparator());
        principal.add(Box.createVerticalStrut(10));
        principal.add(lblTotal);

        setContentPane(principal);
    }

    /**
     * Método del patrón Observer: invocado por QuestionService cada
     * vez que el estado de una pregunta cambia.
     */
    @Override
    public void actualizar(Subject subject) {
        renderizar();
    }

    /**
     * Recalcula y muestra el conteo de preguntas por estado.
     */
    private void renderizar() {

        Map<EstadoPregunta, Long> conteo =
                questionService.contarPorEstado();

        long borrador =
                conteo.getOrDefault(EstadoPregunta.BORRADOR, 0L);

        long pendiente = conteo.getOrDefault(
                EstadoPregunta.PENDIENTE_REVISION, 0L);

        long enRevision = conteo.getOrDefault(
                EstadoPregunta.EN_REVISION, 0L);

        long eliminada =
                conteo.getOrDefault(EstadoPregunta.ELIMINADA, 0L);

        lblBorrador.setText("Borrador: " + borrador);
        lblBorrador.setForeground(EstadoPregunta.BORRADOR.getColor());
        lblPendiente.setText("Pendiente de revisión: " + pendiente);
        lblPendiente.setForeground(
                EstadoPregunta.PENDIENTE_REVISION.getColor());
        lblEnRevision.setText("En revisión: " + enRevision);
        lblEnRevision.setForeground(
                EstadoPregunta.EN_REVISION.getColor());
        lblEliminada.setText("Eliminada: " + eliminada);
        lblEliminada.setForeground(EstadoPregunta.ELIMINADA.getColor());
        lblTotal.setText(
                "Total: " + (borrador + pendiente + enRevision + eliminada));
    }
}

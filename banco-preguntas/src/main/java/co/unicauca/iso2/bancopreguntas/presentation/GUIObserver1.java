/**
 * @file GUIObserver1.java
 * @brief Vista de estadísticas por estado.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;
import co.unicauca.iso2.bancopreguntas.infra.Observer;
import co.unicauca.iso2.bancopreguntas.infra.Subject;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * @brief Muestra cuántas preguntas hay en cada estado.
 *
 * Es Observer de QuestionService y se actualiza sola.
 */
public class GUIObserver1 extends JFrame implements Observer {

    private final QuestionService questionService;

    private JLabel lblBorrador;
    private JLabel lblPendiente;
    private JLabel lblEnRevision;
    private JLabel lblArchivada;
    private JLabel lblTotal;

    public GUIObserver1(QuestionService questionService) {

        this.questionService = questionService;

        inicializarVentana();
        crearComponentes();

        questionService.attach(this);

        renderizar();
    }

    private void inicializarVentana() {

        setTitle("Vista de Estadísticas - Preguntas por estado");
        setSize(320, 260);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(650, 40);
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
        lblArchivada  = new JLabel();
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
        principal.add(lblArchivada);
        principal.add(Box.createVerticalStrut(15));
        principal.add(new JSeparator());
        principal.add(Box.createVerticalStrut(10));
        principal.add(lblTotal);

        setContentPane(principal);
    }

    /** @brief Se llama cuando QuestionService notifica un cambio. */
    @Override
    public void actualizar(Subject subject) {
        renderizar();
    }

    /** @brief Recalcula y muestra el conteo por estado. */
    private void renderizar() {

        Map<EstadoPregunta, Long> conteo =
                questionService.contarPorEstado();

        long borrador =
                conteo.getOrDefault(EstadoPregunta.BORRADOR, 0L);

        long pendiente = conteo.getOrDefault(
                EstadoPregunta.PENDIENTE_REVISION, 0L);

        long enRevision = conteo.getOrDefault(
                EstadoPregunta.EN_REVISION, 0L);

        long archivada =
                conteo.getOrDefault(EstadoPregunta.ARCHIVADA, 0L);

        lblBorrador.setText("Borrador: " + borrador);
        lblBorrador.setForeground(EstadoPregunta.BORRADOR.colorUI());
        lblPendiente.setText("Pendiente de revisión: " + pendiente);
        lblPendiente.setForeground(
                EstadoPregunta.PENDIENTE_REVISION.colorUI());
        lblEnRevision.setText("En revisión: " + enRevision);
        lblEnRevision.setForeground(
                EstadoPregunta.EN_REVISION.colorUI());
        lblArchivada.setText("Archivada: " + archivada);
        lblArchivada.setForeground(EstadoPregunta.ARCHIVADA.colorUI());
        lblTotal.setText(
                "Total: " + (borrador + pendiente + enRevision + archivada));
    }
}

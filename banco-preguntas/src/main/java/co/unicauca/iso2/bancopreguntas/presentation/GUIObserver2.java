package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;
import co.unicauca.iso2.bancopreguntas.infra.Observer;
import co.unicauca.iso2.bancopreguntas.infra.Subject;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Segunda vista pendiente del cambio de estado de las preguntas:
 * "VISTA GRÁFICA" del prototipo del taller.
 *
 * Muestra una gráfica de pastel con el porcentaje de preguntas en
 * cada estado. Implementa {@link Observer} y se registra ante
 * QuestionService (el Subject) en su constructor.
 */
public class GUIObserver2 extends JFrame implements Observer {

    private static final Color COLOR_BORRADOR =
            new Color(66, 133, 244);

    private static final Color COLOR_PENDIENTE =
            new Color(251, 188, 5);

    private static final Color COLOR_EN_REVISION =
            new Color(30, 120, 210);

    private static final Color COLOR_ELIMINADA =
            new Color(234, 67, 53);

    private final QuestionService questionService;

    private PanelPastel panelPastel;
    private JLabel lblLeyendaBorrador;
    private JLabel lblLeyendaPendiente;
    private JLabel lblLeyendaEnRevision;
    private JLabel lblLeyendaEliminada;

    public GUIObserver2(QuestionService questionService) {

        this.questionService = questionService;

        inicializarVentana();
        crearComponentes();

        // Nos registramos como observadores del sujeto, igual que
        // GUIObserver1: nos enteramos solos de los cambios de estado.
        questionService.attach(this);

        renderizar();
    }

    private void inicializarVentana() {

        setTitle("Vista Gráfica - Distribución de preguntas");
        setSize(360, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(650, 340);
        // Bug 3 fix: desregistrar observer al cerrar para evitar
        // notificaciones sobre componentes ya destruidos.
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                questionService.detach(GUIObserver2.this);
            }
        });
    }

    private void crearComponentes() {

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
                "Distribución de preguntas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        panelPastel = new PanelPastel();
        panelPastel.setPreferredSize(new Dimension(260, 260));

        lblLeyendaBorrador   = crearEtiquetaLeyenda(COLOR_BORRADOR);
        lblLeyendaPendiente  = crearEtiquetaLeyenda(COLOR_PENDIENTE);
        lblLeyendaEnRevision = crearEtiquetaLeyenda(COLOR_EN_REVISION);
        lblLeyendaEliminada  = crearEtiquetaLeyenda(COLOR_ELIMINADA);

        JPanel leyenda = new JPanel();
        leyenda.setLayout(new BoxLayout(leyenda, BoxLayout.Y_AXIS));
        leyenda.add(lblLeyendaBorrador);
        leyenda.add(lblLeyendaPendiente);
        leyenda.add(lblLeyendaEnRevision);
        leyenda.add(lblLeyendaEliminada);

        principal.add(titulo, BorderLayout.NORTH);
        principal.add(panelPastel, BorderLayout.CENTER);
        principal.add(leyenda, BorderLayout.SOUTH);

        setContentPane(principal);
    }

    private JLabel crearEtiquetaLeyenda(Color color) {

        JLabel etiqueta = new JLabel();
        etiqueta.setOpaque(false);
        etiqueta.setIcon(new CuadradoColor(color));
        etiqueta.setIconTextGap(8);
        return etiqueta;
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
     * Recalcula los porcentajes por estado y actualiza tanto la
     * gráfica de pastel como la leyenda de texto.
     */
    private void renderizar() {

        Map<EstadoPregunta, Double> porcentajes =
                questionService.porcentajePorEstado();

        panelPastel.setPorcentajes(porcentajes);
        panelPastel.repaint();

        lblLeyendaBorrador.setText(String.format(
                "Borrador: %.0f%%",
                porcentajes.getOrDefault(
                        EstadoPregunta.BORRADOR, 0.0)));

        lblLeyendaPendiente.setText(String.format(
                "Pendiente de revisión: %.0f%%",
                porcentajes.getOrDefault(
                        EstadoPregunta.PENDIENTE_REVISION, 0.0)));

        lblLeyendaEnRevision.setText(String.format(
                "En revisión: %.0f%%",
                porcentajes.getOrDefault(
                        EstadoPregunta.EN_REVISION, 0.0)));

        lblLeyendaEliminada.setText(String.format(
                "Eliminada: %.0f%%",
                porcentajes.getOrDefault(
                        EstadoPregunta.ELIMINADA, 0.0)));
    }

    /**
     * Panel que dibuja la gráfica de pastel usando Graphics2D, sin
     * depender de ninguna librería externa de gráficas.
     */
    private static class PanelPastel extends JPanel {

        private Map<EstadoPregunta, Double> porcentajes =
                new LinkedHashMap<>();

        void setPorcentajes(Map<EstadoPregunta, Double> porcentajes) {
            this.porcentajes = porcentajes;
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            super.paintComponent(graphics);

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int diametro =
                    Math.min(getWidth(), getHeight()) - 20;

            int x = (getWidth() - diametro) / 2;
            int y = (getHeight() - diametro) / 2;

            double anguloInicial = 90;

            anguloInicial = dibujarPorcion(
                    g2d, x, y, diametro, anguloInicial,
                    porcentajes.getOrDefault(
                            EstadoPregunta.BORRADOR, 0.0),
                    COLOR_BORRADOR);

            anguloInicial = dibujarPorcion(
                    g2d, x, y, diametro, anguloInicial,
                    porcentajes.getOrDefault(
                            EstadoPregunta.PENDIENTE_REVISION, 0.0),
                    COLOR_PENDIENTE);

            anguloInicial = dibujarPorcion(
                    g2d, x, y, diametro, anguloInicial,
                    porcentajes.getOrDefault(
                            EstadoPregunta.EN_REVISION, 0.0),
                    COLOR_EN_REVISION);

            dibujarPorcion(
                    g2d, x, y, diametro, anguloInicial,
                    porcentajes.getOrDefault(
                            EstadoPregunta.ELIMINADA, 0.0),
                    COLOR_ELIMINADA);

            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(x, y, diametro, diametro);
        }

        /**
         * Dibuja una porción del pastel y devuelve el ángulo inicial
         * de la siguiente porción.
         */
        private double dibujarPorcion(
                Graphics2D g2d, int x, int y, int diametro,
                double anguloInicial, double porcentaje, Color color) {

            double extension = porcentaje * 3.6;

            if (extension <= 0) {
                return anguloInicial;
            }

            g2d.setColor(color);
            g2d.fillArc(
                    x, y, diametro, diametro,
                    (int) Math.round(anguloInicial),
                    (int) Math.round(-extension));

            return anguloInicial - extension;
        }
    }

    /**
     * Pequeño icono cuadrado de color sólido usado en la leyenda.
     */
    private record CuadradoColor(Color color) implements Icon {

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            g.setColor(color);
            g.fillRect(x, y, getIconWidth(), getIconHeight());
        }

        @Override
        public int getIconWidth() {
            return 14;
        }

        @Override
        public int getIconHeight() {
            return 14;
        }
    }
}

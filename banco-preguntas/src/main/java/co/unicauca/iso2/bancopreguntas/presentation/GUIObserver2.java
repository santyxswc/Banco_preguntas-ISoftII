/**
 * @file GUIObserver2.java
 * @brief Vista gráfica de la distribución por estado.
 * @author Santiago Caicedo
 */
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
 * @brief Gráfica de pastel con el porcentaje de preguntas por estado.
 *
 * Es Observer de QuestionService y se actualiza sola.
 */
public class GUIObserver2 extends JFrame implements Observer {

    private static final Color COLOR_BORRADOR =
            EstadoPregunta.BORRADOR.colorUI();

    private static final Color COLOR_PENDIENTE =
            EstadoPregunta.PENDIENTE_REVISION.colorUI();

    private static final Color COLOR_EN_REVISION =
            EstadoPregunta.EN_REVISION.colorUI();

    private static final Color COLOR_ARCHIVADA =
            EstadoPregunta.ARCHIVADA.colorUI();

    private final QuestionService questionService;

    private PanelPastel panelPastel;
    private JLabel lblLeyendaBorrador;
    private JLabel lblLeyendaPendiente;
    private JLabel lblLeyendaEnRevision;
    private JLabel lblLeyendaArchivada;

    public GUIObserver2(QuestionService questionService) {

        this.questionService = questionService;

        inicializarVentana();
        crearComponentes();

        questionService.attach(this);

        renderizar();
    }

    private void inicializarVentana() {

        setTitle("Vista Gráfica - Distribución de preguntas");
        setSize(360, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(650, 340);
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
        lblLeyendaArchivada  = crearEtiquetaLeyenda(COLOR_ARCHIVADA);

        JPanel leyenda = new JPanel();
        leyenda.setLayout(new BoxLayout(leyenda, BoxLayout.Y_AXIS));
        leyenda.add(lblLeyendaBorrador);
        leyenda.add(lblLeyendaPendiente);
        leyenda.add(lblLeyendaEnRevision);
        leyenda.add(lblLeyendaArchivada);

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

    /** @brief Se llama cuando QuestionService notifica un cambio. */
    @Override
    public void actualizar(Subject subject) {
        renderizar();
    }

    /** @brief Recalcula porcentajes y actualiza gráfica y leyenda. */
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

        lblLeyendaArchivada.setText(String.format(
                "Archivada: %.0f%%",
                porcentajes.getOrDefault(
                        EstadoPregunta.ARCHIVADA, 0.0)));
    }

    /** @brief Panel que dibuja el pastel con Graphics2D. */
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
                            EstadoPregunta.ARCHIVADA, 0.0),
                    COLOR_ARCHIVADA);

            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(x, y, diametro, diametro);
        }

        /**
         * @brief Dibuja una porción del pastel.
         * @return ángulo donde empieza la siguiente porción
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

    /** @brief Cuadro de color para la leyenda. */
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

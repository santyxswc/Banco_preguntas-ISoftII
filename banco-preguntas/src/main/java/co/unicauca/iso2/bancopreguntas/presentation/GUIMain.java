package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.QuestionService;
import co.unicauca.iso2.bancopreguntas.domain.Rol;
import co.unicauca.iso2.bancopreguntas.domain.Usuario;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal post-login del Banco de Preguntas.
 *
 * Muestra un encabezado con el nombre del usuario y su rol.
 * Según el rol presenta:
 * <ul>
 *   <li><b>AUTOR</b>: botones para crear preguntas (RF01) y ver su
 *       listado con filtros (RF03/RF02).</li>
 *   <li><b>ADMINISTRADOR</b>: botón para asignar revisores (RF04) y
 *       acceso a las vistas de estadísticas (GUIObserver1/2).</li>
 * </ul>
 */
public class GUIMain extends JFrame {

    // Paleta de colores
    private static final Color BG_DARK      = new Color(18, 22, 36);
    private static final Color BG_HEADER    = new Color(28, 33, 52);
    private static final Color ACCENT       = new Color(82, 130, 255);
    private static final Color ACCENT_HOVER = new Color(100, 150, 255);
    private static final Color BTN_SECONDARY      = new Color(45, 55, 85);
    private static final Color BTN_SECONDARY_HOVER = new Color(60, 72, 108);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 255);
    private static final Color TEXT_MUTED   = new Color(130, 140, 175);
    private static final Color BORDER_COLOR = new Color(50, 58, 85);
    private static final Color DANGER_COLOR = new Color(200, 60, 60);

    private final QuestionService  questionService;
    private final UsuarioRepository usuarioRepository;
    private final Usuario           usuarioActual;

    // Vistas observadoras (se crean al abrir las estadísticas)
    private GUIObserver1 vistaEstadisticas;
    private GUIObserver2 vistaGrafica;

    public GUIMain(QuestionService questionService,
                   UsuarioRepository usuarioRepository) {

        this.questionService   = questionService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioActual     = SessionContext.getUsuarioActual();

        inicializarVentana();
        construirUI();
    }

    // ----------------------------------------------------------------
    // Construcción de la interfaz
    // ----------------------------------------------------------------

    private void inicializarVentana() {
        setTitle("Banco de Preguntas Saber Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 520));
        getContentPane().setBackground(BG_DARK);
    }

    private void construirUI() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);

        root.add(crearEncabezado(), BorderLayout.NORTH);
        root.add(crearPanelCentral(), BorderLayout.CENTER);
        root.add(crearPiePagina(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    // ---- Encabezado ------------------------------------------------

    private JPanel crearEncabezado() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_HEADER);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));

        // Izquierda: título de la app
        JLabel lblApp = new JLabel("📚 Banco de Preguntas Saber Pro");
        lblApp.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblApp.setForeground(TEXT_PRIMARY);

        // Derecha: info del usuario + botón cerrar sesión
        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panelUsuario.setBackground(BG_HEADER);

        // Badge de rol
        Color colorRol = (usuarioActual.getRol() == Rol.ADMINISTRADOR)
                ? new Color(230, 160, 20) : ACCENT;
        JLabel lblRol = crearBadge(usuarioActual.getRol().getEtiqueta(),
                                   colorRol);

        JLabel lblNombre = new JLabel(usuarioActual.getNombre());
        lblNombre.setForeground(TEXT_PRIMARY);
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton btnLogout = new JButton("Cerrar sesión");
        estilizarBoton(btnLogout, DANGER_COLOR, DANGER_COLOR.darker());
        btnLogout.addActionListener(e -> cerrarSesion());

        panelUsuario.add(lblRol);
        panelUsuario.add(lblNombre);
        panelUsuario.add(btnLogout);

        header.add(lblApp, BorderLayout.WEST);
        header.add(panelUsuario, BorderLayout.EAST);

        return header;
    }

    private JLabel crearBadge(String texto, Color color) {
        JLabel badge = new JLabel(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color.darker().darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setForeground(color.brighter());
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        badge.setOpaque(false);
        return badge;
    }

    // ---- Panel central con tarjetas de acciones --------------------

    private JPanel crearPanelCentral() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        // Saludo
        JLabel lblSaludo = new JLabel(
                "Bienvenido(a), " + usuarioActual.getNombre() + " 👋");
        lblSaludo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblSaludo.setForeground(TEXT_PRIMARY);

        JLabel lblSubtitulo = new JLabel(
                "¿Qué deseas hacer hoy?");
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitulo.setForeground(TEXT_MUTED);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_DARK);
        topPanel.add(lblSaludo);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(lblSubtitulo);

        // Tarjetas de acciones según rol
        JPanel tarjetas = crearTarjetasSegunRol();

        // BoxLayout para apilar verticalmente
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(BG_DARK);
        contenedor.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        contenedor.add(topPanel);
        contenedor.add(Box.createVerticalStrut(28));
        contenedor.add(tarjetas);

        return contenedor;
    }

    private JPanel crearTarjetasSegunRol() {

        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
        grid.setBackground(BG_DARK);

        if (usuarioActual.getRol() == Rol.AUTOR) {
            grid.add(crearTarjeta(
                    "📝",
                    "Crear pregunta",
                    "Registra una nueva pregunta de selección múltiple",
                    ACCENT, ACCENT_HOVER,
                    e -> abrirCrearPregunta()));

            grid.add(crearTarjeta(
                    "📋",
                    "Mis preguntas",
                    "Lista, filtra y gestiona el estado de tus preguntas",
                    BTN_SECONDARY, BTN_SECONDARY_HOVER,
                    e -> abrirListarPreguntas()));
        } else {
            // ADMINISTRADOR
            grid.add(crearTarjeta(
                    "👥",
                    "Asignar revisores",
                    "Asigna revisores a preguntas pendientes de revisión",
                    ACCENT, ACCENT_HOVER,
                    e -> abrirAsignarRevisores()));

            grid.add(crearTarjeta(
                    "📊",
                    "Estadísticas",
                    "Ver conteo de preguntas por estado",
                    BTN_SECONDARY, BTN_SECONDARY_HOVER,
                    e -> abrirEstadisticas()));

            grid.add(crearTarjeta(
                    "🥧",
                    "Gráfica circular",
                    "Ver distribución porcentual de estados",
                    BTN_SECONDARY, BTN_SECONDARY_HOVER,
                    e -> abrirGrafica()));

            grid.add(crearTarjeta(
                    "📋",
                    "Todas las preguntas",
                    "Explorar el banco completo de preguntas",
                    BTN_SECONDARY, BTN_SECONDARY_HOVER,
                    e -> abrirListarPreguntasAdmin()));
        }

        return grid;
    }

    private JPanel crearTarjeta(String emoji, String titulo,
                                 String descripcion,
                                 Color colorBase, Color colorHover,
                                 java.awt.event.ActionListener accion) {

        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setBackground(new Color(28, 33, 52));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lblEmoji = new JLabel(emoji);
        lblEmoji.setFont(new Font("SansSerif", Font.PLAIN, 32));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitulo.setForeground(TEXT_PRIMARY);

        JLabel lblDesc = new JLabel("<html>" + descripcion + "</html>");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDesc.setForeground(TEXT_MUTED);

        JButton btnAccion = new JButton("Abrir →");
        estilizarBoton(btnAccion, colorBase, colorHover);
        btnAccion.addActionListener(accion);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblDesc);
        textos.add(Box.createVerticalStrut(12));
        textos.add(btnAccion);

        card.add(lblEmoji, BorderLayout.NORTH);
        card.add(textos, BorderLayout.CENTER);

        return card;
    }

    // ---- Pie de página ---------------------------------------------

    private JPanel crearPiePagina() {
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pie.setBackground(BG_DARK);
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JLabel lbl = new JLabel(
                "Universidad del Cauca — Ingeniería de Software III © 2026");
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        pie.add(lbl);
        return pie;
    }

    // ----------------------------------------------------------------
    // Navegación a otras vistas
    // ----------------------------------------------------------------

    private void abrirCrearPregunta() {
        new GUICrearPregunta(questionService).setVisible(true);
    }

    private void abrirListarPreguntas() {
        new GUIListarPreguntas(questionService, usuarioActual.getId())
                .setVisible(true);
    }

    private void abrirListarPreguntasAdmin() {
        // Admin puede ver todas las preguntas (sin filtro de autor)
        new GUIListarPreguntas(questionService, null).setVisible(true);
    }

    private void abrirAsignarRevisores() {
        new GUIAsignarRevisores(questionService, usuarioRepository)
                .setVisible(true);
    }

    private void abrirEstadisticas() {
        if (vistaEstadisticas == null || !vistaEstadisticas.isDisplayable()) {
            vistaEstadisticas = new GUIObserver1(questionService);
        }
        vistaEstadisticas.setVisible(true);
        vistaEstadisticas.toFront();
    }

    private void abrirGrafica() {
        if (vistaGrafica == null || !vistaGrafica.isDisplayable()) {
            vistaGrafica = new GUIObserver2(questionService);
        }
        vistaGrafica.setVisible(true);
        vistaGrafica.toFront();
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            SessionContext.cerrar();
            dispose();
            // Re-abrir el login (se construye en ClientMain, pero aquí
            // lo delegamos al application context a través del servicio)
            SwingUtilities.invokeLater(() -> {
                GUILogin login = new GUILogin(usuarioRepository,
                        questionService);
                login.setVisible(true);
            });
        }
    }

    // ----------------------------------------------------------------
    // Utilidades de estilo
    // ----------------------------------------------------------------

    private void estilizarBoton(JButton btn, Color base, Color hover) {
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hover);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(base);
            }
        });
    }
}

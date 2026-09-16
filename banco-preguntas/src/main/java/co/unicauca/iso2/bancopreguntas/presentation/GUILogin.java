package co.unicauca.iso2.bancopreguntas.presentation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import co.unicauca.iso2.bancopreguntas.domain.QuestionService;
import co.unicauca.iso2.bancopreguntas.domain.Usuario;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;

/**
 * Ventana de inicio de sesión del Banco de Preguntas Saber Pro.
 *
 * Valida las credenciales contra {@link UsuarioRepository}. Si son
 * correctas guarda el usuario en {@link SessionContext} y abre
 * {@link GUIMain}. Si son incorrectas muestra un mensaje de error
 * en rojo debajo del formulario sin perder los datos ingresados.
 */
public class GUILogin extends JFrame {

    // ----------------------------------------------------------------
    // Paleta de colores (diseño moderno, oscuro)
    // ----------------------------------------------------------------
    private static final Color BG_DARK      = new Color(18, 22, 36);
    private static final Color BG_CARD      = new Color(28, 33, 52);
    private static final Color ACCENT       = new Color(82, 130, 255);
    private static final Color ACCENT_HOVER = new Color(100, 150, 255);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 255);
    private static final Color TEXT_MUTED   = new Color(130, 140, 175);
    private static final Color ERROR_COLOR  = new Color(255, 85, 85);
    private static final Color BORDER_COLOR = new Color(50, 58, 85);

    private final UsuarioRepository usuarioRepository;
    private final QuestionService   questionService;

    private JTextField  txtEmail;
    private JPasswordField txtPassword;
    private JLabel      lblError;

    public GUILogin(UsuarioRepository usuarioRepository,
                    QuestionService questionService) {

        this.usuarioRepository = usuarioRepository;
        this.questionService   = questionService;

        inicializarVentana();
        construirUI();
    }

    // ----------------------------------------------------------------
    // Construcción de la interfaz
    // ----------------------------------------------------------------

    private void inicializarVentana() {
        setTitle("Banco de Preguntas Saber Pro — Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
    }

    private void construirUI() {

        // Panel raíz con fondo oscuro y centrado
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        root.add(crearTarjeta());

        setContentPane(root);
        pack();
        setLocationRelativeTo(null); // centrar en pantalla
    }

    /** Tarjeta blanca (oscura) que contiene el formulario. */
    private JPanel crearTarjeta() {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(36, 44, 36, 44)));

        // Logo / título
        card.add(crearEncabezado());
        card.add(Box.createVerticalStrut(28));

        // Campo email
        card.add(crearLabel("Correo electrónico"));
        card.add(Box.createVerticalStrut(6));
        txtEmail = crearTextField("correo@unicauca.edu.co");
        card.add(txtEmail);
        card.add(Box.createVerticalStrut(16));

        // Campo contraseña
        card.add(crearLabel("Contraseña"));
        card.add(Box.createVerticalStrut(6));
        txtPassword = crearPasswordField();
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(8));

        // Label de error (inicialmente oculto)
        lblError = new JLabel(" ");
        lblError.setForeground(ERROR_COLOR);
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblError);
        card.add(Box.createVerticalStrut(20));

        // Botón ingresar
        card.add(crearBotonIngresar());
        card.add(Box.createVerticalStrut(20));

        // Hint de credenciales de ejemplo
        card.add(crearHintCredenciales());

        return card;
    }

    private JPanel crearEncabezado() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);

        // Ícono de candado (texto unicode)
        JLabel icono = new JLabel("🔐");
        icono.setFont(new Font("SansSerif", Font.PLAIN, 40));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Banco de Preguntas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Saber Pro — Unicauca");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(TEXT_MUTED);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icono);
        panel.add(Box.createVerticalStrut(8));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitulo);

        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField crearTextField(String placeholder) {
        JTextField field = new JTextField(24) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Placeholder simulado
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2,
                            getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        estilizarCampo(field);
        return field;
    }

    private JPasswordField crearPasswordField() {
        JPasswordField field = new JPasswordField(24);
        estilizarCampo(field);
        // Permitir login con Enter
        field.addActionListener(this::onIngresar);
        return field;
    }

    private void estilizarCampo(JTextField field) {
        field.setBackground(new Color(38, 45, 68));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                field.getPreferredSize().height));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton crearBotonIngresar() {

        JButton btn = new JButton("Iniciar sesión");
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                btn.getPreferredSize().height));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });

        btn.addActionListener(this::onIngresar);
        return btn;
    }

    private JPanel crearHintCredenciales() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(35, 42, 62));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel titulo = new JLabel("Credenciales de ejemplo:");
        titulo.setForeground(TEXT_MUTED);
        titulo.setFont(new Font("Monospaced", Font.BOLD, 11));

        JLabel admin = new JLabel("Admin: admin@unicauca.edu.co / admin123");
        JLabel autor = new JLabel("Autor: autor1@unicauca.edu.co / autor123");

        for (JLabel lbl : new JLabel[]{admin, autor}) {
            lbl.setForeground(new Color(100, 210, 140));
            lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        }

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(admin);
        panel.add(autor);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                panel.getPreferredSize().height + 40));
        return panel;
    }

    // ----------------------------------------------------------------
    // Lógica de autenticación
    // ----------------------------------------------------------------

    private void onIngresar(ActionEvent e) {

        String email    = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor ingresa tu correo y contraseña.");
            return;
        }

        Optional<Usuario> resultado =
                usuarioRepository.findByEmailAndPassword(email, password);

        if (resultado.isEmpty()) {
            mostrarError("Correo o contraseña incorrectos.");
            txtPassword.setText("");
            return;
        }

        // Autenticación exitosa
        Usuario usuario = resultado.get();
        SessionContext.iniciar(usuario);
        lblError.setText(" ");

        // Abrir ventana principal según rol
        GUIMain ventanaMain = new GUIMain(questionService,
                                          usuarioRepository);
        ventanaMain.setVisible(true);
        dispose(); // cerrar login
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }
}

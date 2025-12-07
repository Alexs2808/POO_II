package MemoramaLogaritmos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MemoramaCalculoDiferencial extends JFrame {
    
    // Componentes de la interfaz
    private JPanel panelPrincipal;
    private JButton btnJugarSolo;
    private JButton btnJugarPareja;
    private JButton btnInstrucciones;
    private JButton btnSalir;
    private JButton btnScore;
    private JLabel lblTitulo;
    private JLabel lblSubtitulo;
    
    // Configuración de colores del tema
    private final Color COLOR_FONDO = new Color(240, 248, 255);
    private final Color COLOR_PRIMARIO = new Color(70, 130, 180);
    private final Color COLOR_SECUNDARIO = new Color(100, 149, 237);
    private final Color COLOR_ACENTO = new Color(255, 140, 0);
    
    public MemoramaCalculoDiferencial() {
        configurarVentana();
        crearInterfaz();
    }
    
    private void configurarVentana() {
        setTitle("Memorama - Propiedades Log/Exp/Rad");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
    }
    
    private void crearInterfaz() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        lblTitulo = new JLabel("MEMORAMA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 48));
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblSubtitulo = new JLabel("Propiedades: Logaritmos • Exponentes • Radicales");
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 20));
        lblSubtitulo.setForeground(COLOR_SECUNDARIO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 40)));
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(lblSubtitulo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Botón Jugar Solo
        btnJugarSolo = crearBoton("🎯 JUGAR SOLO", COLOR_PRIMARIO);
        btnJugarSolo.addActionListener(e -> iniciarJuegoSolo());
        panelPrincipal.add(btnJugarSolo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 18)));
        
        // Botón Jugar en Pareja
        btnJugarPareja = crearBoton("👥 JUGAR EN PAREJA", COLOR_SECUNDARIO);
        btnJugarPareja.addActionListener(e -> iniciarJuegoPareja());
        panelPrincipal.add(btnJugarPareja);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 18)));
        
        // Botón Instrucciones
        btnInstrucciones = crearBoton("📖 INSTRUCCIONES / CRÉDITOS", COLOR_ACENTO);
        btnInstrucciones.addActionListener(e -> mostrarInstrucciones());
        panelPrincipal.add(btnInstrucciones);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 18)));
        
        // Botón Score (estadísticas de la sesión)
        btnScore = crearBoton("📊 SCORE", new Color(72, 61, 139));
        btnScore.addActionListener(e -> mostrarScoreSesion());
        panelPrincipal.add(btnScore);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 18)));
        
        // Botón Salir
        btnSalir = crearBoton("🚪 SALIR", new Color(220, 20, 60));
        btnSalir.addActionListener(e -> salirJuego());
        panelPrincipal.add(btnSalir);
        
        add(panelPrincipal);
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 20));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(400, 60));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    private void iniciarJuegoSolo() {
        String[] opciones = {"1 minuto", "3 minutos", "5 minutos"};
        String seleccion = (String) JOptionPane.showInputDialog(
            this,
            "Selecciona el tiempo de la partida (máx. 5 minutos):",
            "Configuración - Modo Solitario",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[2]
        );
        
        if (seleccion != null) {
            int tiempoMinutos = Integer.parseInt(seleccion.split(" ")[0]);
            
            String[] niveles = {"Fácil (4x3)", "Medio (4x4)", "Difícil (6x4)"};
            String nivel = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona el nivel de dificultad:",
                "Nivel de Dificultad",
                JOptionPane.QUESTION_MESSAGE,
                null,
                niveles,
                niveles[1]
            );
            
            if (nivel != null) {
                int filas, columnas;
                if (nivel.startsWith("Fácil")) {
                    filas = 3; columnas = 4;
                } else if (nivel.startsWith("Medio")) {
                    filas = 4; columnas = 4;
                } else {
                    filas = 4; columnas = 6;
                }
                
                abrirVentanaJuego(1, tiempoMinutos, filas, columnas);
            }
        }
    }
    
    private void iniciarJuegoPareja() {
        String[] opciones = {"1 minuto", "3 minutos", "5 minutos"};
        String seleccion = (String) JOptionPane.showInputDialog(
            this,
            "Selecciona el tiempo de la partida (máx. 5 minutos):",
            "Configuración - Modo Pareja",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[2]
        );
        
        if (seleccion != null) {
            int tiempoMinutos = Integer.parseInt(seleccion.split(" ")[0]);
            
            String[] niveles = {"Fácil (4x3)", "Medio (4x4)", "Difícil (6x4)"};
            String nivel = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona el nivel de dificultad:",
                "Nivel de Dificultad",
                JOptionPane.QUESTION_MESSAGE,
                null,
                niveles,
                niveles[1]
            );
            
            if (nivel != null) {
                int filas, columnas;
                if (nivel.startsWith("Fácil")) {
                    filas = 3; columnas = 4;
                } else if (nivel.startsWith("Medio")) {
                    filas = 4; columnas = 4;
                } else {
                    filas = 4; columnas = 6;
                }
                
                abrirVentanaJuego(2, tiempoMinutos, filas, columnas);
            }
        }
    }
    
    private void abrirVentanaJuego(int numJugadores, int tiempoMinutos, int filas, int columnas) {
        this.setVisible(false);
        VentanaJuego ventanaJuego = new VentanaJuego(this, numJugadores, tiempoMinutos, filas, columnas);
        ventanaJuego.setVisible(true);
    }
    
    private void mostrarInstrucciones() {
        String instrucciones = 
            "═══════════════════════════════════════════════════════\n" +
            "                 INSTRUCCIONES DEL JUEGO\n" +
            "═══════════════════════════════════════════════════════\n\n" +
            "OBJETIVO:\n" +
            "Encontrar pares de tarjetas que representen propiedades equivalentes\n" +
            "de logaritmos, leyes de exponentes y radicales.\n\n" +
            "REGLAS:\n" +
            "• MODO SOLITARIO: Debes encontrar TODOS los pares dentro del tiempo\n" +
            "  seleccionado (si encuentras todos los pares antes de que termine el\n" +
            "  tiempo, ganas la partida).\n\n" +
            "• MODO PAREJA: Los jugadores se turnan; si encuentras un par sigues\n" +
            "  jugando. Si un jugador descubre todos los pares durante la partida,\n" +
            "  ese jugador gana inmediatamente. Si se acaba el tiempo, gana quien\n" +
            "  tenga más pares (empate = tiempo extra de 5 min; si persiste empate,\n" +
            "  se considera empate final).\n\n" +
            "TIEMPO:\n" +
            "• Las partidas son de máximo 5 minutos. El menú ofrece 1, 3 o 5 minutos.\n\n" +
            "SCORE / SESIÓN:\n" +
            "• El juego registra la cantidad de pares encontrados y las partidas\n" +
            "  ganadas durante la sesión actual (ver botón 'SCORE').\n\n\n" +
            "EJEMPLOS DE PARES (formato):\n" +
            "  • log(A^n)  ↔  n·log(A)\n" +
            "  • a^m · a^n  ↔  a^(m+n)\n" +
            "  • √(a·b)  ↔  √a · √b\n\n\n" +
            "CRÉDITOS Y FUENTES:\n" +
            "• Integrantes del proyecto: Equipo 5\n" +
            "• Documentación y comunidades: Oracle Java Docs, StackOverflow, Khan Academy\n" +
            "• Recursos académicos: Manuales de Cálculo (varios autores), apuntes del curso\n\n" +
            "═══════════════════════════════════════════════════════\n" +
            "                    ¡BUENA SUERTE! 🍀\n" +
            "═══════════════════════════════════════════════════════";
        
        JTextArea areaTexto = new JTextArea(instrucciones);
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaTexto.setBackground(COLOR_FONDO);
        
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Instrucciones y Créditos",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void mostrarScoreSesion() {
        StringBuilder sb = new StringBuilder();
        sb.append("SCORE - Sesión actual\n\n");
        sb.append("Pares encontrados (acumulado): ").append(StatsManager.getInstance().getParesEncontradosAcumulado()).append("\n");
        sb.append("Partidas jugadas: ").append(StatsManager.getInstance().getPartidasJugadas()).append("\n");
        sb.append("Partidas ganadas (solitario): ").append(StatsManager.getInstance().getPartidasGanadasSolitario()).append("\n");
        sb.append("Partidas ganadas (Jugador1): ").append(StatsManager.getInstance().getPartidasGanadasJugador1()).append("\n");
        sb.append("Partidas ganadas (Jugador2): ").append(StatsManager.getInstance().getPartidasGanadasJugador2()).append("\n\n");
        sb.append("Nota: estadísticas válidas solo para la sesión en curso (reinician al cerrar la app).");
        
        JOptionPane.showMessageDialog(this, sb.toString(), "Score - Sesión", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void salirJuego() {
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de que deseas salir?",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MemoramaCalculoDiferencial juego = new MemoramaCalculoDiferencial();
            juego.setVisible(true);
        });
    }
}

package MemoramaIdentidades;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 * Juego de Memoria con Pares de Expresiones Matemáticas Equivalentes.
 * * Requisitos:
 * - JFrame (ambiente gráfico)
 * - 15 pares de identidades trigonométricas (30 tarjetas)
 * - Cronometrado (5 minutos)
 * - Dos jugadores (J1 y J2)
 * - Lógica de turnos: acierta y repite, falla y cede.
 * - Ganador: quien tenga más pares al final.
 * - Score de pares encontrados (por jugador)
 * - Score de partidas ganadas por sesión (por jugador)
 */
public class JuegoMemoriaMatematico extends JFrame {

    // --- Componentes Gráficos 
    private JPanel panelTablero;
    private JPanel panelInfo;
    private JLabel lblTiempo;
    private JLabel lblTurno;           
    private JLabel lblScores;           
    private JLabel lblPartidasGanadas;  
    private JButton btnIniciar;
    
 
    private JButton[] botones = new JButton[30];   
    private Map<String, String> mapaPares = new HashMap<>();
    private String[] valoresCartas = new String[30];
    private javax.swing.Timer timerJuego; // Temporizador principal del juego
    private int tiempoRestante; // en segundos
    private int partidasGanadasP1 = 0; // Score de sesión J1
    private int partidasGanadasP2 = 0; // Score de sesión J2
    
    // Puntuación de la partida actual
    private int paresP1 = 0;
    private int paresP2 = 0;
    private int jugadorActual = 1; // 1 o 2

    // Variables para manejar la lógica de voltear
    private int indicePrimerBoton = -1; 
    private int indiceSegundoBoton = -1;
    private boolean volteando = false; 

    /**
     * Constructor principal
     */
    public JuegoMemoriaMatematico() {
        setTitle("Juego de Memoria - Trigonométrico (2 Jugadores)");
        setSize(1000, 600); // Tamaño ajustado para 5x6 y 5 etiquetas
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Inicializar la base de datos de pares
        inicializarPares();

        // 2. Crear el panel de información (Norte) 
        panelInfo = new JPanel(new GridLayout(1, 5)); // 1 fila, 5 columnas
        lblTiempo = new JLabel("Tiempo: 05:00", SwingConstants.CENTER);
        
        lblTurno = new JLabel("Turno: J1", SwingConstants.CENTER);
        lblScores = new JLabel("Pares: J1(0) - J2(0)", SwingConstants.CENTER);
        lblPartidasGanadas = new JLabel("Sesión: J1(0) - J2(0)", SwingConstants.CENTER);
        
        btnIniciar = new JButton("Iniciar Partida");
        
        lblTiempo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTurno.setFont(new Font("Arial", Font.BOLD, 14));
        lblTurno.setForeground(Color.BLUE); // Resaltar turno
        lblScores.setFont(new Font("Arial", Font.BOLD, 14));
        lblPartidasGanadas.setFont(new Font("Arial", Font.BOLD, 14));
        
        panelInfo.add(lblTiempo);
        panelInfo.add(lblTurno); 
        panelInfo.add(lblScores); 
        panelInfo.add(lblPartidasGanadas);
        panelInfo.add(btnIniciar);
        add(panelInfo, BorderLayout.NORTH);

        // 3. Crear el panel del tablero (Centro)
        panelTablero = new JPanel(new GridLayout(5, 6, 5, 5)); // 5 filas x 6 columnas
        panelTablero.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < 30; i++) {
            botones[i] = new JButton("");
            botones[i].setFont(new Font("Arial", Font.BOLD, 16)); 
            botones[i].setEnabled(false); 
            
            final int indice = i;
            botones[i].addActionListener(e -> botonPresionado(indice));
            
            panelTablero.add(botones[i]);
        }
        add(panelTablero, BorderLayout.CENTER);

        // 4. Configurar el temporizador del juego
        timerJuego = new javax.swing.Timer(1000, e -> actualizarTiempo());

        // 5. Acción del botón Iniciar
        btnIniciar.addActionListener(e -> iniciarPartida());

        // 6. Configurar el estado inicial
        configurarNuevaPartida();

        setLocationRelativeTo(null); 
        setVisible(true);
    }

    /**
     *los 15 pares de identidades trigonométricas.
     */
    private void inicializarPares() {
        mapaPares.clear();

        // Pares de Cociente (2)
        agregarPar("tan(θ)", "<html>sen(θ)<hr>cos(θ)</html>");
        agregarPar("cot(θ)", "<html>cos(θ)<hr>sen(θ)</html>");

        // Pares Recíprocos (3)
        agregarPar("sec(θ)", "<html>1<hr>cos(θ)</html>");
        agregarPar("csc(θ)", "<html>1<hr>sen(θ)</html>");
        agregarPar("sen(2θ)", "2sen(θ)cos(θ)");

        // Pares Pitagóricos (3)
        agregarPar("sen²(θ) + cos²(θ)", "1");
        agregarPar("1 + tan²(θ)", "sec²(θ)");
        agregarPar("1 + cot²(θ)", "csc²(θ)");

        // Pares de Ángulo Negativo (Par/Impar) (3)
        agregarPar("sen(-θ)", "-sen(θ)");
        agregarPar("cos(-θ)", "cos(θ)");
        agregarPar("tan(-θ)", "-tan(θ)");

        // Pares de Ángulo Doble (2)
        agregarPar("cos(2θ)", "cos²(θ) - sen²(θ)");
        agregarPar("tan(2θ)", "<html>2tan(θ)<hr>1 - tan²(θ)</html>");

        // Pares de Reducción de Potencia (2)
        agregarPar("sen²(θ)", "<html>1 - cos(2θ)<hr>2</html>");
        agregarPar("cos²(θ)", "<html>1 + cos(2θ)<hr>2</html>");
    }


    private void agregarPar(String s1, String s2) {
        mapaPares.put(s1, s2);
        mapaPares.put(s2, s1);
    }

    /**
     * Resetea el tablero y las variables para una nueva partida.
     */
    private void configurarNuevaPartida() {
        timerJuego.stop();
        tiempoRestante = 300; // 5 minutos = 300 segundos
        
        // --- Resetear variables de 2 Jugadores ---
        paresP1 = 0;
        paresP2 = 0;
        jugadorActual = 1; // J1 siempre empieza la nueva partida
        // ------------------------------------------

        indicePrimerBoton = -1;
        indiceSegundoBoton = -1;
        volteando = false;

        actualizarEtiquetas(); 

        // Crear la lista de 30 cartas (15 pares)
        ArrayList<String> cartas = new ArrayList<>();
        Set<String> keysUsadas = new HashSet<>();
        
        for (String key : mapaPares.keySet()) {
            if (!keysUsadas.contains(key) && !keysUsadas.contains(mapaPares.get(key))) {
                cartas.add(key);
                cartas.add(mapaPares.get(key));
                keysUsadas.add(key);
                keysUsadas.add(mapaPares.get(key));
            }
        }

        // Barajar las cartas
        Collections.shuffle(cartas);

        // Asignar valores barajados a los botones (lógica interna)
        for (int i = 0; i < 30; i++) {
            // Manejo de error por si la lista no es de 30
            if (i < cartas.size()) {
                valoresCartas[i] = cartas.get(i);
            } else {
                // Esto no debería pasar si hay 15 pares únicos
                valoresCartas[i] = "ERROR"; 
            }
            botones[i].setText(""); 
            botones[i].setEnabled(false); 
            botones[i].setBackground(null); 
        }

        btnIniciar.setText("Iniciar Partida");
        btnIniciar.setEnabled(true);
    }

    /**
     * Acción que se dispara al presionar "Iniciar Partida".
     */
    private void iniciarPartida() {
        for (JButton btn : botones) {
            btn.setEnabled(true);
        }
        
        btnIniciar.setText("Partida en curso...");
        btnIniciar.setEnabled(false);
        
        timerJuego.start();
    }

    /**
     * Lógica que se ejecuta cada segundo (tick del timer).
     */
    private void actualizarTiempo() {
        tiempoRestante--;
        actualizarEtiquetas();

        if (tiempoRestante <= 0) {
            timerJuego.stop();
            juegoTerminado(false); // 'false' = no se completó el tablero
        }
    }

    /**
     * Refresca las etiquetas de tiempo y puntuación (MODIFICADO para 2 Jugadores).
     */
    private void actualizarEtiquetas() {
        int minutos = tiempoRestante / 60;
        int segundos = tiempoRestante % 60;
        lblTiempo.setText(String.format("Tiempo: %02d:%02d", minutos, segundos));
        
        lblTurno.setText("Turno: J" + jugadorActual);
        // Cambiar color para indicar el turno
        lblTurno.setForeground(jugadorActual == 1 ? Color.BLUE : Color.RED);
        
        lblScores.setText(String.format("Pares: J1(%d) - J2(%d)", paresP1, paresP2));
        lblPartidasGanadas.setText(String.format("Sesión: J1(%d) - J2(%d)", partidasGanadasP1, partidasGanadasP2));
    }

    /**
     * Lógica principal al hacer clic en un botón (tarjeta).
     */
    private void botonPresionado(int indice) {
        if (volteando || !botones[indice].isEnabled() || indice == indicePrimerBoton) {
            return;
        }

        botones[indice].setText(valoresCartas[indice]);

        if (indicePrimerBoton == -1) {
            indicePrimerBoton = indice;
        } else {
            indiceSegundoBoton = indice;
            volteando = true;
            verificarPar();
        }
    }

    /**
     * Comprueba si las dos tarjetas volteadas son un par equivalente.
     */
    private void verificarPar() {
        String valor1 = valoresCartas[indicePrimerBoton];
        String valor2 = valoresCartas[indiceSegundoBoton];

        if (mapaPares.get(valor1) != null && mapaPares.get(valor1).equals(valor2)) {
            esPar();
        } else {
            noEsPar();
        }
    }

    /**
     * Acciones si las tarjetas SON un par (MODIFICADO para 2 Jugadores).
     */
    private void esPar() {
        botones[indicePrimerBoton].setEnabled(false);
        botones[indiceSegundoBoton].setEnabled(false);
        botones[indicePrimerBoton].setBackground(new Color(144, 238, 144)); 
        botones[indiceSegundoBoton].setBackground(new Color(144, 238, 144));
        
        // Sumar punto al jugador actual
        if (jugadorActual == 1) {
            paresP1++;
        } else {
            paresP2++;
        }
        
        // REGLA: El jugador que acierta, repite turno.
        // Por lo tanto, NO llamamos a cambiarTurno().
        
        actualizarEtiquetas();

        // Resetear para el siguiente turno
        indicePrimerBoton = -1;
        indiceSegundoBoton = -1;
        volteando = false; 

        // Comprobar si se acabaron las cartas
        int totalPares = paresP1 + paresP2;
        if (totalPares == 15) {
            timerJuego.stop();
            juegoTerminado(true); // 'true' = sí se completó el tablero
        }
    }

    /**
     * Acciones si las tarjetas NO son un par 
     */
    private void noEsPar() {
        // Timer corto para que el jugador vea las cartas
        Timer delayTimer = new Timer(1200, e -> {
            botones[indicePrimerBoton].setText(""); 
            botones[indiceSegundoBoton].setText("");
            
            // Resetear
            indicePrimerBoton = -1;
            indiceSegundoBoton = -1;
            volteando = false; 
            
            // REGLA: El jugador que falla, cede el turno.
            cambiarTurno();
        });
        
        delayTimer.setRepeats(false); 
        delayTimer.start();
    }

    /**
     * Cambia el turno del jugador y actualiza la UI.
     */
    private void cambiarTurno() {
        jugadorActual = (jugadorActual == 1) ? 2 : 1;
        actualizarEtiquetas();
    }

    /**
     * Lógica de fin de juego
     */
    private void juegoTerminado(boolean tableroCompletado) {
        String mensaje;
        int ganador = 0; // 0 = empate, 1 = P1, 2 = P2

        // 1. Determinar quién ganó la partida
        if (paresP1 > paresP2) {
            ganador = 1;
            partidasGanadasP1++;
        } else if (paresP2 > paresP1) {
            ganador = 2;
            partidasGanadasP2++;
        }

        // 2. Construir el mensaje final
        if (tableroCompletado) {
            mensaje = "¡Tablero completado!\n";
        } else {
            mensaje = "¡Se acabó el tiempo!\n";
        }

        mensaje += String.format("Puntaje Final: J1 (%d) - J2 (%d)\n", paresP1, paresP2);

        if (ganador == 1) {
            mensaje += "¡Gana el Jugador 1!";
        } else if (ganador == 2) {
            mensaje += "¡Gana el Jugador 2!";
        } else {
            mensaje += "¡Es un empate!";
        }
        
        JOptionPane.showMessageDialog(this, mensaje, "Fin de la Partida", JOptionPane.INFORMATION_MESSAGE);
        
        // 3. Actualizar la etiqueta de sesión (antes de resetear)
        actualizarEtiquetas(); 
        
        // 4. Preparar la siguiente partida
        configurarNuevaPartida();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JuegoMemoriaMatematico());
    }
}
package MemoramaLogaritmos;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class VentanaJuego extends JFrame {
    
    private JFrame ventanaMenu;
    
    private int numJugadores;
    private int tiempoTotalSegundos;
    private int filas;
    private int columnas;
    private int totalPares;
    private int paresMinimosRequeridos;
    
    private Tarjeta[][] tablero;
    private Tarjeta primeraTarjeta;
    private Tarjeta segundaTarjeta;
    
    private boolean esperandoVoltear;
    private int turnoActual;
    private int[] puntajes;
    private ArrayList<Long> tiemposJugadas;
    private int jugadaActual;
    private boolean juegoActivo;
    private boolean tiempoExtra;
    
    private Timer temporizadorPartida;
    private Timer temporizadorJugada;
    private int tiempoRestanteSegundos;
    private long inicioJugada;
    
    private JPanel panelTablero;
    private JPanel panelInfo;
    private JLabel lblTiempo;
    private JLabel lblTurno;
    private JLabel lblPuntajeJ1;
    private JLabel lblPuntajeJ2;
    private JLabel lblTiempoJugada;
    private JProgressBar barraProgreso;
    private JButton btnPausa;
    private JButton btnSalir;
    
    private final Color COLOR_FONDO = new Color(240, 248, 255);
    private final Color COLOR_PANEL = new Color(52, 73, 94);
    private final Color COLOR_J1 = new Color(52, 152, 219);
    private final Color COLOR_J2 = new Color(231, 76, 60);
    
    public VentanaJuego(JFrame ventanaMenu, int numJugadores, int tiempoMinutos, 
                        int filas, int columnas) {
        this.ventanaMenu = ventanaMenu;
        this.numJugadores = numJugadores;
        // Forzar máximo de 5 minutos
        int minutos = Math.min(tiempoMinutos, 5);
        this.tiempoTotalSegundos = minutos * 60;
        this.tiempoRestanteSegundos = tiempoTotalSegundos;
        this.filas = filas;
        this.columnas = columnas;
        this.totalPares = (filas * columnas) / 2;
        // Ahora, por instrucción: el jugador debe encontrar TODOS los pares para ganar en modo solitario
        this.paresMinimosRequeridos = this.totalPares;
        
        this.puntajes = new int[2];
        this.tiemposJugadas = new ArrayList<>();
        this.jugadaActual = 0;
        
        this.turnoActual = 0;
        this.esperandoVoltear = false;
        this.juegoActivo = true;
        this.tiempoExtra = false;
        
        configurarVentana();
        crearInterfaz();
        inicializarTablero();
        iniciarTemporizadores();
        
        // Registrar partida iniciada en estadísticas de sesión
        StatsManager.getInstance().registrarPartida();
    }
    
    private void configurarVentana() {
        setTitle("Memorama - Propiedades Log/Exp/Rad - Jugando");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pausarJuego();
            }
        });
    }
    
    private void crearInterfaz() {
        setLayout(new BorderLayout(10, 10));
        crearPanelInformacion();
        crearPanelTablero();
        add(panelInfo, BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
    }
    
    private void crearPanelInformacion() {
        panelInfo = new JPanel();
        panelInfo.setLayout(new GridLayout(numJugadores == 1 ? 4 : 5, 1, 5, 5));
        panelInfo.setBackground(COLOR_PANEL);
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        lblTiempo = new JLabel("⏱️ Tiempo: " + formatearTiempo(tiempoRestanteSegundos), SwingConstants.CENTER);
        lblTiempo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTiempo.setForeground(Color.WHITE);
        
        barraProgreso = new JProgressBar(0, tiempoTotalSegundos);
        barraProgreso.setValue(tiempoRestanteSegundos);
        barraProgreso.setStringPainted(true);
        barraProgreso.setForeground(new Color(46, 204, 113));
        
        lblTiempoJugada = new JLabel("⏲️ Tiempo jugada: 0.0s", SwingConstants.CENTER);
        lblTiempoJugada.setFont(new Font("Arial", Font.PLAIN, 16));
        lblTiempoJugada.setForeground(Color.WHITE);
        
        if (numJugadores == 1) {
            lblPuntajeJ1 = new JLabel("🎯 Pares encontrados: 0 / " + totalPares, SwingConstants.CENTER);
        } else {
            lblTurno = new JLabel("Turno: Jugador 1", SwingConstants.CENTER);
            lblTurno.setFont(new Font("Arial", Font.BOLD, 18));
            lblTurno.setForeground(COLOR_J1);
            
            lblPuntajeJ1 = new JLabel("👤 Jugador 1: 0 pares", SwingConstants.CENTER);
            lblPuntajeJ2 = new JLabel("👤 Jugador 2: 0 pares", SwingConstants.CENTER);
            lblPuntajeJ2.setForeground(Color.WHITE);
        }
        
        lblPuntajeJ1.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPuntajeJ1.setForeground(Color.WHITE);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelBotones.setBackground(COLOR_PANEL);
        
        btnPausa = new JButton("⏸️ Pausar");
        btnPausa.setFont(new Font("Arial", Font.BOLD, 14));
        btnPausa.addActionListener(e -> pausarJuego());
        
        btnSalir = new JButton("🚪 Salir");
        btnSalir.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalir.addActionListener(e -> confirmarSalida());
        
        panelBotones.add(btnPausa);
        panelBotones.add(btnSalir);
        
        panelInfo.add(lblTiempo);
        panelInfo.add(barraProgreso);
        panelInfo.add(lblTiempoJugada);
        
        if (numJugadores == 2) {
            panelInfo.add(lblTurno);
        }
        
        panelInfo.add(lblPuntajeJ1);
        
        if (numJugadores == 2) {
            panelInfo.add(lblPuntajeJ2);
        }
    }
    
    private void crearPanelTablero() {
        panelTablero = new JPanel();
        panelTablero.setLayout(new GridLayout(filas, columnas, 8, 8));
        panelTablero.setBackground(COLOR_FONDO);
        panelTablero.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
    
    private void inicializarTablero() {
        String[][] pares = ParCalculoDiferencial.obtenerParesAleatorios(totalPares);
        ArrayList<Tarjeta> listaTarjetas = new ArrayList<>();
        
        for (int i = 0; i < pares.length; i++) {
            listaTarjetas.add(new Tarjeta(pares[i][0], i));
            listaTarjetas.add(new Tarjeta(pares[i][1], i));
        }
        
        Collections.shuffle(listaTarjetas);
        tablero = new Tarjeta[filas][columnas];
        int indice = 0;
        
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Tarjeta tarjeta = listaTarjetas.get(indice);
                tablero[i][j] = tarjeta;
                tarjeta.addActionListener(e -> manejarClicTarjeta(tarjeta));
                panelTablero.add(tarjeta);
                indice++;
            }
        }
    }
    
    private void manejarClicTarjeta(Tarjeta tarjeta) {
        if (!juegoActivo || esperandoVoltear || tarjeta.estaDescubierta()) {
            return;
        }
        
        if (primeraTarjeta == null) {
            inicioJugada = System.currentTimeMillis();
        }
        
        tarjeta.voltear();
        
        if (primeraTarjeta == null) {
            primeraTarjeta = tarjeta;
        } else if (segundaTarjeta == null && tarjeta != primeraTarjeta) {
            segundaTarjeta = tarjeta;
            esperandoVoltear = true;
            
            long tiempoJugada = System.currentTimeMillis() - inicioJugada;
            tiemposJugadas.add(tiempoJugada);
            jugadaActual++;
            
            verificarPar();
        }
    }
    
    private void verificarPar() {
        // Bloquear interacción mientras se verifica
        bloquearTablero(true);
        
        Timer timer = new Timer(700, e -> {
            if (primeraTarjeta.getIdPar() == segundaTarjeta.getIdPar()) {
                primeraTarjeta.marcarEmparejada();
                segundaTarjeta.marcarEmparejada();
                
                // Actualizar puntaje
                puntajes[turnoActual]++;
                actualizarPuntajes();
                
                // Actualizar estadísticas de sesión
                StatsManager.getInstance().agregarParesEncontrados(2);
                
                // Reset selección
                primeraTarjeta = null;
                segundaTarjeta = null;
                esperandoVoltear = false;
                
                // Verificar si alguien encontró todos los pares
                if (puntajes[0] + puntajes[1] == totalPares) {
                    // Determinar ganador inmediatamente
                    finalizarJuego();
                } else {
                    // En modo pareja, el mismo jugador sigue; en solitario continúa
                    bloquearTablero(false);
                }
            } else {
                primeraTarjeta.mostrarError();
                segundaTarjeta.mostrarError();
                
                Timer timerOcultar = new Timer(500, ev -> {
                    primeraTarjeta.restaurarColor();
                    segundaTarjeta.restaurarColor();
                    
                    primeraTarjeta.ocultar();
                    segundaTarjeta.ocultar();
                    
                    primeraTarjeta = null;
                    segundaTarjeta = null;
                    esperandoVoltear = false;
                    
                    if (numJugadores == 2) {
                        cambiarTurno();
                    }
                    bloquearTablero(false);
                });
                timerOcultar.setRepeats(false);
                timerOcultar.start();
            }
        });
        
        timer.setRepeats(false);
        timer.start();
    }
    
    private void cambiarTurno() {
        turnoActual = (turnoActual + 1) % 2;
        
        if (turnoActual == 0) {
            lblTurno.setText("Turno: Jugador 1");
            lblTurno.setForeground(COLOR_J1);
        } else {
            lblTurno.setText("Turno: Jugador 2");
            lblTurno.setForeground(COLOR_J2);
        }
    }
    
    private void actualizarPuntajes() {
        if (numJugadores == 1) {
            lblPuntajeJ1.setText("🎯 Pares encontrados: " + puntajes[0] + " / " + totalPares);
        } else {
            lblPuntajeJ1.setText("👤 Jugador 1: " + puntajes[0] + " pares");
            lblPuntajeJ2.setText("👤 Jugador 2: " + puntajes[1] + " pares");
        }
    }
    
    private void iniciarTemporizadores() {
        temporizadorPartida = new Timer(1000, e -> {
            tiempoRestanteSegundos--;
            lblTiempo.setText("⏱️ Tiempo: " + formatearTiempo(tiempoRestanteSegundos));
            barraProgreso.setValue(tiempoRestanteSegundos);
            
            if (tiempoRestanteSegundos < 60) {
                barraProgreso.setForeground(Color.RED);
            } else if (tiempoRestanteSegundos < 180) {
                barraProgreso.setForeground(Color.ORANGE);
            }
            
            if (tiempoRestanteSegundos <= 0) {
                temporizadorPartida.stop();
                verificarFinJuego();
            }
        });
        temporizadorPartida.start();
        
        temporizadorJugada = new Timer(100, e -> {
            if (primeraTarjeta != null && !esperandoVoltear) {
                long tiempoActual = System.currentTimeMillis() - inicioJugada;
                lblTiempoJugada.setText("⏲️ Tiempo jugada: " + String.format("%.1f", tiempoActual / 1000.0) + "s");
            }
        });
        temporizadorJugada.start();
    }
    
    private void verificarFinJuego() {
        if (numJugadores == 1) {
            finalizarJuego();
        } else {
            if (puntajes[0] == puntajes[1]) {
                if (!tiempoExtra) {
                    mostrarTiempoExtra();
                } else {
                    finalizarJuego();
                }
            } else {
                finalizarJuego();
            }
        }
    }
    
    private void mostrarTiempoExtra() {
        temporizadorPartida.stop();
        temporizadorJugada.stop();
        
        JOptionPane.showMessageDialog(
            this,
            "¡Hay un empate!\n" +
            "Se agregarán 5 minutos adicionales.\n" +
            "Si persiste el empate, ambos jugadores pierden.",
            "Tiempo Extra",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        tiempoExtra = true;
        tiempoRestanteSegundos = 5 * 60;
        tiempoTotalSegundos = tiempoRestanteSegundos;
        barraProgreso.setMaximum(tiempoTotalSegundos);
        barraProgreso.setValue(tiempoRestanteSegundos);
        barraProgreso.setForeground(new Color(255, 165, 0));
        
        temporizadorPartida.start();
        temporizadorJugada.start();
    }
    
    private void finalizarJuego() {
        juegoActivo = false;
        temporizadorPartida.stop();
        temporizadorJugada.stop();
        
        double tiempoPromedioJugada = calcularTiempoPromedio();
        
        // Actualizar estadísticas de victorias
        if (numJugadores == 1) {
            if (puntajes[0] == totalPares) {
                StatsManager.getInstance().registrarVictoriaSolitario();
            }
            // registrar pares encontrados acumulados ya se hace durante el juego
        } else {
            if (puntajes[0] > puntajes[1]) {
                StatsManager.getInstance().registrarVictoriaJugador1();
            } else if (puntajes[1] > puntajes[0]) {
                StatsManager.getInstance().registrarVictoriaJugador2();
            }
        }
        
        String mensaje = generarMensajeFinal(tiempoPromedioJugada);
        
        int opcion = JOptionPane.showConfirmDialog(
            this,
            mensaje,
            "Fin del Juego",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (opcion == JOptionPane.YES_OPTION) {
            volverAlMenu();
        } else {
            System.exit(0);
        }
    }
    
    private String generarMensajeFinal(double tiempoPromedio) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("═══════════════════════════════════════\n");
        mensaje.append("           RESULTADOS FINALES\n");
        mensaje.append("═══════════════════════════════════════\n\n");
        
        if (numJugadores == 1) {
            mensaje.append("🎯 Pares encontrados: ").append(puntajes[0]).append(" / ").append(totalPares).append("\n\n");
            if (puntajes[0] == totalPares) {
                mensaje.append("🏆 ¡FELICIDADES! ¡HAS GANADO! 🏆\n");
            } else {
                mensaje.append("😔 No encontraste todos los pares dentro del tiempo.\n");
            }
        } else {
            mensaje.append("👤 Jugador 1: ").append(puntajes[0]).append(" pares\n");
            mensaje.append("👤 Jugador 2: ").append(puntajes[1]).append(" pares\n\n");
            if (puntajes[0] > puntajes[1]) {
                mensaje.append("🏆 ¡GANADOR: JUGADOR 1! 🏆\n");
            } else if (puntajes[1] > puntajes[0]) {
                mensaje.append("🏆 ¡GANADOR: JUGADOR 2! 🏆\n");
            } else {
                mensaje.append("⚠️ EMPATE\n");
            }
        }
        
        mensaje.append("\n⏱️ Tiempo promedio por jugada: ").append(String.format("%.2f", tiempoPromedio)).append("s\n");
        mensaje.append("\n--- Estadísticas de la sesión ---\n");
        mensaje.append("Pares encontrados (acumulado): ").append(StatsManager.getInstance().getParesEncontradosAcumulado()).append("\n");
        mensaje.append("Partidas jugadas: ").append(StatsManager.getInstance().getPartidasJugadas()).append("\n");
        mensaje.append("Partidas ganadas (solitario): ").append(StatsManager.getInstance().getPartidasGanadasSolitario()).append("\n");
        mensaje.append("Partidas ganadas (Jugador1): ").append(StatsManager.getInstance().getPartidasGanadasJugador1()).append("\n");
        mensaje.append("Partidas ganadas (Jugador2): ").append(StatsManager.getInstance().getPartidasGanadasJugador2()).append("\n");
        mensaje.append("═══════════════════════════════════════\n\n");
        mensaje.append("¿Deseas volver al menú principal?");
        
        return mensaje.toString();
    }
    
    private double calcularTiempoPromedio() {
        if (tiemposJugadas.isEmpty()) return 0.0;
        long suma = 0;
        for (Long t : tiemposJugadas) suma += t;
        return (suma / (double) tiemposJugadas.size()) / 1000.0;
    }
    
    private void pausarJuego() {
        temporizadorPartida.stop();
        temporizadorJugada.stop();
        
        int opcion = JOptionPane.showOptionDialog(
            this,
            "Juego en pausa\n¿Qué deseas hacer?",
            "Pausa",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[]{"Continuar", "Salir al Menú", "Salir del Juego"},
            "Continuar"
        );
        
        if (opcion == 0) {
            temporizadorPartida.start();
            temporizadorJugada.start();
        } else if (opcion == 1) {
            volverAlMenu();
        } else if (opcion == 2) {
            System.exit(0);
        } else {
            // si cerró el diálogo, continuar
            temporizadorPartida.start();
            temporizadorJugada.start();
        }
    }
    
    private void confirmarSalida() {
        pausarJuego();
    }
    
    private void volverAlMenu() {
        this.dispose();
        ventanaMenu.setVisible(true);
    }
    
    private String formatearTiempo(int segundos) {
        int minutos = segundos / 60;
        int segs = segundos % 60;
        return String.format("%02d:%02d", minutos, segs);
    }
    
    private void bloquearTablero(boolean bloquear) {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                tablero[i][j].setEnabled(!bloquear && !tablero[i][j].estaEmparejada());
            }
        }
    }
}

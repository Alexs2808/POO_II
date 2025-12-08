/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package CruceVial;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class SimulacionCruce extends JFrame {

    public SimulacionCruce() {
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        PanelCruce panel = new PanelCruce();
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulacionCruce frame = new SimulacionCruce();
            frame.setVisible(true);
        });
    }
}

/**
 * Panel principal que maneja la animación y el pintado.
 */
class PanelCruce extends JPanel implements ActionListener {

    // --- Constantes de Diseño ---
    private final int ANCHO_CALLE = 300;
    private final int ANCHO_CARRIL = ANCHO_CALLE / 2;
    
    // --- Estados del Semáforo ---
    // 0: Vertical VERDE, Horizontal ROJO
    // 1: Vertical AMARILLO, Horizontal ROJO
    // 2: Vertical ROJO, Horizontal VERDE
    // 3: Vertical ROJO, Horizontal AMARILLO
    private int estadoSemaforo = 0;
    
    // Tiempos (en ciclos del timer)
    private int contadorTiempo = 0;
    private final int DURACION_VERDE = 150;    // ~5 segundos
    private final int DURACION_AMARILLO = 40;  // ~1.5 segundos

    // --- Vehículos ---
    private ArrayList<Vehiculo> vehiculos;
    private Timer timer;
    private Random random;

    public PanelCruce() {
        setBackground(new Color(34, 139, 34)); // Color césped
        vehiculos = new ArrayList<>();
        random = new Random();
        
        // Timer principal: actualiza la simulación cada 30ms (aprox 33 FPS)
        timer = new Timer(30, this);
        timer.start();
        
        // Emitir sonido inicial
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actualizarSemaforos();
        generarTrafico();
        moverVehiculos();
        repaint(); // Vuelve a dibujar todo
    }

    /**
     * Lógica de la máquina de estados del semáforo.
     */
    private void actualizarSemaforos() {
        contadorTiempo++;
        
        // Lógica de cambio de estado
        if (estadoSemaforo == 0 && contadorTiempo > DURACION_VERDE) {
            cambiarEstado(1); // A amarillo vertical
        } else if (estadoSemaforo == 1 && contadorTiempo > DURACION_AMARILLO) {
            cambiarEstado(2); // A verde horizontal (Rojo vertical)
        } else if (estadoSemaforo == 2 && contadorTiempo > DURACION_VERDE) {
            cambiarEstado(3); // A amarillo horizontal
        } else if (estadoSemaforo == 3 && contadorTiempo > DURACION_AMARILLO) {
            cambiarEstado(0); // A verde vertical
        }
    }

    private void cambiarEstado(int nuevoEstado) {
        estadoSemaforo = nuevoEstado;
        contadorTiempo = 0;
        
        // Alerta sonora cada vez que hay un VERDE
        if (estadoSemaforo == 0 || estadoSemaforo == 2) {
            // Usamos el toolkit del sistema para un 'beep' genérico
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /**
     * Genera coches aleatoriamente en los bordes.
     */
    private void generarTrafico() {
        if (random.nextInt(100) < 3) { // 3% de probabilidad por frame
            int direccion = random.nextInt(4); // 0:Norte, 1:Sur, 2:Este, 3:Oeste
            crearVehiculo(direccion);
        }
    }

    private void crearVehiculo(int dir) {
        int w = getWidth();
        int h = getHeight();
        int centroX = w / 2;
        int centroY = h / 2;
        
        // Coordenadas de inicio según dirección para doble sentido
        // Norte: Viene de abajo hacia arriba (carril derecho vertical)
        // Sur: Viene de arriba hacia abajo (carril izquierdo vertical)
        // Este: Viene de izq a der (carril inferior horizontal)
        // Oeste: Viene de der a izq (carril superior horizontal)
        
        Vehiculo v = null;
        
        switch(dir) {
            case 0: // Hacia Norte (subiendo)
                v = new Vehiculo(centroX + ANCHO_CARRIL/2 - 15, h + 50, 0); 
                break;
            case 1: // Hacia Sur (bajando)
                v = new Vehiculo(centroX - ANCHO_CARRIL/2 - 15, -50, 1);
                break;
            case 2: // Hacia Este (derecha)
                v = new Vehiculo(-50, centroY + ANCHO_CARRIL/2 - 15, 2);
                break;
            case 3: // Hacia Oeste (izquierda)
                v = new Vehiculo(w + 50, centroY - ANCHO_CARRIL/2 - 15, 3);
                break;
        }
        
        // Verificar que no aparezca encima de otro (anti-solapamiento básico)
        boolean libre = true;
        for(Vehiculo existente : vehiculos) {
            if (existente.direccion == dir && v.distancia(existente) < 60) {
                libre = false;
                break;
            }
        }
        
        if (libre) vehiculos.add(v);
    }

    /**
     * Actualiza posiciones y gestiona frenado en semáforos.
     */
    private void moverVehiculos() {
        int w = getWidth();
        int h = getHeight();
        int centroX = w / 2;
        int centroY = h / 2;
        
        // Límites de parada (antes del cruce)
        int stopNorte = centroY + ANCHO_CALLE/2 + 10;
        int stopSur = centroY - ANCHO_CALLE/2 - 10;
        int stopEste = centroX - ANCHO_CALLE/2 - 10;
        int stopOeste = centroX + ANCHO_CALLE/2 + 10;

        Iterator<Vehiculo> it = vehiculos.iterator();
        while(it.hasNext()) {
            Vehiculo v = it.next();
            boolean debeFrenar = false;

            // 1. Lógica de Semáforo
            // Si es dirección vertical (0, 1)
            if (v.direccion == 0 || v.direccion == 1) {
                // Si el semáforo NO es verde (es decir, estado 2 o 3, o amarillo 1 si está cerca)
                boolean luzRoja = (estadoSemaforo == 2 || estadoSemaforo == 3);
                boolean luzAmarilla = (estadoSemaforo == 1);
                
                if (luzRoja || (luzAmarilla && !v.haCruzadoLinea)) {
                    // Verificar si está en la línea de parada
                    if (v.direccion == 0 && v.y > stopNorte && v.y < stopNorte + 60) debeFrenar = true;
                    if (v.direccion == 1 && v.y < stopSur && v.y > stopSur - 60) debeFrenar = true;
                }
            }
            // Si es dirección horizontal (2, 3)
            else {
                boolean luzRoja = (estadoSemaforo == 0 || estadoSemaforo == 1);
                boolean luzAmarilla = (estadoSemaforo == 3);
                
                if (luzRoja || (luzAmarilla && !v.haCruzadoLinea)) {
                    if (v.direccion == 2 && v.x < stopEste && v.x > stopEste - 60) debeFrenar = true;
                    if (v.direccion == 3 && v.x > stopOeste && v.x < stopOeste + 60) debeFrenar = true;
                }
            }

            // Marcar si ya cruzó para no frenar en amarillo si ya pasó
            if (v.direccion == 0 && v.y < stopNorte) v.haCruzadoLinea = true;
            if (v.direccion == 1 && v.y > stopSur) v.haCruzadoLinea = true;
            if (v.direccion == 2 && v.x > stopEste) v.haCruzadoLinea = true;
            if (v.direccion == 3 && v.x < stopOeste) v.haCruzadoLinea = true;

            // 2. Lógica de Choque (Coche enfrente)
            for (Vehiculo otro : vehiculos) {
                if (v != otro && v.direccion == otro.direccion) {
                    // Si el otro está adelante y muy cerca
                    if (v.estaDetrasDe(otro) && v.distancia(otro) < 50) {
                        debeFrenar = true;
                    }
                }
            }

            if (debeFrenar) {
                v.velocidadActual = 0;
            } else {
                v.velocidadActual = v.velocidadMax;
            }

            v.mover();

            // Eliminar si sale de pantalla
            if (v.fueraDeLimites(w, h)) {
                it.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        // 1. Dibujar Carreteras (Gris)
        g2.setColor(Color.GRAY);
        g2.fillRect(cx - ANCHO_CALLE/2, 0, ANCHO_CALLE, h); // Vertical
        g2.fillRect(0, cy - ANCHO_CALLE/2, w, ANCHO_CALLE); // Horizontal

        // 2. Dibujar Líneas (Amarillas dobles centro, Blancas discontinuas carriles)
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.YELLOW);
        g2.drawLine(cx, 0, cx, cy - ANCHO_CALLE/2); // Vert Arriba
        g2.drawLine(cx, cy + ANCHO_CALLE/2, cx, h); // Vert Abajo
        g2.drawLine(0, cy, cx - ANCHO_CALLE/2, cy); // Horiz Izq
        g2.drawLine(cx + ANCHO_CALLE/2, cy, w, cy); // Horiz Der
        
        // Líneas de parada (Blancas gruesas)
        g2.setStroke(new BasicStroke(4));
        g2.setColor(Color.WHITE);
        // Norte (para los que suben)
        g2.drawLine(cx, cy + ANCHO_CALLE/2, cx + ANCHO_CALLE/2, cy + ANCHO_CALLE/2);
        // Sur (para los que bajan)
        g2.drawLine(cx - ANCHO_CALLE/2, cy - ANCHO_CALLE/2, cx, cy - ANCHO_CALLE/2);
        // Este (para los que van derecha)
        g2.drawLine(cx - ANCHO_CALLE/2, cy, cx - ANCHO_CALLE/2, cy + ANCHO_CALLE/2);
        // Oeste (para los que van izquierda)
        g2.drawLine(cx + ANCHO_CALLE/2, cy - ANCHO_CALLE/2, cx + ANCHO_CALLE/2, cy);

        // 3. Dibujar Semáforos
        // Determinamos colores basados en estado
        Color vColorRojo = Color.GRAY, vColorAmarillo = Color.GRAY, vColorVerde = Color.GRAY;
        Color hColorRojo = Color.GRAY, hColorAmarillo = Color.GRAY, hColorVerde = Color.GRAY;
        
        switch (estadoSemaforo) {
            case 0: // Vertical VERDE, Horizontal ROJO
                vColorVerde = Color.GREEN;
                hColorRojo = Color.RED;
                break;
            case 1: // Vertical AMARILLO, Horizontal ROJO
                vColorAmarillo = Color.YELLOW;
                hColorRojo = Color.RED;
                break;
            case 2: // Vertical ROJO, Horizontal VERDE
                vColorRojo = Color.RED;
                hColorVerde = Color.GREEN;
                break;
            case 3: // Vertical ROJO, Horizontal AMARILLO
                vColorRojo = Color.RED;
                hColorAmarillo = Color.YELLOW;
                break;
        }

        // Dibujar los semáforos
        // Semáforos para tráfico Vertical (Norte/Sur)
        dibujarSemaforo(g2, cx + ANCHO_CALLE/2 + 10, cy + ANCHO_CALLE/2 + 10, vColorRojo, vColorAmarillo, vColorVerde); // Esquina Inf Der
        dibujarSemaforo(g2, cx - ANCHO_CALLE/2 - 30, cy - ANCHO_CALLE/2 - 70, vColorRojo, vColorAmarillo, vColorVerde); // Esquina Sup Izq
        
        // Semáforos para tráfico Horizontal (Este/Oeste)
        dibujarSemaforo(g2, cx - ANCHO_CALLE/2 - 50, cy + ANCHO_CALLE/2 + 10, hColorRojo, hColorAmarillo, hColorVerde); // Esquina Inf Izq
        dibujarSemaforo(g2, cx + ANCHO_CALLE/2 + 10, cy - ANCHO_CALLE/2 - 70, hColorRojo, hColorAmarillo, hColorVerde); // Esquina Sup Der

        // 4. Dibujar Vehículos
        for (Vehiculo v : vehiculos) {
            v.dibujar(g2);
        }
    }

    /**
     * Dibuja una caja de semáforo con tres luces: Rojo, Amarillo y Verde.
     */
    private void dibujarSemaforo(Graphics2D g2, int x, int y, Color cRojo, Color cAmarillo, Color cVerde) {
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, 20, 60);
        
        // Luz Roja (arriba)
        g2.setColor(cRojo);
        g2.fillOval(x + 2, y + 5, 16, 16);
        
        // Luz Amarilla (medio)
        g2.setColor(cAmarillo);
        g2.fillOval(x + 2, y + 22, 16, 16);
        
        // Luz Verde (abajo)
        g2.setColor(cVerde);
        g2.fillOval(x + 2, y + 39, 16, 16);
        
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(x, y, 20, 60);
    }
}

/**
 * Clase que representa un coche.
 */
class Vehiculo {
    double x, y;
    int direccion; // 0:Norte, 1:Sur, 2:Este, 3:Oeste
    double velocidadMax;
    double velocidadActual;
    Color color;
    boolean haCruzadoLinea = false;

    public Vehiculo(int startX, int startY, int dir) {
        this.x = startX;
        this.y = startY;
        this.direccion = dir;
        this.velocidadMax = 3 + Math.random() * 2; // Velocidad aleatoria
        this.velocidadActual = velocidadMax;
        
        // Color aleatorio
        Random r = new Random();
        this.color = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

    public void mover() {
        switch(direccion) {
            case 0: y -= velocidadActual; break; // Norte (y disminuye)
            case 1: y += velocidadActual; break; // Sur (y aumenta)
            case 2: x += velocidadActual; break; // Este (x aumenta)
            case 3: x -= velocidadActual; break; // Oeste (x disminuye)
        }
    }

    public void dibujar(Graphics2D g2) {
        g2.setColor(color);
        // Dibujar coche como rectángulo
        if (direccion == 0 || direccion == 1) {
            g2.fillRoundRect((int)x, (int)y, 30, 50, 5, 5); // Vertical
        } else {
            g2.fillRoundRect((int)x, (int)y, 50, 30, 5, 5); // Horizontal
        }
    }

    public double distancia(Vehiculo otro) {
        return Math.sqrt(Math.pow(this.x - otro.x, 2) + Math.pow(this.y - otro.y, 2));
    }

    public boolean estaDetrasDe(Vehiculo otro) {
        switch(direccion) {
            case 0: return this.y > otro.y;
            case 1: return this.y < otro.y;
            case 2: return this.x < otro.x;
            case 3: return this.x > otro.x;
            default: return false;
        }
    }

    public boolean fueraDeLimites(int w, int h) {
        return (x < -100 || x > w + 100 || y < -100 || y > h + 100);
    }
}
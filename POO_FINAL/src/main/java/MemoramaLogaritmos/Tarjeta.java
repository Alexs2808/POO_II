package MemoramaLogaritmos;

import javax.swing.*;
import java.awt.*;

public class Tarjeta extends JButton {
    
    // Atributos de la tarjeta
    private String contenido;           // Función o derivada que representa
    private int idPar;                  // ID del par al que pertenece
    private boolean descubierta;        // Estado de la tarjeta
    private boolean emparejada;         // Si ya fue emparejada
    
    // Colores
    private final Color COLOR_OCULTA = new Color(70, 130, 180);
    private final Color COLOR_DESCUBIERTA = new Color(255, 255, 255);
    private final Color COLOR_EMPAREJADA = new Color(144, 238, 144);
    private final Color COLOR_ERROR = new Color(255, 99, 71);
    
    /**
     * Constructor de la tarjeta
     * @param contenido Texto que muestra la tarjeta (función o derivada)
     * @param idPar Identificador del par al que pertenece
     */
    public Tarjeta(String contenido, int idPar) {
        super();
        this.contenido = contenido;
        this.idPar = idPar;
        this.descubierta = false;
        this.emparejada = false;
        
        configurarTarjeta();
    }
    
    /**
     * Configura la apariencia inicial de la tarjeta
     */
    private void configurarTarjeta() {
        setText("?");
        setFont(new Font("Arial", Font.BOLD, 24));
        setBackground(COLOR_OCULTA);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(47, 79, 79), 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * Voltea la tarjeta para mostrar su contenido
     */
    public void voltear() {
        if (!emparejada && !descubierta) {
            descubierta = true;
            setText(contenido);
            setBackground(COLOR_DESCUBIERTA);
            setForeground(Color.BLACK);
            setFont(new Font("Arial", Font.PLAIN, 16));
        }
    }
    
    /**
     * Oculta la tarjeta volviéndola a su estado original
     */
    public void ocultar() {
        if (!emparejada) {
            descubierta = false;
            setText("?");
            setBackground(COLOR_OCULTA);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 24));
        }
    }
    
    /**
     * Marca la tarjeta como emparejada exitosamente
     */
    public void marcarEmparejada() {
        emparejada = true;
        descubierta = true;
        setBackground(COLOR_EMPAREJADA);
        setEnabled(false);
    }
    
    /**
     * Muestra un efecto visual cuando hay un error
     */
    public void mostrarError() {
        setBackground(COLOR_ERROR);
    }
    
    /**
     * Restaura el color después de mostrar error
     * Vuelve al color de tarjeta descubierta antes de ocultarla
     */
    public void restaurarColor() {
        if (!emparejada && descubierta) {
            setBackground(COLOR_DESCUBIERTA);
        }
    }
    
    // Getters
    public String getContenido() {
        return contenido;
    }
    
    public int getIdPar() {
        return idPar;
    }
    
    public boolean estaDescubierta() {
        return descubierta;
    }
    
    public boolean estaEmparejada() {
        return emparejada;
    }
}
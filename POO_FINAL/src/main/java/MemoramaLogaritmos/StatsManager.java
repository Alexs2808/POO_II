package MemoramaLogaritmos;

/**
 * Singleton simple para llevar estadísticas de la sesión en memoria.
 * NOTA: esto no persiste en disco; si deseas persistencia la añadimos.
 */
public class StatsManager {
    private static StatsManager instancia;
    
    private int paresEncontradosAcumulado = 0;
    private int partidasJugadas = 0;
    private int partidasGanadasSolitario = 0;
    private int partidasGanadasJugador1 = 0;
    private int partidasGanadasJugador2 = 0;
    
    private StatsManager() {}
    
    public static StatsManager getInstance() {
        if (instancia == null) instancia = new StatsManager();
        return instancia;
    }
    
    public synchronized void agregarParesEncontrados(int cantidad) {
        paresEncontradosAcumulado += cantidad;
    }
    
    public synchronized void registrarPartida() {
        partidasJugadas++;
    }
    
    public synchronized void registrarVictoriaSolitario() {
        partidasGanadasSolitario++;
    }
    
    public synchronized void registrarVictoriaJugador1() {
        partidasGanadasJugador1++;
    }
    
    public synchronized void registrarVictoriaJugador2() {
        partidasGanadasJugador2++;
    }
    
    // Getters
    public int getParesEncontradosAcumulado() {
        return paresEncontradosAcumulado;
    }
    
    public int getPartidasJugadas() {
        return partidasJugadas;
    }
    
    public int getPartidasGanadasSolitario() {
        return partidasGanadasSolitario;
    }
    
    public int getPartidasGanadasJugador1() {
        return partidasGanadasJugador1;
    }
    
    public int getPartidasGanadasJugador2() {
        return partidasGanadasJugador2;
    }
}

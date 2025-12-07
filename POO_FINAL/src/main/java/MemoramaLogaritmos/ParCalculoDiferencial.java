package MemoramaLogaritmos;

import java.util.*;

public class ParCalculoDiferencial {
    
    // Pares: [expresión, expresión equivalente]
    private static final String[][] PARES_PROP = {
        {"log(A^n)", "n·log(A)"},
        {"log(A·B)", "log(A) + log(B)"},
        {"log(A/B)", "log(A) - log(B)"},
        {"log(A^1/n)", "(1/n)·log(A)"},
        {"ln(e^x)", "x"},
        {"a^m · a^n", "a^(m+n)"},
        {"a^m / a^n", "a^(m-n)"},
        {"(a^m)^n", "a^(m·n)"},
        {"a^0", "1"},
        {"a^-n", "1 / a^n"},
        {"√(a·b)", "√a · √b"},
        {"√(a/b)", "√a / √b"},
        {"(ab)^(1/n)", "a^(1/n) · b^(1/n)"},
        {"(a/b)^(1/n)", "a^(1/n) / b^(1/n)"},
        {"(x^2)^(1/2)", "|x|"},
        {"n√(a^n)", "a (si n impar y a≥0 para pares)"},
        {"log_a(a^x)", "x"},
        {"e^(ln x)", "x"},
        {"(a·b)^m", "a^m · b^m"},
        {"(a/b)^m", "a^m / b^m"}
    };
    
    public static String[][] obtenerParesAleatorios(int numeroPares) {
        if (numeroPares > PARES_PROP.length) {
            numeroPares = PARES_PROP.length;
        }
        
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < PARES_PROP.length; i++) {
            indices.add(i);
        }
        
        Collections.shuffle(indices);
        
        String[][] paresSeleccionados = new String[numeroPares][2];
        for (int i = 0; i < numeroPares; i++) {
            int idx = indices.get(i);
            paresSeleccionados[i][0] = PARES_PROP[idx][0];
            paresSeleccionados[i][1] = PARES_PROP[idx][1];
        }
        
        return paresSeleccionados;
    }
    
    public static int getTotalPares() {
        return PARES_PROP.length;
    }
    
    public static boolean esPar(String contenido1, String contenido2) {
        for (String[] par : PARES_PROP) {
            if ((par[0].equals(contenido1) && par[1].equals(contenido2)) ||
                (par[1].equals(contenido1) && par[0].equals(contenido2))) {
                return true;
            }
        }
        return false;
    }
}

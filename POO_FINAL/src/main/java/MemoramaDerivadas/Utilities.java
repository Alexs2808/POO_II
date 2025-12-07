package MemoramaDerivadas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Utilities {

    private List<Match> match = new ArrayList<>();
    private List<Match> ok = new ArrayList<>();

    // -------------------------------
    //    GENERADOR DE NUMEROS RANDOM
    // -------------------------------
    public static List<Integer> rowsTable() {
        List<Integer> list = new ArrayList<>();
        Random r = new Random();
        int aument = 1;

        for (int i = 0; i < aument; i++) {
            if (list.size() != 4) {
                aument++;
            }
            int value = r.nextInt(5);
            if (!list.contains(value) && list.size() != 5 && value != 0) {
                list.add(value);
            }
        }
        return list;
    }

    public static List<Integer> rowsTableAll() {
        List<Integer> all = new ArrayList<>();
        all.addAll(rowsTable());
        all.addAll(rowsTable());
        return all;
    }

    // -------------------------------
    //   PINTAR IMAGENES
    // -------------------------------
    public void paintImage(JButton btn, int value) {
        try {
            btn.setIcon(new ImageIcon(getClass().getResource("/Images/0" + value + ".png")));
            btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            btn.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void imgStart() {
        paintImage(PrincipalDerivadas.btn01, 9);
        paintImage(PrincipalDerivadas.btn02, 9);
        paintImage(PrincipalDerivadas.btn03, 9);
        paintImage(PrincipalDerivadas.btn04, 9);
        paintImage(PrincipalDerivadas.btn05, 9);
        paintImage(PrincipalDerivadas.btn06, 9);
        paintImage(PrincipalDerivadas.btn07, 9);
        paintImage(PrincipalDerivadas.btn08, 9);
    }

    public void paintMatch() {
        if (match.size() == 1) {
            paintImage(match.get(0).getBtn(), match.get(0).getValueMatch());
        } else if (match.size() == 2) {
            paintImage(match.get(1).getBtn(), match.get(1).getValueMatch());
        }
    }

    // ------------------------------------------------
    //      CHECK FINAL: CUANDO TODAS ESTÁN CORRECTAS
    // ------------------------------------------------
    public void imgStartMatch() {

        if (ok.size() == 8) { // todas encontradas

            PrincipalDerivadas.stopTimer();

            String mode = PrincipalDerivadas.cbGameMode.getSelectedItem().toString();
            String winnerMsg = "";

            if (mode.equals("Solo")) {

                winnerMsg =
                        "Juego completado\n" +
                        "Tiempo: " + PrincipalDerivadas.lblTimer.getText() + "\n" +
                        "Cartas encontradas: " + PrincipalDerivadas.p1Score;

            } else {  // 1 vs 1

                if (PrincipalDerivadas.p1Score > PrincipalDerivadas.p2Score)
                    winnerMsg = "Ganador: Jugador 1\nPuntaje: " + PrincipalDerivadas.p1Score;
                else if (PrincipalDerivadas.p2Score > PrincipalDerivadas.p1Score)
                    winnerMsg = "Ganador: Jugador 2\nPuntaje: " + PrincipalDerivadas.p2Score;
                else
                    winnerMsg = "Empate!";
            }

            JOptionPane.showMessageDialog(null, winnerMsg);

            // Restauramos tablero
            for (Match m : ok) {
                paintImage(m.getBtn(), m.getValueMatch());
                m.getBtn().setEnabled(false);
            }

            ok.clear();
            imgStart();
        }

        // Pintar las que sí están correctas
        for (Match m : ok) {
            paintImage(m.getBtn(), m.getValueMatch());
            m.getBtn().setEnabled(false);
        }
    }

    // -------------------------------
    //     REINICIAR TABLERO
    // -------------------------------
    public void reset() {
        imgStart();
        imgStartMatch();
    }

    // -------------------------------
    //            MATCH
    // -------------------------------
    public void match() {

        if (match.size() == 2) {

            Match m1 = match.get(0);
            Match m2 = match.get(1);

            if (m1.getNumberBtn() != 0 && m2.getNumberBtn() != 0) {

                // -----------------------------
                //    COINCIDEN
                // -----------------------------
                if (m1.getValueMatch() == m2.getValueMatch()) {

                    ok.add(m1);
                    ok.add(m2);

                    // PUNTAJE
                    if (PrincipalDerivadas.cbGameMode.getSelectedItem().equals("Solo")) {

                        PrincipalDerivadas.p1Score++;
                        PrincipalDerivadas.lblPlayer1Score.setText("Cartas: " + PrincipalDerivadas.p1Score);

                    } else { // 1 vs 1

                        if (PrincipalDerivadas.currentPlayer == 1) {
                            PrincipalDerivadas.p1Score++;
                            PrincipalDerivadas.lblPlayer1Score.setText("P1: " + PrincipalDerivadas.p1Score);
                        } else {
                            PrincipalDerivadas.p2Score++;
                            PrincipalDerivadas.lblPlayer2Score.setText("P2: " + PrincipalDerivadas.p2Score);
                        }
                    }

                    match.clear();
                    reset();

                } else {

                    // -----------------------------
                    //   NO COINCIDEN → cambiar turno
                    // -----------------------------
                    if (PrincipalDerivadas.cbGameMode.getSelectedItem().equals("1 vs 1")) {
                        PrincipalDerivadas.currentPlayer =
                                (PrincipalDerivadas.currentPlayer == 1) ? 2 : 1;
                    }

                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            match.clear();
                            reset();
                        }
                    };

                    setTimeout(r, 2000);
                }
            }
        }
    }

    // -------------------------------
    //        TIMEOUT 2 SEGUNDOS
    // -------------------------------
    public void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public List<Match> getMatch() { return match; }
    public List<Match> getOk() { return ok; }
}
    
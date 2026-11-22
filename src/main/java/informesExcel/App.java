package informesExcel;

import informesExcel.gui.TemaFlatLaf;
import informesExcel.gui.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada principal para la aplicacion de Gestion de Rendimiento Deportivo.
 */
public class App {

    public static void main(String[] args) {
        // Garantizar ejecucion segura en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            // Inicializar Look & Feel moderno con FlatLaf (tema deportivo profesional)
            TemaFlatLaf.configurarEstilosGlobales();

            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}

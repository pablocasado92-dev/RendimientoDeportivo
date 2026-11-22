package informesExcel.gui;

import informesExcel.modelo.Deportista;
import informesExcel.servicio.GeneradorDatosDemo;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PanelAnalisisTest {

    @Test
    void testCreacionYRecalculoPanelAnalisisEnEDT() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        SwingUtilities.invokeAndWait(() -> {
            List<Deportista> demo = GeneradorDatosDemo.generarAtletasDemo();
            PanelAnalisis panel = new PanelAnalisis(demo);
            assertNotNull(panel);
            // Comprobar que no lanza excepciones y recalcula los analisis de JFreeChart
            panel.recalcularAnalisis();
        });
    }
}

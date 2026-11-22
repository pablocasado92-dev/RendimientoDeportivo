package informesExcel.gui;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class TemaFlatLafTest {

    @Test
    void testConfiguracionYAlternanciaTemasEnEDT() throws InterruptedException, InvocationTargetException {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        SwingUtilities.invokeAndWait(() -> {
            TemaFlatLaf.configurarEstilosGlobales();
            assertFalse(TemaFlatLaf.isModoOscuro());

            JFrame frame = new JFrame();
            TemaFlatLaf.alternarTema(frame);
            assertTrue(TemaFlatLaf.isModoOscuro());

            TemaFlatLaf.alternarTema(frame);
            assertFalse(TemaFlatLaf.isModoOscuro());
            frame.dispose();
        });
    }
}

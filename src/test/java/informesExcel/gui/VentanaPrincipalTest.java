package informesExcel.gui;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de inicializacion de componentes Swing en el EDT.
 */
public class VentanaPrincipalTest {

    @Test
    void testInicializacionVentanaPrincipalEnEDT() throws InterruptedException, InvocationTargetException {
        // En entornos CI/headless, evitar lanzar ventanas visibles reales
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        SwingUtilities.invokeAndWait(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            assertNotNull(ventana);
            assertEquals("Gestion y Analisis de Rendimiento Deportivo - 2.o DAM", ventana.getTitle());
            assertEquals(4, ventana.getTabbedPane().getTabCount());
            assertFalse(ventana.getDeportistas().isEmpty());
            ventana.dispose();
        });
    }

    @Test
    void testValidadorFormulariosReglas() {
        assertEquals("Hola", ValidadorFormularios.validarTextoNoVacio("  Hola  ", "Texto"));
        assertThrows(IllegalArgumentException.class, () -> ValidadorFormularios.validarTextoNoVacio("", "Texto"));

        assertEquals(10, ValidadorFormularios.validarEnteroPositivo("10", "Entero"));
        assertThrows(IllegalArgumentException.class, () -> ValidadorFormularios.validarEnteroPositivo("-5", "Entero"));

        assertEquals(12.5, ValidadorFormularios.validarDoublePositivo("12,5", "Decimal"), 0.001);
        assertEquals(12.5, ValidadorFormularios.validarDoublePositivo("12.5", "Decimal"), 0.001);
        assertThrows(IllegalArgumentException.class, () -> ValidadorFormularios.validarDoublePositivo("0", "Decimal"));

        assertEquals(7, ValidadorFormularios.validarRpe("7"));
        assertThrows(IllegalArgumentException.class, () -> ValidadorFormularios.validarRpe("11"));
        assertThrows(IllegalArgumentException.class, () -> ValidadorFormularios.validarRpe("0"));
    }
}

package informesExcel.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Gestor de Look and Feel moderno mediante FlatLaf, configurando estilos
 * redondeados, espaciados profesionales y soporte para alternar entre tema claro y oscuro.
 */
public final class TemaFlatLaf {

    private static boolean modoOscuro = false;

    private TemaFlatLaf() {
    }

    /**
     * Instala la configuracion visual base de FlatLaf (tema claro).
     */
    public static void configurarEstilosGlobales() {
        // Redondeo de componentes y campos
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.width", 12);

        // Pestanas del TabbedPane
        UIManager.put("TabbedPane.tabInsets", new Insets(8, 18, 8, 18));
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("TabbedPane.hasFullBorder", true);

        // Tablas
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 2));
        UIManager.put("Table.rowHeight", 24);

        FlatLightLaf.setup();
        modoOscuro = false;
    }

    /**
     * Alterna en caliente entre tema claro y tema oscuro actualizando toda la jerarquia de componentes.
     */
    public static void alternarTema(Component raiz) {
        try {
            if (modoOscuro) {
                FlatLightLaf.setup();
                modoOscuro = false;
            } else {
                FlatDarkLaf.setup();
                modoOscuro = true;
            }
            FlatLaf.updateUI();
            if (raiz != null) {
                SwingUtilities.updateComponentTreeUI(raiz);
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean isModoOscuro() {
        return modoOscuro;
    }
}

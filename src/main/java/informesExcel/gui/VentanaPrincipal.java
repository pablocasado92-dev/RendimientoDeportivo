package informesExcel.gui;

import informesExcel.modelo.Deportista;
import informesExcel.servicio.GeneradorDatosDemo;
import informesExcel.servicio.GestorPersistencia;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal de la aplicacion de escritorio en Java Swing.
 * Gestiona la navegacion por pestanas (JTabbedPane) y la persistencia local de datos.
 */
public class VentanaPrincipal extends JFrame {

    private final Path rutaDatos = Paths.get("datos", "club_rendimiento.json");
    private final GestorPersistencia persistencia = new GestorPersistencia();
    private List<Deportista> deportistas = new ArrayList<>();

    private JTabbedPane tabbedPane;
    private PanelDeportistas panelDeportistas;
    private PanelEntrenamientosTests panelEntrenamientos;
    private PanelAnalisis panelAnalisis;
    private PanelExportacion panelExportacion;

    public VentanaPrincipal() {
        super("Gestion y Analisis de Rendimiento Deportivo - 2.o DAM");
        cargarDatosIniciales();
        inicializarUI();
    }

    private void cargarDatosIniciales() {
        try {
            deportistas = persistencia.cargar(rutaDatos);
            if (deportistas.isEmpty()) {
                deportistas = GeneradorDatosDemo.generarAtletasDemo();
                persistencia.guardar(deportistas, rutaDatos);
            }
        } catch (IOException e) {
            deportistas = GeneradorDatosDemo.generarAtletasDemo();
        }
    }

    private void guardarDatos() {
        try {
            persistencia.guardar(deportistas, rutaDatos);
        } catch (IOException e) {
            ValidadorFormularios.mostrarError(this, "Error al persistir los datos: " + e.getMessage());
        }
    }

    private void inicializarUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setMinimumSize(new Dimension(850, 580));
        setLocationRelativeTo(null);

        // Barra de herramientas superior
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnGuardar = new JButton("Guardar Datos");
        btnGuardar.addActionListener(e -> {
            guardarDatos();
            ValidadorFormularios.mostrarInfo(this, "Datos guardados correctamente en: " + rutaDatos.toAbsolutePath());
        });

        JButton btnCargarDemo = new JButton("Restablecer Datos Demo");
        btnCargarDemo.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this, "¿Desea sobrescribir los datos actuales con los atletas de prueba demo?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                deportistas.clear();
                deportistas.addAll(GeneradorDatosDemo.generarAtletasDemo());
                guardarDatos();
                sincronizarPaneles();
                ValidadorFormularios.mostrarInfo(this, "Datos demo cargados con exito.");
            }
        });

        JButton btnTema = new JButton("🌙 Alternar Modo Oscuro / Claro");
        btnTema.addActionListener(e -> {
            TemaFlatLaf.alternarTema(this);
            btnTema.setText(TemaFlatLaf.isModoOscuro() ? "☀️ Modo Claro" : "🌙 Modo Oscuro");
        });

        toolBar.add(btnGuardar);
        toolBar.addSeparator();
        toolBar.add(btnCargarDemo);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(btnTema);
        add(toolBar, BorderLayout.NORTH);

        // Pestanas principales
        tabbedPane = new JTabbedPane();

        panelDeportistas = new PanelDeportistas(deportistas, this::sincronizarPaneles);
        panelEntrenamientos = new PanelEntrenamientosTests(deportistas, this::sincronizarPaneles);
        panelAnalisis = new PanelAnalisis(deportistas);
        panelExportacion = new PanelExportacion(deportistas);

        tabbedPane.addTab("1. Deportistas", panelDeportistas);
        tabbedPane.addTab("2. Entrenamientos y Tests", panelEntrenamientos);
        tabbedPane.addTab("3. Panel de Analisis", panelAnalisis);
        tabbedPane.addTab("4. Exportacion de Informes", panelExportacion);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void sincronizarPaneles() {
        guardarDatos();
        if (panelDeportistas != null) panelDeportistas.refrescarTabla();
        if (panelEntrenamientos != null) panelEntrenamientos.actualizarComboAtletas();
        if (panelAnalisis != null) panelAnalisis.actualizarCombo();
        if (panelExportacion != null) panelExportacion.actualizarCombo();
    }

    public List<Deportista> getDeportistas() {
        return deportistas;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void setPanelAnalisis(PanelAnalisis nuevoPanel) {
        this.panelAnalisis = nuevoPanel;
        tabbedPane.setComponentAt(2, nuevoPanel);
    }

    public void setPanelExportacion(PanelExportacion nuevoPanel) {
        this.panelExportacion = nuevoPanel;
        tabbedPane.setComponentAt(3, nuevoPanel);
    }
}

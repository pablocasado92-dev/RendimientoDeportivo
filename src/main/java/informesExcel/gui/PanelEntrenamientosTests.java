package informesExcel.gui;

import informesExcel.modelo.Deportista;
import informesExcel.modelo.SesionEntrenamiento;
import informesExcel.modelo.TestRendimiento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel para el registro y visualizacion de entrenamientos y pruebas de rendimiento.
 */
public class PanelEntrenamientosTests extends JPanel {

    private final List<Deportista> listaDeportistas;
    private final Runnable alModificarDatos;

    private JComboBox<Deportista> cbAtletas;
    private JTable tablaSesiones;
    private DefaultTableModel modeloSesiones;
    private JTable tablaTests;
    private DefaultTableModel modeloTests;

    // Formulario Sesion
    private JTextField txtFechaSesion;
    private JComboBox<String> cbTipoSesion;
    private JTextField txtDuracionMin;
    private JTextField txtDistanciaKm;
    private JTextField txtFcMedia;
    private JSpinner spRpe;

    // Formulario Test
    private JTextField txtFechaTest;
    private JTextField txtMarcaSegundos;
    private JTextField txtSemanasTest;

    public PanelEntrenamientosTests(List<Deportista> listaDeportistas, Runnable alModificarDatos) {
        this.listaDeportistas = listaDeportistas;
        this.alModificarDatos = alModificarDatos;
        inicializarUI();
        actualizarComboAtletas();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Selector superior de Atleta
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTop.add(new JLabel("Seleccionar Deportista:"));
        cbAtletas = new JComboBox<>();
        cbAtletas.setPreferredSize(new Dimension(320, 26));
        cbAtletas.addActionListener(e -> refrescarTablas());
        panelTop.add(cbAtletas);
        add(panelTop, BorderLayout.NORTH);

        // Centro: Split pane con Sesiones a la izquierda y Tests a la derecha
        JSplitPane splitCentro = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitCentro.setResizeWeight(0.6);

        // Bloque Izquierdo: Sesiones de Entrenamiento
        JPanel panelSesiones = new JPanel(new BorderLayout(5, 5));
        panelSesiones.setBorder(BorderFactory.createTitledBorder("Sesiones de Entrenamiento"));

        modeloSesiones = new DefaultTableModel(new String[]{"Fecha", "Tipo", "Duracion (min)", "Distancia (km)", "FC", "RPE", "Carga (UA)"}, 0);
        tablaSesiones = new JTable(modeloSesiones);
        panelSesiones.add(new JScrollPane(tablaSesiones), BorderLayout.CENTER);

        // Formulario alta sesion
        JPanel formSesion = new JPanel(new GridLayout(3, 4, 5, 5));
        formSesion.setBorder(BorderFactory.createTitledBorder("Registrar Nueva Sesion"));

        formSesion.add(new JLabel("Fecha (AAAA-MM-DD):"));
        txtFechaSesion = new JTextField(LocalDate.now().toString());
        formSesion.add(txtFechaSesion);

        formSesion.add(new JLabel("Tipo Sesion:"));
        cbTipoSesion = new JComboBox<>(new String[]{"Rodaje Suave", "Series Cortas", "Series 1000m", "Ritmo Controlado", "Tirada Larga", "Cuestas"});
        formSesion.add(cbTipoSesion);

        formSesion.add(new JLabel("Duracion (min):"));
        txtDuracionMin = new JTextField("60");
        formSesion.add(txtDuracionMin);

        formSesion.add(new JLabel("Distancia (km):"));
        txtDistanciaKm = new JTextField("10.0");
        formSesion.add(txtDistanciaKm);

        formSesion.add(new JLabel("FC Media (ppm):"));
        txtFcMedia = new JTextField("150");
        formSesion.add(txtFcMedia);

        formSesion.add(new JLabel("RPE (1 a 10):"));
        spRpe = new JSpinner(new SpinnerNumberModel(7, 1, 10, 1));
        formSesion.add(spRpe);

        JButton btnAgregarSesion = new JButton("Agregar Sesion");
        btnAgregarSesion.addActionListener(e -> agregarSesion());

        JPanel boxSesionSouth = new JPanel(new BorderLayout());
        boxSesionSouth.add(formSesion, BorderLayout.CENTER);
        boxSesionSouth.add(btnAgregarSesion, BorderLayout.SOUTH);
        panelSesiones.add(boxSesionSouth, BorderLayout.SOUTH);

        splitCentro.setLeftComponent(panelSesiones);

        // Bloque Derecho: Tests de Rendimiento 5K
        JPanel panelTests = new JPanel(new BorderLayout(5, 5));
        panelTests.setBorder(BorderFactory.createTitledBorder("Tests de Rendimiento (5 km)"));

        modeloTests = new DefaultTableModel(new String[]{"Fecha", "Semana (x)", "Marca (s)", "Tiempo (mm:ss)"}, 0);
        tablaTests = new JTable(modeloTests);
        panelTests.add(new JScrollPane(tablaTests), BorderLayout.CENTER);

        // Formulario alta test
        JPanel formTest = new JPanel(new GridLayout(3, 2, 5, 5));
        formTest.setBorder(BorderFactory.createTitledBorder("Registrar Nuevo Test"));

        formTest.add(new JLabel("Fecha (AAAA-MM-DD):"));
        txtFechaTest = new JTextField(LocalDate.now().toString());
        formTest.add(txtFechaTest);

        formTest.add(new JLabel("Marca en segundos:"));
        txtMarcaSegundos = new JTextField("1180.0");
        formTest.add(txtMarcaSegundos);

        formTest.add(new JLabel("Semanas transcurridas (x):"));
        txtSemanasTest = new JTextField("1.0");
        formTest.add(txtSemanasTest);

        JButton btnAgregarTest = new JButton("Agregar Test 5K");
        btnAgregarTest.addActionListener(e -> agregarTest());

        JPanel boxTestSouth = new JPanel(new BorderLayout());
        boxTestSouth.add(formTest, BorderLayout.CENTER);
        boxTestSouth.add(btnAgregarTest, BorderLayout.SOUTH);
        panelTests.add(boxTestSouth, BorderLayout.SOUTH);

        splitCentro.setRightComponent(panelTests);
        add(splitCentro, BorderLayout.CENTER);
    }

    private Deportista getDeportistaSeleccionado() {
        return (Deportista) cbAtletas.getSelectedItem();
    }

    private void agregarSesion() {
        Deportista d = getDeportistaSeleccionado();
        if (d == null) {
            ValidadorFormularios.mostrarError(null, "Debe seleccionar un deportista.");
            return;
        }
        try {
            LocalDate fecha = ValidadorFormularios.validarFecha(txtFechaSesion.getText());
            String tipo = (String) cbTipoSesion.getSelectedItem();
            int duracion = ValidadorFormularios.validarEnteroPositivo(txtDuracionMin.getText(), "Duracion");
            double km = ValidadorFormularios.validarDoublePositivo(txtDistanciaKm.getText(), "Distancia");
            int fc = ValidadorFormularios.validarEnteroPositivo(txtFcMedia.getText(), "FC Media");
            int rpe = (Integer) spRpe.getValue();

            SesionEntrenamiento nueva = new SesionEntrenamiento(fecha, tipo, duracion, km, fc, rpe);
            d.agregarSesion(nueva);
            refrescarTablas();
            alModificarDatos.run();
            ValidadorFormularios.mostrarInfo(null, "Sesion registrada correctamente.");
        } catch (IllegalArgumentException ex) {
            ValidadorFormularios.mostrarError(null, ex.getMessage());
        }
    }

    private void agregarTest() {
        Deportista d = getDeportistaSeleccionado();
        if (d == null) {
            ValidadorFormularios.mostrarError(null, "Debe seleccionar un deportista.");
            return;
        }
        try {
            LocalDate fecha = ValidadorFormularios.validarFecha(txtFechaTest.getText());
            double marca = ValidadorFormularios.validarDoublePositivo(txtMarcaSegundos.getText(), "Marca en segundos");
            double semanas = ValidadorFormularios.validarDoublePositivo(txtSemanasTest.getText(), "Semanas transcurridas");

            TestRendimiento nuevo = new TestRendimiento(fecha, marca, semanas);
            d.agregarTest(nuevo);
            refrescarTablas();
            alModificarDatos.run();
            ValidadorFormularios.mostrarInfo(null, "Test de rendimiento registrado correctamente.");
        } catch (IllegalArgumentException ex) {
            ValidadorFormularios.mostrarError(null, ex.getMessage());
        }
    }

    public void actualizarComboAtletas() {
        Deportista seleccionado = getDeportistaSeleccionado();
        cbAtletas.removeAllItems();
        for (Deportista d : listaDeportistas) {
            cbAtletas.addItem(d);
        }
        if (seleccionado != null && listaDeportistas.contains(seleccionado)) {
            cbAtletas.setSelectedItem(seleccionado);
        } else if (!listaDeportistas.isEmpty()) {
            cbAtletas.setSelectedIndex(0);
        }
        refrescarTablas();
    }

    public void refrescarTablas() {
        modeloSesiones.setRowCount(0);
        modeloTests.setRowCount(0);

        Deportista d = getDeportistaSeleccionado();
        if (d == null) return;

        for (SesionEntrenamiento s : d.getSesiones()) {
            modeloSesiones.addRow(new Object[]{
                    s.getFecha(),
                    s.getTipo(),
                    s.getDuracionMinutos(),
                    String.format(java.util.Locale.US, "%.2f", s.getDistanciaKm()),
                    s.getFcMedia(),
                    s.getRpe(),
                    s.getCargaFoster()
            });
        }

        for (TestRendimiento t : d.getTests()) {
            modeloTests.addRow(new Object[]{
                    t.getFecha(),
                    String.format(java.util.Locale.US, "%.1f", t.getSemanasDesdeInicio()),
                    String.format(java.util.Locale.US, "%.1f", t.getMarcaSegundos()),
                    t.getMarcaFormateada()
            });
        }
    }
}

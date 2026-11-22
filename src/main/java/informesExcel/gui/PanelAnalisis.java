package informesExcel.gui;

import informesExcel.modelo.Deportista;
import informesExcel.modelo.SesionEntrenamiento;
import informesExcel.modelo.TestRendimiento;
import informesExcel.servicio.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * Panel de analisis de rendimiento deportivo con KPIs, estadisticas,
 * regresion lineal y graficos JFreeChart interactivos.
 */
public class PanelAnalisis extends JPanel {

    private final List<Deportista> listaDeportistas;

    private JComboBox<Deportista> cbAtletas;
    private JLabel lblKpis;
    private JLabel lblRegresion;
    private JTable tablaSemanas;
    private DefaultTableModel modeloSemanas;
    private JPanel panelGraficoCarga;
    private JPanel panelGraficoMarcas;

    public PanelAnalisis(List<Deportista> listaDeportistas) {
        this.listaDeportistas = listaDeportistas;
        inicializarUI();
        actualizarCombo();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Selector de deportista y panel superior
        JPanel panelTop = new JPanel(new BorderLayout(5, 5));
        JPanel subTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        subTop.add(new JLabel("Seleccionar Deportista para Análisis:"));
        cbAtletas = new JComboBox<>();
        cbAtletas.setPreferredSize(new Dimension(320, 26));
        cbAtletas.addActionListener(e -> recalcularAnalisis());
        subTop.add(cbAtletas);
        panelTop.add(subTop, BorderLayout.NORTH);

        // Bloque KPI
        JPanel panelKpiBox = new JPanel(new GridLayout(2, 1, 4, 4));
        panelKpiBox.setBorder(BorderFactory.createTitledBorder("Indicadores Clave de Rendimiento (KPIs)"));

        lblKpis = new JLabel("Cargando indicadores...", SwingConstants.CENTER);
        lblKpis.setFont(lblKpis.getFont().deriveFont(Font.BOLD, 13f));

        lblRegresion = new JLabel("Parámetros de Regresión...", SwingConstants.CENTER);
        lblRegresion.setForeground(new Color(30, 100, 150));

        panelKpiBox.add(lblKpis);
        panelKpiBox.add(lblRegresion);
        panelTop.add(panelKpiBox, BorderLayout.CENTER);

        add(panelTop, BorderLayout.NORTH);

        // Split central: Tabla semanal a la izquierda y graficos a la derecha
        JSplitPane splitCentro = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitCentro.setResizeWeight(0.45);

        // Tabla semanal
        String[] cols = {"Semana", "Fechas", "Km", "Minutos", "Carga UA", "Media Móvil 3S", "Monotonía", "Tensión"};
        modeloSemanas = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaSemanas = new JTable(modeloSemanas);
        JPanel panelTablaBox = new JPanel(new BorderLayout());
        panelTablaBox.setBorder(BorderFactory.createTitledBorder("Resumen Semanal de Carga (Foster / Monotonía)"));
        panelTablaBox.add(new JScrollPane(tablaSemanas), BorderLayout.CENTER);
        splitCentro.setLeftComponent(panelTablaBox);

        // Pestañas de graficos
        JTabbedPane tabGraficos = new JTabbedPane();
        panelGraficoCarga = new JPanel(new BorderLayout());
        panelGraficoMarcas = new JPanel(new BorderLayout());

        tabGraficos.addTab("Carga Semanal (Barras)", panelGraficoCarga);
        tabGraficos.addTab("Evolución 5K y Regresión", panelGraficoMarcas);

        splitCentro.setRightComponent(tabGraficos);
        add(splitCentro, BorderLayout.CENTER);
    }

    public void actualizarCombo() {
        Deportista sel = (Deportista) cbAtletas.getSelectedItem();
        cbAtletas.removeAllItems();
        for (Deportista d : listaDeportistas) {
            cbAtletas.addItem(d);
        }
        if (sel != null && listaDeportistas.contains(sel)) {
            cbAtletas.setSelectedItem(sel);
        } else if (!listaDeportistas.isEmpty()) {
            cbAtletas.setSelectedIndex(0);
        }
        recalcularAnalisis();
    }

    public void recalcularAnalisis() {
        Deportista d = (Deportista) cbAtletas.getSelectedItem();
        if (d == null) return;

        List<ResumenSemanal> semanas = CalculadoraRendimiento.calcularResumenesSemanales(d.getSesiones());
        Optional<ResultadoRegresion> regOpt = CalculadoraRendimiento.calcularRegresionLineal(d.getTests());

        // Actualizar KPIs
        double mejor5k = d.getMejorMarca5k();
        int min5k = (int) (mejor5k / 60);
        double seg5k = mejor5k % 60;
        String mejor5kTxt = String.format(java.util.Locale.US, "%02d:%04.1f", min5k, seg5k);

        ResumenSemanal ultimaSem = semanas.isEmpty() ? null : semanas.get(semanas.size() - 1);
        String monotoniaTxt = (ultimaSem != null) ? String.format(java.util.Locale.US, "%.2f", ultimaSem.getMonotonia()) : "-";
        String tensionTxt = (ultimaSem != null) ? String.format(java.util.Locale.US, "%.1f", ultimaSem.getTensionSemanal()) : "-";

        lblKpis.setText(String.format(java.util.Locale.US,
                "<html>Sesiones: <b>%d</b> | Distancia: <b>%.1f km</b> | Tiempo: <b>%d min</b> | Carga Total: <b>%d UA</b> | Mejor 5K: <b>%s</b> | Última Monotonía: <b>%s</b> | Última Tensión: <b>%s</b></html>",
                d.getSesiones().size(), d.getKilometrosTotales(), d.getMinutosTotales(), d.getCargaTotalFoster(), mejor5kTxt, monotoniaTxt, tensionTxt));

        if (regOpt.isPresent()) {
            ResultadoRegresion reg = regOpt.get();
            lblRegresion.setText(String.format(java.util.Locale.US,
                    "Recta de Regresión: %s | Coeficiente R² = %.3f | Estimación Semana %.0f: %.1f s",
                    reg.getEcuacion(), reg.getRCuadrado(), reg.getSemanaSiguienteX(), reg.getEstimacionSiguienteY()));
        } else {
            lblRegresion.setText("Regresión lineal: Se requieren al menos 2 tests de rendimiento 5K para calcular la tendencia.");
        }

        // Actualizar tabla semanal
        modeloSemanas.setRowCount(0);
        for (ResumenSemanal r : semanas) {
            modeloSemanas.addRow(new Object[]{
                    "Semana " + r.getNumeroSemana(),
                    r.getFechaInicio() + " a " + r.getFechaFin(),
                    String.format(java.util.Locale.US, "%.1f", r.getKilometrosTotales()),
                    r.getMinutosTotales(),
                    r.getCargaSemanal(),
                    r.getMediaMovilTexto(),
                    String.format(java.util.Locale.US, "%.2f", r.getMonotonia()),
                    String.format(java.util.Locale.US, "%.1f", r.getTensionSemanal())
            });
        }

        // Actualizar graficos JFreeChart
        panelGraficoCarga.removeAll();
        JFreeChart chartCarga = GeneradorGraficos.crearGraficoCargaSemanal(semanas);
        panelGraficoCarga.add(new ChartPanel(chartCarga), BorderLayout.CENTER);
        panelGraficoCarga.revalidate();
        panelGraficoCarga.repaint();

        panelGraficoMarcas.removeAll();
        JFreeChart chartMarcas = GeneradorGraficos.crearGraficoEvolucionMarcas(d.getTests(), regOpt.orElse(null));
        panelGraficoMarcas.add(new ChartPanel(chartMarcas), BorderLayout.CENTER);
        panelGraficoMarcas.revalidate();
        panelGraficoMarcas.repaint();
    }
}

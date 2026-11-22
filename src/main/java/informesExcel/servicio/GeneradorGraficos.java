package informesExcel.servicio;

import informesExcel.modelo.TestRendimiento;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Servicio para la creacion programatica de graficos deportivos con JFreeChart
 * para su visualizacion en Swing y exportacion a imagenes PNG para informes PDF y Excel.
 */
public final class GeneradorGraficos {

    private GeneradorGraficos() {
    }

    /**
     * Genera un grafico de barras con la carga semanal de entrenamiento (Foster UA).
     */
    public static JFreeChart crearGraficoCargaSemanal(List<ResumenSemanal> resumenes) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (resumenes != null) {
            for (ResumenSemanal r : resumenes) {
                dataset.addValue(r.getCargaSemanal(), "Carga (UA)", "Sem " + r.getNumeroSemana());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Evolución de Carga Semanal (Sesión-RPE)",
                "Semana",
                "Carga (Unidades Arbitrarias)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(41, 128, 185)); // Azul deportivo
        return chart;
    }

    /**
     * Genera un grafico de evolucion de marcas en 5k (puntos reales) y la recta de tendencia de regresion lineal.
     */
    public static JFreeChart crearGraficoEvolucionMarcas(List<TestRendimiento> tests, ResultadoRegresion regresion) {
        XYSeries serieMarcas = new XYSeries("Marca Test (segundos)");
        double minSem = Double.MAX_VALUE;
        double maxSem = 0.0;

        if (tests != null && !tests.isEmpty()) {
            for (TestRendimiento t : tests) {
                serieMarcas.add(t.getSemanasDesdeInicio(), t.getMarcaSegundos());
                if (t.getSemanasDesdeInicio() < minSem) minSem = t.getSemanasDesdeInicio();
                if (t.getSemanasDesdeInicio() > maxSem) maxSem = t.getSemanasDesdeInicio();
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieMarcas);

        if (regresion != null && tests != null && tests.size() >= 2) {
            XYSeries serieRegresion = new XYSeries("Tendencia Lineal (" + regresion.getEcuacion() + ")");
            serieRegresion.add(minSem, regresion.getInterceptoA() + (regresion.getPendienteB() * minSem));
            serieRegresion.add(maxSem + 1.0, regresion.getEstimacionSiguienteY());
            dataset.addSeries(serieRegresion);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Evolución de Marcas en 5K y Proyección",
                "Semanas de Entrenamiento",
                "Marca (segundos)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Serie 0 (Tests): Puntos visibles sin linea
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesPaint(0, new Color(192, 57, 43)); // Rojo

        // Serie 1 (Regresion): Linea continua sin puntos gigantes
        if (dataset.getSeriesCount() > 1) {
            renderer.setSeriesLinesVisible(1, true);
            renderer.setSeriesShapesVisible(1, false);
            renderer.setSeriesPaint(1, new Color(39, 174, 96)); // Verde
            renderer.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }

        plot.setRenderer(renderer);
        return chart;
    }

    /**
     * Exporta el grafico a un archivo de imagen PNG en disco.
     */
    public static void exportarGraficoAPng(JFreeChart chart, Path destino, int ancho, int alto) throws IOException {
        if (destino.getParent() != null) {
            java.nio.file.Files.createDirectories(destino.getParent());
        }
        ChartUtils.saveChartAsPNG(destino.toFile(), chart, ancho, alto);
    }
}

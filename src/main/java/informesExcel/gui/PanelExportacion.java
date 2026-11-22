package informesExcel.gui;

import informesExcel.exportacion.ExportadorExcel;
import informesExcel.exportacion.GeneradorInformePDF;
import informesExcel.modelo.Deportista;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Panel para la exportacion de documentos oficiales:
 * 1. Libro Excel (.xlsx) con 5 hojas mediante Apache POI.
 * 2. Informe Individual PDF (5 paginas) con Apache PDFBox y JFreeChart.
 */
public class PanelExportacion extends JPanel {

    private final List<Deportista> listaDeportistas;
    private final ExportadorExcel exportadorExcel = new ExportadorExcel();
    private final GeneradorInformePDF generadorPDF = new GeneradorInformePDF();

    private JComboBox<Deportista> cbAtletas;
    private JLabel lblEstado;

    public PanelExportacion(List<Deportista> listaDeportistas) {
        this.listaDeportistas = listaDeportistas;
        inicializarUI();
        actualizarCombo();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Titulo
        JLabel lblTitulo = new JLabel("Centro de Exportación de Informes Deportivos", SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 18f));
        lblTitulo.setForeground(new Color(24, 44, 97));
        add(lblTitulo, BorderLayout.NORTH);

        // Contenido en 2 tarjetas (Excel y PDF)
        JPanel gridTarjetas = new JPanel(new GridLayout(1, 2, 20, 20));

        // 1. Tarjeta Excel
        JPanel cardExcel = new JPanel(new BorderLayout(10, 10));
        cardExcel.setBorder(BorderFactory.createTitledBorder("Exportación a Microsoft Excel (.xlsx)"));

        JTextArea txtDescExcel = new JTextArea(
                "Genera un libro Excel interactivo con 5 hojas obligatorias:\n\n" +
                "• 1. Resumen: Métricas globales del club.\n" +
                "• 2. Deportistas: Fichas tabulares de atletas.\n" +
                "• 3. Entrenamientos: Registros con autofiltros y paneles inmovilizados.\n" +
                "• 4. Análisis: Semanas, monotonía, tensión y formato condicional.\n" +
                "• 5. Rendimiento: Tests 5K, recta de regresión y gráfico insertado.\n\n" +
                "Incluye fórmulas nativas (SUM, AVERAGE) y auto-ajuste de columnas."
        );
        txtDescExcel.setEditable(false);
        txtDescExcel.setOpaque(false);
        txtDescExcel.setWrapStyleWord(true);
        txtDescExcel.setLineWrap(true);
        cardExcel.add(txtDescExcel, BorderLayout.CENTER);

        JButton btnExportarExcel = new JButton("Exportar Libro Excel Completo (.xlsx)");
        btnExportarExcel.setFont(btnExportarExcel.getFont().deriveFont(Font.BOLD, 12f));
        btnExportarExcel.setBackground(new Color(39, 174, 96));
        btnExportarExcel.addActionListener(e -> exportarExcel());
        cardExcel.add(btnExportarExcel, BorderLayout.SOUTH);

        gridTarjetas.add(cardExcel);

        // 2. Tarjeta PDF
        JPanel cardPDF = new JPanel(new BorderLayout(10, 10));
        cardPDF.setBorder(BorderFactory.createTitledBorder("Informe Individual en PDF (5 Páginas)"));

        JPanel subPdfTop = new JPanel(new BorderLayout(5, 5));
        subPdfTop.add(new JLabel("Seleccionar Deportista para el Informe:"), BorderLayout.NORTH);
        cbAtletas = new JComboBox<>();
        subPdfTop.add(cbAtletas, BorderLayout.CENTER);
        cardPDF.add(subPdfTop, BorderLayout.NORTH);

        JTextArea txtDescPDF = new JTextArea(
                "Genera un informe ejecutivo de 5 páginas para el atleta seleccionado:\n\n" +
                "• Pág 1: Portada con escudo del club y foto/avatar del atleta.\n" +
                "• Pág 2: Resumen global, texto ejecutivo y 4 tarjetas de KPIs.\n" +
                "• Pág 3: Tabla semanal Foster y gráfico de barras de carga.\n" +
                "• Pág 4: Historial de tests 5K, regresión y gráfico de evolución.\n" +
                "• Pág 5: Conclusiones automáticas, monotonía y descargo de responsabilidad."
        );
        txtDescPDF.setEditable(false);
        txtDescPDF.setOpaque(false);
        txtDescPDF.setWrapStyleWord(true);
        txtDescPDF.setLineWrap(true);
        cardPDF.add(txtDescPDF, BorderLayout.CENTER);

        JButton btnExportarPDF = new JButton("Generar Informe Individual en PDF");
        btnExportarPDF.setFont(btnExportarPDF.getFont().deriveFont(Font.BOLD, 12f));
        btnExportarPDF.setBackground(new Color(41, 128, 185));
        btnExportarPDF.addActionListener(e -> exportarPDF());
        cardPDF.add(btnExportarPDF, BorderLayout.SOUTH);

        gridTarjetas.add(cardPDF);
        add(gridTarjetas, BorderLayout.CENTER);

        // Barra inferior de estado
        lblEstado = new JLabel("Listo para exportar.", SwingConstants.CENTER);
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.ITALIC, 11f));
        add(lblEstado, BorderLayout.SOUTH);
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
    }

    private void exportarExcel() {
        if (listaDeportistas.isEmpty()) {
            ValidadorFormularios.mostrarError(null, "No hay deportistas registrados para exportar.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("informe_rendimiento_club.xlsx"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            if (!dest.getName().toLowerCase().endsWith(".xlsx")) {
                dest = new File(dest.getAbsolutePath() + ".xlsx");
            }
            try {
                exportadorExcel.exportarLibroCompleto(listaDeportistas, dest.toPath());
                lblEstado.setText("Excel exportado con éxito: " + dest.getAbsolutePath());
                ValidadorFormularios.mostrarInfo(null, "Libro Excel (.xlsx) de 5 hojas exportado correctamente.");
            } catch (IOException ex) {
                lblEstado.setText("Error al exportar Excel: " + ex.getMessage());
                ValidadorFormularios.mostrarError(null, "Error al generar el archivo Excel: " + ex.getMessage());
            }
        }
    }

    private void exportarPDF() {
        Deportista d = (Deportista) cbAtletas.getSelectedItem();
        if (d == null) {
            ValidadorFormularios.mostrarError(null, "Debe seleccionar un deportista.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        String safeName = d.getNombre().toLowerCase().replace(" ", "_");
        chooser.setSelectedFile(new File("informe_" + safeName + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            if (!dest.getName().toLowerCase().endsWith(".pdf")) {
                dest = new File(dest.getAbsolutePath() + ".pdf");
            }
            try {
                generadorPDF.generarInformeIndividual(d, dest.toPath());
                lblEstado.setText("PDF exportado con éxito: " + dest.getAbsolutePath());
                ValidadorFormularios.mostrarInfo(null, "Informe PDF de 5 páginas generado correctamente.");
            } catch (IOException ex) {
                lblEstado.setText("Error al exportar PDF: " + ex.getMessage());
                ValidadorFormularios.mostrarError(null, "Error al generar el informe PDF: " + ex.getMessage());
            }
        }
    }
}

package informesExcel.exportacion;

import informesExcel.modelo.Deportista;
import informesExcel.modelo.SesionEntrenamiento;
import informesExcel.modelo.TestRendimiento;
import informesExcel.servicio.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.JFreeChart;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Generador de informe individual en formato PDF de 5 paginas completas mediante Apache PDFBox 3.x
 * e insercion de graficos vectoriales/rasterizados con JFreeChart y Java 2D.
 */
public class GeneradorInformePDF {

    private static final String NOMBRE_CLUB = "Club de Rendimiento Deportivo (CRD)";
    private static final Color COLOR_PRIMARIO = new Color(24, 44, 97);
    private static final Color COLOR_SECUNDARIO = new Color(41, 128, 185);
    private static final Color COLOR_GRIS_FONDO = new Color(245, 247, 250);
    private static final Color COLOR_TEXTO = new Color(44, 62, 80);

    public void generarInformeIndividual(Deportista deportista, Path rutaDestino) throws IOException {
        if (rutaDestino.getParent() != null && !Files.exists(rutaDestino.getParent())) {
            Files.createDirectories(rutaDestino.getParent());
        }

        List<ResumenSemanal> semanas = CalculadoraRendimiento.calcularResumenesSemanales(deportista.getSesiones());
        Optional<ResultadoRegresion> regOpt = CalculadoraRendimiento.calcularRegresionLineal(deportista.getTests());

        try (PDDocument doc = new PDDocument()) {
            PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont fontOblique = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            // Generar 5 paginas exactas
            pagina1Portada(doc, deportista, semanas, fontBold, fontRegular);
            pagina2ResumenGlobal(doc, deportista, semanas, fontBold, fontRegular);
            pagina3EntrenamientoYCarga(doc, deportista, semanas, fontBold, fontRegular);
            pagina4RendimientoYEvolucion(doc, deportista, regOpt.orElse(null), fontBold, fontRegular);
            pagina5ConclusionesYDisclaimer(doc, deportista, semanas, regOpt.orElse(null), fontBold, fontRegular, fontOblique);

            doc.save(rutaDestino.toFile());
        }
    }

    // ==========================================
    // PAGINA 1: PORTADA
    // ==========================================
    private void pagina1Portada(PDDocument doc, Deportista dep, List<ResumenSemanal> semanas, PDFont fb, PDFont fr) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float w = page.getMediaBox().getWidth();
        float h = page.getMediaBox().getHeight();

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            // Franja superior de marca
            cs.setNonStrokingColor(COLOR_PRIMARIO);
            cs.addRect(0, h - 140, w, 140);
            cs.fill();

            // Logo del club
            byte[] logoBytes = RecursosGraficos.generarLogoClubBytes(110, 110);
            PDImageXObject imgLogo = PDImageXObject.createFromByteArray(doc, logoBytes, "logo");
            cs.drawImage(imgLogo, 45, h - 125, 90, 90);

            // Titulo del club
            cs.setNonStrokingColor(Color.WHITE);
            cs.beginText();
            cs.setFont(fb, 20);
            cs.newLineAtOffset(150, h - 70);
            cs.showText(NOMBRE_CLUB);
            cs.setFont(fr, 12);
            cs.newLineAtOffset(0, -22);
            cs.showText("Área de Biomecánica y Rendimiento Físico - Temporada Oficial");
            cs.endText();

            // Titulo principal del informe
            cs.setNonStrokingColor(COLOR_PRIMARIO);
            cs.beginText();
            cs.setFont(fb, 26);
            cs.newLineAtOffset(50, h - 230);
            cs.showText("INFORME DE RENDIMIENTO DEPORTIVO");
            cs.endText();

            cs.setStrokingColor(COLOR_SECUNDARIO);
            cs.setLineWidth(3f);
            cs.moveTo(50, h - 245);
            cs.lineTo(w - 50, h - 245);
            cs.stroke();

            // Imagen / Foto del atleta
            PDImageXObject imgAtleta;
            if (dep.getRutaFoto() != null && new File(dep.getRutaFoto()).exists()) {
                imgAtleta = PDImageXObject.createFromFile(dep.getRutaFoto(), doc);
            } else {
                byte[] avatarBytes = RecursosGraficos.generarAvatarDeportistaBytes(dep.getNombre(), 200);
                imgAtleta = PDImageXObject.createFromByteArray(doc, avatarBytes, "avatar");
            }
            cs.drawImage(imgAtleta, 50, h - 480, 180, 180);

            // Tarjeta de datos del atleta
            float cardX = 250;
            float cardY = h - 480;
            float cardW = w - 300;
            float cardH = 180;

            cs.setNonStrokingColor(COLOR_GRIS_FONDO);
            cs.addRect(cardX, cardY, cardW, cardH);
            cs.fill();

            cs.setStrokingColor(new Color(200, 210, 220));
            cs.setLineWidth(1f);
            cs.addRect(cardX, cardY, cardW, cardH);
            cs.stroke();

            cs.setNonStrokingColor(COLOR_TEXTO);
            float txtY = cardY + cardH - 30;
            escribirLinea(cs, fb, 13, cardX + 15, txtY, "Deportista: " + dep.getNombre());
            escribirLinea(cs, fr, 11, cardX + 15, txtY - 24, "ID Federado: " + dep.getId());
            escribirLinea(cs, fr, 11, cardX + 15, txtY - 44, "Edad: " + dep.getEdad() + " años | Categoría: " + dep.getCategoria());
            escribirLinea(cs, fr, 11, cardX + 15, txtY - 64, "Objetivo: " + dep.getObjetivoCompeticion());
            escribirLinea(cs, fr, 11, cardX + 15, txtY - 84, String.format(Locale.US, "Marca de Referencia Inicial 5K: %.1f s", dep.getMarcaInicial5k()));

            LocalDate fin = semanas.isEmpty() ? LocalDate.now() : semanas.get(semanas.size() - 1).getFechaFin();
            LocalDate ini = semanas.isEmpty() ? fin.minusWeeks(10) : semanas.get(0).getFechaInicio();
            escribirLinea(cs, fb, 11, cardX + 15, txtY - 114, "Periodo Evaluado: " + ini + " al " + fin);

            // Pie de portada
            cs.setNonStrokingColor(new Color(120, 140, 160));
            escribirLinea(cs, fr, 10, 50, 70, "Fecha de Emisión: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            escribirLinea(cs, fr, 10, w - 170, 70, "Página 1 de 5");
        }
    }

    // ==========================================
    // PAGINA 2: RESUMEN GLOBAL (KPIS Y TEXTO EJECUTIVO)
    // ==========================================
    private void pagina2ResumenGlobal(PDDocument doc, Deportista dep, List<ResumenSemanal> semanas, PDFont fb, PDFont fr) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        float w = page.getMediaBox().getWidth();
        float h = page.getMediaBox().getHeight();

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            dibujarEncabezadoPagina(cs, "2. RESUMEN GLOBAL DEL ATLETA", 2, w, h, fb, fr);

            // Texto ejecutivo
            float curY = h - 110;
            escribirLinea(cs, fb, 14, 50, curY, "Evaluación Ejecutiva de Temporada");
            curY -= 20;

            String parrafo1 = String.format(Locale.US,
                    "Durante el periodo de seguimiento, el atleta %s ha acumulado un volumen global de %.1f kilómetros distribuidos en %d sesiones de entrenamiento planificadas, totalizando %d minutos de actividad física controlada.",
                    dep.getNombre(), dep.getKilometrosTotales(), dep.getSesiones().size(), dep.getMinutosTotales());
            curY = escribirParrafo(cs, fr, 11, 50, curY, w - 100, parrafo1, 15);

            curY -= 15;
            String parrafo2 = String.format(Locale.US,
                    "La carga acumulada bajo el modelo de Foster (Sesión-RPE) asciende a %d Unidades Arbitrarias (UA). En las pruebas específicas de 5 km, el atleta ha acreditado una mejor marca de %.1f segundos, frente a los %.1f segundos de inicio.",
                    dep.getCargaTotalFoster(), dep.getMejorMarca5k(), dep.getMarcaInicial5k());
            curY = escribirParrafo(cs, fr, 11, 50, curY, w - 100, parrafo2, 15);

            // 4 Tarjetas de KPIs visuales (2x2)
            curY -= 30;
            escribirLinea(cs, fb, 14, 50, curY, "Métricas Clave de Rendimiento (KPIs)");
            curY -= 20;

            float cardW = (w - 120) / 2;
            float cardH = 80;

            dibujarTarjetaKpi(cs, 50, curY - cardH, cardW, cardH, "VOLUMEN TOTAL", String.format(Locale.US, "%.1f km", dep.getKilometrosTotales()), dep.getSesiones().size() + " sesiones realizadas", fb, fr);
            dibujarTarjetaKpi(cs, 70 + cardW, curY - cardH, cardW, cardH, "TIEMPO ACUMULADO", dep.getMinutosTotales() + " min", String.format(Locale.US, "%.1f horas de trabajo", dep.getMinutosTotales() / 60.0), fb, fr);

            curY -= (cardH + 15);
            dibujarTarjetaKpi(cs, 50, curY - cardH, cardW, cardH, "CARGA FOSTER TOTAL", dep.getCargaTotalFoster() + " UA", "Fatiga e impacto neuromuscular", fb, fr);
            double difMejor = dep.getMarcaInicial5k() - dep.getMejorMarca5k();
            String difTxt = (difMejor >= 0) ? String.format(Locale.US, "Mejora de -%.1f s", difMejor) : String.format(Locale.US, "+%.1f s", Math.abs(difMejor));
            dibujarTarjetaKpi(cs, 70 + cardW, curY - cardH, cardW, cardH, "MEJOR MARCA 5K", String.format(Locale.US, "%.1f s", dep.getMejorMarca5k()), difTxt + " vs inicial", fb, fr);

            dibujarPiePagina(cs, 2, w, fr);
        }
    }

    // ==========================================
    // PAGINA 3: ENTRENAMIENTO Y CARGA SEMANAL
    // ==========================================
    private void pagina3EntrenamientoYCarga(PDDocument doc, Deportista dep, List<ResumenSemanal> semanas, PDFont fb, PDFont fr) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        float w = page.getMediaBox().getWidth();
        float h = page.getMediaBox().getHeight();

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            dibujarEncabezadoPagina(cs, "3. DINÁMICA DE ENTRENAMIENTO Y CARGA", 3, w, h, fb, fr);

            // Tabla semanal
            float curY = h - 100;
            escribirLinea(cs, fb, 13, 50, curY, "Registro Semanal de Carga, Monotonía y Tensión");
            curY -= 15;

            float[] colWidths = {55, 120, 55, 65, 80, 60, 60};
            String[] headers = {"Semana", "Periodo", "Km", "Carga UA", "Media Móvil 3S", "Monotonía", "Tensión"};

            // Cabecera tabla
            cs.setNonStrokingColor(COLOR_PRIMARIO);
            cs.addRect(50, curY - 18, w - 100, 18);
            cs.fill();

            cs.setNonStrokingColor(Color.WHITE);
            float curX = 55;
            for (int i = 0; i < headers.length; i++) {
                escribirLinea(cs, fb, 9, curX, curY - 14, headers[i]);
                curX += colWidths[i];
            }
            curY -= 18;

            // Filas
            int limitFilas = Math.min(10, semanas.size());
            for (int i = 0; i < limitFilas; i++) {
                ResumenSemanal r = semanas.get(i);
                if (i % 2 == 1) {
                    cs.setNonStrokingColor(COLOR_GRIS_FONDO);
                    cs.addRect(50, curY - 16, w - 100, 16);
                    cs.fill();
                }
                cs.setNonStrokingColor(COLOR_TEXTO);
                curX = 55;
                escribirLinea(cs, fr, 9, curX, curY - 12, "Sem " + r.getNumeroSemana());
                curX += colWidths[0];
                escribirLinea(cs, fr, 8, curX, curY - 12, r.getFechaInicio() + " a " + r.getFechaFin());
                curX += colWidths[1];
                escribirLinea(cs, fr, 9, curX, curY - 12, String.format(Locale.US, "%.1f", r.getKilometrosTotales()));
                curX += colWidths[2];
                escribirLinea(cs, fb, 9, curX, curY - 12, String.valueOf(r.getCargaSemanal()));
                curX += colWidths[3];
                escribirLinea(cs, fr, 8, curX, curY - 12, r.getMediaMovilTexto());
                curX += colWidths[4];
                escribirLinea(cs, fr, 9, curX, curY - 12, String.format(Locale.US, "%.2f", r.getMonotonia()));
                curX += colWidths[5];
                escribirLinea(cs, fr, 9, curX, curY - 12, String.format(Locale.US, "%.1f", r.getTensionSemanal()));

                curY -= 16;
            }

            // Insercion de grafico JFreeChart de barras
            curY -= 20;
            escribirLinea(cs, fb, 13, 50, curY, "Evolución Gráfica de la Carga Semanal");
            curY -= 10;

            JFreeChart chartCarga = GeneradorGraficos.crearGraficoCargaSemanal(semanas);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            org.jfree.chart.ChartUtils.writeChartAsPNG(baos, chartCarga, 500, 240);
            PDImageXObject imgChart = PDImageXObject.createFromByteArray(doc, baos.toByteArray(), "chartCarga");
            cs.drawImage(imgChart, 50, curY - 240, w - 100, 240);

            dibujarPiePagina(cs, 3, w, fr);
        }
    }

    // ==========================================
    // PAGINA 4: RENDIMIENTO Y EVOLUCION 5K
    // ==========================================
    private void pagina4RendimientoYEvolucion(PDDocument doc, Deportista dep, ResultadoRegresion reg, PDFont fb, PDFont fr) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        float w = page.getMediaBox().getWidth();
        float h = page.getMediaBox().getHeight();

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            dibujarEncabezadoPagina(cs, "4. RENDIMIENTO DEPORTIVO Y REGRESIÓN LINEAL", 4, w, h, fb, fr);

            float curY = h - 100;
            escribirLinea(cs, fb, 13, 50, curY, "Historial de Pruebas de 5 Km y Comparativa");
            curY -= 15;

            float[] colWidths = {60, 110, 80, 110, 135};
            String[] headers = {"Test #", "Fecha", "Semana (x)", "Marca (s)", "Tiempo (mm:ss)"};

            cs.setNonStrokingColor(COLOR_PRIMARIO);
            cs.addRect(50, curY - 18, w - 100, 18);
            cs.fill();

            cs.setNonStrokingColor(Color.WHITE);
            float curX = 55;
            for (int i = 0; i < headers.length; i++) {
                escribirLinea(cs, fb, 9, curX, curY - 14, headers[i]);
                curX += colWidths[i];
            }
            curY -= 18;

            List<TestRendimiento> tests = dep.getTests();
            for (int i = 0; i < tests.size(); i++) {
                TestRendimiento t = tests.get(i);
                if (i % 2 == 1) {
                    cs.setNonStrokingColor(COLOR_GRIS_FONDO);
                    cs.addRect(50, curY - 16, w - 100, 16);
                    cs.fill();
                }
                cs.setNonStrokingColor(COLOR_TEXTO);
                curX = 55;
                escribirLinea(cs, fr, 9, curX, curY - 12, "Test " + (i + 1));
                curX += colWidths[0];
                escribirLinea(cs, fr, 9, curX, curY - 12, t.getFecha().toString());
                curX += colWidths[1];
                escribirLinea(cs, fr, 9, curX, curY - 12, String.format(Locale.US, "%.1f", t.getSemanasDesdeInicio()));
                curX += colWidths[2];
                escribirLinea(cs, fb, 9, curX, curY - 12, String.format(Locale.US, "%.1f s", t.getMarcaSegundos()));
                curX += colWidths[3];
                escribirLinea(cs, fr, 9, curX, curY - 12, t.getMarcaFormateada());

                curY -= 16;
            }

            // Insercion de parametros matematicos y grafico
            curY -= 15;
            if (reg != null) {
                cs.setNonStrokingColor(COLOR_GRIS_FONDO);
                cs.addRect(50, curY - 32, w - 100, 32);
                cs.fill();
                cs.setStrokingColor(COLOR_SECUNDARIO);
                cs.addRect(50, curY - 32, w - 100, 32);
                cs.stroke();

                cs.setNonStrokingColor(COLOR_TEXTO);
                escribirLinea(cs, fb, 10, 60, curY - 16, "Modelo Lineal: " + reg.getEcuacion() + " | Coeficiente R² = " + String.format(Locale.US, "%.3f", reg.getRCuadrado()));
                escribirLinea(cs, fr, 10, 60, curY - 28, String.format(Locale.US, "Proyección estimada para la Semana %.0f: %.1f s", reg.getSemanaSiguienteX(), reg.getEstimacionSiguienteY()));
                curY -= 42;
            }

            JFreeChart chartMarcas = GeneradorGraficos.crearGraficoEvolucionMarcas(tests, reg);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            org.jfree.chart.ChartUtils.writeChartAsPNG(baos, chartMarcas, 500, 240);
            PDImageXObject imgChart = PDImageXObject.createFromByteArray(doc, baos.toByteArray(), "chartMarcas");
            cs.drawImage(imgChart, 50, curY - 240, w - 100, 240);

            dibujarPiePagina(cs, 4, w, fr);
        }
    }

    // ==========================================
    // PAGINA 5: INTERPRETACION, CONCLUSIONES Y DISCLAIMER
    // ==========================================
    private void pagina5ConclusionesYDisclaimer(PDDocument doc, Deportista dep, List<ResumenSemanal> semanas, ResultadoRegresion reg, PDFont fb, PDFont fr, PDFont fo) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        float w = page.getMediaBox().getWidth();
        float h = page.getMediaBox().getHeight();

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            dibujarEncabezadoPagina(cs, "5. CONCLUSIONES Y RECOMENDACIONES", 5, w, h, fb, fr);

            float curY = h - 110;
            escribirLinea(cs, fb, 14, 50, curY, "Interpretación Automática de Rendimiento");
            curY -= 22;

            // Analisis de tendencia segun pendiente de regresion
            String textoTendencia;
            if (reg != null) {
                if (reg.getPendienteB() < -1.0) {
                    textoTendencia = String.format(Locale.US,
                            "La pendiente de la recta de regresión (b = %.2f s/sem) refleja una tendencia claramente favorable de reducción de marca cronometrada a lo largo del periodo, con un ajuste del modelo R² de %.3f. Esto sugiere que las adaptaciones fisiológicas al estímulo de entrenamiento están consolidándose positivamente.",
                            reg.getPendienteB(), reg.getRCuadrado());
                } else if (reg.getPendienteB() >= -1.0 && reg.getPendienteB() <= 1.0) {
                    textoTendencia = "La pendiente de la recta de regresión evidencia una meseta de rendimiento o estabilización temporal de marcas. Se aconseja revisar la densidad de estímulos de alta intensidad y verificar los periodos de asimilación y descanso regenerativo.";
                } else {
                    textoTendencia = "Se observa una ligera deriva ascendente en los tiempos de prueba. Se sugiere descartar acumulación excesiva de fatiga periférica o sobrecarga muscular transitoria.";
                }
            } else {
                textoTendencia = "Se dispone de volumen suficiente de sesiones pero de número limitado de pruebas estandarizadas, por lo que se recomienda programar un nuevo test de 5 km en las próximas dos semanas.";
            }

            curY = escribirParrafo(cs, fr, 11, 50, curY, w - 100, textoTendencia, 16);

            curY -= 20;
            escribirLinea(cs, fb, 14, 50, curY, "Control de Variabilidad y Fatiga (Monotonía)");
            curY -= 22;

            ResumenSemanal ultSem = semanas.isEmpty() ? null : semanas.get(semanas.size() - 1);
            String textoFatiga;
            if (ultSem != null && ultSem.getMonotonia() > 2.0) {
                textoFatiga = String.format(Locale.US,
                        "En la última semana evaluada se registró una monotonía de %.2f UA (superior al umbral de seguridad de 2.0). Se recomienda introducir mayor variabilidad diaria entre sesiones suaves y exigentes para mitigar picos de tensión innecesarios.",
                        ultSem.getMonotonia());
            } else {
                textoFatiga = "El índice de monotonía semanal se ha mantenido dentro de márgenes equilibrados, lo que indica una distribución adecuada de jornadas de descanso intercaladas y una modulación correcta del esfuerzo.";
            }

            curY = escribirParrafo(cs, fr, 11, 50, curY, w - 100, textoFatiga, 16);

            // Caja de Descargo de Responsabilidad (Disclaimer)
            curY -= 40;
            float discW = w - 100;
            float discH = 110;

            cs.setNonStrokingColor(new Color(254, 249, 231)); // Fondo suave advertencia
            cs.addRect(50, curY - discH, discW, discH);
            cs.fill();

            cs.setStrokingColor(new Color(243, 156, 18));
            cs.setLineWidth(1.5f);
            cs.addRect(50, curY - discH, discW, discH);
            cs.stroke();

            cs.setNonStrokingColor(new Color(125, 102, 8));
            escribirLinea(cs, fb, 11, 65, curY - 22, "AVISO DE EXENCIÓN DE RESPONSABILIDAD (DISCLAIMER):");

            String disclaimer = "Este informe ha sido generado de manera programática mediante modelos matemáticos y algorítmicos estándar de la ciencia deportiva (Foster / Sesión-RPE, Regresión de Mínimos Cuadrados). Los datos e interpretaciones presentadas tienen fines estrictamente metodológicos y pedagógicos, no constituyendo diagnóstico clínico ni prescripción médica facultativa. Cualquier ajuste en la carga debe ser supervisado por personal técnico cualificado.";
            escribirParrafo(cs, fo, 9, 65, curY - 38, discW - 30, disclaimer, 13);

            dibujarPiePagina(cs, 5, w, fr);
        }
    }

    // ==========================================
    // HELPERS DE MAQUETADO Y DIBUJO
    // ==========================================
    private void dibujarEncabezadoPagina(PDPageContentStream cs, String tituloSeccion, int numPagina, float w, float h, PDFont fb, PDFont fr) throws IOException {
        cs.setNonStrokingColor(COLOR_PRIMARIO);
        cs.beginText();
        cs.setFont(fb, 14);
        cs.newLineAtOffset(50, h - 50);
        cs.showText(tituloSeccion);
        cs.endText();

        cs.setNonStrokingColor(COLOR_SECUNDARIO);
        cs.beginText();
        cs.setFont(fr, 10);
        cs.newLineAtOffset(w - 220, h - 50);
        cs.showText(NOMBRE_CLUB);
        cs.endText();

        cs.setStrokingColor(new Color(220, 225, 230));
        cs.setLineWidth(1f);
        cs.moveTo(50, h - 60);
        cs.lineTo(w - 50, h - 60);
        cs.stroke();
    }

    private void dibujarPiePagina(PDPageContentStream cs, int numPagina, float w, PDFont fr) throws IOException {
        cs.setStrokingColor(new Color(220, 225, 230));
        cs.setLineWidth(1f);
        cs.moveTo(50, 50);
        cs.lineTo(w - 50, 50);
        cs.stroke();

        cs.setNonStrokingColor(new Color(130, 145, 160));
        escribirLinea(cs, fr, 9, 50, 36, "Informe Confidencial para Uso Técnico del Deportista");
        escribirLinea(cs, fr, 9, w - 130, 36, "Página " + numPagina + " de 5");
    }

    private void dibujarTarjetaKpi(PDPageContentStream cs, float x, float y, float w, float h, String titulo, String valor, String sub, PDFont fb, PDFont fr) throws IOException {
        cs.setNonStrokingColor(COLOR_GRIS_FONDO);
        cs.addRect(x, y, w, h);
        cs.fill();

        cs.setStrokingColor(COLOR_SECUNDARIO);
        cs.setLineWidth(1.5f);
        cs.addRect(x, y, w, h);
        cs.stroke();

        cs.setNonStrokingColor(new Color(100, 120, 140));
        escribirLinea(cs, fb, 10, x + 12, y + h - 22, titulo);

        cs.setNonStrokingColor(COLOR_PRIMARIO);
        escribirLinea(cs, fb, 18, x + 12, y + h - 48, valor);

        cs.setNonStrokingColor(new Color(120, 140, 160));
        escribirLinea(cs, fr, 9, x + 12, y + h - 66, sub);
    }

    private void escribirLinea(PDPageContentStream cs, PDFont font, float tam, float x, float y, String texto) throws IOException {
        cs.beginText();
        cs.setFont(font, tam);
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
    }

    private float escribirParrafo(PDPageContentStream cs, PDFont font, float tam, float x, float y, float anchoMax, String texto, float interlineado) throws IOException {
        String[] palabras = texto.split("\\s+");
        StringBuilder lineaActual = new StringBuilder();
        float curY = y;

        for (String palabra : palabras) {
            String prueba = lineaActual.isEmpty() ? palabra : lineaActual + " " + palabra;
            float anchoPrueba = (font.getStringWidth(prueba) / 1000f) * tam;
            if (anchoPrueba > anchoMax) {
                escribirLinea(cs, font, tam, x, curY, lineaActual.toString());
                curY -= interlineado;
                lineaActual = new StringBuilder(palabra);
            } else {
                lineaActual = new StringBuilder(prueba);
            }
        }
        if (!lineaActual.isEmpty()) {
            escribirLinea(cs, font, tam, x, curY, lineaActual.toString());
            curY -= interlineado;
        }
        return curY;
    }
}

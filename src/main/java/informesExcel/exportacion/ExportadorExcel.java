package informesExcel.exportacion;

import informesExcel.modelo.Deportista;
import informesExcel.modelo.SesionEntrenamiento;
import informesExcel.modelo.TestRendimiento;
import informesExcel.servicio.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.JFreeChart;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de exportacion a Microsoft Excel (.xlsx) con Apache POI.
 * Genera un libro completo con 5 hojas obligatorias: Resumen, Deportistas,
 * Entrenamientos, Analisis y Rendimiento, aplicando formatos condicionales,
 * formulas nativas, inmovilizacion de paneles y autofiltros.
 */
public class ExportadorExcel {

    public void exportarLibroCompleto(List<Deportista> deportistas, Path rutaDestino) throws IOException {
        if (rutaDestino.getParent() != null && !Files.exists(rutaDestino.getParent())) {
            Files.createDirectories(rutaDestino.getParent());
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle estiloCabecera = crearEstiloCabecera(workbook);
            CellStyle estiloDecimal = crearEstiloDecimal(workbook);
            CellStyle estiloPorcentaje = crearEstiloPorcentaje(workbook);
            CellStyle estiloFecha = crearEstiloFecha(workbook);
            CellStyle estiloEntero = crearEstiloEntero(workbook);

            // 1. Hoja Resumen
            crearHojaResumen(workbook, deportistas, estiloCabecera, estiloDecimal, estiloEntero);

            // 2. Hoja Deportistas
            crearHojaDeportistas(workbook, deportistas, estiloCabecera, estiloDecimal, estiloEntero);

            // 3. Hoja Entrenamientos
            crearHojaEntrenamientos(workbook, deportistas, estiloCabecera, estiloDecimal, estiloFecha, estiloEntero);

            // 4. Hoja Analisis
            crearHojaAnalisis(workbook, deportistas, estiloCabecera, estiloDecimal, estiloPorcentaje, estiloEntero);

            // 5. Hoja Rendimiento
            crearHojaRendimiento(workbook, deportistas, estiloCabecera, estiloDecimal, estiloFecha);

            try (FileOutputStream fos = new FileOutputStream(rutaDestino.toFile())) {
                workbook.write(fos);
            }
        }
    }

    private void crearHojaResumen(Workbook wb, List<Deportista> deportistas, CellStyle header, CellStyle dec, CellStyle ent) {
        Sheet sheet = wb.createSheet("Resumen");
        sheet.setDisplayGridlines(true);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RESUMEN GLOBAL DEL CLUB DE RENDIMIENTO DEPORTIVO");
        CellStyle titleStyle = wb.createCellStyle();
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        String[] headers = {"Métrica Global", "Valor Acumulado", "Unidad / Referencia"};
        Row hRow = sheet.createRow(2);
        for (int i = 0; i < headers.length; i++) {
            Cell c = hRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(header);
        }

        int totalAtletas = deportistas.size();
        int totalSesiones = deportistas.stream().mapToInt(d -> d.getSesiones().size()).sum();
        double totalKm = deportistas.stream().mapToDouble(Deportista::getKilometrosTotales).sum();
        int totalCarga = deportistas.stream().mapToInt(Deportista::getCargaTotalFoster).sum();

        Object[][] metricas = {
                {"Total Atletas Federados", (double) totalAtletas, "deportistas", ent},
                {"Sesiones de Entrenamiento Realizadas", (double) totalSesiones, "sesiones", ent},
                {"Distancia Total Recorrida", totalKm, "kilómetros", dec},
                {"Carga Total de Entrenamiento (Foster)", (double) totalCarga, "Unidades Arbitrarias (UA)", ent}
        };

        int rIdx = 3;
        for (Object[] m : metricas) {
            Row r = sheet.createRow(rIdx++);
            r.createCell(0).setCellValue((String) m[0]);
            Cell valCell = r.createCell(1);
            valCell.setCellValue((Double) m[1]);
            valCell.setCellStyle((CellStyle) m[3]);
            r.createCell(2).setCellValue((String) m[2]);
        }

        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);
    }

    private void crearHojaDeportistas(Workbook wb, List<Deportista> deportistas, CellStyle header, CellStyle dec, CellStyle ent) {
        Sheet sheet = wb.createSheet("Deportistas");
        sheet.setDisplayGridlines(true);
        sheet.createFreezePane(0, 1);

        String[] headers = {"ID", "Nombre", "Edad", "Categoría", "Objetivo Competición", "Marca Inicial 5K (s)", "Km Totales", "Sesiones"};
        Row hRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell c = hRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(header);
        }

        int rIdx = 1;
        for (Deportista d : deportistas) {
            Row r = sheet.createRow(rIdx++);
            r.createCell(0).setCellValue(d.getId());
            r.createCell(1).setCellValue(d.getNombre());

            Cell cEdad = r.createCell(2);
            cEdad.setCellValue(d.getEdad());
            cEdad.setCellStyle(ent);

            r.createCell(3).setCellValue(d.getCategoria());
            r.createCell(4).setCellValue(d.getObjetivoCompeticion());

            Cell cMarca = r.createCell(5);
            cMarca.setCellValue(d.getMarcaInicial5k());
            cMarca.setCellStyle(dec);

            Cell cKm = r.createCell(6);
            cKm.setCellValue(d.getKilometrosTotales());
            cKm.setCellStyle(dec);

            Cell cSes = r.createCell(7);
            cSes.setCellValue(d.getSesiones().size());
            cSes.setCellStyle(ent);
        }

        sheet.setAutoFilter(new CellRangeAddress(0, Math.max(1, rIdx - 1), 0, headers.length - 1));
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }

    private void crearHojaEntrenamientos(Workbook wb, List<Deportista> deportistas, CellStyle header, CellStyle dec, CellStyle fechaEstilo, CellStyle ent) {
        Sheet sheet = wb.createSheet("Entrenamientos");
        sheet.setDisplayGridlines(true);
        sheet.createFreezePane(0, 1);

        String[] headers = {"ID Atleta", "Deportista", "Fecha", "Tipo Sesión", "Duración (min)", "Distancia (km)", "FC Media", "RPE", "Carga Foster (UA)"};
        Row hRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell c = hRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(header);
        }

        int rIdx = 1;
        for (Deportista d : deportistas) {
            for (SesionEntrenamiento s : d.getSesiones()) {
                Row r = sheet.createRow(rIdx++);
                r.createCell(0).setCellValue(d.getId());
                r.createCell(1).setCellValue(d.getNombre());

                Cell cFecha = r.createCell(2);
                cFecha.setCellValue(s.getFecha().toString());
                cFecha.setCellStyle(fechaEstilo);

                r.createCell(3).setCellValue(s.getTipo());

                Cell cDur = r.createCell(4);
                cDur.setCellValue(s.getDuracionMinutos());
                cDur.setCellStyle(ent);

                Cell cKm = r.createCell(5);
                cKm.setCellValue(s.getDistanciaKm());
                cKm.setCellStyle(dec);

                Cell cFc = r.createCell(6);
                cFc.setCellValue(s.getFcMedia());
                cFc.setCellStyle(ent);

                Cell cRpe = r.createCell(7);
                cRpe.setCellValue(s.getRpe());
                cRpe.setCellStyle(ent);

                Cell cCarga = r.createCell(8);
                cCarga.setCellValue(s.getCargaFoster());
                cCarga.setCellStyle(ent);
            }
        }

        // Fila de totales con formulas nativas de Excel: SUM y AVERAGE
        if (rIdx > 1) {
            Row totRow = sheet.createRow(rIdx);
            totRow.createCell(1).setCellValue("TOTALES Y PROMEDIOS:");
            totRow.getCell(1).setCellStyle(header);

            Cell sumDur = totRow.createCell(4);
            sumDur.setCellFormula(String.format("SUM(E2:E%d)", rIdx));
            sumDur.setCellStyle(ent);

            Cell sumKm = totRow.createCell(5);
            sumKm.setCellFormula(String.format("SUM(F2:F%d)", rIdx));
            sumKm.setCellStyle(dec);

            Cell avgRpe = totRow.createCell(7);
            avgRpe.setCellFormula(String.format("AVERAGE(H2:H%d)", rIdx));
            avgRpe.setCellStyle(dec);

            Cell sumCarga = totRow.createCell(8);
            sumCarga.setCellFormula(String.format("SUM(I2:I%d)", rIdx));
            sumCarga.setCellStyle(ent);

            sheet.setAutoFilter(new CellRangeAddress(0, rIdx - 1, 0, headers.length - 1));
        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }

    private void crearHojaAnalisis(Workbook wb, List<Deportista> deportistas, CellStyle header, CellStyle dec, CellStyle porc, CellStyle ent) {
        Sheet sheet = wb.createSheet("Análisis");
        sheet.setDisplayGridlines(true);
        sheet.createFreezePane(0, 1);

        String[] headers = {"Atleta", "Semana", "Inicio (Lunes)", "Fin (Domingo)", "Distancia (km)", "Tiempo (min)", "Carga Semanal", "MM 3 Semanas", "Monotonía", "Tensión Semanal"};
        Row hRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell c = hRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(header);
        }

        int rIdx = 1;
        int filaInicioDatos = 2; // Fila 2 en base 1 de Excel
        for (Deportista d : deportistas) {
            List<ResumenSemanal> semanas = CalculadoraRendimiento.calcularResumenesSemanales(d.getSesiones());
            for (ResumenSemanal sem : semanas) {
                Row r = sheet.createRow(rIdx++);
                r.createCell(0).setCellValue(d.getNombre());
                r.createCell(1).setCellValue("Semana " + sem.getNumeroSemana());
                r.createCell(2).setCellValue(sem.getFechaInicio().toString());
                r.createCell(3).setCellValue(sem.getFechaFin().toString());

                Cell cKm = r.createCell(4);
                cKm.setCellValue(sem.getKilometrosTotales());
                cKm.setCellStyle(dec);

                Cell cMin = r.createCell(5);
                cMin.setCellValue(sem.getMinutosTotales());
                cMin.setCellStyle(ent);

                Cell cCarga = r.createCell(6);
                cCarga.setCellValue(sem.getCargaSemanal());
                cCarga.setCellStyle(ent);

                Cell cMm = r.createCell(7);
                if (sem.getMediaMovil3Semanas() != null) {
                    cMm.setCellValue(sem.getMediaMovil3Semanas());
                    cMm.setCellStyle(dec);
                } else {
                    cMm.setCellValue("Datos insuficientes");
                }

                Cell cMono = r.createCell(8);
                cMono.setCellValue(sem.getMonotonia());
                cMono.setCellStyle(dec);

                Cell cTens = r.createCell(9);
                cTens.setCellValue(sem.getTensionSemanal());
                cTens.setCellStyle(dec);
            }
        }

        // Regla de formato condicional: Resaltar en fondo ambar/rojo si Monotonia > 2.0 (sobreentrenamiento)
        if (rIdx > 1) {
            SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
            ConditionalFormattingRule ruleMonotonia = sheetCF.createConditionalFormattingRule(ComparisonOperator.GT, "2.0");
            PatternFormatting fill = ruleMonotonia.createPatternFormatting();
            fill.setFillBackgroundColor(IndexedColors.ROSE.getIndex());
            fill.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

            CellRangeAddress[] regiones = {new CellRangeAddress(1, rIdx - 1, 8, 8)};
            sheetCF.addConditionalFormatting(regiones, ruleMonotonia);
        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }

    private void crearHojaRendimiento(Workbook wb, List<Deportista> deportistas, CellStyle header, CellStyle dec, CellStyle fechaEstilo) {
        Sheet sheet = wb.createSheet("Rendimiento");
        sheet.setDisplayGridlines(true);
        sheet.createFreezePane(0, 1);

        String[] headers = {"Atleta", "Fecha Test", "Semana (x)", "Marca (seg)", "Tiempo (mm:ss)", "Diferencia Marca Inicial (s)"};
        Row hRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell c = hRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(header);
        }

        int rIdx = 1;
        for (Deportista d : deportistas) {
            for (TestRendimiento t : d.getTests()) {
                Row r = sheet.createRow(rIdx++);
                r.createCell(0).setCellValue(d.getNombre());

                Cell cFecha = r.createCell(1);
                cFecha.setCellValue(t.getFecha().toString());
                cFecha.setCellStyle(fechaEstilo);

                Cell cSem = r.createCell(2);
                cSem.setCellValue(t.getSemanasDesdeInicio());
                cSem.setCellStyle(dec);

                Cell cMarca = r.createCell(3);
                cMarca.setCellValue(t.getMarcaSegundos());
                cMarca.setCellStyle(dec);

                r.createCell(4).setCellValue(t.getMarcaFormateada());

                Cell cDiff = r.createCell(5);
                double diff = t.getMarcaSegundos() - d.getMarcaInicial5k();
                cDiff.setCellValue(diff);
                cDiff.setCellStyle(dec);
            }
        }

        // Insertar imagen de grafico de evolucion deportiva generado con JFreeChart si hay atletas
        if (!deportistas.isEmpty() && !deportistas.get(0).getTests().isEmpty()) {
            Deportista primerAtleta = deportistas.get(0);
            Optional<ResultadoRegresion> reg = CalculadoraRendimiento.calcularRegresionLineal(primerAtleta.getTests());
            JFreeChart chart = GeneradorGraficos.crearGraficoEvolucionMarcas(primerAtleta.getTests(), reg.orElse(null));

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                org.jfree.chart.ChartUtils.writeChartAsPNG(baos, chart, 560, 320);
                int pictureIdx = wb.addPicture(baos.toByteArray(), Workbook.PICTURE_TYPE_PNG);

                CreationHelper helper = wb.getCreationHelper();
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(7);
                anchor.setRow1(2);
                Picture pict = drawing.createPicture(anchor, pictureIdx);
                pict.resize();
            } catch (Exception ignored) {
            }
        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
    }

    private CellStyle crearEstiloCabecera(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloDecimal(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();
        style.setDataFormat(df.getFormat("0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloPorcentaje(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();
        style.setDataFormat(df.getFormat("0.0%"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloEntero(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();
        style.setDataFormat(df.getFormat("#,##0"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloFecha(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}

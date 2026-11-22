package informesExcel.exportacion;

import informesExcel.modelo.Deportista;
import informesExcel.servicio.GeneradorDatosDemo;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExportadorExcelTest {

    @Test
    void testExportacionCompleta5Hojas(@TempDir Path tempDir) throws IOException {
        ExportadorExcel exportador = new ExportadorExcel();
        Path archivoExcel = tempDir.resolve("informe_completo_club.xlsx");

        List<Deportista> atletas = GeneradorDatosDemo.generarAtletasDemo();
        exportador.exportarLibroCompleto(atletas, archivoExcel);

        assertTrue(archivoExcel.toFile().exists());
        assertTrue(archivoExcel.toFile().length() > 5000);

        // Abrir y validar las 5 hojas obligatorias con POI
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(archivoExcel.toFile()))) {
            assertEquals(5, wb.getNumberOfSheets());
            assertNotNull(wb.getSheet("Resumen"));
            assertNotNull(wb.getSheet("Deportistas"));
            assertNotNull(wb.getSheet("Entrenamientos"));
            assertNotNull(wb.getSheet("Análisis"));
            assertNotNull(wb.getSheet("Rendimiento"));

            Sheet entSheet = wb.getSheet("Entrenamientos");
            assertTrue(entSheet.getLastRowNum() > 10);

            Sheet analSheet = wb.getSheet("Análisis");
            assertEquals(1, analSheet.getSheetConditionalFormatting().getNumConditionalFormattings());
        }
    }
}

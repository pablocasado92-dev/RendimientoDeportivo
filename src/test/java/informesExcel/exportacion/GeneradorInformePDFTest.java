package informesExcel.exportacion;

import informesExcel.modelo.Deportista;
import informesExcel.servicio.GeneradorDatosDemo;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneradorInformePDFTest {

    @Test
    void testGeneracionPdf5PaginasCompletas(@TempDir Path tempDir) throws IOException {
        GeneradorInformePDF generador = new GeneradorInformePDF();
        Path archivoPdf = tempDir.resolve("informe_prueba_atleta.pdf");

        List<Deportista> atletas = GeneradorDatosDemo.generarAtletasDemo();
        assertFalse(atletas.isEmpty());

        Deportista atleta = atletas.get(0);
        generador.generarInformeIndividual(atleta, archivoPdf);

        assertTrue(archivoPdf.toFile().exists());
        assertTrue(archivoPdf.toFile().length() > 10000); // Con graficos pesa mas de 10 KB

        // Verificar con PDFBox que contiene exactamente 5 paginas
        try (PDDocument doc = Loader.loadPDF(archivoPdf.toFile())) {
            assertEquals(5, doc.getNumberOfPages());
        }
    }
}

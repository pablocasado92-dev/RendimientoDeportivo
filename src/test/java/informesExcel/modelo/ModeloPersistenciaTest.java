package informesExcel.modelo;

import informesExcel.servicio.GeneradorDatosDemo;
import informesExcel.servicio.GestorPersistencia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para los POJOs del modelo y el gestor de persistencia JSON.
 */
public class ModeloPersistenciaTest {

    @Test
    void testCreacionSesionEntrenamientoYCalculoCargaFoster() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(
                LocalDate.of(2026, 11, 2), "Series", 60, 10.5, 168, 8);

        assertEquals(LocalDate.of(2026, 11, 2), sesion.getFecha());
        assertEquals("Series", sesion.getTipo());
        assertEquals(60, sesion.getDuracionMinutos());
        assertEquals(10.5, sesion.getDistanciaKm(), 0.001);
        assertEquals(8, sesion.getRpe());
        // Foster: 60 * 8 = 480 UA
        assertEquals(480, sesion.getCargaFoster());
    }

    @Test
    void testValidacionRpeInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                new SesionEntrenamiento(LocalDate.now(), "Rodaje", 40, 6.0, 140, 11));
        assertThrows(IllegalArgumentException.class, () ->
                new SesionEntrenamiento(LocalDate.now(), "Rodaje", 40, 6.0, 140, 0));
    }

    @Test
    void testTestRendimientoFormatoMarca() {
        TestRendimiento test = new TestRendimiento(LocalDate.of(2026, 11, 8), 1145.5, 1.0);
        assertEquals(1145.5, test.getMarcaSegundos(), 0.001);
        assertEquals(1.0, test.getSemanasDesdeInicio(), 0.001);
        // 1145.5s = 19 minutos y 5.5s -> "19:05.5"
        assertEquals("19:05.5", test.getMarcaFormateada());
    }

    @Test
    void testPersistenciaJsonIdaYVuelta(@TempDir Path tempDir) throws IOException {
        GestorPersistencia persistencia = new GestorPersistencia();
        Path archivoJson = tempDir.resolve("datos_prueba.json");

        List<Deportista> demoOriginal = GeneradorDatosDemo.generarAtletasDemo();
        assertEquals(3, demoOriginal.size());

        persistencia.guardar(demoOriginal, archivoJson);
        assertTrue(archivoJson.toFile().exists());
        assertTrue(archivoJson.toFile().length() > 500);

        List<Deportista> cargados = persistencia.cargar(archivoJson);
        assertEquals(3, cargados.size());
        assertEquals("Carlos Gomez", cargados.get(0).getNombre());
        assertFalse(cargados.get(0).getSesiones().isEmpty());
        assertFalse(cargados.get(0).getTests().isEmpty());
    }
}

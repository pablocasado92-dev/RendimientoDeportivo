package informesExcel.servicio;

import informesExcel.modelo.SesionEntrenamiento;
import informesExcel.modelo.TestRendimiento;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el motor matematico y de calculo deportivo.
 */
public class CalculadoraRendimientoTest {

    @Test
    void testCalculoResumenSemanalFosterYMediaMovil() {
        List<SesionEntrenamiento> sesiones = new ArrayList<>();
        LocalDate lunesSem1 = LocalDate.of(2026, 11, 2);

        // Semana 1: 3 entrenamientos (Lunes 400 UA, Miercoles 300 UA, Viernes 500 UA) -> Carga = 1200 UA
        sesiones.add(new SesionEntrenamiento(lunesSem1, "Rodaje", 50, 8.0, 140, 8)); // 400
        sesiones.add(new SesionEntrenamiento(lunesSem1.plusDays(2), "Series", 30, 5.0, 160, 10)); // 300
        sesiones.add(new SesionEntrenamiento(lunesSem1.plusDays(4), "Rodaje", 50, 8.0, 145, 10)); // 500

        // Semana 2: Carga = 1500 UA
        LocalDate lunesSem2 = lunesSem1.plusWeeks(1);
        sesiones.add(new SesionEntrenamiento(lunesSem2, "Tirada", 150, 20.0, 140, 10)); // 1500

        // Semana 3: Carga = 900 UA
        LocalDate lunesSem3 = lunesSem1.plusWeeks(2);
        sesiones.add(new SesionEntrenamiento(lunesSem3, "Recuperacion", 90, 10.0, 130, 10)); // 900

        List<ResumenSemanal> resumenes = CalculadoraRendimiento.calcularResumenesSemanales(sesiones);
        assertEquals(3, resumenes.size());

        // Semana 1: MM3 es null (datos insuficientes)
        assertEquals(1200, resumenes.get(0).getCargaSemanal());
        assertNull(resumenes.get(0).getMediaMovil3Semanas());
        assertEquals("Datos insuficientes", resumenes.get(0).getMediaMovilTexto());

        // Semana 2: MM3 es null
        assertEquals(1500, resumenes.get(1).getCargaSemanal());
        assertNull(resumenes.get(1).getMediaMovil3Semanas());

        // Semana 3: MM3 = (1200 + 1500 + 900) / 3 = 1200.0 UA
        assertEquals(900, resumenes.get(2).getCargaSemanal());
        assertNotNull(resumenes.get(2).getMediaMovil3Semanas());
        assertEquals(1200.0, resumenes.get(2).getMediaMovil3Semanas(), 0.001);
        assertEquals("1200.0 UA", resumenes.get(2).getMediaMovilTexto());

        // Monotonia y Tension positivas
        assertTrue(resumenes.get(0).getMonotonia() > 0);
        assertTrue(resumenes.get(0).getTensionSemanal() > 0);
    }

    @Test
    void testEstadisticaDescriptivaCalculos() {
        double[] datos = {10.0, 20.0, 30.0, 40.0, 50.0};
        EstadisticaDescriptiva est = CalculadoraRendimiento.calcularEstadisticaDescriptiva(datos);

        assertEquals(30.0, est.getMedia(), 0.001);
        assertEquals(30.0, est.getMediana(), 0.001);
        assertEquals(10.0, est.getMinimo(), 0.001);
        assertEquals(50.0, est.getMaximo(), 0.001);
        // Desviacion tipica poblacional de {10,20,30,40,50}: sqrt(200) ~= 14.142
        assertEquals(14.142, est.getDesviacionTipica(), 0.01);
        // CV = (14.142 / 30.0) * 100 ~= 47.14%
        assertEquals(47.14, est.getCoeficienteVariacion(), 0.1);
    }

    @Test
    void testRegresionLinealYEstimacionSiguienteSemana() {
        List<TestRendimiento> tests = new ArrayList<>();
        // y = 1200 - 10x
        tests.add(new TestRendimiento(LocalDate.of(2026, 10, 1), 1190.0, 1.0));
        tests.add(new TestRendimiento(LocalDate.of(2026, 10, 8), 1180.0, 2.0));
        tests.add(new TestRendimiento(LocalDate.of(2026, 10, 15), 1170.0, 3.0));
        tests.add(new TestRendimiento(LocalDate.of(2026, 10, 22), 1160.0, 4.0));

        Optional<ResultadoRegresion> regOpt = CalculadoraRendimiento.calcularRegresionLineal(tests);
        assertTrue(regOpt.isPresent());

        ResultadoRegresion reg = regOpt.get();
        assertEquals(-10.0, reg.getPendienteB(), 0.001);
        assertEquals(1200.0, reg.getInterceptoA(), 0.001);
        assertEquals(1.0, reg.getRCuadrado(), 0.001); // Ajuste perfecto R^2 = 1.0
        assertEquals(5.0, reg.getSemanaSiguienteX(), 0.001);
        assertEquals(1150.0, reg.getEstimacionSiguienteY(), 0.001); // y(5) = 1200 - 50 = 1150
    }

    @Test
    void testCorrelacionPearson() {
        double[] x = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yDirecta = {2.0, 4.0, 6.0, 8.0, 10.0};
        double[] yInversa = {10.0, 8.0, 6.0, 4.0, 2.0};

        assertEquals(1.0, CalculadoraRendimiento.calcularCorrelacionPearson(x, yDirecta), 0.001);
        assertEquals(-1.0, CalculadoraRendimiento.calcularCorrelacionPearson(x, yInversa), 0.001);
    }
}

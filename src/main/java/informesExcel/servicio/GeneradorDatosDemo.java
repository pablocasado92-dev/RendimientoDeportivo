package informesExcel.servicio;

import informesExcel.modelo.Deportista;
import informesExcel.modelo.SesionEntrenamiento;
import informesExcel.modelo.TestRendimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Generador de datos de demostracion realistas para 3 atletas con 10 semanas de entrenamientos y tests.
 * Facilita la evaluacion inmediata de graficos, informes Excel y documentos PDF.
 */
public final class GeneradorDatosDemo {

    private GeneradorDatosDemo() {
    }

    public static List<Deportista> generarAtletasDemo() {
        List<Deportista> atletas = new ArrayList<>();
        LocalDate fechaBase = LocalDate.now().minusWeeks(10).with(java.time.DayOfWeek.MONDAY);

        // 1. Carlos Gomez (Senior, Fondo)
        Deportista carlos = new Deportista("DEP-001", "Carlos Gomez", 26, "Senior", "Sub 17:30 en 5K Valencia", 1140.0, null);
        poblarAtleta(carlos, fechaBase, 1140.0, -12.5, 1.1);
        atletas.add(carlos);

        // 2. Lucia Morales (Sub-23, Mediofondo)
        Deportista lucia = new Deportista("DEP-002", "Lucia Morales", 21, "Sub-23", "Podio Campeonato Autonomico", 1260.0, null);
        poblarAtleta(lucia, fechaBase, 1260.0, -9.0, 0.95);
        atletas.add(lucia);

        // 3. Marcos Ruiz (Master 40, Popular Competitivo)
        Deportista marcos = new Deportista("DEP-003", "Marcos Ruiz", 42, "Master 40", "Bajar de 19 minutos en 5K", 1210.0, null);
        poblarAtleta(marcos, fechaBase, 1210.0, -6.0, 0.85);
        atletas.add(marcos);

        return atletas;
    }

    private static void poblarAtleta(Deportista dep, LocalDate inicio, double marcaInicial, double progresoPorSemana, double factorVolumen) {
        // Generar 10 semanas de entrenamientos (4 a 5 sesiones por semana)
        for (int sem = 0; sem < 10; sem++) {
            LocalDate lunes = inicio.plusWeeks(sem);

            // Lunes: Rodaje suave
            dep.agregarSesion(new SesionEntrenamiento(lunes, "Rodaje Suave", (int) (45 * factorVolumen), 8.5 * factorVolumen, 142, 5));
            // Martes: Series o cambios de ritmo
            dep.agregarSesion(new SesionEntrenamiento(lunes.plusDays(1), "Series 1000m", (int) (55 * factorVolumen), 10.2 * factorVolumen, 172, 8));
            // Jueves: Rodaje a ritmo controlado
            dep.agregarSesion(new SesionEntrenamiento(lunes.plusDays(3), "Ritmo Controlado", (int) (50 * factorVolumen), 9.8 * factorVolumen, 158, 7));
            // Sabado: Tirada larga progresiva
            dep.agregarSesion(new SesionEntrenamiento(lunes.plusDays(5), "Tirada Larga", (int) (75 * factorVolumen), 14.5 * factorVolumen, 150, 6));

            // Cada 2 semanas: Test de rendimiento en 5K
            if (sem % 2 == 1 || sem == 0) {
                double marca = Math.max(900.0, marcaInicial + (sem * progresoPorSemana) + ((sem % 3 == 0) ? 2.5 : -1.5));
                dep.agregarTest(new TestRendimiento(lunes.plusDays(6), marca, sem + 1.0));
            }
        }
    }
}

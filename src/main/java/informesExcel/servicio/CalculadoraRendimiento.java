package informesExcel.servicio;

import informesExcel.modelo.SesionEntrenamiento;
import informesExcel.modelo.TestRendimiento;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Motor matematico y de calculo de rendimiento deportivo (Foster, Monotonia, Tension,
 * Estadistica descriptiva, Regresion Lineal y Correlacion de Pearson).
 */
public final class CalculadoraRendimiento {

    private CalculadoraRendimiento() {
    }

    /**
     * Agrupa sesiones en semanas naturales (Lunes a Domingo) y calcula indicadores semanales de Foster.
     */
    public static List<ResumenSemanal> calcularResumenesSemanales(List<SesionEntrenamiento> sesiones) {
        if (sesiones == null || sesiones.isEmpty()) {
            return Collections.emptyList();
        }

        // Ordenar cronologicamente
        List<SesionEntrenamiento> ordenadas = sesiones.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SesionEntrenamiento::getFecha))
                .collect(Collectors.toList());

        if (ordenadas.isEmpty()) {
            return Collections.emptyList();
        }

        // Agrupar por fecha de inicio de semana (Lunes)
        Map<LocalDate, List<SesionEntrenamiento>> porLunes = new TreeMap<>();
        for (SesionEntrenamiento s : ordenadas) {
            LocalDate lunes = s.getFecha().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            porLunes.computeIfAbsent(lunes, k -> new ArrayList<>()).add(s);
        }

        List<ResumenSemanal> resumenes = new ArrayList<>();
        List<Integer> historicoCargas = new ArrayList<>();
        int numSemana = 1;

        for (Map.Entry<LocalDate, List<SesionEntrenamiento>> entry : porLunes.entrySet()) {
            LocalDate lunes = entry.getKey();
            LocalDate domingo = lunes.plusDays(6);
            List<SesionEntrenamiento> sesionesSemana = entry.getValue();

            double kmTotal = sesionesSemana.stream().mapToDouble(SesionEntrenamiento::getDistanciaKm).sum();
            int minTotal = sesionesSemana.stream().mapToInt(SesionEntrenamiento::getDuracionMinutos).sum();
            int cargaSemanal = sesionesSemana.stream().mapToInt(SesionEntrenamiento::getCargaFoster).sum();
            historicoCargas.add(cargaSemanal);

            // Media movil de 3 semanas: [C(t) + C(t-1) + C(t-2)] / 3
            Double mm3 = null;
            if (historicoCargas.size() >= 3) {
                int n = historicoCargas.size();
                mm3 = (historicoCargas.get(n - 1) + historicoCargas.get(n - 2) + historicoCargas.get(n - 3)) / 3.0;
            }

            // Monotonia y Tension: vector de 7 dias (Lunes=0 .. Domingo=6, descanso = 0 UA)
            double[] cargasPorDia = new double[7];
            for (SesionEntrenamiento s : sesionesSemana) {
                int diaIndex = s.getFecha().getDayOfWeek().getValue() - 1; // Lunes=1 -> 0
                if (diaIndex >= 0 && diaIndex < 7) {
                    cargasPorDia[diaIndex] += s.getCargaFoster();
                }
            }

            double mediaDiaria = cargaSemanal / 7.0;
            double sumaDiferenciasCuadradas = 0.0;
            for (double cd : cargasPorDia) {
                double diff = cd - mediaDiaria;
                sumaDiferenciasCuadradas += diff * diff;
            }
            // Desviacion estandar poblacional (N = 7)
            double desvDiaria = Math.sqrt(sumaDiferenciasCuadradas / 7.0);

            // Monotonia = Media Diaria / Desviacion Tipica Diaria
            double monotonia = 0.0;
            if (desvDiaria > 0.000001) {
                monotonia = mediaDiaria / desvDiaria;
            } else if (mediaDiaria > 0) {
                monotonia = 10.0; // Caso especial: variabilidad cero con entrenamiento uniforme
            }

            // Tension Semanal = Carga Semanal * Monotonia
            double tension = cargaSemanal * monotonia;

            resumenes.add(new ResumenSemanal(
                    numSemana++, lunes, domingo, kmTotal, minTotal, cargaSemanal,
                    mm3, mediaDiaria, desvDiaria, monotonia, tension
            ));
        }

        return resumenes;
    }

    /**
     * Calcula estadisticas descriptivas basicas (media, mediana, min, max, desviacion y CV%).
     */
    public static EstadisticaDescriptiva calcularEstadisticaDescriptiva(double[] valores) {
        if (valores == null || valores.length == 0) {
            return new EstadisticaDescriptiva(0, 0, 0, 0, 0, 0);
        }

        int n = valores.length;
        double sum = 0.0;
        double min = valores[0];
        double max = valores[0];

        for (double v : valores) {
            sum += v;
            if (v < min) min = v;
            if (v > max) max = v;
        }

        double media = sum / n;

        // Mediana
        double[] ordenados = Arrays.copyOf(valores, n);
        Arrays.sort(ordenados);
        double mediana;
        if (n % 2 == 1) {
            mediana = ordenados[n / 2];
        } else {
            mediana = (ordenados[(n / 2) - 1] + ordenados[n / 2]) / 2.0;
        }

        // Desviacion estandar poblacional
        double sumaVar = 0.0;
        for (double v : valores) {
            double d = v - media;
            sumaVar += d * d;
        }
        double desviacion = Math.sqrt(sumaVar / n);

        // Coeficiente de Variacion: (desviacion / media) * 100
        double cv = (media != 0.0) ? (desviacion / media) * 100.0 : 0.0;

        return new EstadisticaDescriptiva(media, mediana, min, max, desviacion, cv);
    }

    /**
     * Ajusta la recta de regresion lineal y = a + bx a partir de los tests de rendimiento.
     * x: semanas transcurridas; y: marca en segundos.
     */
    public static Optional<ResultadoRegresion> calcularRegresionLineal(List<TestRendimiento> tests) {
        if (tests == null || tests.size() < 2) {
            return Optional.empty();
        }

        int n = tests.size();
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;
        double maxSemana = tests.get(0).getSemanasDesdeInicio();

        for (TestRendimiento t : tests) {
            double x = t.getSemanasDesdeInicio();
            double y = t.getMarcaSegundos();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            if (x > maxSemana) {
                maxSemana = x;
            }
        }

        double denominador = (n * sumX2) - (sumX * sumX);
        if (Math.abs(denominador) < 1e-9) {
            return Optional.empty();
        }

        // b = [n*sum(xy) - sum(x)*sum(y)] / [n*sum(x^2) - (sum(x))^2]
        double b = ((n * sumXY) - (sumX * sumY)) / denominador;
        double mediaX = sumX / n;
        double mediaY = sumY / n;
        // a = media(y) - b * media(x)
        double a = mediaY - (b * mediaX);

        // Coeficiente de determinacion R^2
        double r = calcularCorrelacionPearson(
                tests.stream().mapToDouble(TestRendimiento::getSemanasDesdeInicio).toArray(),
                tests.stream().mapToDouble(TestRendimiento::getMarcaSegundos).toArray()
        );
        double r2 = r * r;

        double siguienteSemana = maxSemana + 1.0;
        double estimacion = a + (b * siguienteSemana);

        return Optional.of(new ResultadoRegresion(b, a, r2, siguienteSemana, estimacion));
    }

    /**
     * Coeficiente de correlacion de Pearson (r) entre dos series numericas del mismo tamano.
     */
    public static double calcularCorrelacionPearson(double[] x, double[] y) {
        if (x == null || y == null || x.length != y.length || x.length < 2) {
            return 0.0;
        }

        int n = x.length;
        double sumX = 0.0, sumY = 0.0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
        }
        double mediaX = sumX / n;
        double mediaY = sumY / n;

        double numerador = 0.0;
        double denX = 0.0;
        double denY = 0.0;

        for (int i = 0; i < n; i++) {
            double diffX = x[i] - mediaX;
            double diffY = y[i] - mediaY;
            numerador += diffX * diffY;
            denX += diffX * diffX;
            denY += diffY * diffY;
        }

        double denominador = Math.sqrt(denX * denY);
        if (denominador < 1e-9) {
            return 0.0;
        }

        return numerador / denominador;
    }
}

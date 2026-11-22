package informesExcel.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa una prueba o test de rendimiento en 5 km para el seguimiento evolutivo.
 */
public class TestRendimiento {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    private double marcaSegundos;
    private double semanasDesdeInicio; // Variable independiente (x) para la recta de regresion

    public TestRendimiento() {
    }

    public TestRendimiento(LocalDate fecha, double marcaSegundos, double semanasDesdeInicio) {
        if (marcaSegundos <= 0) {
            throw new IllegalArgumentException("La marca en segundos debe ser un valor positivo.");
        }
        if (semanasDesdeInicio < 0) {
            throw new IllegalArgumentException("Las semanas transcurridas no pueden ser negativas.");
        }
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula.");
        this.marcaSegundos = marcaSegundos;
        this.semanasDesdeInicio = semanasDesdeInicio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getMarcaSegundos() {
        return marcaSegundos;
    }

    public void setMarcaSegundos(double marcaSegundos) {
        if (marcaSegundos <= 0) {
            throw new IllegalArgumentException("La marca en segundos debe ser un valor positivo.");
        }
        this.marcaSegundos = marcaSegundos;
    }

    public double getSemanasDesdeInicio() {
        return semanasDesdeInicio;
    }

    public void setSemanasDesdeInicio(double semanasDesdeInicio) {
        if (semanasDesdeInicio < 0) {
            throw new IllegalArgumentException("Las semanas transcurridas no pueden ser negativas.");
        }
        this.semanasDesdeInicio = semanasDesdeInicio;
    }

    /**
     * Devuelve la marca formateada en mm:ss (ej. 1145.5s -> 19:05.5).
     */
    public String getMarcaFormateada() {
        int minutos = (int) (marcaSegundos / 60);
        double segundos = marcaSegundos % 60;
        return String.format(java.util.Locale.US, "%02d:%04.1f", minutos, segundos);
    }

    @Override
    public String toString() {
        return String.format("%s (Semana %.1f): %s (%.1f s)", fecha, semanasDesdeInicio, getMarcaFormateada(), marcaSegundos);
    }
}

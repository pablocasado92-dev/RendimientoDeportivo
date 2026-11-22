package informesExcel.servicio;

import java.time.LocalDate;

/**
 * Resumen de indicadores de carga y fatiga para una semana de entrenamiento (Lunes a Domingo).
 */
public class ResumenSemanal {

    private final int numeroSemana;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final double kilometrosTotales;
    private final int minutosTotales;
    private final int cargaSemanal;
    private final Double mediaMovil3Semanas; // null si no hay 3 semanas completas
    private final double mediaDiaria;
    private final double desviacionDiaria;
    private final double monotonia;
    private final double tensionSemanal;

    public ResumenSemanal(int numeroSemana, LocalDate fechaInicio, LocalDate fechaFin,
                          double kilometrosTotales, int minutosTotales, int cargaSemanal,
                          Double mediaMovil3Semanas, double mediaDiaria, double desviacionDiaria,
                          double monotonia, double tensionSemanal) {
        this.numeroSemana = numeroSemana;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.kilometrosTotales = kilometrosTotales;
        this.minutosTotales = minutosTotales;
        this.cargaSemanal = cargaSemanal;
        this.mediaMovil3Semanas = mediaMovil3Semanas;
        this.mediaDiaria = mediaDiaria;
        this.desviacionDiaria = desviacionDiaria;
        this.monotonia = monotonia;
        this.tensionSemanal = tensionSemanal;
    }

    public int getNumeroSemana() {
        return numeroSemana;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public double getKilometrosTotales() {
        return kilometrosTotales;
    }

    public int getMinutosTotales() {
        return minutosTotales;
    }

    public int getCargaSemanal() {
        return cargaSemanal;
    }

    public Double getMediaMovil3Semanas() {
        return mediaMovil3Semanas;
    }

    public String getMediaMovilTexto() {
        return (mediaMovil3Semanas != null) ? String.format(java.util.Locale.US, "%.1f UA", mediaMovil3Semanas) : "Datos insuficientes";
    }

    public double getMediaDiaria() {
        return mediaDiaria;
    }

    public double getDesviacionDiaria() {
        return desviacionDiaria;
    }

    public double getMonotonia() {
        return monotonia;
    }

    public double getTensionSemanal() {
        return tensionSemanal;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US,
                "Semana %d (%s a %s): Carga=%d UA, MM3=%s, Monotonia=%.2f, Tension=%.1f",
                numeroSemana, fechaInicio, fechaFin, cargaSemanal, getMediaMovilTexto(), monotonia, tensionSemanal);
    }
}

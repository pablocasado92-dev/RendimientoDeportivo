package informesExcel.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa una sesion individual de entrenamiento realizada por un deportista.
 */
public class SesionEntrenamiento {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    private String tipo; // Serie, Rodaje, Tirada Larga, Cuestas, etc.
    private int duracionMinutos;
    private double distanciaKm;
    private int fcMedia;
    private int rpe; // Escala de esfuerzo percibido (1 a 10)

    public SesionEntrenamiento() {
    }

    public SesionEntrenamiento(LocalDate fecha, String tipo, int duracionMinutos, double distanciaKm, int fcMedia, int rpe) {
        if (rpe < 1 || rpe > 10) {
            throw new IllegalArgumentException("El RPE debe encontrarse en la escala de 1 a 10.");
        }
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duracion debe ser mayor a 0 minutos.");
        }
        if (distanciaKm < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa.");
        }
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula.");
        this.tipo = (tipo == null || tipo.isBlank()) ? "Rodaje" : tipo.trim();
        this.duracionMinutos = duracionMinutos;
        this.distanciaKm = distanciaKm;
        this.fcMedia = Math.max(0, fcMedia);
        this.rpe = rpe;
    }

    /**
     * Calcula la Carga Foster (Sesion-RPE) en Unidades Arbitrarias (UA).
     * Carga = duracionMinutos * rpe
     */
    @JsonIgnore
    public int getCargaFoster() {
        return duracionMinutos * rpe;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public int getFcMedia() {
        return fcMedia;
    }

    public void setFcMedia(int fcMedia) {
        this.fcMedia = fcMedia;
    }

    public int getRpe() {
        return rpe;
    }

    public void setRpe(int rpe) {
        if (rpe < 1 || rpe > 10) {
            throw new IllegalArgumentException("El RPE debe encontrarse en la escala de 1 a 10.");
        }
        this.rpe = rpe;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d min, %.2f km, RPE %d, Carga %d UA)",
                fecha, tipo, duracionMinutos, distanciaKm, rpe, getCargaFoster());
    }
}

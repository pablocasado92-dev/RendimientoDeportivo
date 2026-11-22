package informesExcel.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad principal que representa a un atleta federado en el club deportivo.
 */
public class Deportista {

    private String id;
    private String nombre;
    private int edad;
    private String categoria; // Senior, Sub-23, Master 40, etc.
    private String objetivoCompeticion; // Ej: "Sub 18:00 en 5K de Valencia"
    private double marcaInicial5k; // Marca de referencia inicial en segundos
    private String rutaFoto; // Ruta local a la fotografia del atleta (opcional)

    private List<SesionEntrenamiento> sesiones = new ArrayList<>();
    private List<TestRendimiento> tests = new ArrayList<>();

    public Deportista() {
    }

    public Deportista(String id, String nombre, int edad, String categoria, String objetivoCompeticion, double marcaInicial5k, String rutaFoto) {
        this.id = Objects.requireNonNull(id, "El ID del deportista no puede ser nulo.").trim();
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo.").trim();
        this.edad = edad;
        this.categoria = (categoria == null || categoria.isBlank()) ? "Senior" : categoria.trim();
        this.objetivoCompeticion = (objetivoCompeticion == null || objetivoCompeticion.isBlank()) ? "Mejora general" : objetivoCompeticion.trim();
        this.marcaInicial5k = Math.max(0, marcaInicial5k);
        this.rutaFoto = (rutaFoto != null && !rutaFoto.isBlank()) ? rutaFoto.trim() : null;
    }

    public void agregarSesion(SesionEntrenamiento sesion) {
        if (sesion != null) {
            sesiones.add(sesion);
        }
    }

    public void agregarTest(TestRendimiento test) {
        if (test != null) {
            tests.add(test);
        }
    }

    @JsonIgnore
    public double getKilometrosTotales() {
        return sesiones.stream().mapToDouble(SesionEntrenamiento::getDistanciaKm).sum();
    }

    @JsonIgnore
    public int getMinutosTotales() {
        return sesiones.stream().mapToInt(SesionEntrenamiento::getDuracionMinutos).sum();
    }

    @JsonIgnore
    public int getCargaTotalFoster() {
        return sesiones.stream().mapToInt(SesionEntrenamiento::getCargaFoster).sum();
    }

    @JsonIgnore
    public double getMejorMarca5k() {
        if (tests.isEmpty()) {
            return marcaInicial5k;
        }
        return tests.stream()
                .mapToDouble(TestRendimiento::getMarcaSegundos)
                .min()
                .orElse(marcaInicial5k);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getObjetivoCompeticion() {
        return objetivoCompeticion;
    }

    public void setObjetivoCompeticion(String objetivoCompeticion) {
        this.objetivoCompeticion = objetivoCompeticion;
    }

    public double getMarcaInicial5k() {
        return marcaInicial5k;
    }

    public void setMarcaInicial5k(double marcaInicial5k) {
        this.marcaInicial5k = marcaInicial5k;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public List<SesionEntrenamiento> getSesiones() {
        return sesiones;
    }

    public void setSesiones(List<SesionEntrenamiento> sesiones) {
        this.sesiones = (sesiones != null) ? sesiones : new ArrayList<>();
    }

    public List<TestRendimiento> getTests() {
        return tests;
    }

    public void setTests(List<TestRendimiento> tests) {
        this.tests = (tests != null) ? tests : new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s, %d anos)", id, nombre, categoria, edad);
    }
}

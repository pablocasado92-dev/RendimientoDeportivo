package informesExcel.servicio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import informesExcel.modelo.Deportista;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de persistencia local en formato JSON para el almacenamiento de deportistas y entrenamientos.
 */
public class GestorPersistencia {

    private final ObjectMapper objectMapper;

    public GestorPersistencia() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Guarda la coleccion de deportistas en la ruta especificada en formato JSON.
     */
    public void guardar(List<Deportista> deportistas, Path rutaArchivo) throws IOException {
        if (rutaArchivo.getParent() != null && !Files.exists(rutaArchivo.getParent())) {
            Files.createDirectories(rutaArchivo.getParent());
        }
        objectMapper.writeValue(rutaArchivo.toFile(), deportistas);
    }

    /**
     * Carga la lista de deportistas desde el archivo JSON indicado.
     * Si el archivo no existe, devuelve una lista vacia.
     */
    public List<Deportista> cargar(Path rutaArchivo) throws IOException {
        if (!Files.exists(rutaArchivo)) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(rutaArchivo.toFile(), new TypeReference<List<Deportista>>() {});
    }
}

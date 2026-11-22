package informesExcel.gui;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validador de entradas de usuario para formularios Swing.
 */
public final class ValidadorFormularios {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ValidadorFormularios() {
    }

    public static String validarTextoNoVacio(String campo, String nombreCampo) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede estar vacio.");
        }
        return campo.trim();
    }

    public static int validarEnteroPositivo(String valor, String nombreCampo) {
        try {
            int num = Integer.parseInt(valor.trim());
            if (num <= 0) {
                throw new IllegalArgumentException("El campo '" + nombreCampo + "' debe ser un numero mayor a 0.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' debe contener un numero entero valido.");
        }
    }

    public static double validarDoublePositivo(String valor, String nombreCampo) {
        try {
            double num = Double.parseDouble(valor.trim().replace(',', '.'));
            if (num <= 0) {
                throw new IllegalArgumentException("El campo '" + nombreCampo + "' debe ser un valor numerico positivo.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' debe contener un valor decimal valido.");
        }
    }

    public static int validarRpe(String valor) {
        int rpe = validarEnteroPositivo(valor, "RPE (Esfuerzo Percibido)");
        if (rpe < 1 || rpe > 10) {
            throw new IllegalArgumentException("El RPE debe estar comprendido entre 1 y 10.");
        }
        return rpe;
    }

    public static LocalDate validarFecha(String fechaStr) {
        try {
            return LocalDate.parse(fechaStr.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha debe tener el formato AAAA-MM-DD (ej. 2026-11-21).");
        }
    }

    public static void mostrarError(JFrame ventana, String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "Error de Validacion", JOptionPane.WARNING_MESSAGE);
    }

    public static void mostrarInfo(JFrame ventana, String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "Operacion Correcta", JOptionPane.INFORMATION_MESSAGE);
    }
}

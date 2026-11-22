package informesExcel.servicio;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utilitarios de generacion de recursos graficos en memoria (Java 2D)
 * para logotipos y avatares vectorizados/rasterizados.
 */
public final class RecursosGraficos {

    private RecursosGraficos() {
    }

    /**
     * Genera un escudo/logotipo deportivo profesional para el club deportivo.
     */
    public static byte[] generarLogoClubBytes(int ancho, int alto) throws IOException {
        BufferedImage img = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fondo escudo
        int[] xPoints = {ancho / 2, ancho - 10, ancho - 10, ancho / 2, 10, 10};
        int[] yPoints = {10, 20, alto - 40, alto - 10, alto - 40, 20};
        Polygon escudo = new Polygon(xPoints, yPoints, 6);

        GradientPaint gradient = new GradientPaint(0, 0, new Color(24, 44, 97), 0, alto, new Color(41, 128, 185));
        g.setPaint(gradient);
        g.fill(escudo);

        // Borde dorado
        g.setColor(new Color(241, 196, 15));
        g.setStroke(new BasicStroke(4.0f));
        g.draw(escudo);

        // Texto CRD
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, alto / 4));
        FontMetrics fm = g.getFontMetrics();
        String txt = "CRD";
        int txtX = (ancho - fm.stringWidth(txt)) / 2;
        int txtY = (alto / 2) + (fm.getAscent() / 3);
        g.drawString(txt, txtX, txtY);

        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        return baos.toByteArray();
    }

    /**
     * Genera un avatar circular elegante con las iniciales del atleta si no dispone de fotografia en disco.
     */
    public static byte[] generarAvatarDeportistaBytes(String nombre, int tamano) throws IOException {
        BufferedImage img = new BufferedImage(tamano, tamano, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Circulo fondo
        g.setColor(new Color(52, 73, 94));
        g.fillOval(4, 4, tamano - 8, tamano - 8);

        g.setColor(new Color(41, 128, 185));
        g.setStroke(new BasicStroke(3.0f));
        g.drawOval(4, 4, tamano - 8, tamano - 8);

        // Iniciales
        String iniciales = obtenerIniciales(nombre);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, tamano / 3));
        FontMetrics fm = g.getFontMetrics();
        int tx = (tamano - fm.stringWidth(iniciales)) / 2;
        int ty = (tamano / 2) + (fm.getAscent() / 3);
        g.drawString(iniciales, tx, ty);

        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        return baos.toByteArray();
    }

    private static String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "AT";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        return ("" + partes[0].charAt(0) + partes[partes.length - 1].charAt(0)).toUpperCase();
    }
}

package informesExcel.servicio;

/**
 * Contenedor con los parametros de la recta de regresion lineal y = a + bx.
 */
public class ResultadoRegresion {

    private final double pendienteB;
    private final double interceptoA;
    private final double rCuadrado;
    private final double semanaSiguienteX;
    private final double estimacionSiguienteY;

    public ResultadoRegresion(double pendienteB, double interceptoA, double rCuadrado, double semanaSiguienteX, double estimacionSiguienteY) {
        this.pendienteB = pendienteB;
        this.interceptoA = interceptoA;
        this.rCuadrado = rCuadrado;
        this.semanaSiguienteX = semanaSiguienteX;
        this.estimacionSiguienteY = estimacionSiguienteY;
    }

    public double getPendienteB() {
        return pendienteB;
    }

    public double getInterceptoA() {
        return interceptoA;
    }

    public double getRCuadrado() {
        return rCuadrado;
    }

    public double getSemanaSiguienteX() {
        return semanaSiguienteX;
    }

    public double getEstimacionSiguienteY() {
        return estimacionSiguienteY;
    }

    public String getEcuacion() {
        return String.format(java.util.Locale.US, "y = %.2f + (%.2f)x", interceptoA, pendienteB);
    }
}

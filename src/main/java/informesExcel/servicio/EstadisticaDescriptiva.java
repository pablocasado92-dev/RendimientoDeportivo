package informesExcel.servicio;

/**
 * Contenedor de metricas de estadistica descriptiva.
 */
public class EstadisticaDescriptiva {

    private final double media;
    private final double mediana;
    private final double minimo;
    private final double maximo;
    private final double desviacionTipica;
    private final double coeficienteVariacion; // CV = (desviacion / media) * 100

    public EstadisticaDescriptiva(double media, double mediana, double minimo, double maximo, double desviacionTipica, double coeficienteVariacion) {
        this.media = media;
        this.mediana = mediana;
        this.minimo = minimo;
        this.maximo = maximo;
        this.desviacionTipica = desviacionTipica;
        this.coeficienteVariacion = coeficienteVariacion;
    }

    public double getMedia() {
        return media;
    }

    public double getMediana() {
        return mediana;
    }

    public double getMinimo() {
        return minimo;
    }

    public double getMaximo() {
        return maximo;
    }

    public double getDesviacionTipica() {
        return desviacionTipica;
    }

    public double getCoeficienteVariacion() {
        return coeficienteVariacion;
    }
}

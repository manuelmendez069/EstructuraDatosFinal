package ciudadnav.grafo;

public class Arista {

    private String origen;
    private String destino;
    private double peso;

    public Arista(String origen, String destino, double peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    @Override
    public String toString() {
        return origen + " --> " + destino + "  [" + peso + " km]";
    }
}

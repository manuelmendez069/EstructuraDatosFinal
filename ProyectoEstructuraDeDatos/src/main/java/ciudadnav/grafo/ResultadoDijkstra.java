package ciudadnav.grafo;

import java.util.List;

public class ResultadoDijkstra {

    private List<String>  camino;
    private double        distanciaTotal;


    public ResultadoDijkstra(List<String> camino, double distanciaTotal) {
        this.camino          = camino;
        this.distanciaTotal  = distanciaTotal;
    }


    public List<String> getCamino()        { return camino; }
    public double       getDistanciaTotal() { return distanciaTotal; }

    public boolean tieneCamino() {
        return !camino.isEmpty() && distanciaTotal != Double.MAX_VALUE;
    }
}

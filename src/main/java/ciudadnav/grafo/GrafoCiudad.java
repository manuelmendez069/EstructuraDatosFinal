package ciudadnav.grafo;

import ciudadnav.modelo.Punto;

import java.util.*;

public class GrafoCiudad {

    private Map<String, Punto> verticesMap;
    private Map<String, List<Arista>> listaAdyacencia;

    public GrafoCiudad() {
        this.verticesMap = new LinkedHashMap<>();
        this.listaAdyacencia = new LinkedHashMap<>();
    }

    public boolean agregarVertice(Punto p) {
        if (verticesMap.containsKey(p.getId()))
            return false;
        verticesMap.put(p.getId(), p);
        listaAdyacencia.put(p.getId(), new ArrayList<>());
        return true;
    }

    public boolean agregarArista(String idOrigen, String idDestino, double peso) {
        if (!verticesMap.containsKey(idOrigen) || !verticesMap.containsKey(idDestino))
            return false;
        if (idOrigen.equals(idDestino))
            return false;

        if (existeArista(idOrigen, idDestino))
            return false;

        listaAdyacencia.get(idOrigen).add(new Arista(idOrigen, idDestino, peso));
        listaAdyacencia.get(idDestino).add(new Arista(idDestino, idOrigen, peso));
        return true;
    }

    public boolean existeArista(String idOrigen, String idDestino) {
        List<Arista> ady = listaAdyacencia.get(idOrigen);
        if (ady == null)
            return false;
        for (Arista a : ady) {
            if (a.getDestino().equals(idDestino))
                return true;
        }
        return false;
    }

    public List<String> bfs(String idInicio) {
        List<String> visitados = new ArrayList<>();
        if (!verticesMap.containsKey(idInicio))
            return visitados;

        Set<String> visto = new LinkedHashSet<>();
        Queue<String> cola = new LinkedList<>();

        cola.add(idInicio);
        visto.add(idInicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            visitados.add(actual);

            for (Arista arista : listaAdyacencia.get(actual)) {
                String vecino = arista.getDestino();
                if (!visto.contains(vecino)) {
                    visto.add(vecino);
                    cola.add(vecino);
                }
            }
        }
        return visitados;
    }

    public List<String> dfs(String idInicio) {
        List<String> visitados = new ArrayList<>();
        Set<String> visto = new LinkedHashSet<>();
        if (!verticesMap.containsKey(idInicio))
            return visitados;
        dfsRec(idInicio, visto, visitados);
        return visitados;
    }

    private void dfsRec(String actual, Set<String> visto, List<String> visitados) {
        visto.add(actual);
        visitados.add(actual);

        for (Arista arista : listaAdyacencia.get(actual)) {
            String vecino = arista.getDestino();
            if (!visto.contains(vecino)) {
                dfsRec(vecino, visto, visitados);
            }
        }
    }

    public ResultadoDijkstra dijkstra(String idOrigen, String idDestino) {
        if (!verticesMap.containsKey(idOrigen) || !verticesMap.containsKey(idDestino)) {
            return new ResultadoDijkstra(new ArrayList<>(), Double.MAX_VALUE);
        }

        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> previo = new HashMap<>();
        // Almacena (id, distancia-en-el-momento-de-inserción) para evitar que el
        // comparador lea un map mutable y viole el invariante del heap.
        PriorityQueue<Map.Entry<String, Double>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (String v : verticesMap.keySet()) {
            distancias.put(v, Double.MAX_VALUE);
        }
        distancias.put(idOrigen, 0.0);
        pq.add(Map.entry(idOrigen, 0.0));

        Set<String> procesados = new HashSet<>();

        while (!pq.isEmpty()) {
            Map.Entry<String, Double> entrada = pq.poll();
            String u = entrada.getKey();
            if (procesados.contains(u))
                continue;
            procesados.add(u);

            if (u.equals(idDestino))
                break;

            for (Arista arista : listaAdyacencia.get(u)) {
                String v = arista.getDestino();
                double nueva = distancias.get(u) + arista.getPeso();

                if (nueva < distancias.get(v)) {
                    distancias.put(v, nueva);
                    previo.put(v, u);
                    pq.add(Map.entry(v, nueva));
                }
            }
        }

        List<String> camino = new ArrayList<>();
        if (Double.MAX_VALUE == distancias.get(idDestino)) {
            return new ResultadoDijkstra(camino, Double.MAX_VALUE);
        }

        String cursor = idDestino;
        while (cursor != null) {
            camino.add(0, cursor);
            cursor = previo.get(cursor);
        }

        return new ResultadoDijkstra(camino, distancias.get(idDestino));
    }

    public Map<String, Punto> getVerticesMap() {
        return verticesMap;
    }

    public Map<String, List<Arista>> getListaAdyacencia() {
        return listaAdyacencia;
    }

    public Collection<Punto> getVertices() {
        return verticesMap.values();
    }

    public Punto getPunto(String id) {
        return verticesMap.get(id);
    }

    public boolean estaVacio() {
        return verticesMap.isEmpty();
    }
}

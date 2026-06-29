package ciudadnav.arbol;

import ciudadnav.modelo.Zona;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ArbolZonas {

    private Zona raiz;

    public ArbolZonas() {
        this.raiz = null;
    }

    public void insertar(String nombrePadre, Zona nuevaZona) {
        if (raiz == null) {
            raiz = nuevaZona;
            return;
        }

        Zona padreEncontrado = buscarPorNombre(raiz, nombrePadre);
        if (padreEncontrado != null) {
            padreEncontrado.agregarSubzona(nuevaZona);
        }
    }

    public Zona buscarPorNombre(String nombre) {
        return buscarPorNombre(raiz, nombre);
    }

    private Zona buscarPorNombre(Zona nodo, String nombre) {
        if (nodo == null || nombre == null)
            return null;
        if (nodo.getNombre().equalsIgnoreCase(nombre))
            return nodo;

        for (Zona hijo : nodo.getSubzonas()) {
            Zona encontrado = buscarPorNombre(hijo, nombre);
            if (encontrado != null)
                return encontrado;
        }
        return null;
    }

    public List<String> recorridoPreorden() {
        List<String> resultado = new ArrayList<>();
        preordenRec(raiz, resultado, 0);
        return resultado;
    }

    private void preordenRec(Zona nodo, List<String> resultado, int nivel) {
        if (nodo == null)
            return;

        String prefijo = "  ".repeat(nivel);
        resultado.add(prefijo + nodo.toString());

        for (Zona hijo : nodo.getSubzonas()) {
            preordenRec(hijo, resultado, nivel + 1);
        }
    }

    public List<String> recorridoBFS() {
        List<String> resultado = new ArrayList<>();
        if (raiz == null)
            return resultado;

        Queue<Zona> cola = new LinkedList<>();
        cola.add(raiz);

        while (!cola.isEmpty()) {
            Zona actual = cola.poll();
            resultado.add(actual.toString());

            for (Zona hijo : actual.getSubzonas()) {
                cola.add(hijo);
            }
        }
        return resultado;
    }

    public Zona getRaiz() {
        return raiz;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public List<String> obtenerNombresZonas() {
        List<String> nombres = new ArrayList<>();
        recopilarNombres(raiz, nombres);
        return nombres;
    }

    private void recopilarNombres(Zona nodo, List<String> nombres) {
        if (nodo == null)
            return;
        nombres.add(nodo.getNombre());
        for (Zona hijo : nodo.getSubzonas()) {
            recopilarNombres(hijo, nombres);
        }
    }
}

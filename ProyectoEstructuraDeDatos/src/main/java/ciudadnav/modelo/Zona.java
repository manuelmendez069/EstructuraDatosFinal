package ciudadnav.modelo;

import java.util.ArrayList;
import java.util.List;

public class Zona {

    private String nombre;
    private String tipo;
    private List<Zona> subzonas;

    public Zona(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.subzonas = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public List<Zona> getSubzonas() {
        return subzonas;
    }

    public void agregarSubzona(Zona z) {
        subzonas.add(z);
    }

    public boolean tieneHijos() {
        return !subzonas.isEmpty();
    }

    @Override
    public String toString() {
        return nombre + " [" + tipo + "]";
    }
}

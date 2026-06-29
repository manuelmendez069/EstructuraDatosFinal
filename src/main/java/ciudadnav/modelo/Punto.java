package ciudadnav.modelo;

public class Punto {

    private String id;
    private String nombre;
    private String zonaPertenece;
    private double coordX;
    private double coordY;

    public Punto(String id, String nombre, String zonaPertenece, double coordX, double coordY) {
        this.id = id;
        this.nombre = nombre;
        this.zonaPertenece = zonaPertenece;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getZonaPertenece() {
        return zonaPertenece;
    }

    public double getCoordX() {
        return coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    @Override
    public String toString() {
        return nombre + " (" + zonaPertenece + ")";
    }
}

package ciudadnav.modelo;

import ciudadnav.arbol.ArbolZonas;
import ciudadnav.grafo.GrafoCiudad;

public class DatosIniciales {

    public static void cargar(ArbolZonas arbol, GrafoCiudad grafo) {

        Zona ciudad = new Zona("Ciudad", "Raiz");
        Zona norte = new Zona("Norte", "Sector");
        Zona sur = new Zona("Sur", "Sector");
        Zona centro = new Zona("Centro", "Sector");
        Zona estacion = new Zona("Estación", "Barrio");
        Zona parque = new Zona("Parque", "Barrio");
        Zona mercado = new Zona("Mercado", "Barrio");
        Zona hospital = new Zona("Hospital", "Barrio");
        Zona plaza = new Zona("Plaza", "Barrio");
        Zona museo = new Zona("Museo", "Barrio");

        arbol.insertar(null, ciudad);
        arbol.insertar("Ciudad", norte);
        arbol.insertar("Ciudad", sur);
        arbol.insertar("Ciudad", centro);
        arbol.insertar("Norte", estacion);
        arbol.insertar("Norte", parque);
        arbol.insertar("Sur", mercado);
        arbol.insertar("Sur", hospital);
        arbol.insertar("Centro", plaza);
        arbol.insertar("Centro", museo);

        Punto pA = new Punto("A", "Estación Central", "Norte", 120, 80);
        Punto pB = new Punto("B", "Parque Libertad", "Norte", 220, 100);
        Punto pC = new Punto("C", "Mercado Municipal", "Sur", 100, 300);
        Punto pD = new Punto("D", "Hospital Regional", "Sur", 300, 320);
        Punto pE = new Punto("E", "Plaza Mayor", "Centro", 220, 220);
        Punto pF = new Punto("F", "Museo Nacional", "Centro", 350, 190);
        Punto pG = new Punto("G", "Avenida Principal", "Centro", 180, 170);

        grafo.agregarVertice(pA);
        grafo.agregarVertice(pB);
        grafo.agregarVertice(pC);
        grafo.agregarVertice(pD);
        grafo.agregarVertice(pE);
        grafo.agregarVertice(pF);
        grafo.agregarVertice(pG);

        grafo.agregarArista("A", "B", 3.5);
        grafo.agregarArista("A", "G", 5.0);
        grafo.agregarArista("B", "E", 4.2);
        grafo.agregarArista("B", "F", 6.1);
        grafo.agregarArista("C", "G", 4.8);
        grafo.agregarArista("C", "D", 3.0);
        grafo.agregarArista("D", "E", 5.5);
        grafo.agregarArista("E", "F", 2.8);
        grafo.agregarArista("E", "G", 2.0);
        grafo.agregarArista("F", "D", 4.0);
    }
}

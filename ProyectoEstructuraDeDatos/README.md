# CiudadNav

Como parte del proyecto integrador de la asignatura Estructura de Datos II, desarrollé una aplicación denominada CiudadNav, cuyo propósito es representar una ciudad mediante estructuras de datos no lineales y demostrar cómo estas pueden trabajar de forma conjunta para resolver un problema práctico.

Actualmente, los sistemas de navegación requieren organizar grandes cantidades de información. No basta con conocer únicamente los lugares existentes dentro de una ciudad; también es necesario representar la organización territorial de dichos lugares y las conexiones que existen entre ellos. Este tipo de problemas resulta adecuado para aplicar estructuras de datos como los árboles y los grafos, ya que cada una permite modelar un aspecto diferente de la información.

## Descripción del proyecto

CiudadNav es una aplicación en Java con interfaz gráfica basada en JavaFX que modela:

- una jerarquía territorial mediante un árbol de zonas;
- un conjunto de puntos de interés y rutas mediante un grafo;
- un algoritmo de búsqueda de ruta mínima con Dijkstra.

La idea central es mostrar cómo las estructuras de datos no lineales permiten organizar información espacial y facilitar la navegación dentro de un contexto urbano.

## Funcionalidades principales

- Visualización del árbol de zonas de la ciudad.
- Inserción de nuevas zonas en la estructura jerárquica.
- Búsqueda de zonas por nombre.
- Recorridos del árbol en preorden y BFS.
- Creación de puntos en el grafo.
- Creación de rutas entre puntos con pesos de distancia.
- Ejecución de recorridos BFS y DFS sobre el grafo.
- Cálculo de la ruta mínima con Dijkstra.

## Tecnologías utilizadas

- Java 21
- JavaFX
- Maven
- Estructuras de datos: árbol, grafo, listas, colas y heaps

## Estructura del proyecto

- src/main/java/ciudadnav/arbol: implementación del árbol de zonas.
- src/main/java/ciudadnav/grafo: implementación del grafo, aristas y algoritmo de Dijkstra.
- src/main/java/ciudadnav/modelo: clases de dominio como Zona, Punto y DatosIniciales.
- src/main/java/ciudadnav/ui: interfaz gráfica de la aplicación.

## Ejecución

1. Clona este repositorio.
2. Asegúrate de tener Java 21 y Maven instalados.
3. Ejecuta el siguiente comando:

```bash
mvn javafx:run
```

## Autor

Manuel A. Méndez Martínez

## Nota

Este proyecto incluye un documento técnico en formato Word con la explicación del diseño, implementación y análisis del sistema.

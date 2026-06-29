package ciudadnav.ui;

import ciudadnav.arbol.ArbolZonas;
import ciudadnav.grafo.Arista;
import ciudadnav.grafo.GrafoCiudad;
import ciudadnav.grafo.ResultadoDijkstra;
import ciudadnav.modelo.Punto;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.*;

public class PanelGrafo extends VBox {

    private GrafoCiudad grafoRef;
    private ArbolZonas arbolRef;
    private Runnable onCambio;

    private Canvas canvasGrafo;
    private Pane canvasContenedor;
    private TextArea areaResultados;

    private ComboBox<String> comboOrigen;
    private ComboBox<String> comboDestino;
    private ComboBox<String> comboZonaVertice;

    private List<String> caminoResaltado;
    private String nodoResaltado;

    public PanelGrafo(GrafoCiudad grafo, ArbolZonas arbol, Runnable onCambio) {
        this.grafoRef = grafo;
        this.arbolRef = arbol;
        this.onCambio = onCambio;
        this.caminoResaltado = new ArrayList<>();
        construirUI();
        refrescarCanvas();
    }

    private void construirUI() {
        setSpacing(0);
        setPadding(new Insets(0));
        setStyle("-fx-background-color: #0a0a16;");

        canvasGrafo = new Canvas();
        canvasContenedor = new Pane(canvasGrafo);
        canvasContenedor.setStyle("-fx-background-color: #07070f;");
        canvasContenedor.setMinHeight(260);
        canvasContenedor.setPrefHeight(300);
        canvasContenedor.setMaxHeight(360);

        canvasContenedor.widthProperty().addListener((obs, ov, nv) -> {
            canvasGrafo.setWidth(nv.doubleValue());
            refrescarCanvas();
        });
        canvasContenedor.heightProperty().addListener((obs, ov, nv) -> {
            canvasGrafo.setHeight(nv.doubleValue());
            refrescarCanvas();
        });

        VBox.setVgrow(canvasContenedor, Priority.NEVER);

        VBox panelControles = new VBox(10);
        panelControles.setPadding(new Insets(16, 20, 16, 20));
        panelControles.setStyle("-fx-background-color: #0a0a16;");
        panelControles.getChildren().addAll(
                tarjetaAlgoritmos(),
                tarjetaAgregarPunto(),
                tarjetaAgregarRuta(),
                new Separator(),
                seccionResultados());

        ScrollPane scroll = new ScrollPane(panelControles);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(
                "-fx-background: #0a0a16;" +
                        "-fx-background-color: #0a0a16;" +
                        "-fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(canvasContenedor, scroll);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    private VBox tarjetaAlgoritmos() {
        VBox t = tarjeta();

        Label tit = tituloSeccion("🗺  Algoritmos de recorrido y ruta mínima");
        Label sub = subTexto(
                "Dijkstra encuentra el camino más corto entre dos puntos. " +
                        "BFS y DFS recorren el grafo desde el origen seleccionado.");

        GridPane g = grid();
        Label lOrigen = labelCampo("Origen");
        comboOrigen = combo(250);
        comboOrigen.setPromptText("Selecciona punto de origen...");

        Label lDestino = labelCampo("Destino");
        comboDestino = combo(250);
        comboDestino.setPromptText("Selecciona punto de destino...");

        actualizarCombosVertices();

        g.add(lOrigen, 0, 0);
        g.add(comboOrigen, 1, 0);
        g.add(lDestino, 0, 1);
        g.add(comboDestino, 1, 1);

        Label nota = new Label("ℹ  Dijkstra necesita origen + destino. BFS y DFS solo usan el origen.");
        nota.setFont(Font.font("Monospace", 9));
        nota.setTextFill(Color.web("#2a2a48"));

        FlowPane botones = new FlowPane(8, 6);
        botones.setAlignment(Pos.CENTER_LEFT);

        Button bDij = btnPrimario("Dijkstra — ruta mínima");
        Button bBFS = btnSecundario("BFS");
        Button bDFS = btnSecundario("DFS");
        Button bLim = btnApagado("✕  Limpiar");

        animarBoton(bDij);
        animarBoton(bBFS);
        animarBoton(bDFS);
        animarBoton(bLim);

        bDij.setOnAction(e -> {
            String or = comboOrigen.getValue();
            String de = comboDestino.getValue();
            if (or == null || de == null) {
                resultado("⚠  Selecciona origen y destino.", false);
                return;
            }
            if (or.equals(de)) {
                resultado("⚠  Origen y destino son el mismo punto.", false);
                return;
            }

            ResultadoDijkstra res = grafoRef.dijkstra(or, de);
            caminoResaltado = res.getCamino();
            nodoResaltado = null;
            refrescarCanvas();

            if (res.tieneCamino()) {
                StringBuilder sb = new StringBuilder("✔  Camino mínimo (Dijkstra)\n\n");
                List<String> c = res.getCamino();
                for (int i = 0; i < c.size(); i++) {
                    Punto p = grafoRef.getPunto(c.get(i));
                    sb.append("  ").append(c.get(i)).append("  —  ").append(p != null ? p.getNombre() : "?");
                    if (i < c.size() - 1)
                        sb.append("\n  ↓\n");
                }
                sb.append("\n\n  Distancia total: ").append(String.format("%.1f", res.getDistanciaTotal()))
                        .append(" km");
                resultado(sb.toString(), true);
            } else {
                resultado("✘  No existe ruta entre " + or + " y " + de + ".", false);
            }
        });

        bBFS.setOnAction(e -> {
            String or = comboOrigen.getValue();
            if (or == null) {
                resultado("⚠  Selecciona un origen.", false);
                return;
            }
            List<String> vis = grafoRef.bfs(or);
            caminoResaltado = vis;
            nodoResaltado = or;
            refrescarCanvas();
            StringBuilder sb = new StringBuilder("✔  BFS desde " + or + "\n   (amplitud — nivel por nivel)\n\n");
            for (int i = 0; i < vis.size(); i++) {
                Punto p = grafoRef.getPunto(vis.get(i));
                sb.append("  ").append(i + 1).append(". ").append(vis.get(i))
                        .append("  —  ").append(p != null ? p.getNombre() : "?").append("\n");
            }
            resultado(sb.toString(), true);
        });

        bDFS.setOnAction(e -> {
            String or = comboOrigen.getValue();
            if (or == null) {
                resultado("⚠  Selecciona un origen.", false);
                return;
            }
            List<String> vis = grafoRef.dfs(or);
            caminoResaltado = vis;
            nodoResaltado = or;
            refrescarCanvas();
            StringBuilder sb = new StringBuilder(
                    "✔  DFS desde " + or + "\n   (profundidad — va al fondo antes de retroceder)\n\n");
            for (int i = 0; i < vis.size(); i++) {
                Punto p = grafoRef.getPunto(vis.get(i));
                sb.append("  ").append(i + 1).append(". ").append(vis.get(i))
                        .append("  —  ").append(p != null ? p.getNombre() : "?").append("\n");
            }
            resultado(sb.toString(), true);
        });

        bLim.setOnAction(e -> {
            caminoResaltado.clear();
            nodoResaltado = null;
            refrescarCanvas();
            areaResultados.setStyle(estiloArea(true));
            areaResultados.setText("Mapa limpiado.");
        });

        botones.getChildren().addAll(bDij, bBFS, bDFS, bLim);
        t.getChildren().addAll(tit, sub, g, nota, botones);
        return t;
    }

    private VBox tarjetaAgregarPunto() {
        VBox t = tarjeta();

        Label tit = tituloSeccion("📍  Agregar punto (nodo)");
        Label sub = subTexto(
                "Un punto es un lugar de la ciudad. Asígnale un ID de una letra, " +
                        "un nombre y la zona del árbol a la que pertenece.");

        GridPane g = grid();

        Label lId = labelCampo("ID (letra)");
        TextField fId = campo("ej. H", 55);

        Label lNom = labelCampo("Nombre");
        TextField fNom = campo("ej. Terminal Norte", 230);

        Label lZona = labelCampo("Zona");
        comboZonaVertice = combo(220);
        comboZonaVertice.setPromptText("Selecciona zona...");
        actualizarComboZonas();

        g.add(lId, 0, 0);
        g.add(fId, 1, 0);
        g.add(lNom, 0, 1);
        g.add(fNom, 1, 1);
        g.add(lZona, 0, 2);
        g.add(comboZonaVertice, 1, 2);

        Button btn = btnPrimario("Agregar punto al grafo");
        btn.setMaxWidth(Double.MAX_VALUE);
        animarBoton(btn);

        btn.setOnAction(e -> {
            String id = fId.getText().trim().toUpperCase();
            String nom = fNom.getText().trim();
            String zona = comboZonaVertice.getValue();

            if (id.isEmpty() || nom.isEmpty() || zona == null) {
                resultado("⚠  Completa ID, nombre y zona.", false);
                return;
            }
            if (grafoRef.getVerticesMap().containsKey(id)) {
                resultado("⚠  Ya existe un punto con ID '" + id + "'.", false);
                return;
            }

            double w = Math.max(canvasGrafo.getWidth(), 400);
            double h = Math.max(canvasGrafo.getHeight(), 260);
            double rx = 50 + Math.random() * (w - 100);
            double ry = 40 + Math.random() * (h - 80);

            grafoRef.agregarVertice(new Punto(id, nom, zona, rx, ry));
            actualizarCombosVertices();
            refrescarCanvas();
            fId.clear();
            fNom.clear();
            if (onCambio != null)
                onCambio.run();
            resultado("✔  Punto '" + nom + "' (ID: " + id + ") agregado en zona " + zona + ".", true);
        });

        t.getChildren().addAll(tit, sub, g, btn);
        return t;
    }

    private VBox tarjetaAgregarRuta() {
        VBox t = tarjeta();

        Label tit = tituloSeccion("🔗  Agregar ruta (arista)");
        Label sub = subTexto(
                "Una ruta conecta dos puntos con un peso en km. La conexión es bidireccional.");

        GridPane g = grid();

        Label lDe = labelCampo("Desde");
        ComboBox<String> cDe = combo(220);

        Label lA = labelCampo("Hasta");
        ComboBox<String> cA = combo(220);

        Label lPeso = labelCampo("Distancia km");
        TextField fPeso = campo("ej. 3.5", 90);
        fPeso.setText("1.0");

        actualizarCombosAristas(cDe, cA);

        g.add(lDe, 0, 0);
        g.add(cDe, 1, 0);
        g.add(lA, 0, 1);
        g.add(cA, 1, 1);
        g.add(lPeso, 0, 2);
        g.add(fPeso, 1, 2);

        Button btn = btnPrimario("Agregar ruta");
        btn.setMaxWidth(Double.MAX_VALUE);
        animarBoton(btn);

        btn.setOnAction(e -> {
            String de = cDe.getValue();
            String a = cA.getValue();
            String pStr = fPeso.getText().trim();

            if (de == null || a == null || pStr.isEmpty()) {
                resultado("⚠  Completa todos los campos.", false);
                return;
            }
            if (de.equals(a)) {
                resultado("⚠  Los dos puntos no pueden ser el mismo.", false);
                return;
            }
            double peso;
            try {
                peso = Double.parseDouble(pStr);
                if (peso <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                resultado("⚠  La distancia debe ser un número mayor que cero.", false);
                return;
            }

            if (grafoRef.agregarArista(de, a, peso)) {
                refrescarCanvas();
                Punto pDe = grafoRef.getPunto(de);
                Punto pA2 = grafoRef.getPunto(a);
                if (onCambio != null)
                    onCambio.run();
                resultado(
                        "✔  Ruta agregada:\n\n  " +
                                de + " — " + (pDe != null ? pDe.getNombre() : "?") +
                                "\n  ↔\n  " +
                                a + " — " + (pA2 != null ? pA2.getNombre() : "?") +
                                "\n\n  Distancia: " + peso + " km",
                        true);
                fPeso.setText("1.0");
            } else {
                resultado("⚠  La ruta ya existe o los puntos no son válidos.", false);
            }
        });

        t.getChildren().addAll(tit, sub, g, btn);
        return t;
    }

    private VBox seccionResultados() {
        VBox c = new VBox(6);
        Label lbl = new Label("RESULTADO");
        lbl.setFont(Font.font("Monospace", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web("#2a2a50"));

        areaResultados = new TextArea("Los resultados aparecerán aquí...");
        areaResultados.setPrefHeight(130);
        areaResultados.setEditable(false);
        areaResultados.setWrapText(true);
        areaResultados.setStyle(estiloArea(true));

        c.getChildren().addAll(lbl, areaResultados);
        return c;
    }

    private void resultado(String texto, boolean ok) {
        areaResultados.setStyle(estiloArea(ok));
        areaResultados.setText(texto);
        FadeTransition ft = new FadeTransition(Duration.millis(200), areaResultados);
        ft.setFromValue(0.3);
        ft.setToValue(1.0);
        ft.play();
    }

    private void actualizarCombosVertices() {
        String selOr = comboOrigen != null ? comboOrigen.getValue() : null;
        String selDe = comboDestino != null ? comboDestino.getValue() : null;
        List<String> ids = new ArrayList<>(grafoRef.getVerticesMap().keySet());

        if (comboOrigen != null) {
            comboOrigen.getItems().setAll(ids);
            if (selOr != null && ids.contains(selOr))
                comboOrigen.setValue(selOr);
            else if (!ids.isEmpty())
                comboOrigen.setValue(ids.get(0));
        }
        if (comboDestino != null) {
            comboDestino.getItems().setAll(ids);
            if (selDe != null && ids.contains(selDe))
                comboDestino.setValue(selDe);
            else if (ids.size() > 1)
                comboDestino.setValue(ids.get(1));
        }
    }

    private void actualizarCombosAristas(ComboBox<String> de, ComboBox<String> a) {
        List<String> ids = new ArrayList<>(grafoRef.getVerticesMap().keySet());
        de.getItems().setAll(ids);
        a.getItems().setAll(ids);
        if (!ids.isEmpty())
            de.setValue(ids.get(0));
        if (ids.size() > 1)
            a.setValue(ids.get(1));
        de.setOnMouseClicked(ev -> {
            List<String> fr = new ArrayList<>(grafoRef.getVerticesMap().keySet());
            de.getItems().setAll(fr);
            a.getItems().setAll(fr);
        });
    }

    public void actualizarComboZonas() {
        if (comboZonaVertice == null)
            return;
        String prev = comboZonaVertice.getValue();
        List<String> n = arbolRef.obtenerNombresZonas();
        comboZonaVertice.getItems().setAll(n);
        if (prev != null && n.contains(prev))
            comboZonaVertice.setValue(prev);
        else if (!n.isEmpty())
            comboZonaVertice.setValue(n.get(0));
    }

    public void refrescarCanvas() {
        double w = canvasGrafo.getWidth();
        double h = canvasGrafo.getHeight();
        if (w <= 0 || h <= 0)
            return;

        GraphicsContext gc = canvasGrafo.getGraphicsContext2D();

        LinearGradient bg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#07070f")),
                new Stop(1.0, Color.web("#0c0c1e")));
        gc.setFill(bg);
        gc.fillRect(0, 0, w, h);

        if (grafoRef.estaVacio()) {
            gc.setFill(Color.web("#2a2a44"));
            gc.setFont(Font.font("Monospace", 12));
            gc.fillText("El grafo está vacío — agrega puntos y rutas para comenzar.", w / 2 - 240, h / 2);
            return;
        }

        Map<String, List<Arista>> adj = grafoRef.getListaAdyacencia();
        Set<String> dibujadas = new HashSet<>();

        for (Map.Entry<String, List<Arista>> ent : adj.entrySet()) {
            String idU = ent.getKey();
            Punto pU = grafoRef.getPunto(idU);
            if (pU == null)
                continue;

            double ux = escalarX(pU.getCoordX(), w);
            double uy = escalarY(pU.getCoordY(), h);

            for (Arista ar : ent.getValue()) {
                String idV = ar.getDestino();
                Punto pV = grafoRef.getPunto(idV);
                if (pV == null)
                    continue;

                String clave = idU.compareTo(idV) < 0 ? idU + "_" + idV : idV + "_" + idU;
                if (dibujadas.contains(clave))
                    continue;
                dibujadas.add(clave);

                double vx = escalarX(pV.getCoordX(), w);
                double vy = escalarY(pV.getCoordY(), h);

                boolean enC = aristaEnCamino(idU, idV);

                gc.setStroke(enC ? Color.web("#c8a84b") : Color.web("#1a1a30"));
                gc.setLineWidth(enC ? 2.8 : 1.4);
                gc.strokeLine(ux, uy, vx, vy);

                double mx = (ux + vx) / 2;
                double my = (uy + vy) / 2;
                gc.setFill(enC ? Color.web("#c8a84b") : Color.web("#3a3a5a"));
                gc.setFont(Font.font("Monospace", enC ? FontWeight.BOLD : FontWeight.NORMAL, 9));
                gc.fillText(String.format("%.1f", ar.getPeso()), mx + 3, my - 4);
            }
        }

        double radio = Math.max(16, Math.min(22, w / 50.0));
        for (Punto p : grafoRef.getVertices()) {
            boolean enC = caminoResaltado.contains(p.getId());
            boolean esOr = p.getId().equals(nodoResaltado);

            double px = escalarX(p.getCoordX(), w);
            double py = escalarY(p.getCoordY(), h);

            if (enC) {
                gc.setFill(Color.web("#c8a84b22"));
                gc.fillOval(px - radio * 1.7, py - radio * 1.7, radio * 3.4, radio * 3.4);
            }

            gc.setFill(enC ? Color.web("#c8a84b") : Color.web("#0f0f24"));
            gc.fillOval(px - radio, py - radio, radio * 2, radio * 2);

            gc.setStroke(esOr ? Color.web("#ffffff") : enC ? Color.web("#ffe080") : Color.web("#2a2a50"));
            gc.setLineWidth(enC ? 2.2 : 1.2);
            gc.strokeOval(px - radio, py - radio, radio * 2, radio * 2);

            gc.setFill(enC ? Color.web("#07070f") : Color.web("#c8a84b"));
            gc.setFont(Font.font("Monospace", FontWeight.BOLD, (int) (radio * 0.65)));
            gc.fillText(p.getId(), px - radio * 0.25, py + radio * 0.35);

            gc.setFill(Color.web("#6060a0"));
            gc.setFont(Font.font("Monospace", 8));
            String etq = p.getNombre().length() > 13 ? p.getNombre().substring(0, 12) + "." : p.getNombre();
            gc.fillText(etq, px - radio, py + radio + 12);

            gc.setFill(Color.web("#2e2e50"));
            gc.setFont(Font.font("Monospace", 7));
            gc.fillText(p.getZonaPertenece(), px - radio, py + radio + 21);
        }
    }

    private boolean aristaEnCamino(String idU, String idV) {
        if (caminoResaltado.size() < 2)
            return false;
        for (int k = 0; k < caminoResaltado.size() - 1; k++) {
            String a = caminoResaltado.get(k);
            String b = caminoResaltado.get(k + 1);
            if ((a.equals(idU) && b.equals(idV)) || (a.equals(idV) && b.equals(idU)))
                return true;
        }
        return false;
    }

    private double escalarX(double xOriginal, double anchoCanvas) {
        return xOriginal * (anchoCanvas / 760.0);
    }

    private double escalarY(double yOriginal, double altoCanvas) {
        return yOriginal * (altoCanvas / 310.0);
    }

    private VBox tarjeta() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(14, 16, 14, 16));
        v.setStyle(
                "-fx-background-color: #0d0d20;" +
                        "-fx-border-color: #1c1c38;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;");
        return v;
    }

    private GridPane grid() {
        GridPane g = new GridPane();
        g.setHgap(14);
        g.setVgap(10);
        return g;
    }

    private Label tituloSeccion(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#9090cc"));
        return l;
    }

    private Label subTexto(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Monospace", 9));
        l.setTextFill(Color.web("#3a3a5a"));
        l.setWrapText(true);
        return l;
    }

    private Label labelCampo(String t) {
        Label l = new Label(t);
        l.setMinWidth(100);
        l.setFont(Font.font("Monospace", 11));
        l.setTextFill(Color.web("#6060a0"));
        return l;
    }

    private TextField campo(String prompt, double ancho) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefWidth(ancho);
        f.setStyle(
                "-fx-control-inner-background: #0a0a18;" +
                        "-fx-text-fill: #d0d0f0;" +
                        "-fx-font-family: Monospace;" +
                        "-fx-font-size: 11px;" +
                        "-fx-border-color: #1e1e3c;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 3;" +
                        "-fx-background-radius: 3;");
        return f;
    }

    private ComboBox<String> combo(double ancho) {
        ComboBox<String> c = new ComboBox<>();
        c.setPrefWidth(ancho);
        c.setStyle(
                "-fx-background-color: #0a0a18;" +
                        "-fx-text-fill: #d0d0f0;" +
                        "-fx-font-family: Monospace;" +
                        "-fx-border-color: #1e1e3c;" +
                        "-fx-border-radius: 3;");
        return c;
    }

    private Button btnPrimario(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color: #c8a84b;" +
                        "-fx-text-fill: #07070f;" +
                        "-fx-font-family: Monospace;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 8 18 8 18;" +
                        "-fx-background-radius: 4;" +
                        "-fx-cursor: hand;");
        return b;
    }

    private Button btnSecundario(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #c8a84b;" +
                        "-fx-font-family: Monospace;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 8 18 8 18;" +
                        "-fx-background-radius: 4;" +
                        "-fx-border-color: #c8a84b;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 4;" +
                        "-fx-cursor: hand;");
        return b;
    }

    private Button btnApagado(String t) {
        Button b = new Button(t);
        b.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #3a3a5a;" +
                        "-fx-font-family: Monospace;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 8 14 8 14;" +
                        "-fx-background-radius: 4;" +
                        "-fx-border-color: #1e1e3c;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 4;" +
                        "-fx-cursor: hand;");
        return b;
    }

    private void animarBoton(Button b) {
        b.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(110), b);
            st.setToX(1.04);
            st.setToY(1.04);
            st.play();
        });
        b.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(110), b);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    private String estiloArea(boolean ok) {
        return "-fx-control-inner-background: #06060f;" +
                "-fx-text-fill: " + (ok ? "#c8a84b" : "#bb4444") + ";" +
                "-fx-font-family: Monospace;" +
                "-fx-font-size: 11px;" +
                "-fx-border-color: " + (ok ? "#1c1c38" : "#441a1a") + ";" +
                "-fx-border-width: 1;";
    }
}

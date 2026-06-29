package ciudadnav.ui;

import ciudadnav.arbol.ArbolZonas;
import ciudadnav.modelo.Zona;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;

public class PanelArbol extends VBox {

    private ArbolZonas arbolRef;
    private Canvas canvasArbol;
    private Pane canvasContenedor;
    private TextArea areaResultados;
    private ComboBox<String> comboPadres;
    private Runnable onCambio;

    public PanelArbol(ArbolZonas arbol, Runnable onCambio) {
        this.arbolRef = arbol;
        this.onCambio = onCambio;
        construirUI();
        refrescarCanvas();
    }

    private void construirUI() {
        setSpacing(0);
        setPadding(new Insets(0));
        setStyle("-fx-background-color: #0a0a16;");

        canvasArbol = new Canvas();
        canvasContenedor = new Pane(canvasArbol);
        canvasContenedor.setStyle("-fx-background-color: #07070f;");
        canvasContenedor.setMinHeight(240);
        canvasContenedor.setPrefHeight(260);
        canvasContenedor.setMaxHeight(300);

        canvasContenedor.widthProperty().addListener((obs, ov, nv) -> {
            canvasArbol.setWidth(nv.doubleValue());
            refrescarCanvas();
        });
        canvasContenedor.heightProperty().addListener((obs, ov, nv) -> {
            canvasArbol.setHeight(nv.doubleValue());
            refrescarCanvas();
        });

        HBox.setHgrow(canvasContenedor, Priority.ALWAYS);
        VBox.setVgrow(canvasContenedor, Priority.NEVER);

        VBox panelControles = new VBox(10);
        panelControles.setPadding(new Insets(16, 20, 16, 20));
        panelControles.setStyle("-fx-background-color: #0a0a16;");
        panelControles.getChildren().addAll(
                tarjetaInsertar(),
                tarjetaBuscar(),
                tarjetaRecorridos(),
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

    private VBox tarjetaInsertar() {
        VBox t = tarjeta();

        Label tit = tituloSeccion("➕  Insertar zona");
        Label sub = subTexto("Elige el nodo padre, escribe nombre y tipo, y agrega la nueva zona al árbol.");

        GridPane g = grid();

        Label lPadre = labelCampo("Zona padre");
        comboPadres = combo(200);
        comboPadres.setPromptText("Selecciona padre...");
        actualizarComboPadres();

        Label lNombre = labelCampo("Nombre");
        TextField fNombre = campo("ej. Barrio Las Palmas", 210);

        Label lTipo = labelCampo("Tipo");
        TextField fTipo = campo("ej. Barrio, Sector", 160);

        g.add(lPadre, 0, 0);
        g.add(comboPadres, 1, 0);
        g.add(lNombre, 0, 1);
        g.add(fNombre, 1, 1);
        g.add(lTipo, 0, 2);
        g.add(fTipo, 1, 2);

        Button btn = btnPrimario("Agregar zona al árbol");
        btn.setMaxWidth(Double.MAX_VALUE);
        animarBoton(btn);
        btn.setOnAction(e -> {
            String padre = comboPadres.getValue();
            String nombre = fNombre.getText().trim();
            String tipo = fTipo.getText().trim();

            if (nombre.isEmpty() || tipo.isEmpty()) {
                resultado("⚠  Completa nombre y tipo antes de agregar.", false);
                return;
            }
            if (arbolRef.buscarPorNombre(nombre) != null) {
                resultado("⚠  Ya existe una zona llamada '" + nombre + "'.", false);
                return;
            }
            arbolRef.insertar(padre, new Zona(nombre, tipo));
            actualizarComboPadres();
            refrescarCanvas();
            fNombre.clear();
            fTipo.clear();
            if (onCambio != null)
                onCambio.run();
            resultado("✔  Zona '" + nombre + "' insertada bajo '" + padre + "'.", true);
        });

        t.getChildren().addAll(tit, sub, g, btn);
        return t;
    }

    private VBox tarjetaBuscar() {
        VBox t = tarjeta();

        Label tit = tituloSeccion("🔍  Buscar zona");
        Label sub = subTexto("Escribe el nombre de cualquier zona para ver sus detalles e hijos directos.");

        HBox fila = new HBox(8);
        fila.setAlignment(Pos.CENTER_LEFT);

        TextField fBus = campo("ej. Norte, Hospital...", 260);
        Button btn = btnSecundario("Buscar");
        animarBoton(btn);

        btn.setOnAction(e -> {
            String nombre = fBus.getText().trim();
            if (nombre.isEmpty()) {
                resultado("⚠  Escribe un nombre para buscar.", false);
                return;
            }

            Zona z = arbolRef.buscarPorNombre(nombre);
            if (z != null) {
                StringBuilder sb = new StringBuilder("✔  Zona encontrada\n\n");
                sb.append("   Nombre : ").append(z.getNombre()).append("\n");
                sb.append("   Tipo   : ").append(z.getTipo()).append("\n");
                sb.append("   Hijos  : ");
                if (z.tieneHijos())
                    z.getSubzonas().forEach(h -> sb.append(h.getNombre()).append("  "));
                else
                    sb.append("(sin subzonas)");
                resultado(sb.toString(), true);
            } else {
                resultado("✘  No existe ninguna zona llamada '" + nombre + "'.", false);
            }
            fBus.clear();
        });
        fBus.setOnAction(e -> btn.fire());

        fila.getChildren().addAll(fBus, btn);
        t.getChildren().addAll(tit, sub, fila);
        return t;
    }

    private VBox tarjetaRecorridos() {
        VBox t = tarjeta();

        Label tit = tituloSeccion("🔄  Recorridos del árbol");

        HBox mini = new HBox(32);
        mini.getChildren().addAll(
                miniDesc("Preorden", "Raíz primero, luego sus hijos (profundidad)"),
                miniDesc("BFS", "Nivel a nivel de arriba hacia abajo (anchura)"));

        HBox botones = new HBox(10);
        Button bPre = btnPrimario("▶  Preorden");
        Button bBFS = btnPrimario("▶  BFS");
        bPre.setPrefWidth(190);
        bBFS.setPrefWidth(190);
        animarBoton(bPre);
        animarBoton(bBFS);

        bPre.setOnAction(e -> {
            List<String> r = arbolRef.recorridoPreorden();
            resultado("Preorden (raíz → hijos):\n\n" + String.join("\n", r), true);
        });
        bBFS.setOnAction(e -> {
            List<String> r = arbolRef.recorridoBFS();
            resultado("BFS (nivel por nivel):\n\n" + String.join("  →  ", r), true);
        });

        botones.getChildren().addAll(bPre, bBFS);
        t.getChildren().addAll(tit, mini, botones);
        return t;
    }

    private VBox seccionResultados() {
        VBox c = new VBox(6);

        Label lbl = new Label("RESULTADO");
        lbl.setFont(Font.font("Monospace", FontWeight.BOLD, 9));
        lbl.setTextFill(Color.web("#2a2a50"));

        areaResultados = new TextArea("Los resultados aparecerán aquí...");
        areaResultados.setPrefHeight(110);
        areaResultados.setEditable(false);
        areaResultados.setWrapText(true);
        areaResultados.setStyle(estiloArea(false));

        c.getChildren().addAll(lbl, areaResultados);
        return c;
    }

    private void resultado(String texto, boolean ok) {
        areaResultados.setStyle(estiloArea(ok));
        areaResultados.setText(texto);

        FadeTransition ft = new FadeTransition(Duration.millis(200), areaResultados);
        ft.setFromValue(0.4);
        ft.setToValue(1.0);
        ft.play();
    }

    private void actualizarComboPadres() {
        String sel = comboPadres.getValue();
        comboPadres.getItems().clear();
        List<String> nombres = arbolRef.obtenerNombresZonas();
        comboPadres.getItems().addAll(nombres);
        if (sel != null && comboPadres.getItems().contains(sel))
            comboPadres.setValue(sel);
        else if (!comboPadres.getItems().isEmpty())
            comboPadres.setValue(comboPadres.getItems().get(0));
    }

    public void refrescarCanvas() {
        double w = canvasArbol.getWidth();
        double h = canvasArbol.getHeight();
        if (w <= 0 || h <= 0)
            return;

        GraphicsContext gc = canvasArbol.getGraphicsContext2D();

        LinearGradient fondo = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#07070f")),
                new Stop(1.0, Color.web("#0d0d1f")));
        gc.setFill(fondo);
        gc.fillRect(0, 0, w, h);

        if (arbolRef.getRaiz() == null) {
            gc.setFill(Color.web("#2a2a44"));
            gc.setFont(Font.font("Monospace", 12));
            gc.fillText("El árbol está vacío — inserta la primera zona.", w / 2 - 190, h / 2);
            return;
        }

        dibujarNodo(gc, arbolRef.getRaiz(), (int) (w / 2), 38, (int) (w / 2) - 30, h);
    }

    private void dibujarNodo(GraphicsContext gc, Zona nodo, int x, int y, int ancho, double altCanvas) {
        double radio = 26;

        gc.setFill(Color.web("#10102a"));
        gc.fillOval(x - radio, y - radio, radio * 2, radio * 2);

        gc.setStroke(Color.web("#c8a84b"));
        gc.setLineWidth(1.8);
        gc.strokeOval(x - radio, y - radio, radio * 2, radio * 2);

        gc.setFill(Color.web("#c8a84b"));
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 9));
        String txt = nodo.getNombre().length() > 8 ? nodo.getNombre().substring(0, 7) + "." : nodo.getNombre();
        gc.fillText(txt, x - txt.length() * 3.0, y + 3);

        gc.setFill(Color.web("#3a3a66"));
        gc.setFont(Font.font("Monospace", 7));
        String tipo = nodo.getTipo();
        gc.fillText(tipo, x - tipo.length() * 2.2, y + radio + 11);

        List<Zona> hijos = nodo.getSubzonas();
        if (hijos.isEmpty())
            return;

        int n = hijos.size();
        int espaciado = Math.max(72, ancho / Math.max(n, 1));
        int inicioX = x - ((n - 1) * espaciado) / 2;
        int hijoY = y + 82;

        if (hijoY + radio > altCanvas)
            return;

        for (int i = 0; i < n; i++) {
            int hijoX = inicioX + i * espaciado;

            gc.setStroke(Color.web("#1e1e3c"));
            gc.setLineWidth(1.5);
            gc.strokeLine(x, y + radio, hijoX, hijoY - radio);

            dibujarNodo(gc, hijos.get(i), hijoX, hijoY, espaciado, altCanvas);
        }
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
        l.setMinWidth(90);
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

    private void animarBoton(Button b) {
        b.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), b);
            st.setToX(1.04);
            st.setToY(1.04);
            st.play();
        });
        b.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), b);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    private VBox miniDesc(String titulo, String sub) {
        VBox v = new VBox(2);
        Label lt = new Label(titulo);
        lt.setFont(Font.font("Monospace", FontWeight.BOLD, 10));
        lt.setTextFill(Color.web("#c8a84b"));
        Label ls = new Label(sub);
        ls.setFont(Font.font("Monospace", 9));
        ls.setTextFill(Color.web("#3a3a5a"));
        v.getChildren().addAll(lt, ls);
        return v;
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

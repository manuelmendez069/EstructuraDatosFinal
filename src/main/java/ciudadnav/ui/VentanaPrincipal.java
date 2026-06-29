package ciudadnav.ui;

import ciudadnav.arbol.ArbolZonas;
import ciudadnav.grafo.GrafoCiudad;
import ciudadnav.modelo.DatosIniciales;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VentanaPrincipal {

    private ArbolZonas arbolCiudad;
    private GrafoCiudad grafoCiudad;
    private PanelArbol panelArbol;
    private PanelGrafo panelGrafo;
    private Label lblEstado;

    public void iniciar(Stage escenario) {
        arbolCiudad = new ArbolZonas();
        grafoCiudad = new GrafoCiudad();
        DatosIniciales.cargar(arbolCiudad, grafoCiudad);

        panelArbol = new PanelArbol(arbolCiudad, this::actualizarEstado);
        panelGrafo = new PanelGrafo(grafoCiudad, arbolCiudad, this::actualizarEstado);

        BorderPane raiz = new BorderPane();
        raiz.setStyle("-fx-background-color: #07070f;");

        raiz.setTop(construirNavbar());
        raiz.setCenter(construirTabs());
        raiz.setBottom(construirStatusBar());

        Scene escena = new Scene(raiz, 920, 880, Color.web("#07070f"));

        escenario.setTitle("CiudadNav");
        escenario.setScene(escena);
        escenario.setMinWidth(700);
        escenario.setMinHeight(600);
        escenario.show();

        Timeline entrada = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(raiz.opacityProperty(), 0.0)),
                new KeyFrame(Duration.millis(380), new KeyValue(raiz.opacityProperty(), 1.0)));
        entrada.play();
    }

    private HBox construirNavbar() {
        HBox nav = new HBox(14);
        nav.setPadding(new Insets(12, 22, 12, 22));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle(
                "-fx-background-color: #07070f;" +
                        "-fx-border-color: #1c1c38;" +
                        "-fx-border-width: 0 0 1 0;");

        Label punto = new Label("◈");
        punto.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        punto.setTextFill(Color.web("#c8a84b"));

        Label logo = new Label("CiudadNav");
        logo.setFont(Font.font("Monospace", FontWeight.BOLD, 17));
        logo.setTextFill(Color.web("#e8e8f8"));

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        Label curso = new Label("Estructura de Datos II · 2026");
        curso.setFont(Font.font("Monospace", 10));
        curso.setTextFill(Color.web("#333355"));

        nav.getChildren().addAll(punto, logo, espaciador, curso);
        return nav;
    }

    private TabPane construirTabs() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle(
                "-fx-background-color: #0a0a16;" +
                        "-fx-tab-min-width: 180px;" +
                        "-fx-tab-min-height: 36px;");

        Tab tabArbol = new Tab("  🌳  Árbol de Zonas  ");
        tabArbol.setContent(panelArbol);

        Tab tabGrafo = new Tab("  🗺  Grafo de Rutas  ");
        tabGrafo.setContent(panelGrafo);

        tabs.getTabs().addAll(tabArbol, tabGrafo);

        tabs.getSelectionModel().selectedItemProperty().addListener((obs, ant, sel) -> {
            if (sel == tabGrafo) {
                panelGrafo.actualizarComboZonas();
                panelGrafo.refrescarCanvas();
            }
            if (sel != null) {
                FadeTransition ft = new FadeTransition(Duration.millis(180),
                        sel.getContent());
                ft.setFromValue(0.6);
                ft.setToValue(1.0);
                ft.play();
            }
        });

        return tabs;
    }

    private HBox construirStatusBar() {
        HBox barra = new HBox(16);
        barra.setPadding(new Insets(6, 22, 6, 22));
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setStyle(
                "-fx-background-color: #05050c;" +
                        "-fx-border-color: #1c1c38;" +
                        "-fx-border-width: 1 0 0 0;");

        Label dot = new Label("●");
        dot.setTextFill(Color.web("#3a3a60"));
        dot.setFont(Font.font("Monospace", 8));

        lblEstado = new Label();
        lblEstado.setFont(Font.font("Monospace", 10));
        lblEstado.setTextFill(Color.web("#3a3a60"));
        actualizarEstado();

        barra.getChildren().addAll(dot, lblEstado);
        return barra;
    }

    public void actualizarEstado() {
        if (lblEstado == null)
            return;
        int zonas = arbolCiudad.obtenerNombresZonas().size();
        int puntos = grafoCiudad.getVerticesMap().size();
        int rutas = grafoCiudad.getListaAdyacencia().values()
                .stream().mapToInt(java.util.List::size).sum() / 2;
        lblEstado.setText(
                zonas + " zonas  ·  " +
                        puntos + " puntos  ·  " +
                        rutas + " rutas");
    }
}

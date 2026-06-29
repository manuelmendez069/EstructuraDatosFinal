package ciudadnav;

import ciudadnav.ui.VentanaPrincipal;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage escenario) {
        VentanaPrincipal ventana = new VentanaPrincipal();
        ventana.iniciar(escenario);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

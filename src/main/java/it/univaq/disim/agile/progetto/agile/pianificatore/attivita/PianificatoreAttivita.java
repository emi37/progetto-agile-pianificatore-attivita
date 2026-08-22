package it.univaq.disim.agile.progetto.agile.pianificatore.attivita;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import javafx.application.Application;
import javafx.stage.Stage;

public class PianificatoreAttivita extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            // Otteniamo l'unica istanza del ViewDispatcher
            ViewDispatcher viewDispatcher = ViewDispatcher.getInstance();

            // Passiamo lo stage iniziale per mostrare la schermata di login
            viewDispatcher.loginView(stage);

        } catch (ViewException e) {
            e.printStackTrace();
        }
    }
}

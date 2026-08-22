package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.main;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.ViewException;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe di avvio dell'applicazione JavaFX.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Chiediamo al Dispatcher di caricare la schermata iniziale (il Login)
            ViewDispatcher.getInstance().loginView(stage);
        } catch (ViewException e) {
            System.err.println("Errore critico all'avvio dell'applicazione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Metodo standard per lanciare il framework JavaFX
        launch(args);
    }
}
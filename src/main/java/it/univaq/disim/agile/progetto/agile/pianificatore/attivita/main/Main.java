package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.main;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewException;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // il dispatcher carica la schermata inziale del progetto ovvero il login
            ViewDispatcher.getInstance().loginView(stage);
        } catch (ViewException e) {
            System.err.println("Errore all'avvio riprovare: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //  per lanciare javafx
        launch(args);
    }
}
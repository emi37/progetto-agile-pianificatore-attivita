/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view;

/**
 *
 * @author edoar
 */
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.ViewException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewDispatcher {

    // 1. Implementazione del Singleton
    private static ViewDispatcher instance = new ViewDispatcher();

    private Stage stage;
    private BorderPane layout;

    private static final String FXML_SUFFIX = ".fxml";
    private static final String RESOURCE_BASE = "/viste/";

    // Costruttore privato per il Singleton
    private ViewDispatcher() {
    }

    // Metodo statico per ottenere l'istanza
    public static ViewDispatcher getInstance() {
        return instance;
    }

    // 2. Metodo per visualizzare la schermata di login iniziale
    public void loginView(Stage stage) throws ViewException {
        this.stage = stage;
        Parent loginView = loadView("login");
        Scene scene = new Scene(loginView);
        stage.setScene(scene);
        stage.show();
    }

    // 3. Metodo invocato dopo il login: imposta il layout base e la home
    public void loggedIn() throws ViewException {
        try {
            layout = (BorderPane) loadView("layout");
            Parent home = loadView("home");
            layout.setCenter(home);
            Scene scene = new Scene(layout);
            stage.setScene(scene);
        } catch (ViewException e) {
            renderError(e);
        }
    }

    // 4. Metodo per il logout: ricarica la pagina di login
    public void logout() {
        try {
            Parent loginView = loadView("login");
            Scene scene = new Scene(loginView);
            stage.setScene(scene);
        } catch (ViewException e) {
            renderError(e);
        }
    }

    // 5. Metodo per caricare viste generiche al centro del layout
    public void renderView(String viewName) {
        try {
            Parent view = loadView(viewName);
            layout.setCenter(view);
        } catch (ViewException e) {
            renderError(e);
        }
    }

    // 6. Metodi di utilità per gestire gli errori e caricare fisicamente i file FXML
    private void renderError(ViewException e) {
        e.printStackTrace();
        System.exit(1);
    }

    private Parent loadView(String view) throws ViewException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_BASE + view + FXML_SUFFIX));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ViewException(e);
        }
    }

    /**
     * @throws ViewException
     */
    public void registrazioneView() throws ViewException {
        try {
            Parent registrazioneRoot = FXMLLoader.load(getClass().getResource("/viste/registrazione.fxml"));
            Scene scene = new Scene(registrazioneRoot);
            stage.setScene(scene);
            stage.setTitle("Pianificatore attività (Registrazione)");
            stage.show();
        } catch (IOException e) {
            throw new ViewException("Errore durante il caricamento della schermata di Registrazione", e);
        }
    }
<<<<<<< Updated upstream
}
=======
}
>>>>>>> Stashed changes

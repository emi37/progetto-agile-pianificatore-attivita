package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.UtenteDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller per la vista del Login. Initializable e per eseguire le logiche
 * all'avvio.
 */
public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private UtenteDAO utenteDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utenteDAO = new UtenteDAO();
        errorLabel.setText("");
    }

    @FXML
    private void loginAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Inserisci username e password.");
            return;
        }

        Utente utente = utenteDAO.autentica(username, password);

        if (utente != null) {
            errorLabel.setText("");
            System.out.println("Login confermato per: " + utente.getUsername());

            // Salviamo l'utente del dispatcher prima di cambiare vista
            ViewDispatcher.getInstance().setUtenteLoggato(utente);

            try {
                ViewDispatcher.getInstance().homeView();
            } catch (ViewException e) {
                e.printStackTrace();
                errorLabel.setText("Errore di caricamento della dashboard.");
            }
        } else {
            errorLabel.setText("Credenziali non valide.");
        }
    }

    @FXML
    private void vaiARegistrazioneAction(ActionEvent event) {
        try {
            ViewDispatcher.getInstance().registrazioneView();
        } catch (ViewException e) {
            e.printStackTrace();
        }
    }
}//ok

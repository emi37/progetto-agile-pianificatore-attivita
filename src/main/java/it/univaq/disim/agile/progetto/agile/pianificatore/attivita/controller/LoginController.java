package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.UtenteDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller per la vista di Login. Implementa Initializable per eseguire logiche all'avvio.
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
            // Futuro collegamento alla dashboard tramite il Dispatcher
            System.out.println("Login confermato per: " + utente.getUsername());
        } else {
            errorLabel.setText("Credenziali non valide.");
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

/**
 *
 * @author Filippo
 */


import database.UtenteDAO;
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
 * Controller per la vista di Registrazione. Implementa Initializable.
 */
public class RegistrazioneController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confermaPasswordField;

    @FXML
    private Label errorLabel;

    private UtenteDAO utenteDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utenteDAO = new UtenteDAO();
        errorLabel.setText(""); 
    }

    @FXML
    private void registraAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String conferma = confermaPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || conferma.isEmpty()) {
            errorLabel.setText("Compila tutti i campi.");
            return;
        }

        if (!password.equals(conferma)) {
            errorLabel.setText("Le password non corrispondono.");
            return;
        }

        boolean successo = utenteDAO.registraUtente(username, password);

        if (successo) {
            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Registrazione completata! Torna al login.");
        } else {
            errorLabel.setText("Errore durante la registrazione. Username forse già in uso?");
        }
    }
    
    @FXML
    private void tornaAlLoginAction(ActionEvent event) {
        try {
            // Utilizziamo il dispatcher, passando lo stage attualmente in uso
            ViewDispatcher.getInstance().loginView((javafx.stage.Stage) usernameField.getScene().getWindow());
        } catch (ViewException e) {
            e.printStackTrace();
        }
    }
}
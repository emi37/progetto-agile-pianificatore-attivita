package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.AttivitaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Categoria;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Priorita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CreazioneAttivitaController implements Initializable {

    @FXML private TextField titoloField;
    @FXML private DatePicker scadenzaPicker;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ComboBox<String> prioritaComboBox;
    @FXML private Label erroreLabel;

    private AttivitaDAO attivitaDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.attivitaDAO = new AttivitaDAO();
        // Aggiungiamo tutte le opzioni coerenti con il database
        this.categoriaComboBox.getItems().addAll("Studio", "Lavoro", "Palestra", "Hobby", "Finanze");
        this.prioritaComboBox.getItems().addAll("Bassa", "Media", "Alta");
        this.erroreLabel.setText("");
    }

    @FXML
    private void salvaAttivitaAction(ActionEvent event) {
        try {
            String titolo = this.titoloField.getText();
            LocalDate scadenza = this.scadenzaPicker.getValue();
            
            if (titolo == null || titolo.trim().isEmpty()) {
                this.erroreLabel.setText("Attenzione: Il Titolo è obbligatorio.");
                return;
            }
            
            if (this.categoriaComboBox.getValue() == null || this.prioritaComboBox.getValue() == null) {
                this.erroreLabel.setText("Attenzione: Seleziona Categoria e Priorità.");
                return;
            }

            String categoriaSelezionata = this.categoriaComboBox.getValue();
            String prioritaSelezionata = this.prioritaComboBox.getValue();

            // Mappatura dinamica della Categoria basata sui veri ID del tuo DB (Screenshot)
            int idCategoriaReale = 4; // Fallback su Studio
            switch (categoriaSelezionata) {
                case "Studio":   idCategoriaReale = 4; break;
                case "Lavoro":   idCategoriaReale = 5; break;
                case "Palestra": idCategoriaReale = 6; break;
                case "Hobby":    idCategoriaReale = 7; break;
                case "Finanze":  idCategoriaReale = 8; break;
            }

            // Mappatura dinamica della Priorità (1=Bassa, 2=Media, 3=Alta)
            int idPrioritaReale = 3;
            switch (prioritaSelezionata) {
                case "Bassa": idPrioritaReale = 1; break;
                case "Media": idPrioritaReale = 2; break;
                case "Alta":  idPrioritaReale = 3; break;
            }

            Utente utenteLoggato = ViewDispatcher.getInstance().getUtenteLoggato();
            if (utenteLoggato == null) {
                this.erroreLabel.setText("Errore critico: Utente non trovato in sessione.");
                return;
            }
            
            int idRealeUtente = utenteLoggato.getId();

            // Creiamo gli oggetti di Dominio con i dati reali e dinamici dell'utente
            Categoria categoria = new Categoria(idCategoriaReale, categoriaSelezionata, idRealeUtente);
            Priorita priorita = new Priorita(idPrioritaReale, prioritaSelezionata);
            
            Attivita nuovaAttivita = new Attivita(0, titolo, "", scadenza, null, false, null, categoria, priorita);

            // Salvataggio tramite DAO
            boolean salvato = this.attivitaDAO.inserisciAttivita(nuovaAttivita, idRealeUtente, idCategoriaReale, idPrioritaReale);

            if (salvato) {
                ViewDispatcher.getInstance().homeView();
            } else {
                this.erroreLabel.setText("Errore dal Database (verifica connessione/query).");
            }
            
        } catch (Exception e) {
            this.erroreLabel.setText("Eccezione di sistema: " + e.toString());
            e.printStackTrace();
        }
    }

    @FXML
    private void annullaAction(ActionEvent event) {
        try {
            ViewDispatcher.getInstance().homeView();
        } catch (ViewException e) {
            e.printStackTrace();
        }
    }
}
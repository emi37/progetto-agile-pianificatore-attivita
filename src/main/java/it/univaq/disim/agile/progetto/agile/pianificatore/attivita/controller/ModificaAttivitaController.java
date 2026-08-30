/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

/**
 *
 * @author Filippo
 */
import database.AttivitaDAO;
import database.NotificaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Notifica;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModificaAttivitaController implements Initializable {

    @FXML private TextField titoloField;
    @FXML private DatePicker scadenzaPicker;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ComboBox<String> prioritaComboBox;
    @FXML private CheckBox completataCheckBox;
    
    // UI Epica 5
    @FXML private CheckBox promemoriaManualeCheck;
    @FXML private ComboBox<String> anticipoComboBox;
    
    @FXML private Label erroreLabel;

    private AttivitaDAO attivitaDAO;
    private NotificaDAO notificaDAO;
    private Attivita attivitaInModifica;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.attivitaDAO = new AttivitaDAO();
            this.notificaDAO = new NotificaDAO();
            
            this.categoriaComboBox.getItems().addAll("Studio", "Lavoro", "Palestra", "Hobby", "Finanze");
            this.prioritaComboBox.getItems().addAll("Bassa", "Media", "Alta");
            this.anticipoComboBox.getItems().addAll("1 ora prima", "1 giorno prima", "2 giorni prima");
            
            this.erroreLabel.setText("");

            this.attivitaInModifica = ViewDispatcher.getInstance().getAttivitaSelezionata();

            // binding dati iniziali
            if (this.attivitaInModifica != null) {
                this.titoloField.setText(this.attivitaInModifica.getTitolo());
                this.scadenzaPicker.setValue(this.attivitaInModifica.getDataScadenza());
                this.categoriaComboBox.setValue(this.attivitaInModifica.getCategoria().getNomeCategoria());
                this.prioritaComboBox.setValue(this.attivitaInModifica.getPriorita().getLivello());
                this.completataCheckBox.setSelected(this.attivitaInModifica.isCompletata());
            } else {
                this.erroreLabel.setText("Nessuna attività trovata nel dispatcher.");
            }
        } catch (Exception e) {
            System.err.println("Errore in fase di init del controller di modifica.");
            e.printStackTrace();
        }
    }

    @FXML
    private void abilitaPromemoriaAction(ActionEvent event) {
        // toggle tendina
        this.anticipoComboBox.setDisable(!this.promemoriaManualeCheck.isSelected());
    }

    @FXML
    private void salvaModificheAction(ActionEvent event) {
        try {
            String titolo = this.titoloField.getText();
            LocalDate scadenza = this.scadenzaPicker.getValue();
            
            // check parametri base
            if (titolo == null || titolo.trim().isEmpty()) {
                this.erroreLabel.setText("Il Titolo è obbligatorio.");
                return;
            }
            
            if (this.categoriaComboBox.getValue() == null || this.prioritaComboBox.getValue() == null) {
                this.erroreLabel.setText("Seleziona Categoria e Priorità.");
                return;
            }
            
            if (scadenza == null) {
                this.erroreLabel.setText("Data scadenza richiesta per i promemoria.");
                return;
            }

            // mapping da stringhe a id db
            int idCategoriaReale = 4;
            switch (this.categoriaComboBox.getValue()) {
                case "Studio":   idCategoriaReale = 4; break;
                case "Lavoro":   idCategoriaReale = 5; break;
                case "Palestra": idCategoriaReale = 6; break;
                case "Hobby":    idCategoriaReale = 7; break;
                case "Finanze":  idCategoriaReale = 8; break;
            }

            int idPrioritaReale = 3;
            switch (this.prioritaComboBox.getValue()) {
                case "Bassa": idPrioritaReale = 1; break;
                case "Media": idPrioritaReale = 2; break;
                case "Alta":  idPrioritaReale = 3; break;
            }

            // update oggetto in RAM
            this.attivitaInModifica.setTitolo(titolo);
            this.attivitaInModifica.setDataScadenza(scadenza);
            
            boolean isCompletataOra = this.completataCheckBox.isSelected();
            this.attivitaInModifica.setCompletata(isCompletataOra);
            
            if (isCompletataOra && this.attivitaInModifica.getDataCompletamento() == null) {
                this.attivitaInModifica.setDataCompletamento(LocalDate.now());
            } else if (!isCompletataOra) {
                this.attivitaInModifica.setDataCompletamento(null);
            }

            // push su DB
            boolean salvataggioOk = this.attivitaDAO.aggiornaAttivita(this.attivitaInModifica, idCategoriaReale, idPrioritaReale);

            if (salvataggioOk) {
                // per fare le cose precise andrebbero cancellate le vecchie notifiche associate
                // a questo ID prima di inserire quelle nuove 
                
                LocalDateTime orarioScadenzaAggiornato = scadenza.atTime(9, 0);

                if (idPrioritaReale == 3) {
                    Notifica n15 = new Notifica("Alert 15gg: " + titolo, "DA_LEGGERE", orarioScadenzaAggiornato.minusDays(15), this.attivitaInModifica);
                    Notifica n5 = new Notifica("Alert 5gg: " + titolo, "DA_LEGGERE", orarioScadenzaAggiornato.minusDays(5), this.attivitaInModifica);
                    this.notificaDAO.inserisciNotifica(n15);
                    this.notificaDAO.inserisciNotifica(n5);
                }
                if (idPrioritaReale >= 2) {
                    Notifica n1 = new Notifica("Scadenza domani: " + titolo, "DA_LEGGERE", orarioScadenzaAggiornato.minusDays(1), this.attivitaInModifica);
                    this.notificaDAO.inserisciNotifica(n1);
                }

                // override del metodo di notifiche manuali 
                if (this.promemoriaManualeCheck.isSelected() && this.anticipoComboBox.getValue() != null) {
                    String comboScelta = this.anticipoComboBox.getValue();
                    LocalDateTime invioManuale = orarioScadenzaAggiornato;
                    
                    if (comboScelta.equals("1 ora prima")) invioManuale = orarioScadenzaAggiornato.minusHours(1);
                    if (comboScelta.equals("1 giorno prima")) invioManuale = orarioScadenzaAggiornato.minusDays(1);
                    if (comboScelta.equals("2 giorni prima")) invioManuale = orarioScadenzaAggiornato.minusDays(2);
                    
                    Notifica customNotifica = new Notifica("Custom alert: " + titolo, "DA_LEGGERE", invioManuale, this.attivitaInModifica);
                    this.notificaDAO.inserisciNotifica(customNotifica);
                }

                ViewDispatcher.getInstance().homeView();
            } else {
                this.erroreLabel.setText("Niente, l'update sul database è fallito.");
            }
        } catch (Exception e) {
            System.err.println("Eccezione catturata in salvaModificheAction.");
            this.erroreLabel.setText("Errore logico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminaAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Sicuro di voler procedere?");
        alert.setContentText("I dati verranno rimossi definitivamente dal DB.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean eliminato = this.attivitaDAO.eliminaAttivita(this.attivitaInModifica.getId());
            if (eliminato) {
                try {
                    ViewDispatcher.getInstance().homeView();
                } catch (ViewException e) {
                    e.printStackTrace();
                }
            } else {
                this.erroreLabel.setText("Errore SQL durante la delete.");
            }
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
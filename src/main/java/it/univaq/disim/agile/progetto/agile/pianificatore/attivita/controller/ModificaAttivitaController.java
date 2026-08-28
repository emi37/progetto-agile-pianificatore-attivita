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
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class ModificaAttivitaController implements Initializable {

    @FXML private TextField titoloField;
    @FXML private DatePicker scadenzaPicker;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ComboBox<String> prioritaComboBox;
    @FXML private CheckBox completataCheckBox;
    @FXML private Label erroreLabel;

    private AttivitaDAO attivitaDAO;
    private Attivita attivitaInModifica;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.attivitaDAO = new AttivitaDAO();
            
            this.categoriaComboBox.getItems().addAll("Studio", "Lavoro", "Palestra", "Hobby", "Finanze");
            this.prioritaComboBox.getItems().addAll("Bassa", "Media", "Alta");
            this.erroreLabel.setText("");

            this.attivitaInModifica = ViewDispatcher.getInstance().getAttivitaSelezionata();

            if (this.attivitaInModifica != null) {
                this.titoloField.setText(this.attivitaInModifica.getTitolo());
                this.scadenzaPicker.setValue(this.attivitaInModifica.getDataScadenza());
                this.categoriaComboBox.setValue(this.attivitaInModifica.getCategoria().getNomeCategoria());
                this.prioritaComboBox.setValue(this.attivitaInModifica.getPriorita().getLivello());
                
                // Binding dei dati: pre-compila la checkbox se l'attività era già completata
                this.completataCheckBox.setSelected(this.attivitaInModifica.isCompletata());
            } else {
                this.erroreLabel.setText("Errore di caricamento attività.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void salvaModificheAction(ActionEvent event) {
        try {
            String titolo = this.titoloField.getText();
            LocalDate scadenza = this.scadenzaPicker.getValue();
            
            if (titolo == null || titolo.trim().isEmpty()) {
                this.erroreLabel.setText("Il Titolo è obbligatorio.");
                return;
            }
            
            if (this.categoriaComboBox.getValue() == null || this.prioritaComboBox.getValue() == null) {
                this.erroreLabel.setText("Seleziona Categoria e Priorità.");
                return;
            }

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

            // 1. Aggiorna i campi di testo nel Dominio
            this.attivitaInModifica.setTitolo(titolo);
            this.attivitaInModifica.setDataScadenza(scadenza);
            
            // 2. Verifica lo stato della CheckBox
            boolean isCompletataOra = this.completataCheckBox.isSelected();
            this.attivitaInModifica.setCompletata(isCompletataOra);
            
            // 3. Gestione automatica della data di completamento
            if (isCompletataOra && this.attivitaInModifica.getDataCompletamento() == null) {
                // Se la spuntiamo oggi, assegna la data odierna
                this.attivitaInModifica.setDataCompletamento(LocalDate.now());
            } else if (!isCompletataOra) {
                // Se togliamo la spunta per errore, resetta la data
                this.attivitaInModifica.setDataCompletamento(null);
            }

            boolean aggiornato = this.attivitaDAO.aggiornaAttivita(this.attivitaInModifica, idCategoriaReale, idPrioritaReale);

            if (aggiornato) {
                ViewDispatcher.getInstance().homeView();
            } else {
                this.erroreLabel.setText("Errore durante l'aggiornamento.");
            }
        } catch (Exception e) {
            this.erroreLabel.setText("Eccezione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminaAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Sei sicuro di voler eliminare questa attività?");
        alert.setContentText("L'azione è irreversibile e i dati andranno persi.");

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
                this.erroreLabel.setText("Errore durante l'eliminazione.");
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
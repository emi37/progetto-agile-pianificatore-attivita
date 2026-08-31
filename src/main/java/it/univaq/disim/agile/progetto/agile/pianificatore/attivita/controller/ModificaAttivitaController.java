package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.AttivitaDAO;
import database.CategoriaDAO;
import database.NotificaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Categoria;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Notifica;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
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
import javafx.scene.control.TextInputDialog;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModificaAttivitaController implements Initializable {

    @FXML private TextField titoloField;
    @FXML private DatePicker scadenzaPicker;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ComboBox<String> prioritaComboBox;
    @FXML private CheckBox completataCheckBox;
    
    // Nodi per l'Epica 5 e 6
    @FXML private CheckBox promemoriaManualeCheck;
    @FXML private ComboBox<String> anticipoComboBox;
    @FXML private Label erroreLabel;

    private AttivitaDAO attivitaDAO;
    private NotificaDAO notificaDAO;
    private CategoriaDAO categoriaDAO;
    private Attivita attivitaInModifica;
    private List<Categoria> listaCategorieDB;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.attivitaDAO = new AttivitaDAO();
            this.notificaDAO = new NotificaDAO();
            this.categoriaDAO = new CategoriaDAO();
            
            Utente utenteLoggato = ViewDispatcher.getInstance().getUtenteLoggato();
            if (utenteLoggato != null) {
                aggiornaTendinaCategorie(utenteLoggato.getId());
            }
            
            this.prioritaComboBox.getItems().addAll("Bassa", "Media", "Alta");
            this.anticipoComboBox.getItems().addAll("1 ora prima", "1 giorno prima", "2 giorni prima");
            
            this.erroreLabel.setText("");

            this.attivitaInModifica = ViewDispatcher.getInstance().getAttivitaSelezionata();

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
            e.printStackTrace();
        }
    }

    private void aggiornaTendinaCategorie(int idUtente) {
        this.categoriaComboBox.getItems().clear();
        this.listaCategorieDB = this.categoriaDAO.getCategorieUtente(idUtente);
        for (Categoria c : this.listaCategorieDB) {
            this.categoriaComboBox.getItems().add(c.getNomeCategoria());
        }
    }

    @FXML
    private void aggiungiCategoriaAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Gestione Categorie");
        dialog.setHeaderText("Aggiungi una nuova categoria");
        dialog.setContentText("Nome della categoria:");

        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(nome -> {
            if (nome.trim().isEmpty()) {
                this.erroreLabel.setText("Il nome della categoria è vuoto");
                return;
            }
            try {
                Utente u = ViewDispatcher.getInstance().getUtenteLoggato();
                Categoria salvata = this.categoriaDAO.inserisciCategoriaCustom(nome.trim(), u.getId());
                
                if (salvata != null) {
                    aggiornaTendinaCategorie(u.getId());
                    this.categoriaComboBox.setValue(salvata.getNomeCategoria());
                } else {
                    this.erroreLabel.setText("Errore SQL durante l'inserimento");
                }
            } catch (Exception e) {
                System.err.println("Errore logico in aggiungiCategoriaAction");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void abilitaPromemoriaAction(ActionEvent event) {
        this.anticipoComboBox.setDisable(!this.promemoriaManualeCheck.isSelected());
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

            if (scadenza == null) {
                this.erroreLabel.setText("Data scadenza richiesta per i promemoria.");
                return;
            }

            String categoriaSelezionata = this.categoriaComboBox.getValue();
            String prioritaSelezionata = this.prioritaComboBox.getValue();

            // Sostituzione dello switch con il fetch dell'ID reale dal DB
            int idCategoriaReale = -1;
            if (this.listaCategorieDB != null) {
                for (Categoria c : this.listaCategorieDB) {
                    if (c.getNomeCategoria().equals(categoriaSelezionata)) {
                        idCategoriaReale = c.getId();
                        break;
                    }
                }
            }

            if (idCategoriaReale == -1) {
                this.erroreLabel.setText("Errore: categoria non mappata.");
                return;
            }

            int idPrioritaReale = 3;
            switch (prioritaSelezionata) {
                case "Bassa": idPrioritaReale = 1; break;
                case "Media": idPrioritaReale = 2; break;
                case "Alta":  idPrioritaReale = 3; break;
            }

            this.attivitaInModifica.setTitolo(titolo);
            this.attivitaInModifica.setDataScadenza(scadenza);
            
            boolean isCompletataOra = this.completataCheckBox.isSelected();
            this.attivitaInModifica.setCompletata(isCompletataOra);
            
            if (isCompletataOra && this.attivitaInModifica.getDataCompletamento() == null) {
                this.attivitaInModifica.setDataCompletamento(LocalDate.now());
            } else if (!isCompletataOra) {
                this.attivitaInModifica.setDataCompletamento(null);
            }

            boolean aggiornato = this.attivitaDAO.aggiornaAttivita(this.attivitaInModifica, idCategoriaReale, idPrioritaReale);

            if (aggiornato) {
                
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
package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.AttivitaDAO;
import database.CategoriaDAO;
import database.NotificaDAO; 
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Categoria;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Notifica;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Priorita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class CreazioneAttivitaController implements Initializable {

    @FXML private TextField titoloField;
    @FXML private DatePicker scadenzaPicker;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ComboBox<String> prioritaComboBox;
    
    // Variabili FXML in base alla modifica grafica (Epica 5)
    @FXML private CheckBox promemoriaManualeCheck;
    @FXML private ComboBox<String> anticipoComboBox;
    @FXML private Label erroreLabel;

    private AttivitaDAO attivitaDAO;
    private NotificaDAO notificaDAO; 
    private CategoriaDAO categoriaDAO; // DAO per gestire le categorie custom (Epica 6)
    
    private List<Categoria> listaCategorieDB; // cache locale per mappare i nomi agli ID reali

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.attivitaDAO = new AttivitaDAO();
        this.notificaDAO = new NotificaDAO();
        this.categoriaDAO = new CategoriaDAO();
        
        Utente utenteLoggato = ViewDispatcher.getInstance().getUtenteLoggato();
        if (utenteLoggato != null) {
            // carica le categorie dal DB invece di usare stringhe hardcoded
            aggiornaTendinaCategorie(utenteLoggato.getId());
        }
        
        this.prioritaComboBox.getItems().addAll("Bassa", "Media", "Alta");
        this.anticipoComboBox.getItems().addAll("1 ora prima", "1 giorno prima", "2 giorni prima");
        
        this.erroreLabel.setText("");
    }

    // Metodo helper per ricaricare la UI dal DB
    private void aggiornaTendinaCategorie(int idUtente) {
        this.categoriaComboBox.getItems().clear();
        this.listaCategorieDB = this.categoriaDAO.getCategorieUtente(idUtente);
        for (Categoria c : this.listaCategorieDB) {
            this.categoriaComboBox.getItems().add(c.getNomeCategoria());
        }
    }

    /**
     * Handler per il bottone "+ agg. categoria" (Epica 6).
     * Istanzia un dialog di sistema, intercetta l'input e fa il push sul DB.
     */
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
            
            Utente u = ViewDispatcher.getInstance().getUtenteLoggato();
            Categoria salvata = this.categoriaDAO.inserisciCategoriaCustom(nome.trim(), u.getId());
            
            if (salvata != null) {
                // refresh della ui e auto-selezione
                aggiornaTendinaCategorie(u.getId());
                this.categoriaComboBox.setValue(salvata.getNomeCategoria());
            } else {
                this.erroreLabel.setText("Errore SQL durante l'inserimento della categoria");
            }
        });
    }

    @FXML
    private void abilitaPromemoriaAction(ActionEvent event) {
        this.anticipoComboBox.setDisable(!this.promemoriaManualeCheck.isSelected());
    }

    @FXML
    private void salvaAttivitaAction(ActionEvent event) {
        try {
            String titolo = this.titoloField.getText();
            LocalDate scadenza = this.scadenzaPicker.getValue();
            
            if (titolo == null || titolo.trim().isEmpty()) {
                this.erroreLabel.setText("titolo obbligatorio.");
                return;
            }
            
            if (this.categoriaComboBox.getValue() == null || this.prioritaComboBox.getValue() == null) {
                this.erroreLabel.setText("Seleziona una categoria e priorità.");
                return;
            }

            if(scadenza == null) {
                this.erroreLabel.setText("Seleziona una data per creare la notifica.");
                return;
            }

            String categoriaSelezionata = this.categoriaComboBox.getValue();
            String prioritaSelezionata = this.prioritaComboBox.getValue();

            // Mapping dinamico: scansiona la cache RAM per trovare l'ID reale della categoria
            int idCategoriaReale = -1;
            for (Categoria c : this.listaCategorieDB) {
                if (c.getNomeCategoria().equals(categoriaSelezionata)) {
                    idCategoriaReale = c.getId();
                    break;
                }
            }
            
            if (idCategoriaReale == -1) {
                this.erroreLabel.setText("Errore: categoria non valida o non mappata.");
                return;
            }

            // Mapping statico della priorità
            int idPrioritaReale = 3;
            switch (prioritaSelezionata) {
                case "Bassa": idPrioritaReale = 1; break;
                case "Media": idPrioritaReale = 2; break;
                case "Alta":  idPrioritaReale = 3; break;
            }

            Utente utenteLoggato = ViewDispatcher.getInstance().getUtenteLoggato();
            if (utenteLoggato == null) {
                this.erroreLabel.setText("Errore: utente non ha una sessione");
                return;
            }
            
            int idRealeUtente = utenteLoggato.getId();

            Categoria categoria = new Categoria(idCategoriaReale, categoriaSelezionata, idRealeUtente);
            Priorita priorita = new Priorita(idPrioritaReale, prioritaSelezionata);
            
            Attivita nuovaAttivita = new Attivita(0, titolo, "", scadenza, null, false, utenteLoggato, categoria, priorita);

            int idGeneratoDaMySQL = this.attivitaDAO.inserisciAttivitaRestituendoId(nuovaAttivita, idRealeUtente, idCategoriaReale, idPrioritaReale);

            if (idGeneratoDaMySQL > 0) {
                
                nuovaAttivita.setId(idGeneratoDaMySQL);
                
                LocalDateTime orarioScadenzaBase = scadenza.atTime(9, 0);
                
                if(idPrioritaReale == 3) {
                    Notifica n15 = new Notifica("Mancano 15 giorni all'attività: " + titolo, "DA_LEGGERE", orarioScadenzaBase.minusDays(15), nuovaAttivita);
                    Notifica n5 = new Notifica("Mancano 5 giorni per l'attività: " + titolo, "DA_LEGGERE", orarioScadenzaBase.minusDays(5), nuovaAttivita);
                    this.notificaDAO.inserisciNotifica(n15);
                    this.notificaDAO.inserisciNotifica(n5);
                }
                if(idPrioritaReale >= 2) {
                    Notifica n1 = new Notifica("Domani scade l'attività: " + titolo, "DA_LEGGERE", orarioScadenzaBase.minusDays(1), nuovaAttivita);
                    this.notificaDAO.inserisciNotifica(n1);
                }
                
                if(this.promemoriaManualeCheck.isSelected() && this.anticipoComboBox.getValue() != null) {
                    String comboScelta = this.anticipoComboBox.getValue();
                    LocalDateTime invioManuale = orarioScadenzaBase;
                    
                    if (comboScelta.equals("1 ora prima")) invioManuale = orarioScadenzaBase.minusHours(1);
                    if (comboScelta.equals("1 giorno prima")) invioManuale = orarioScadenzaBase.minusDays(1);
                    if (comboScelta.equals("2 giorni prima")) invioManuale = orarioScadenzaBase.minusDays(2);
                    
                    Notifica nPers = new Notifica("Promemoria : " + titolo, "DA_LEGGERE", invioManuale, nuovaAttivita);
                    this.notificaDAO.inserisciNotifica(nPers);
                }
                
                ViewDispatcher.getInstance().homeView();
                
            } else {
                this.erroreLabel.setText("Errore nel DB");
            }
            
        } catch (Exception e) {
            System.err.println("errore del salvataggio");
            this.erroreLabel.setText("Eccezione : " + e.toString());
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
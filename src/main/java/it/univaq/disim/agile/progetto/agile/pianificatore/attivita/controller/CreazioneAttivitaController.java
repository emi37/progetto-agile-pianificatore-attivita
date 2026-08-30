package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.AttivitaDAO;
import database.NotificaDAO; // Mi serve per l'Epica 5
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
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private NotificaDAO notificaDAO; // Dichiaro il DAO delle notifiche

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.attivitaDAO = new AttivitaDAO();
        this.notificaDAO = new NotificaDAO();
        
        // Aggiungiamo tutte le opzioni coerenti con il database
        this.categoriaComboBox.getItems().addAll("Studio", "Lavoro", "Palestra", "Hobby", "Finanze");
        this.prioritaComboBox.getItems().addAll("Bassa", "Media", "Alta");
        
        // Carichiamo le opzioni per la tendina del promemoria
        this.anticipoComboBox.getItems().addAll("1 ora prima", "1 giorno prima", "2 giorni prima");
        
        this.erroreLabel.setText("");
    }

    /**
     * Sblocca la tendina dell'anticipo solo se l'utente clicca la checkbox
     */
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

            // Un'attività ha senso notificarla solo se ha una data...
            if(scadenza == null) {
                this.erroreLabel.setText("Seleziona una data per creare la notifica.");
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
                this.erroreLabel.setText("Errore: utente non ha una sessione");
                return;
            }
            
            int idRealeUtente = utenteLoggato.getId();

            // Creiamo gli oggetti di Dominio con i dati reali e dinamici dell'utente
            Categoria categoria = new Categoria(idCategoriaReale, categoriaSelezionata, idRealeUtente);
            Priorita priorita = new Priorita(idPrioritaReale, prioritaSelezionata);
            
            Attivita nuovaAttivita = new Attivita(0, titolo, "", scadenza, null, false, utenteLoggato, categoria, priorita);

            // Salvataggio tramite DAO (Modificato per farci restituire l'ID generato dal DB)
            int idGeneratoDaMySQL = this.attivitaDAO.inserisciAttivitaRestituendoId(nuovaAttivita, idRealeUtente, idCategoriaReale, idPrioritaReale);

            if (idGeneratoDaMySQL > 0) {
                
                // Setto l'ID reale all'oggetto appena creato
                nuovaAttivita.setId(idGeneratoDaMySQL);
                
                // --------- INIZIO LOGICA SCALARE NOTIFICHE (EPICA 5) ---------
                
                // Fissiamo una data di default, tipo le 9 di mattina del giorno di scadenza
                LocalDateTime orarioScadenzaBase = scadenza.atTime(9, 0);
                
                // Logica Automatica: 1gg, 5gg e 15gg in base alla priorità
                if(idPrioritaReale == 3) {
                    // E' urgente, alert a 15 e a 5 giorni
                    Notifica n15 = new Notifica("Mancano 15 giorni all'attività: " + titolo, "DA_LEGGERE", orarioScadenzaBase.minusDays(15), nuovaAttivita);
                    Notifica n5 = new Notifica("Mancano 5 giorni per l'attività: " + titolo, "DA_LEGGERE", orarioScadenzaBase.minusDays(5), nuovaAttivita);
                    this.notificaDAO.inserisciNotifica(n15);
                    this.notificaDAO.inserisciNotifica(n5);
                }
                if(idPrioritaReale >= 2) {
                    // Alta o Media, ti avviso il giorno prima
                    Notifica n1 = new Notifica("Domani scade l'attività: " + titolo, "DA_LEGGERE", orarioScadenzaBase.minusDays(1), nuovaAttivita);
                    this.notificaDAO.inserisciNotifica(n1);
                }
                
                // Logica Manuale Personalizzata
                if(this.promemoriaManualeCheck.isSelected() && this.anticipoComboBox.getValue() != null) {
                    String comboScelta = this.anticipoComboBox.getValue();
                    LocalDateTime invioManuale = orarioScadenzaBase;
                    
                    if (comboScelta.equals("1 ora prima")) invioManuale = orarioScadenzaBase.minusHours(1);
                    if (comboScelta.equals("1 giorno prima")) invioManuale = orarioScadenzaBase.minusDays(1);
                    if (comboScelta.equals("2 giorni prima")) invioManuale = orarioScadenzaBase.minusDays(2);
                    
                    Notifica nPers = new Notifica("Promemoria : " + titolo, "DA_LEGGERE", invioManuale, nuovaAttivita);
                    this.notificaDAO.inserisciNotifica(nPers);
                }
                // --------- FINE LOGICA SCALARE ---------
                
                
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
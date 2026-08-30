package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.AttivitaDAO;
import database.NotificaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Notifica;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Label benvenutoLabel;
    @FXML
    private Label statSettimanaLabel;
    @FXML
    private Label statMeseLabel;

    @FXML
    private TableView<Attivita> urgentiTable;
    @FXML
    private TableColumn<Attivita, String> titoloUrgentiCol;
    @FXML
    private TableColumn<Attivita, String> scadenzaUrgentiCol;
    @FXML
    private TableColumn<Attivita, String> categoriaUrgentiCol;
    @FXML
    private TableColumn<Attivita, String> prioritaUrgentiCol;

    @FXML
    private TableView<Attivita> completateTable;
    @FXML
    private TableColumn<Attivita, String> titoloCompletateCol;
    @FXML
    private TableColumn<Attivita, String> dataCompletamentoCol;
    @FXML
    private TableColumn<Attivita, String> categoriaCompletateCol;

    private AttivitaDAO attivitaDAO;
    private NotificaDAO notificaDAO; // Aggiunto per l'Epica 5

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.attivitaDAO = new AttivitaDAO();
        this.notificaDAO = new NotificaDAO(); // Istanziamo il motore delle notifiche
        
        Utente utenteLoggato = ViewDispatcher.getInstance().getUtenteLoggato();

        if (utenteLoggato != null) {
            this.benvenutoLabel.setText("Benvenuto nella tua home personale, " + utenteLoggato.getUsername() );

            // 1. Configurazione colonne Tabella Urgenti
            titoloUrgentiCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitolo()));
            scadenzaUrgentiCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getDataScadenza() != null ? cellData.getValue().getDataScadenza().toString() : ""));
            categoriaUrgentiCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().getNomeCategoria()));
            prioritaUrgentiCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriorita().getLivello()));

            // 2. Configurazione colonne Tabella Completate
            titoloCompletateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitolo()));
            dataCompletamentoCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getDataCompletamento() != null ? cellData.getValue().getDataCompletamento().toString() : ""));
            categoriaCompletateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().getNomeCategoria()));

            // 3. Popolamento Dati dal DAO
            caricaDati(utenteLoggato.getId());
            
            // --- INIZIO EPICA 5: Check notifiche all'avvio della Dashboard ---
            gestisciPopUpNotifiche(utenteLoggato.getId());
            // --- FINE EPICA 5 ---

            // 4. Implementazione del click per modifica (Epica 2 / Epica 3)
            urgentiTable.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Attivita selezionata = urgentiTable.getSelectionModel().getSelectedItem();
                    if (selezionata != null) {
                        ViewDispatcher.getInstance().setAttivitaSelezionata(selezionata);
                        // Rispettato il tuo codice originale: nessuna eccezione aggiunta qui!
                        ViewDispatcher.getInstance().modificaAttivitaView();
                    }
                }
            });
        }
    }

    private void caricaDati(int idUtente) {
        // Estrazione attività Urgenti (ordinate dal DB)
        List<Attivita> listaUrgenti = this.attivitaDAO.getAttivitaUrgenti(idUtente);
        ObservableList<Attivita> observableUrgenti = FXCollections.observableArrayList(listaUrgenti);
        this.urgentiTable.setItems(observableUrgenti);

        // Estrazione attività Completate
        List<Attivita> listaCompletate = this.attivitaDAO.getAttivitaCompletate(idUtente);
        ObservableList<Attivita> observableCompletate = FXCollections.observableArrayList(listaCompletate);
        this.completateTable.setItems(observableCompletate);

        // Calcolo Analisi (Statistiche)
        LocalDate inizioSettimana = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate inizioMese = LocalDate.now().withDayOfMonth(1);

        int countSettimana = this.attivitaDAO.contaAttivitaCompletateDal(idUtente, inizioSettimana);
        int countMese = this.attivitaDAO.contaAttivitaCompletateDal(idUtente, inizioMese);

        this.statSettimanaLabel.setText(String.valueOf(countSettimana));
        this.statMeseLabel.setText(String.valueOf(countMese));
    }
    
    /**
     * Metodo per mostrare a schermo i promemoria scaduti appena si apre la dashboard.
     */
    private void gestisciPopUpNotifiche(int idUtente) {
        // Peschiamo le notifiche scadute o da leggere subito
        List<Notifica> notificheScadute = this.notificaDAO.estraiNotificheDaMostrare(idUtente);
        
        for (Notifica n : notificheScadute) {
            // Mostriamo un pop-up bloccante per ogni notifica
            Alert avviso = new Alert(Alert.AlertType.WARNING);
            avviso.setTitle("Ehi, Promemoria Scadenza!");
            avviso.setHeaderText("Attività in scadenza: " + n.getAttivita().getTitolo());
            avviso.setContentText(n.getMessaggio() + "\n\n(Priorità " + n.getAttivita().getPriorita().getLivello() + ")");
            
            avviso.showAndWait();
            
            // L'utente ha chiuso il popup, quindi la segniamo come letta sul database
            boolean aggiornata = this.notificaDAO.aggiornaStatoLetta(n.getIdNotifica());
            if (!aggiornata) {
                // Se non riesco ad aggiornarla, stampo un errorino per il debug
                System.err.println("Maronn, errore nell'aggiornamento della notifica id: " + n.getIdNotifica());
            }
        }
    }

    @FXML
    private void vaiACreazioneAttivitaAction(ActionEvent event) {
        try {
            ViewDispatcher.getInstance().creazioneAttivitaView();
        } catch (ViewException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void vaiACalendarioAction(ActionEvent event) {
        ViewDispatcher.getInstance().calendarioView();
    }
}
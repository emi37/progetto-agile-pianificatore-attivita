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
    private NotificaDAO notificaDAO; //dao per prendere le notifiche dal db con le query

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.attivitaDAO = new AttivitaDAO();
        this.notificaDAO = new NotificaDAO(); // creo una nuova istanza delle notifiche
        
        Utente utenteLoggato = ViewDispatcher.getInstance().getUtenteLoggato();

        if (utenteLoggato != null) {
            this.benvenutoLabel.setText("Benvenuto nella tua home personale, " + utenteLoggato.getUsername() );

            
            titoloUrgentiCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitolo()));
            scadenzaUrgentiCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getDataScadenza() != null ? cellData.getValue().getDataScadenza().toString() : ""));
            categoriaUrgentiCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().getNomeCategoria()));
            prioritaUrgentiCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriorita().getLivello()));

            titoloCompletateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitolo()));
            dataCompletamentoCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getDataCompletamento() != null ? cellData.getValue().getDataCompletamento().toString() : ""));
            categoriaCompletateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().getNomeCategoria()));

            // prendo i dati che passa il dao 
            caricaDati(utenteLoggato.getId());
            
            gestisciPopUpNotifiche(utenteLoggato.getId());

            //  clicko l'attività per modificarla
            urgentiTable.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Attivita selezionata = urgentiTable.getSelectionModel().getSelectedItem();
                    if (selezionata != null) {
                        ViewDispatcher.getInstance().setAttivitaSelezionata(selezionata);
                        ViewDispatcher.getInstance().modificaAttivitaView();
                    }
                }
            });
        }
    }

    private void caricaDati(int idUtente) {
        // estrazione delle attività urgenti 
        List<Attivita> listaUrgenti = this.attivitaDAO.getAttivitaUrgenti(idUtente);
        ObservableList<Attivita> observableUrgenti = FXCollections.observableArrayList(listaUrgenti);
        this.urgentiTable.setItems(observableUrgenti);

        // estraggo le attività completate
        List<Attivita> listaCompletate = this.attivitaDAO.getAttivitaCompletate(idUtente);
        ObservableList<Attivita> observableCompletate = FXCollections.observableArrayList(listaCompletate);
        this.completateTable.setItems(observableCompletate);

        
      
        LocalDate inizioSettimana = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate inizioMese = LocalDate.now().withDayOfMonth(1);

        int countSettimana = this.attivitaDAO.contaAttivitaCompletateDal(idUtente, inizioSettimana);
        int countMese = this.attivitaDAO.contaAttivitaCompletateDal(idUtente, inizioMese);

        this.statSettimanaLabel.setText(String.valueOf(countSettimana));
        this.statMeseLabel.setText(String.valueOf(countMese));
    }
    
   
    
    private void gestisciPopUpNotifiche(int idUtente) {

        List<Notifica> notificheScadute = this.notificaDAO.estraiNotificheDaMostrare(idUtente);
        
        for (Notifica n : notificheScadute) {
            Alert avviso = new Alert(Alert.AlertType.WARNING);
            avviso.setTitle("Promemoria della scadenza: ");
            avviso.setHeaderText("Attività in scadenza: " + n.getAttivita().getTitolo());
            avviso.setContentText(n.getMessaggio() + "\n\n(Priorità " + n.getAttivita().getPriorita().getLivello() + ")");
            
            avviso.showAndWait();
            
            boolean aggiornata = this.notificaDAO.aggiornaStatoLetta(n.getIdNotifica());
            if (!aggiornata) {
                System.err.println("errore nell'aggiornamento della notifica id,(non è stata modificata) " + n.getIdNotifica());
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
package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.AttivitaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.attivitaDAO = new AttivitaDAO();
        Utente utenteLoggato = ViewDispatcher.getInstance().getUtenteLoggato();

        if (utenteLoggato != null) {
            this.benvenutoLabel.setText("Benvenuto nella tua Dashboard, " + utenteLoggato.getUsername() + "!");

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

            // 4. Implementazione del click per modifica (Epica 2 / Epica 3)
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
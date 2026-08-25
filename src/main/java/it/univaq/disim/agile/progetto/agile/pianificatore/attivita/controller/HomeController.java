package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.AttivitaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller per la Dashboard di Riepilogo (Home). Allineato con gli id
 * definiti nel file FXML e responsabile del popolamento delle tabelle.
 */
public class HomeController implements Initializable {

    @FXML
    private Label benvenutoLabel;

    // --- Tabella Attività Urgenti ---
    @FXML
    private TableView<Attivita> tabellaAttivitaUrgenti;

    @FXML
    private TableColumn<Attivita, String> colTitle;

    @FXML
    private TableColumn<Attivita, LocalDate> colDeadline;

    @FXML
    private TableColumn<Attivita, String> colCategory;

    @FXML
    private TableColumn<Attivita, String> colPriority;

    // --- Tabella Attività Completate ---
    @FXML
    private TableView<Attivita> tabellaAttivitaCompletate;

    @FXML
    private TableColumn<Attivita, String> colTitleDone;

    @FXML
    private TableColumn<Attivita, LocalDate> colCompletionDate;

    @FXML
    private TableColumn<Attivita, String> colCategoryDone;

    private AttivitaDAO attivitaDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.attivitaDAO = new AttivitaDAO();

        benvenutoLabel.setText("Benvenuto nella tua Home Personale!");

        // Configurazione delle tabelle
        configuraTabelle();

        // Caricamento dei dati dal DB
        caricaDatiDashboard();
    }

    private void configuraTabelle() {
        // Mappatura Tabella Urgenti
        colTitle.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("dataScadenza"));

        // Estrazione corretta della stringa per la categoria (evita il problema dell'oggetto complesso)
        colCategory.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCategoria() != null) {
                return new SimpleStringProperty(cellData.getValue().getCategoria().getNomeCategoria());
            }
            return new SimpleStringProperty("");
        });

        // Estrazione corretta della stringa per la priorità basata sulla classe Priorita
        colPriority.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPriorita() != null) {
                return new SimpleStringProperty(cellData.getValue().getPriorita().getLivello());
            }
            return new SimpleStringProperty("");
        });

        // Mappatura Tabella Completate
        colTitleDone.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        colCompletionDate.setCellValueFactory(new PropertyValueFactory<>("dataCompletamento"));

        colCategoryDone.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCategoria() != null) {
                return new SimpleStringProperty(cellData.getValue().getCategoria().getNomeCategoria());
            }
            return new SimpleStringProperty("");
        });
    }

    private void caricaDatiDashboard() {
        // Recuperiamo dinamicamente l'utente loggato dal ViewDispatcher
        Utente utenteCorrente = ViewDispatcher.getInstance().getUtenteLoggato();

        int idUtenteLoggato = (utenteCorrente != null) ? utenteCorrente.getId() : 1;

        try {
            List<Attivita> listaUrgenti = attivitaDAO.getAttivitaUrgenti(idUtenteLoggato);
            System.out.println(">>> DEBUG URGENTI: Trovate " + listaUrgenti.size() + " attività per l'utente ID: " + idUtenteLoggato);

            ObservableList<Attivita> obsUrgenti = FXCollections.observableArrayList(listaUrgenti);
            tabellaAttivitaUrgenti.setItems(obsUrgenti);

            List<Attivita> listaCompletate = attivitaDAO.getAttivitaCompletateRecenti(idUtenteLoggato);
            System.out.println(">>> DEBUG COMPLETATE: Trovate " + listaCompletate.size() + " attività.");

            ObservableList<Attivita> obsCompletate = FXCollections.observableArrayList(listaCompletate);
            tabellaAttivitaCompletate.setItems(obsCompletate);

        } catch (Exception e) {
            System.err.println(">>> ERRORE CRITICO NEL CARICAMENTO DASHBOARD:");
            e.printStackTrace();
        }
    }
}

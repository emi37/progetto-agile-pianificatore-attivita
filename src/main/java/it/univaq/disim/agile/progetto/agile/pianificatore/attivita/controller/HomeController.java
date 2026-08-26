package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.AttivitaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private Label benvenutoLabel;

    // --- Elementi FXML per la Tabella Attività Urgenti ---
    @FXML private TableView<Attivita> tabellaAttivitaUrgenti;
    @FXML private TableColumn<Attivita, String> colTitle;
    @FXML private TableColumn<Attivita, String> colDeadline;
    @FXML private TableColumn<Attivita, String> colCategory;
    @FXML private TableColumn<Attivita, String> colPriority;

    // --- Elementi FXML per la Tabella Attività Completate ---
    @FXML private TableView<Attivita> tabellaAttivitaCompletate;
    @FXML private TableColumn<Attivita, String> colTitleDone;
    @FXML private TableColumn<Attivita, String> colCompletionDate;
    @FXML private TableColumn<Attivita, String> colCategoryDone;

    private AttivitaDAO attivitaDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // L'aggiunta del Try-Catch qui è vitale: se i dati falliscono, la finestra si apre comunque
        // e stampa il VERO errore nella console di NetBeans permettendoci di risolverlo.
        try {
            this.attivitaDAO = new AttivitaDAO();
            Utente utente = ViewDispatcher.getInstance().getUtenteLoggato();
            
            if (utente != null) {
                // ATTENZIONE: Se getUsername() o getId() sono rossi, significa che Hasan 
                // li ha chiamati diversamente (es. getNome() o getIdUtente()).
                this.benvenutoLabel.setText("Benvenuto nella tua home personale, " + utente.getUsername() + "!");
                
                configuraColonne();
                caricaDatiNelleTabelle(utente.getId());
            } else {
                this.benvenutoLabel.setText("Benvenuto! (Nessun utente rilevato in sessione)");
            }
        } catch (Exception e) {
            System.out.println("====== ATTENZIONE: ERRORE NELLA DASHBOARD ======");
            e.printStackTrace();
        }
    }

    private void configuraColonne() {
        this.colTitle.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        
        this.colDeadline.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDataScadenza() != null) {
                return new SimpleStringProperty(cellData.getValue().getDataScadenza().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new SimpleStringProperty("");
        });
        
        // ATTENZIONE: Se getNomeCategoria() o getLivello() sono rossi, modifica il nome del metodo
        this.colCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().getNomeCategoria()));
        this.colPriority.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriorita().getLivello()));

        this.colTitleDone.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        
        this.colCompletionDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDataCompletamento() != null) {
                return new SimpleStringProperty(cellData.getValue().getDataCompletamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new SimpleStringProperty("");
        });
        
        this.colCategoryDone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().getNomeCategoria()));
    }

    private void caricaDatiNelleTabelle(int idUtente) {
        List<Attivita> urgenti = this.attivitaDAO.getAttivitaUrgenti(idUtente);
        List<Attivita> completate = this.attivitaDAO.getAttivitaCompletateRecenti(idUtente);

        ObservableList<Attivita> datiUrgenti = FXCollections.observableArrayList(urgenti);
        this.tabellaAttivitaUrgenti.setItems(datiUrgenti);

        ObservableList<Attivita> datiCompletate = FXCollections.observableArrayList(completate);
        this.tabellaAttivitaCompletate.setItems(datiCompletate);
    }

    @FXML
    private void vaiACreazioneAttivitaAction(ActionEvent event) {
        ViewDispatcher.getInstance().creazioneAttivitaView();
    }
}
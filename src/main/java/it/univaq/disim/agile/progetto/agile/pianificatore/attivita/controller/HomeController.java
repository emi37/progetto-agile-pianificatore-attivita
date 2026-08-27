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

    // ---FXML per la tabella delle attività urgenti---
    @FXML private TableView<Attivita> tabellaAttivitaUrgenti;
    @FXML private TableColumn<Attivita, String> colTitle;
    @FXML private TableColumn<Attivita, String> colDeadline;
    @FXML private TableColumn<Attivita, String> colCategory;
    @FXML private TableColumn<Attivita, String> colPriority;

      // --- altri elementi FXML ma per la tabella adttività completate---
    @FXML private TableView<Attivita> tabellaAttivitaCompletate;
    @FXML private TableColumn<Attivita, String> colTitleDone;
    @FXML private TableColumn<Attivita, String> colCompletionDate;
    @FXML private TableColumn<Attivita, String> colCategoryDone;

    private AttivitaDAO attivitaDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        try {
            this.attivitaDAO = new AttivitaDAO();
            Utente utente = ViewDispatcher.getInstance().getUtenteLoggato();
            
            if (utente != null) {
                 
                this.benvenutoLabel.setText("Benvenuto nella tua home personale, " + utente.getUsername() + "!");
                
                configuraColonne();
                caricaDatiNelleTabelle(utente.getId());
            } else {
                this.benvenutoLabel.setText("Benvenuto!(Nessun utente rilevato in sessione)");
            }
        } catch (Exception e) {
            System.out.println("ERRORE NELLa DASHBOARD");
            e.printStackTrace();
        }
        tabellaAttivitaUrgenti.setOnMouseClicked(event -> {
            
            // controllo sul doppio click
            if (event.getClickCount() == 2) { 
                
                // estraggo l'oggetto attivita della riga che h cliccato
                Attivita attivitaCliccata = tabellaAttivitaUrgenti.getSelectionModel().getSelectedItem();
                              if (attivitaCliccata != null) {
                                ViewDispatcher.getInstance().setAttivitaSelezionata(attivitaCliccata);
                                    ViewDispatcher.getInstance().modificaAttivitaView();
                }
            }
        });
    }

    private void configuraColonne() {
        this.colTitle.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        
        this.colDeadline.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDataScadenza() != null) {
                return new SimpleStringProperty(cellData.getValue().getDataScadenza().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new SimpleStringProperty("");
        });
        
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
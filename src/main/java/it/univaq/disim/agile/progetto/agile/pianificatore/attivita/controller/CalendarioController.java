package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import database.AttivitaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// Controller del calendario per gestire la vista mensile e settimanale delle attività.
public class CalendarioController implements Initializable {

    @FXML
    private ComboBox<String> vistaComboBox;

    @FXML
    private CheckBox filtroPrioritaAltaCheckBox;

    @FXML
    private VBox calendarioContainer;

    private AttivitaDAO attivitaDAO;
    private Utente utenteCorrente;
    private LocalDate dataCorrente;
    private Label meseAnnoLabel;
    private List<Attivita> tutteLeAttivita;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Inizializzo il DAO per le interrogazioni al database
        attivitaDAO = new AttivitaDAO();

        // Recupero l'utente che ha fatto il login tramite il nostro Singleton
        utenteCorrente = ViewDispatcher.getInstance().getUtenteLoggato();

        // Parto dalla data odierna come riferimento iniziale
        dataCorrente = LocalDate.now();

        vistaComboBox.getItems().addAll("Mese Corrente", "Settimana Corrente");
        vistaComboBox.setValue("Mese Corrente");
        vistaComboBox.setOnAction(event -> generaGrigliaCalendario());

        // Carico i dati dal DB e genero il calendario
        caricaDatiDatabase();
        generaGrigliaCalendario();
    }

    // Faccio una chiamata al DB per prendere tutte le attività programmate per l'utente
    private void caricaDatiDatabase() {
        if (utenteCorrente != null) {
            tutteLeAttivita = attivitaDAO.getAttivitaUrgenti(utenteCorrente.getId());
        }
    }

    // Costruisce il calendario e applica i filtri
    private void generaGrigliaCalendario() {
        calendarioContainer.getChildren().clear();

        // Controlla se l'utente ha messo la spunta sul filtro delle priorità alte
        boolean soloAlta = filtroPrioritaAltaCheckBox.isSelected();

        // Filtriamo con gli stream di Java tenendo solo quelle che hanno priorità alta (gestendo un po' di flessibilità nei testi)
        List<Attivita> attivitaFiltrate = tutteLeAttivita;
        if (tutteLeAttivita != null && soloAlta) {
            attivitaFiltrate = tutteLeAttivita.stream()
                    .filter(a -> {
                        String pStr = estraiPrioritaStringa(a);
                        return "ALTA".equalsIgnoreCase(pStr) || "Alta".equalsIgnoreCase(pStr);
                    })
                    .collect(Collectors.toList());
        }

        String vistaSelezionata = vistaComboBox.getValue();

        // Intestazione con pulsanti di navigazione dinamici
        HBox navBox = new HBox(15);
        navBox.setAlignment(Pos.CENTER);

        // Pulsante per andare nel mese o settimana precedenti
        Button btnIndietro = new Button(vistaSelezionata.equals("Settimana Corrente") ? "< Sett. Prec." : "< Mese Prec.");
        btnIndietro.getStyleClass().add("nav-button");
        btnIndietro.setOnAction(e -> {
            if ("Settimana Corrente".equals(vistaComboBox.getValue())) {
                dataCorrente = dataCorrente.minusWeeks(1);
            } else {
                dataCorrente = dataCorrente.minusMonths(1);
            }
            generaGrigliaCalendario();
        });

        // Etichetta centrale che mostra il mese/anno o l'intervallo della settimana in corso
        meseAnnoLabel = new Label();
        meseAnnoLabel.getStyleClass().add("mese-anno-label");

        if ("Settimana Corrente".equals(vistaSelezionata)) {
            LocalDate inizioSet = dataCorrente.minusDays(dataCorrente.getDayOfWeek().getValue() - 1);
            LocalDate fineSet = inizioSet.plusDays(6);
            meseAnnoLabel.setText("SETTIMANA: " + inizioSet.getDayOfMonth() + " " + inizioSet.getMonth().getDisplayName(TextStyle.SHORT, Locale.ITALIAN) + " - " + fineSet.getDayOfMonth() + " " + fineSet.getMonth().getDisplayName(TextStyle.SHORT, Locale.ITALIAN) + " " + fineSet.getYear());
        } else {
            String nomeMese = dataCorrente.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
            meseAnnoLabel.setText(nomeMese.toUpperCase() + " " + dataCorrente.getYear());
        }

        // Pulsante per andare nel mese o settimana successivi
        Button btnAvanti = new Button(vistaSelezionata.equals("Settimana Corrente") ? "Sett. Succ. >" : "Mese Succ. >");
        btnAvanti.getStyleClass().add("nav-button");
        btnAvanti.setOnAction(e -> {
            if ("Settimana Corrente".equals(vistaComboBox.getValue())) {
                dataCorrente = dataCorrente.plusWeeks(1);
            } else {
                dataCorrente = dataCorrente.plusMonths(1);
            }
            generaGrigliaCalendario();
        });

        navBox.getChildren().addAll(btnIndietro, meseAnnoLabel, btnAvanti);
        calendarioContainer.getChildren().add(navBox);

        // Griglia vera e propria per disporre le caselle dei giorni
        GridPane gridPane = new GridPane();
        gridPane.setHgap(8);
        gridPane.setVgap(8);
        gridPane.setAlignment(Pos.CENTER);

        // Se la vista è in modalità settimanale, mostro i 7 giorni della settimana
        if ("Settimana Corrente".equals(vistaSelezionata)) {
            LocalDate inizioSettimana = dataCorrente.minusDays(dataCorrente.getDayOfWeek().getValue() - 1);
            String[] giorniSettimana = {"Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"};

            for (int i = 0; i < 7; i++) {
                LocalDate giornoCorrente = inizioSettimana.plusDays(i);
                VBox giornoBox = creaBoxGiorno(giornoCorrente, giorniSettimana[i] + "\n" + giornoCorrente.getDayOfMonth(), attivitaFiltrate);
                gridPane.add(giornoBox, i, 0);
            }
        } // Altrimenti mostro la classica vista mensile
        else {
            String[] giorniAbbrev = {"lu", "ma", "me", "gi", "ve", "sa", "do"};
            for (int i = 0; i < giorniAbbrev.length; i++) {
                Label lbl = new Label(giorniAbbrev[i]);
                lbl.getStyleClass().add("giorno-intestazione-label");
                lbl.setAlignment(Pos.CENTER);
                gridPane.add(lbl, i, 0);
            }

            YearMonth annoMese = YearMonth.from(dataCorrente);
            LocalDate primoDelMese = dataCorrente.withDayOfMonth(1);
            int giornoInizio = primoDelMese.getDayOfWeek().getValue();
            int giorniTotali = annoMese.lengthOfMonth();

            int riga = 1;
            int colonna = giornoInizio - 1;
            
            // Ciclo tutti i giorni del mese posizionandoli nelle celle corrette del GridPane
            for (int giorno = 1; giorno <= giorniTotali; giorno++) {
                LocalDate dataCasella = LocalDate.of(dataCorrente.getYear(), dataCorrente.getMonth(), giorno);
                VBox giornoBox = creaBoxGiorno(dataCasella, String.valueOf(giorno), attivitaFiltrate);
                gridPane.add(giornoBox, colonna, riga);

                colonna++;
                if (colonna > 6) {
                    colonna = 0;
                    riga++;
                }
            }
        }

        calendarioContainer.getChildren().add(gridPane);
    }
    
    // Costruisce graficamente la singola casella (il box) per un giorno specifico del calendario
    // Se ci sono attività in quella data, colora il box
    private VBox creaBoxGiorno(LocalDate data, String testoVisualizzato, List<Attivita> listaAttivita) {
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(80, 65);

        boolean haAttivita = false;
        if (listaAttivita != null) {
            haAttivita = listaAttivita.stream()
                    .anyMatch(a -> a.getDataScadenza() != null && a.getDataScadenza().equals(data));
        }
        
        // Applica lo stile CSS a seconda che il giorno abbia o meno attivita programmate
        if (haAttivita) {
            box.getStyleClass().add("giorno-box-attivo");
        } else {
            box.getStyleClass().add("giorno-box-vuoto");
        }

        Label lbl = new Label(testoVisualizzato);
        lbl.setAlignment(Pos.CENTER);
        if (haAttivita) {
            lbl.getStyleClass().add("giorno-label-attivo");
        } else {
            lbl.getStyleClass().add("giorno-label-vuoto");
        }
        box.getChildren().add(lbl);
        
        // Se il giorno è occupato, mette un pallino come promemoria visivo rapido
        if (haAttivita) {
            Label badge = new Label("●");
            badge.getStyleClass().add("giorno-badge");
            box.getChildren().add(badge);
        }
        
        // Cliccando sul giorno si apre un popup con i dettagli delle attività di quella giornata
        box.setOnMouseClicked(event -> mostraDettaglioGiorno(data, listaAttivita));

        return box;
    }
    
    // Metodo per estrarre la stringa della priorità da un'attività
    private String estraiPrioritaStringa(Attivita a) {
        try {
            Object p = a.getPriorita();
            if (p != null) {
                try {
                    java.lang.reflect.Method m = p.getClass().getMethod("getLivello");
                    Object val = m.invoke(p);
                    if (val != null) {
                        return val.toString();
                    }
                } catch (Exception ex) {
                    return p.toString();
                }
            }
        } catch (Exception e) {
            // fallback
        }
        return "Normale";
    }

    // Genera un alert con l'elenco dettagliato delle cose da fare in una certa data
    private void mostraDettaglioGiorno(LocalDate data, List<Attivita> listaAttivita) {
        List<Attivita> attivitaDelGiorno = null;
        if (listaAttivita != null) {
            attivitaDelGiorno = listaAttivita.stream()
                    .filter(a -> a.getDataScadenza() != null && a.getDataScadenza().equals(data))
                    .collect(Collectors.toList());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Impegni del giorno");
        alert.setHeaderText("Data: " + data);

        if (attivitaDelGiorno != null && !attivitaDelGiorno.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Attivita a : attivitaDelGiorno) {
                String titolo = a.getTitolo() != null ? a.getTitolo() : "Senza titolo";
                String prioritaStr = estraiPrioritaStringa(a);
                sb.append("• ").append(titolo).append(" (Priorità: ").append(prioritaStr).append(")\n");
            }
            alert.setContentText(sb.toString());
        } else {
            alert.setContentText("Nessuna attività programmata per questa data.");
        }
        alert.showAndWait();
    }

    @FXML
    private void applicaFiltriAction(ActionEvent event) {
        generaGrigliaCalendario();
    }

    // Ripristina i filtri ai valori iniziali di default e ricarica il calendario
    @FXML
    private void resetFiltriAction(ActionEvent event) {
        filtroPrioritaAltaCheckBox.setSelected(false);
        vistaComboBox.setValue("Mese Corrente");
        dataCorrente = LocalDate.now();
        generaGrigliaCalendario();
    }

    /**
     * Gestisce l'evento di click sul bottone "Torna alla Dashboard" 
     * Invoca il Singleton ViewDispatcher per eseguire lo switch della Scena verso la Home
     */
    @FXML
    private void tornaDashboardAction(javafx.event.ActionEvent event) {
        try {
            // Chiama il dispatcher per tornare alla homeView
            it.univaq.disim.agile.progetto.agile.pianificatore.attivita.view.ViewDispatcher.getInstance().homeView();
        } catch (Exception e) {
            System.err.println("Errore durante il ritorno alla Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
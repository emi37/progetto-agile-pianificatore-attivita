package it.univaq.disim.agile.progetto.agile.pianificatore.attivita;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.dao.UtenteDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.dao.AttivitaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model.Utente;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model.Attivita;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ProgettoAgilePianificatoreAttivita extends Application {

    private TextField uField;
    private PasswordField pField;
    private Button inviaBtn;
    private Button vaiRegBtn; // bottone per la registrazione di un nuovo profilo
    private Utente utenteLoggato;
    private TableView<Attivita> tabellaAttivita;

    @Override
    public void start(Stage finestratop) {
        finestratop.setTitle("Login o registrati");
                // tutta la parte di design main, bottonei, sfondo ecc.
        GridPane griglia = new GridPane();
        griglia.setAlignment(Pos.CENTER);
        griglia.setHgap(15); griglia.setVgap(15);
        griglia.setPadding(new Insets(40));
        
        // stile scuro per il background della griglia principale
        griglia.setStyle("-fx-background-color: #1e1e24;");

        Label titolo = new Label("BENVENUTO");
        titolo.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
        titolo.setStyle("-fx-text-fill: #ffffff; -fx-letter-spacing: 2px;");
        griglia.add(titolo, 0, 0, 2, 1);
        GridPane.setMargin(titolo, new Insets(0, 0, 15, 0));

        Label uLabel = new Label("Username");
        uLabel.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, 14));
        uLabel.setStyle("-fx-text-fill: #a0a0aa;");
        griglia.add(uLabel, 0, 1);

        uField = new TextField();
        uField.setPromptText("Inserisci lo username");
        uField.setStyle("-fx-background-color: #2a2a35; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-padding: 10; -fx-font-size: 14;");
        griglia.add(uField, 1, 1);

        Label pLabel = new Label("Password");
        pLabel.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, 14));
        pLabel.setStyle("-fx-text-fill: #a0a0aa;");
        griglia.add(pLabel, 0, 2);

        pField = new PasswordField();
        pField.setPromptText("••••••••");
        pField.setStyle("-fx-background-color: #2a2a35; -fx-text-fill: #ffffff; -fx-background-radius: 8; -fx-padding: 10; -fx-font-size: 14;");
        griglia.add(pField, 1, 2);

        inviaBtn = new Button("ACCEDI");
        inviaBtn.setFont(Font.font("Helvetica", FontWeight.BOLD, 14));
        inviaBtn.setMaxWidth(Double.MAX_VALUE);
        inviaBtn.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");
        griglia.add(inviaBtn, 0, 4, 2, 1);
        GridPane.setMargin(inviaBtn, new Insets(10, 0, 0, 0));

     //button di registrazione
        vaiRegBtn = new Button("Registrati");
        vaiRegBtn.setFont(Font.font("Helvetica", 12));
        vaiRegBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #007acc; -fx-cursor: hand; -fx-underline: true;");
        griglia.add(vaiRegBtn, 0, 5, 2, 1);
        GridPane.setHalignment(vaiRegBtn, javafx.geometry.HPos.CENTER);
        
        gestisciInterazioniBottone();

        // evento per aprire il popup di iscrizione
        vaiRegBtn.setOnAction(e -> mostraFinestraRegistrazione(finestratop));

        inviaBtn.setOnAction(evento -> {
            String inputUser = uField.getText();
            String inputPass = pField.getText();
            
            //esecuzione della verifica tramite il dao 
            UtenteDAO coreDao = new UtenteDAO();
            Utente accountTrovato = coreDao.verificaLogin(inputUser, inputPass);
            
            if (accountTrovato != null) {
                this.utenteLoggato = accountTrovato;
                // loggato, credenziali corrette(cioe trovate nel DB)
                mostraNotificaCustom(finestratop, "Esito login", "Login effettuato con successo !", "Benvenuto/a, " + accountTrovato.getUsername()  );
                mostraDashboardPrincipale(finestratop);
            } else {
                // errore perceh esiste nessuna corrispondenza sul DB   
                mostraNotificaCustom(finestratop, "Errore di Autenticazione", "Errore", "Username o Password non corretti,riprova.");
            }   
        });

        Scene schermata = new Scene(griglia, 450, 420);
        finestratop.setScene(schermata);
        finestratop.setResizable(false);
        finestratop.show();
    }

    private void mostraDashboardPrincipale(Stage stageAttuale) {
        stageAttuale.setTitle("Dashboard delle attività");

        GridPane panelDash = new GridPane();
        panelDash.setAlignment(Pos.TOP_LEFT); panelDash.setHgap(20); panelDash.setVgap(15);
        panelDash.setPadding(new Insets(30)); panelDash.setStyle("-fx-background-color: #1e1e24;");

        //Label benvLabel = new Label("PANNELLO DI: " + utenteLoggato.getUsername().toUpperCase());
        // benvLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 20)); benvLabel.setStyle("-fx-text-fill: #ffffff;");
        // panelDash.add(benvLabel, 0, 0, 2, 1);
        Label tForm = new Label("Crea una nuova attività");
        tForm.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, 15)); tForm.setStyle("-fx-text-fill: #007acc;");
        panelDash.add(tForm, 0, 1, 2, 1); GridPane.setMargin(tForm, new Insets(10, 0, 5, 0));

        Label lTit = new Label("Titolo *"); lTit.setStyle("-fx-text-fill: #a0a0aa;"); panelDash.add(lTit, 0, 2);
        TextField fTitolo = new TextField(); fTitolo.setPromptText("Nome del task");
        fTitolo.setStyle("-fx-background-color: #2a2a35; -fx-text-fill: white; -fx-background-radius: 6;"); panelDash.add(fTitolo, 1, 2);

        Label lDesc = new Label("Descrizione"); lDesc.setStyle("-fx-text-fill: #a0a0aa;"); panelDash.add(lDesc, 0, 3);
        TextArea fDesc = new TextArea(); fDesc.setPromptText("Dettagli facoltativi..."); fDesc.setPrefRowCount(3); fDesc.setPrefWidth(200);
        fDesc.setStyle("-fx-control-inner-background: #2a2a35; -fx-text-fill: white;"); panelDash.add(fDesc, 1, 3);

        Label lScad = new Label("Scadenza *"); lScad.setStyle("-fx-text-fill: #a0a0aa;"); panelDash.add(lScad, 0, 4);
        DatePicker fData = new DatePicker(); fData.setStyle("-fx-control-inner-background: #2a2a35;"); panelDash.add(fData, 1, 4);

        Label lPrio = new Label("Priorità *"); lPrio.setStyle("-fx-text-fill: #a0a0aa;"); panelDash.add(lPrio, 0, 5);
        ComboBox<String> cbPrio = new ComboBox<>(); cbPrio.getItems().addAll("Bassa", "Media", "Alta");
        cbPrio.setValue("Media"); 
        cbPrio.setStyle("-fx-background-color: #2a2a35; -fx-text-fill: white;");
        
        cbPrio.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(String voce, boolean svuotato) {
                super.updateItem(voce, svuotato);
                if (svuotato || voce == null) { setStyle("-fx-background-color: #2a2a35;"); setText(null); } 
                else { setStyle("-fx-background-color: #2a2a35; -fx-text-fill: white;"); setText(voce); }
            }
        });
        cbPrio.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(String voce, boolean svuotato) {
                super.updateItem(voce, svuotato);
                if (svuotato || voce == null) { setStyle("-fx-text-fill: white;"); setText(null); } 
                else { setStyle("-fx-text-fill: white;"); setText(voce); }
            }
        });
        panelDash.add(cbPrio, 1, 5);

        Button salvaTaskBtn = new Button("SALVA TASK");
        salvaTaskBtn.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 10; -fx-cursor: hand;");
        panelDash.add(salvaTaskBtn, 1, 6);

        Label tTabella = new Label("Attività in corso (ordinate per urgenza)");
        tTabella.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, 14)); tTabella.setStyle("-fx-text-fill: #ffffff;");
        panelDash.add(tTabella, 2, 1);

        tabellaAttivita = new TableView<>();
        tabellaAttivita.setPrefWidth(350); tabellaAttivita.setPrefHeight(250);
        tabellaAttivita.setStyle("-fx-background-color: #2a2a35; -fx-control-inner-background: #2a2a35;");

        TableColumn<Attivita, String> colTitolo = new TableColumn<>("Titolo");
        colTitolo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("titolo"));
        colTitolo.setPrefWidth(150);

        TableColumn<Attivita, String> colScadenza = new TableColumn<>("Scadenza");
        colScadenza.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dataScadenza"));
        colScadenza.setPrefWidth(180);

        tabellaAttivita.getColumns().addAll(colTitolo, colScadenza);
        panelDash.add(tabellaAttivita, 2, 2, 1, 4);

        AttivitaDAO daoTask = new AttivitaDAO();
        tabellaAttivita.setItems(daoTask.getAttivitaUtente(utenteLoggato.getIdUtente()));

        salvaTaskBtn.setOnAction(e -> {
            String titoloText = fTitolo.getText(); String descrizioneText = fDesc.getText();
            java.time.LocalDate dataSelezionata = fData.getValue();
            
            if(titoloText.isEmpty() || dataSelezionata == null) {
                mostraNotificaCustom(stageAttuale, "Errore", "Mancano dei dati", "Compila i campi obbligatori!");
            } else {
                Attivita t = new Attivita(); t.setTitolo(titoloText); t.setDescrizione(descrizioneText);
                t.setDataScadenza(LocalDateTime.of(dataSelezionata, LocalTime.MIDNIGHT));
                t.setCompletata(0); t.setIdUtente(utenteLoggato.getIdUtente());
                
                int priorityId = cbPrio.getValue().equals("Bassa") ? 1 : cbPrio.getValue().equals("Media") ? 2 : 3;
                t.setIdPriorita(priorityId); t.setIdCategoria(null);
                
                if(daoTask.inserisciNuovaTask(t)) {
                    mostraNotificaCustom(stageAttuale, "Esito", "", "Attività inserita");
                    fTitolo.clear(); fDesc.clear(); fData.setValue(null);
                    tabellaAttivita.setItems(daoTask.getAttivitaUtente(utenteLoggato.getIdUtente()));
                } else {
                    mostraNotificaCustom(stageAttuale, "Errore", "Fail SQL", "Errore di scrittura nel database.");
                }
            }
        });

        Scene scenaDash = new Scene(panelDash, 850, 520);
        stageAttuale.setScene(scenaDash); stageAttuale.centerOnScreen();
    }
    
    

    private void gestisciInterazioniBottone() {
        inviaBtn.setOnMouseEntered(mouseIn -> inviaBtn.setStyle("-fx-background-color: #0098ff; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;"));
        inviaBtn.setOnMouseExited(mouseOut -> inviaBtn.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;"));
        vaiRegBtn.setOnMouseEntered(h -> vaiRegBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #0098ff; -fx-cursor: hand; -fx-underline: true;"));
        vaiRegBtn.setOnMouseExited(h -> vaiRegBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #007acc; -fx-cursor: hand; -fx-underline: true;"));
    }
    private void mostraFinestraRegistrazione(Stage princ) {
        Stage popReg = new Stage();
        popReg.initModality(Modality.APPLICATION_MODAL);
        popReg.initOwner(princ); popReg.setTitle("Registrati");

        GridPane gReg = new GridPane();
        gReg.setAlignment(Pos.CENTER); gReg.setHgap(12); gReg.setVgap(12); gReg.setPadding(new Insets(30));
        gReg.setStyle("-fx-background-color: #1e1e24;");

        Label tReg = new Label("REGISTRAZIONE");
        tReg.setFont(Font.font("Helvetica", FontWeight.BOLD, 18)); tReg.setStyle("-fx-text-fill: #ffffff;");
        gReg.add(tReg, 0, 0, 2, 1);

        Label lu = new Label("Nome utente"); lu.setStyle("-fx-text-fill: #a0a0aa;"); gReg.add(lu, 0, 1);
        TextField tfUser = new TextField();
        tfUser.setStyle("-fx-background-color: #2a2a35; -fx-text-fill: white; -fx-background-radius: 6;"); gReg.add(tfUser, 1, 1);

        Label lp = new Label("Password"); lp.setStyle("-fx-text-fill: #a0a0aa;"); gReg.add(lp, 0, 2);
        PasswordField pfPass = new PasswordField();
        pfPass.setStyle("-fx-background-color: #2a2a35; -fx-text-fill: white; -fx-background-radius: 6;"); gReg.add(pfPass, 1, 2);

        Button eseguiRegBtn = new Button("REGISTRA");
        eseguiRegBtn.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8; -fx-cursor: hand;");
        gReg.add(eseguiRegBtn, 1, 4);

        eseguiRegBtn.setOnAction(ev -> {
            String u = tfUser.getText(); String p = pfPass.getText();
            if(u.isEmpty() || p.isEmpty()) {
                mostraNotificaCustom(popReg, "Errore", "Campi vuoti", "compila tutto prima di continuare");
            } else {
                UtenteDAO regDao = new UtenteDAO();
                boolean transazioneOk = regDao.registraUtente(u, p);
                if(transazioneOk) {
                    mostraNotificaCustom(popReg, "Esito", "Successo!", "Ti sei registrato con successo");
                    popReg.close();
                } else {
                    mostraNotificaCustom(popReg, "Errore", "Riprova", "Username gia esistente");
                }
            }
        });

        Scene sc = new Scene(gReg, 380, 280);
        popReg.setScene(sc); popReg.setResizable(false); popReg.showAndWait();
    }

    private void mostraNotificaCustom(Stage principale, String titoloFinestra, String intestazione, String messaggio) {
        Stage finestraPopup = new Stage();
        finestraPopup.initModality(Modality.APPLICATION_MODAL); finestraPopup.initOwner(principale); finestraPopup.setTitle(titoloFinestra);
        VBox boxContenuto = new VBox(15); boxContenuto.setAlignment(Pos.CENTER); boxContenuto.setPadding(new Insets(25));
        boxContenuto.setStyle("-fx-background-color: #1e1e24;");
        Label lblIntestazione = new Label(intestazione); lblIntestazione.setFont(Font.font("Helvetica", FontWeight.BOLD, 18)); lblIntestazione.setStyle("-fx-text-fill: #ffffff;");
        Label lblMessaggio = new Label(messaggio); lblMessaggio.setFont(Font.font("Helvetica", 14)); lblMessaggio.setStyle("-fx-text-fill: #a0a0aa;"); lblMessaggio.setWrapText(true);
        Button chiudiBtn = new Button("OK"); chiudiBtn.setFont(Font.font("Helvetica", FontWeight.BOLD, 13)); chiudiBtn.setPrefWidth(100);
        chiudiBtn.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8; -fx-cursor: hand;");
        chiudiBtn.setOnAction(e -> finestraPopup.close());
        boxContenuto.getChildren().addAll(lblIntestazione, lblMessaggio, chiudiBtn);
        Scene scenaPopup = new Scene(boxContenuto, 350, 180); finestraPopup.setScene(scenaPopup); finestraPopup.setResizable(false); finestraPopup.showAndWait();
    }

    public static void main(String[] args) { 
        
            launch(args); }
}
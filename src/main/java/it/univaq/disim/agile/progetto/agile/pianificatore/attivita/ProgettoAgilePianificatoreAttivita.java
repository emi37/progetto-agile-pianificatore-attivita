package it.univaq.disim.agile.progetto.agile.pianificatore.attivita;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.dao.UtenteDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model.Utente;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgettoAgilePianificatoreAttivita extends Application {

    private TextField uField;
    private PasswordField pField;
    private Button inviaBtn;

    @Override
    public void start(Stage finestratop) {
        finestratop.setTitle("Agile Task Manager - Login");
                // tutta la parte di design main, bottone, sfondo ecc.
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

        gestisciInterazioniBottone();

        inviaBtn.setOnAction(evento -> {
            String inputUser = uField.getText();
            String inputPass = pField.getText();
            
            //esecuzione della verifica tramite il dao 
            UtenteDAO coreDao = new UtenteDAO();
            Utente accountTrovato = coreDao.verificaLogin(inputUser, inputPass);
            
            if (accountTrovato != null) {
                // loggato, credenziali corrette(cioe trovate nel DB)
                mostraNotificaCustom(finestratop, "Esito Login", "Login Effettuato!", "Benvenuto, " + accountTrovato.getUsername() + "!");
            } else {
                // errore perceh esiste nessuna corrispondenza sul DB   
                mostraNotificaCustom(finestratop, "Errore di Autenticazione", "Errore", "Username o Password non corretti. Riprova.");
            }  
        }
        );
        Scene schermata = new Scene(griglia, 450, 350);
        finestratop.setScene(schermata);
        finestratop.setResizable(false);
        finestratop.show();
    }
    private void gestisciInterazioniBottone() {
        inviaBtn.setOnMouseEntered(mouseIn -> inviaBtn.setStyle(
            "-fx-background-color: #0098ff; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;"
        ));
        inviaBtn.setOnMouseExited(mouseOut -> inviaBtn.setStyle(
            "-fx-background-color: #007acc; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;"));
    }
    // metodo per generare la finestra di avviso del login con lo stesso stile di grafica
    private void mostraNotificaCustom(Stage principale, String titoloFinestra, String intestazione, String messaggio) {
        Stage finestraPopup = new Stage();
        finestraPopup.initModality(Modality.APPLICATION_MODAL);
        finestraPopup.initOwner(principale);
        finestraPopup.setTitle(titoloFinestra);

        VBox boxContenuto = new VBox(15);
        boxContenuto.setAlignment(Pos.CENTER);
        boxContenuto.setPadding(new Insets(25));
        boxContenuto.setStyle("-fx-background-color: #1e1e24;");

        Label lblIntestazione = new Label(intestazione);
        lblIntestazione.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        lblIntestazione.setStyle("-fx-text-fill: #ffffff;");

        Label lblMessaggio = new Label(messaggio);
        lblMessaggio.setFont(Font.font("Helvetica", 14));
        lblMessaggio.setStyle("-fx-text-fill: #a0a0aa;");
        lblMessaggio.setWrapText(true);

        Button chiudiBtn = new Button("OK");
        chiudiBtn.setFont(Font.font("Helvetica", FontWeight.BOLD, 13));
        chiudiBtn.setPrefWidth(100);
        chiudiBtn.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8; -fx-cursor: hand;");
        
        chiudiBtn.setOnMouseEntered(e -> chiudiBtn.setStyle("-fx-background-color: #0098ff; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8; -fx-cursor: hand;"));
        chiudiBtn.setOnMouseExited(e -> chiudiBtn.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8; -fx-cursor: hand;"));
        chiudiBtn.setOnAction(e -> finestraPopup.close());

        boxContenuto.getChildren().addAll(lblIntestazione, lblMessaggio, chiudiBtn);

        Scene scenaPopup = new Scene(boxContenuto, 350, 180);
        finestraPopup.setScene(scenaPopup);
        finestraPopup.setResizable(false);
        finestraPopup.showAndWait();
    }

            public static void main(String[] args) {
                launch(args);
                }
}
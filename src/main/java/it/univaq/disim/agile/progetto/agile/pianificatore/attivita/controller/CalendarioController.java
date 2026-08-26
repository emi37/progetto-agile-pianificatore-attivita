/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author hasan
 */
package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;

public class CalendarioController implements Initializable {

    @FXML
    private ComboBox<String> vistaComboBox;

    @FXML
    private CheckBox filtroPrioritaAltaCheckBox;

    @FXML
    private VBox calendarioContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Popoliamo la ComboBox per la scelta della visualizzazione (Mese / Settimana)
        vistaComboBox.getItems().addAll("Mese Corrente", "Settimana Corrente");
        vistaComboBox.setValue("Mese Corrente"); // Default come da criteri di accettazione

        System.out.println("Calendario inizializzato correttamente in modalità Mese.");
    }

    @FXML
    private void applicaFiltriAction(ActionEvent event) {
        boolean soloAlta = filtroPrioritaAltaCheckBox.isSelected();
        if (soloAlta) {
            System.out.println("Filtro applicato: visualizzazione esclusiva delle attività a Priorità Alta.");
            // Qui implementeremo la logica di filtraggio della griglia del calendario
        } else {
            System.out.println("Filtro rimosso: visualizzazione di tutte le attività.");
        }
    }

    @FXML
    private void resetFiltriAction(ActionEvent event) {
        filtroPrioritaAltaCheckBox.setSelected(false);
        System.out.println("Filtri resettati con successo.");
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.controller;

/**
 *
 * @author Filippo
 */

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller per la Dashboard principale ch gestisce la visualizzazione delle attività .
 */
public class HomeController implements Initializable {

    @FXML
    private Label benvenutoLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        benvenutoLabel.setText("Benvenuto nella tua home personale!");
    }
}
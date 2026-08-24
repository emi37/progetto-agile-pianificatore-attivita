/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author Filippo
 */

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object per l'entità Attivita.
 * Si occupa di tradurre l'oggetto Java in record relazionali sul database MySQL.
 */

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Data Access Object per l'entità Attivita.
 * Allineato con la struttura reale del database (inclusivo di Foreign Keys e Datetime).
 */
public class AttivitaDAO {

    // URL aggiornato con il nome reale del tuo schema dal Workbench
    private static final String URL = "jdbc:mysql://localhost:3306/progetto_agile_pianificatore_attivita";
    private static final String USER = "root";
    private static final String PASS = "12345"; 

    /**
     * Inserisce una nuova attività nel database utilizzando PreparedStatement.
     * Riceve l'oggetto Attivita e le chiavi esterne (ID) necessarie per le relazioni.
     */
    public boolean inserisciAttivita(Attivita attivita, int idUtente, int idCategoria, int idPriorita) {
        String query = "INSERT INTO attivita (titolo, descrizione, data_scadenza, data_completamento, completata, id_utente, id_categoria, id_priorita) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, attivita.getTitolo());
            statement.setString(2, attivita.getDescrizione());
            
            // Gestione sicura del passaggio da LocalDate (Java) a Datetime (SQL)
            if (attivita.getDataScadenza() != null) {
                statement.setTimestamp(3, Timestamp.valueOf(attivita.getDataScadenza().atStartOfDay()));
            } else {
                statement.setNull(3, Types.TIMESTAMP);
            }
            
            if (attivita.getDataCompletamento() != null) {
                statement.setTimestamp(4, Timestamp.valueOf(attivita.getDataCompletamento().atStartOfDay()));
            } else {
                statement.setNull(4, Types.TIMESTAMP);
            }
            
            statement.setBoolean(5, attivita.isCompletata());
            
            // Inserimento delle chiavi esterne
            statement.setInt(6, idUtente);
            statement.setInt(7, idCategoria);
            statement.setInt(8, idPriorita);
            
            int righeInserite = statement.executeUpdate();
            return righeInserite > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
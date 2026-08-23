package database;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe che gestisce le operazioni di database riguardanti gli utenti.
 */
public class UtenteDAO {

    public Utente autentica(String username, String password) {
        String query = "SELECT * FROM utenti WHERE username = ? AND password = ?";
        
        // Uso del try-with-resources e del DatabaseManager per la connessione
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            // Parametri sicuri per prevenire attacchi SQL Injection
            statement.setString(1, username);
            statement.setString(2, password);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Utente(
                        resultSet.getString("username"), 
                        resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Registra un nuovo utente nel database.
     * Utilizza una query INSERT protetta da PreparedStatement.
     */
    public boolean registraUtente(String username, String password) {
        String query = "INSERT INTO utenti (username, password) VALUES (?, ?)";
        
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            // Impostiamo i parametri in modo sicuro contro le SQL Injection
            statement.setString(1, username);
            statement.setString(2, password);
            
            // executeUpdate() restituisce il numero di righe modificate nel DB
            int righeInserite = statement.executeUpdate();
            
            // Se almeno una riga è stata inserita, la registrazione ha avuto successo
            return righeInserite > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
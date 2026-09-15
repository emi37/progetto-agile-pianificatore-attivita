package database;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * la classe gestisce le operazion di base delli utenti
 */
public class UtenteDAO {

    public Utente autentica(String username, String password) {
        String query = "SELECT * FROM utenti WHERE username = ? AND password = ?";
        
        // per la connessioe al db manager 
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, username);
            statement.setString(2, password);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id_utente"); 
                    
                    return new Utente(
                        id,
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
     * nuovo utente nel db     */
    public boolean registraUtente(String username, String password) {
        String query = "INSERT INTO utenti (username, password) VALUES (?, ?)";
        
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, username);
            statement.setString(2, password);
            
            // executeUpdate() restituisce il numero di righe modificate nel db
            int righeInserite = statement.executeUpdate();
            
            // Se almeno una riga è stata inserita, la registrazione ha avuto successo
            return righeInserite > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
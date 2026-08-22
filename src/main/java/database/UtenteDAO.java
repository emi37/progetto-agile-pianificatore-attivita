package database;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * classe che gestisce le operazioni di database riguardanti gli utenti.
 */
public class UtenteDAO {

    // Da sostituire con le credenziali reali del tuo database MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/dufftech_db";
    private static final String USER = "root";
    private static final String PASS = "password";

    public Utente autentica(String username, String password) {
        String query = "SELECT * FROM utenti WHERE username = ? AND password = ?";
        
        // Uso del try-with-resources per chiudere in automatico la connessione
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS);
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
}
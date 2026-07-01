package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.dao;

import it.univaq.disim.progettoagile.database.DatabaseConnection;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model.Utente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAO {

    // verifica le credenziali inseriyte dall'utente
    public Utente verificaLogin(String username, String password) {
        String query = "SELECT * FROM utente WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                        rs.getInt("id_utente"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore nella query di login: ");
            e.printStackTrace();
        }
        return null;
    }

    // "nuovo metodo sprint 2 per inserire il profilo nel db usando i tuoi parametri"
    public boolean registraUtente(String nUser, String nPass) {
        String queryReg = "INSERT INTO utente (username, password) VALUES (?, ?)";
        
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement pSt = c.prepareStatement(queryReg)) {
            
            pSt.setString(1, nUser); pSt.setString(2, nPass);
            
            int righ = pSt.executeUpdate();
            return righ > 0; // da true se inserisce la riga su mysql
            
        } catch (SQLException exDb) {
            System.err.println("fail nell inserimento della registrazione utente: " + exDb.getMessage());
            return false;
        }
    }
}
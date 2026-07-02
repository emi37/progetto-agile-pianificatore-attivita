package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.dao;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model.Attivita;
import it.univaq.disim.progettoagile.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class AttivitaDAO {
    public boolean inserisciNuovaTask(Attivita tsk) {
        String queryInsert = "INSERT INTO attivita (titolo, descrizione, data_scadenza, completata, id_utente, id_categoria, id_priorita) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pSt = con.prepareStatement(queryInsert)) {
            
            pSt.setString(1, tsk.getTitolo()); pSt.setString(2, tsk.getDescrizione());
            
            if (tsk.getDataScadenza() != null) {
                pSt.setTimestamp(3, Timestamp.valueOf(tsk.getDataScadenza()));
            } else { pSt.setNull(3, Types.TIMESTAMP); }
            
            pSt.setInt(4, tsk.getCompletata()); pSt.setInt(5, tsk.getIdUtente());
            
            if (tsk.getIdCategoria() != null) {
                pSt.setInt(6, tsk.getIdCategoria());
            } else { 
                pSt.setNull(6, Types.INTEGER); 
            
            }
            
            pSt.setInt(7, tsk.getIdPriorita());
            
            int recordToccati = pSt.executeUpdate();
            return recordToccati > 0;
            
            
        } catch (SQLException exMysql) {
            System.err.println("fallimento inserimento attività, riprovare: " + exMysql.getMessage());
            return false;
        }
    }

    public javafx.collections.ObservableList<Attivita> getAttivitaUtente(int idUtente) {
        javafx.collections.ObservableList<Attivita> lista = javafx.collections.FXCollections.observableArrayList();
        String sqlSelect = "SELECT * FROM attivita WHERE id_utente = ? AND completata = 0 ORDER BY id_priorita DESC, data_scadenza ASC";
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pSt = con.prepareStatement(sqlSelect)) {
            
            pSt.setInt(1, idUtente);
            try (ResultSet rs = pSt.executeQuery()) {
                while (rs.next()) {
                    Attivita a = new Attivita();
                    a.setIdAttivita(rs.getInt("id_attivita"));
                    a.setTitolo(rs.getString("titolo"));
                    a.setDescrizione(rs.getString("descrizione"));
                    Timestamp tScad = rs.getTimestamp("data_scadenza");
                    if (tScad != null) a.setDataScadenza(tScad.toLocalDateTime());
                    a.setCompletata(rs.getInt("completata"));
                    a.setIdUtente(rs.getInt("id_utente"));
                    a.setIdPriorita(rs.getInt("id_priorita"));
                    lista.add(a);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Errore nel recupero delle attivita: " + ex.getMessage());
        }
        return lista;
    }
}
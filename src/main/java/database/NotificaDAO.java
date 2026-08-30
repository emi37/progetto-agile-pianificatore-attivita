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
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Notifica;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Priorita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NotificaDAO {

    /**
     * Salva una nuova notifica sul database
     */
    public boolean inserisciNotifica(Notifica notifica) {
        String sql = "INSERT INTO notifica (messaggio, stato, data_invio, id_attivita) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, notifica.getMessaggio());
            ps.setString(2, notifica.getStato()); 
            ps.setTimestamp(3, Timestamp.valueOf(notifica.getDataInvio()));
            
            // Estraiamo l'ID dall'oggetto associato
            ps.setInt(4, notifica.getAttivita().getId());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore in inserimento notifica: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tira fuori le notifiche da far poppare a schermo per l'utente loggato.
     * Filtra per stato e per orario (tutto quello che è antecedente o uguale ad adesso).
     */
    public List<Notifica> estraiNotificheDaMostrare(int idUtente) {
        List<Notifica> lista = new ArrayList<>();
        
        // La join serve per capire di che attività stiamo parlando (ci serve almeno il titolo)
        String sql = "SELECT n.id_notifica, n.messaggio, n.stato, n.data_invio, " +
                     "a.id_attivita, a.titolo, p.id_priorita, p.livello " +
                     "FROM notifica n " +
                     "JOIN attivita a ON n.id_attivita = a.id_attivita " +
                     "JOIN priorita p ON a.id_priorita = p.id_priorita " +
                     "WHERE a.id_utente = ? AND n.stato = 'DA_LEGGERE' AND n.data_invio <= NOW()";
                     
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUtente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    
                    // Ricostruiamo i pezzi del dominio partendo dal basso
                    Priorita p = new Priorita(rs.getInt("id_priorita"), rs.getString("livello"));
                    Attivita a = new Attivita(rs.getInt("id_attivita"), rs.getString("titolo"), null, null, null, false, null, null, p);
                    
                    Notifica n = new Notifica(
                        rs.getInt("id_notifica"),
                        rs.getString("messaggio"),
                        rs.getString("stato"),
                        rs.getTimestamp("data_invio").toLocalDateTime(),
                        a
                    );
                    
                    lista.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Da chiamare quando l'utente chiude il pop-up
     */
    public boolean aggiornaStatoLetta(int idNotifica) {
        String sql = "UPDATE notifica SET stato = 'LETTA' WHERE id_notifica = ?";
        
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idNotifica);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
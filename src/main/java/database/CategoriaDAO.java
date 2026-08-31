/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author Filippo
 */
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    /**
     * Estrae tutte le categorie associate a un utente (incluse quelle standard).
     */
    public List<Categoria> getCategorieUtente(int idUtente) {
        List<Categoria> lista = new ArrayList<>();
        // Prende le categorie standard (id_utente = 0 o null a seconda del tuo DB) e quelle specifiche dell'utente
        String sql = "SELECT * FROM categoria WHERE id_utente IS NULL OR id_utente = 0 OR id_utente = ?";
        
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria cat = new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nome_categoria"),
                        rs.getInt("id_utente")
                    );
                    lista.add(cat);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore in getCategorieUtente");
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Inserisce una nuova categoria custom nel DB e restituisce l'oggetto aggiornato con l'ID.
     */
    public Categoria inserisciCategoriaCustom(String nomeCategoria, int idUtente) {
        String sql = "INSERT INTO categoria (nome_categoria, id_utente) VALUES (?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nomeCategoria);
            ps.setInt(2, idUtente);
            
            int righe = ps.executeUpdate();
            if (righe > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerato = rs.getInt(1);
                        return new Categoria(idGenerato, nomeCategoria, idUtente);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Fallito inserimento nuova categoria a scelta");
            e.printStackTrace();
        }
        return null; // Ritorna null se la query fallisce
    }
}
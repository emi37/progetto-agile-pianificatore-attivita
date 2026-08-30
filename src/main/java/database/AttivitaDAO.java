package database;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Categoria;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Priorita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttivitaDAO {

    public boolean inserisciAttivita(Attivita attivita, int idUtente, int idCategoria, int idPriorita) {
        String query = "INSERT INTO attivita (titolo, descrizione, data_scadenza, data_completamento, completata, id_utente, id_categoria, id_priorita) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, attivita.getTitolo());
            statement.setString(2, attivita.getDescrizione());

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

    public List<Attivita> getAttivitaUrgenti(int idUtente) {
        List<Attivita> listaUrgenti = new ArrayList<>();
        String query = "SELECT a.*, c.nome_categoria, p.livello " +
                       "FROM attivita a " +
                       "JOIN categoria c ON a.id_categoria = c.id_categoria " +
                       "JOIN priorita p ON a.id_priorita = p.id_priorita " +
                       "WHERE a.id_utente = ? AND a.completata = 0 " +
                       "ORDER BY a.id_priorita DESC, a.data_scadenza ASC";
                       
        try (Connection connection = DatabaseManager.getConnection(); 
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUtente);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Categoria categoria = new Categoria(resultSet.getInt("id_categoria"), resultSet.getString("nome_categoria"), idUtente);
                    Priorita priorita = new Priorita(resultSet.getInt("id_priorita"), resultSet.getString("livello"));
                    
                    Attivita attivita = new Attivita(
                        resultSet.getInt("id_attivita"),
                        resultSet.getString("titolo"),
                        resultSet.getString("descrizione"),
                        resultSet.getTimestamp("data_scadenza") != null ? resultSet.getTimestamp("data_scadenza").toLocalDateTime().toLocalDate() : null,
                        null, 
                        false,
                        null,
                        categoria,
                        priorita
                    );
                    listaUrgenti.add(attivita);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaUrgenti;
    }

    public int contaAttivitaCompletateDal(int idUtente, LocalDate dataInizio) {
        int conteggio = 0;
        String query = "SELECT COUNT(*) AS totale FROM attivita WHERE id_utente = ? AND completata = 1 AND data_completamento >= ?";
        
        try (Connection connection = DatabaseManager.getConnection(); 
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUtente);
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(dataInizio.atStartOfDay()));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    conteggio = resultSet.getInt("totale");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conteggio;
    }

    public List<Attivita> getAttivitaCompletate(int idUtente) {
        List<Attivita> lista = new ArrayList<>();
        String query = "SELECT a.id_attivita AS id_att, a.titolo, a.descrizione, a.data_scadenza, a.data_completamento, a.completata, "
                + "c.id_categoria, c.nome_categoria, c.id_utente as cat_utente, p.id_priorita, p.livello "
                + "FROM attivita a "
                + "JOIN categoria c ON a.id_categoria = c.id_categoria "
                + "JOIN priorita p ON a.id_priorita = p.id_priorita "
                + "WHERE a.id_utente = ? AND a.completata = 1 "
                + "ORDER BY a.data_completamento DESC LIMIT 10";

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUtente);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("nome_categoria"), rs.getInt("cat_utente"));
                    Priorita priorita = new Priorita(rs.getInt("id_priorita"), rs.getString("livello"));

                    Attivita attivita = new Attivita(
                        rs.getInt("id_att"), 
                        rs.getString("titolo"), 
                        rs.getString("descrizione"), 
                        rs.getTimestamp("data_scadenza") != null ? rs.getTimestamp("data_scadenza").toLocalDateTime().toLocalDate() : null, 
                        rs.getTimestamp("data_completamento") != null ? rs.getTimestamp("data_completamento").toLocalDateTime().toLocalDate() : null, 
                        rs.getBoolean("completata"), 
                        null, 
                        categoria, 
                        priorita
                    );
                    lista.add(attivita);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean aggiornaAttivita(Attivita attivita, int idCategoria, int idPriorita) {
        String query = "UPDATE attivita SET titolo = ?, descrizione = ?, data_scadenza = ?, data_completamento = ?, completata = ?, id_categoria = ?, id_priorita = ? WHERE id_attivita = ?";
        
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, attivita.getTitolo());
            statement.setString(2, attivita.getDescrizione());

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
            statement.setInt(6, idCategoria);
            statement.setInt(7, idPriorita);
            statement.setInt(8, attivita.getId());

            int righeAggiornate = statement.executeUpdate();
            return righeAggiornate > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminaAttivita(int idAttivita) {
        String query = "DELETE FROM attivita WHERE id_attivita=?";
        
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idAttivita);
            int righeEliminate = statement.executeUpdate();
            return righeEliminate > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Inserisce l'attività e restituisce l'ID generato da MySQL (fondamentale per collegare le notifiche).
     */
    public int inserisciAttivitaRestituendoId(Attivita attivita, int idUtente, int idCategoria, int idPriorita) {
        String sql = "INSERT INTO attivita (titolo, descrizione, data_scadenza, data_completamento, completata, id_utente, id_categoria, id_priorita) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Passiamo RETURN_GENERATED_KEYS a JDBC per dirgli "Ehi, dammi l'ID che hai appena creato!"
        try (Connection connessione = DatabaseManager.getConnection(); 
             PreparedStatement ps = connessione.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, attivita.getTitolo());
            ps.setString(2, attivita.getDescrizione());
            
            if (attivita.getDataScadenza() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(attivita.getDataScadenza().atStartOfDay()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.setNull(4, Types.TIMESTAMP); // È nuova, quindi non completata
            ps.setBoolean(5, false);
            ps.setInt(6, idUtente);
            ps.setInt(7, idCategoria);
            ps.setInt(8, idPriorita);

            int righeSalvate = ps.executeUpdate();
            if (righeSalvate > 0) {
                // Peschiamo l'ID autoincrementale generato dal database
                try (ResultSet chiaviGenerate = ps.getGeneratedKeys()) {
                    if (chiaviGenerate.next()) {
                        return chiaviGenerate.getInt(1); // Restituiamo l'id_attivita
                    }
                }
            }
            return -1; // Ritorna -1 in caso di errore logico
        } catch (SQLException e) {
            System.err.println("Maronn, impossibile generare l'ID durante l'inserimento dell'attività!");
            e.printStackTrace();
            return -1;
        }
    }
}
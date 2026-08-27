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

/**
 * Data Access Object per l'entità Attivita. Gestisce la persistenza e le query
 * sul database MySQL tramite JDBC (PreparedStatement).
 * AGGIORNATO: Sincronizzato con i nuovi costruttori di Dominio (inserimento ID Priorita).
 */
public class AttivitaDAO {

    /**
     * Inserisce una nuova attività nel database utilizzando PreparedStatement.
     */
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

    /**
     * Recupera la lista delle attività non completate per la Dashboard,
     * ordinate per scadenza imminente. Sfrutta le JOIN per mappare
     * correttamente Categoria e Priorità in base ai costruttori del dominio.
     */
    public List<Attivita> getAttivitaUrgenti(int idUtente) {
        List<Attivita> lista = new ArrayList<>();
        // FIX: Aggiunto p.id_priorita nella SELECT per poterlo passare al costruttore
        String query = "SELECT a.id_attivita AS id_att, a.titolo, a.descrizione, a.data_scadenza, a.data_completamento, a.completata, "
                + "c.id_categoria, c.nome_categoria, c.id_utente as cat_utente, p.id_priorita, p.livello "
                + "FROM attivita a "
                + "LEFT JOIN categoria c ON a.id_categoria = c.id_categoria "
                + "INNER JOIN priorita p ON a.id_priorita = p.id_priorita "
                + "WHERE a.id_utente = ? AND a.completata = false "
                + "ORDER BY a.data_scadenza ASC";

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idUtente);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_att");
                    String titolo = rs.getString("titolo");
                    String descrizione = rs.getString("descrizione");

                    Timestamp tsScadenza = rs.getTimestamp("data_scadenza");
                    LocalDate dataScadenza = (tsScadenza != null) ? tsScadenza.toLocalDateTime().toLocalDate() : null;

                    Timestamp tsCompletamento = rs.getTimestamp("data_completamento");
                    LocalDate dataCompletamento = (tsCompletamento != null) ? tsCompletamento.toLocalDateTime().toLocalDate() : null;

                    boolean completata = rs.getBoolean("completata");

                    // Ricostruzione oggetti di dominio coerenti con i costruttori ricevuti
                    int idCat = rs.getInt("id_categoria");
                    String nomeCat = rs.getString("nome_categoria");
                    int catUtente = rs.getInt("cat_utente");
                    Categoria categoria = new Categoria(idCat, nomeCat, catUtente);

                    // FIX: Ora peschiamo anche l'ID della priorità e lo passiamo al costruttore
                    int idPrio = rs.getInt("id_priorita");
                    String livelloPrio = rs.getString("livello");
                    Priorita priorita = new Priorita(idPrio, livelloPrio);

                    Attivita attivita = new Attivita(id, titolo, descrizione, dataScadenza, dataCompletamento, completata, null, categoria, priorita);
                    lista.add(attivita);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Recupera le ultime attività completate dall'utente, ordinate per data di
     * completamento più recente.
     */
    public List<Attivita> getAttivitaCompletateRecenti(int idUtente) {
        List<Attivita> lista = new ArrayList<>();
        // FIX: Aggiunto p.id_priorita nella SELECT
        String query = "SELECT a.id_attivita AS id_att, a.titolo, a.descrizione, a.data_scadenza, a.data_completamento, a.completata, "
                + "c.id_categoria, c.nome_categoria, c.id_utente as cat_utente, p.id_priorita, p.livello "
                + "FROM attivita a "
                + "LEFT JOIN categoria c ON a.id_categoria = c.id_categoria "
                + "INNER JOIN priorita p ON a.id_priorita = p.id_priorita "
                + "WHERE a.id_utente = ? AND a.completata = true "
                + "ORDER BY a.data_completamento DESC LIMIT 10";

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idUtente);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_att");
                    String titolo = rs.getString("titolo");
                    String descrizione = rs.getString("descrizione");

                    Timestamp tsScadenza = rs.getTimestamp("data_scadenza");
                    LocalDate dataScadenza = (tsScadenza != null) ? tsScadenza.toLocalDateTime().toLocalDate() : null;

                    Timestamp tsCompletamento = rs.getTimestamp("data_completamento");
                    LocalDate dataCompletamento = (tsCompletamento != null) ? tsCompletamento.toLocalDateTime().toLocalDate() : null;

                    boolean completata = rs.getBoolean("completata");

                    int idCat = rs.getInt("id_categoria");
                    String nomeCat = rs.getString("nome_categoria");
                    int catUtente = rs.getInt("cat_utente");
                    Categoria categoria = new Categoria(idCat, nomeCat, catUtente);

                    // FIX: inserito l'ID della priorità per il costruttore a 2 parametri
                    int idPrio = rs.getInt("id_priorita");
                    String livelloPrio = rs.getString("livello");
                    Priorita priorita = new Priorita(idPrio, livelloPrio);

                    Attivita attivita = new Attivita(id, titolo, descrizione, dataScadenza, dataCompletamento, completata, null, categoria, priorita);
                    lista.add(attivita);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    /**
     * Aggiorna un'attività esistente nel database sovrascrivendo i campi.
     */
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
            
            // Il parametro 8 è l'ID dell'attività (clausola WHERE) che permette a MySQL di trovare la riga esatta!
            statement.setInt(8, attivita.getId());

            int righeAggiornate = statement.executeUpdate();
            return righeAggiornate > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina definitivamente un'attività dal database tramite il suo ID primario.
     */
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
}
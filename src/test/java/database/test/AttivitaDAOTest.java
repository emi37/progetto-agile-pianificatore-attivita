package database.test;

import database.AttivitaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test (TDD) per verificare il salvataggio delle attività sul database reale.
 * Aggiornata per riflettere il nuovo costruttore della classe Attivita (inclusione di Utente).
 */
public class AttivitaDAOTest {

    @Test
    public void testInserimentoNuovaAttivita() {
        
        AttivitaDAO dao = new AttivitaDAO();
        
        // 1. Creiamo un'attività fittizia rispettando il nuovo costruttore a 9 parametri.
        // Abbiamo aggiunto un 'null' in più per soddisfare il requisito dell'oggetto Utente.
        Attivita nuovaAttivita = new Attivita(0, "Test Finale", "Verifica allineamento costruttori", 
                                              LocalDate.now().plusDays(3), null, false, null, null, null);
        
        // 2. Simuliamo l'inserimento passando gli ID reali preparati nel tuo database.
        boolean risultato = dao.inserisciAttivita(nuovaAttivita, 2, 3, 3);
        
        // 3. Asserzione: verifichiamo che il database accetti l'inserimento senza violare le Foreign Keys.
        assertTrue(risultato, "L'inserimento nel DB con le Foreign Keys non è andato a buon fine.");
    }
}
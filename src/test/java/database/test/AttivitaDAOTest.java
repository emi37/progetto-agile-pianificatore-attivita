package database.test;

import database.AttivitaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 classe di  testing TDD per testare le attività 
 */
public class AttivitaDAOTest {

    @Test
    public void testInserimentoNuovaAttivita() {
        
        AttivitaDAO dao = new AttivitaDAO();
        
        // 1. crea un'attività finta
        Attivita nuovaAttivita = new Attivita(0, "Test Finale", "Verifica allineamento costruttori", 
                                              LocalDate.now().plusDays(3), null, false, null, null, null);
        
        // 2. simulo l'inserimento passando gli id reali nel db:
        boolean risultato = dao.inserisciAttivita(nuovaAttivita, 2, 3, 3);
        
        // 3. facciamo l'asserzione cioè verifichiamo che il database accetti l'inserimento senza dare errore sulle foreign key
        assertTrue(risultato, "l'inserimento nel DB con le FK da errore");
    }
}
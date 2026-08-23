package database.test;

import database.UtenteDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*; 

/**
 * Classe di test per validare la correttezza delle query sul database.
 */
public class UtenteDAOTest {
    
// spiegazione: questa classe avvia il test automatico del DAO, inviando finte credenziali e verificando 
// che il sistema chiuda le porte (restituendo null) in caso di errore. 
// Le asserzioni come assertNull verificano istantaneamente che il codice produca l'esito che mi aspetto, 
// rivelando difetti da subito nel codice.  
    @Test
    public void testAutenticazioneFallita() {
        UtenteDAO dao = new UtenteDAO();
        
        // Tentiamo di accedere con credenziali errate
        Utente risultato = dao.autentica("utenteFantasma", "passwordErrata123");
        
        // Utilizziamo un'asserzione JUnit per verificare che il DAO restituisca null
        // segnalando che l'autenticazione è stata bloccata come previsto.
        assertNull(risultato, "L'autenticazione dovrebbe restituire null per credenziali errate.");
    }
    @Test
    public void testRegistrazioneFallitaDatiVuoti() {
        UtenteDAO dao = new UtenteDAO();
        
        // Similiamo un inserimento errato dal DB passando parametri nulli
        boolean risultato = dao.registraUtente(null, null);
        
        // L'inserimento non deve andare a buon fine
        assertFalse(risultato, "La registrazione non dovrebbe avere successo con parametri nulli.");
    }
}
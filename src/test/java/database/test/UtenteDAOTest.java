package database.test;

import database.UtenteDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Utente;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*; 

/**
 testiamo li utenti 
 */
public class UtenteDAOTest {
    
    // spiegazione: allora la classe avvia il test automatico del dao, inviando finte credenziali e verificando 
    // che il sistema restituisce null se da errore. Poi le asserzioni come assertNull verificano che il codice dia l'esito che mi aspetto
    //dove se mi da errore so che ci sono errori da correggere. 
    //non mi dice quali sono queti errori, quindi da qu parte la fase di debugging dove cerco di capire quale è stato l'errore
    @Test
    public void testAutenticazioneFallita() {
        UtenteDAO dao = new UtenteDAO();
        
        // provo ad accedere con delle credenziali sbagliate
        Utente risultato = dao.autentica("utenteFantasma", "passwordErrata123");
        
        // utilizzo un'asserzione di j unit ovvero assert null, per verificare che il DAO restituisca null
        // segnalando che l'autenticazione è stata bloccata come previsto
        assertNull(risultato, "L'autenticazione dovrebbe restituire null per le credenziali errate");
    }
    @Test
    public void testRegistrazioneFallitaDatiVuoti() {
        UtenteDAO dao = new UtenteDAO();
        
        //similiamo un inserimento errato dal database passando parametri nulli
        boolean risultato = dao.registraUtente(null, null);
        
        //quindi so che l'inserimento non deve andare a buon fine
        assertFalse(risultato, "La registrazione non deve avere successo con dei parametri nulli");
    }
}
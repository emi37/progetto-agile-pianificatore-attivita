package it.univaq.disim.agile.progetto.agile.pianificatore.attivita;

import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.dao.UtenteDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model.Utente;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestingLoginUtente {

    @Test
    public void testLoginCorretto() {
        // verifica che l utente admin con credenziali giuste venga trovato
        UtenteDAO dao = new UtenteDAO();
        Utente risultato = dao.verificaLogin("admin", "admin123");
        
        // il test passa se l oggetto ritornato non e nullo
        assertNotNull(risultato);
        assertEquals("admin", risultato.getUsername());
    }

    @Test
    public void testLoginErrato() {
        // verifica che con una password sballata il sistema risponda null
        UtenteDAO dao = new UtenteDAO();
        Utente risultato = dao.verificaLogin("admin", "password_sbagliata_999");
        
        // il test passa se il risultato e null
        assertNull(risultato);
    }
    
    @Test
    public void testUtenteInesistente() {
        // verifica il comportamento con uno username che non sta sul database mysql
        UtenteDAO dao = new UtenteDAO();
        Utente risultato = dao.verificaLogin("user_fantasma_fake", "12345");
        
        assertNull(risultato);
    }
}
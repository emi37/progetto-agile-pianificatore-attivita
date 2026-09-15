package database.test;

import database.CategoriaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Categoria;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**classe di testing per verificare l'inserimento delle nuove categorie
 */
public class CategoriaDAOTest {

    @Test
    public void testInserimentoNuovaCategoria() {
        
        CategoriaDAO dao = new CategoriaDAO();
        
        // 1.scelgo un nome per la categoria finta e un id utente (es. 2)
        String nomeCategoria = "Categoria di Prova";
        int idUtente = 2;
        
        // 2. simulo l'inserimento nel db
        Categoria risultato = dao.inserisciCategoriaCustom(nomeCategoria, idUtente);
        
        // 3. faccio l'asserzione, verifico che succede quello che mi aspetto,cioè che il db abbia accettato l'inserimento restituendomi un oggetto di tipo "categoria" valido(quindi non nullo)
        assertNotNull(risultato, "l'inserimento della nuova categ. ha dato errore");
    }
}   

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database.test;

import database.NotificaDAO;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Attivita;
import it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain.Notifica;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author edoar
 */
public class NotificaDAOtest {

    private NotificaDAO dao;


    @BeforeEach
    public void setUp() {
        dao = new NotificaDAO();
    }

    @Test
    public void testInserisciNotificaSuccesso() {

        Attivita attivitaFinta = new Attivita(1, "Test Notifica", "Descrizione",
                null, null, false, null, null, null);

        Notifica nuovaNotifica = new Notifica(
                "Scadenza imminente: completare i test del DAO entro stasera",
                "DA_LEGGERE",
                LocalDateTime.now(),
                attivitaFinta
        );

        // --- 2. ACT (Esegui) ---
        boolean risultato = dao.inserisciNotifica(nuovaNotifica);

        // --- 3. ASSERT (Verifica) ---
        assertTrue(risultato, "L'inserimento della notifica nel database dovrebbe restituire true");
    }
}

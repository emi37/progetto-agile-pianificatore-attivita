package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.main;

/**
 * classe che avvia l'applicazione javaFX.
 * poiché NON estende javafx.application.Application, aggira i controlli
 *  della java virtual machine, cosi che Maven puo caricare 
 * correttamente le librerie di javaFX.
 */
public class AppLauncher {

    public static void main(String[] args) {
        // Richiama semplicemente il metodo main della nostra vera classe Main
        Main.main(args);
    }
}
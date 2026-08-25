package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain;

/**
 * Classe che rappresenta un utente base dell'app
 */
public class Utente {
    
    private int id; // Aggiunto l'ID dell'utente!
    private String username;
    private String password;
    
    // Costruttore con ID (utile quando lo prelevate dal DB)
    public Utente(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Costruttore originale (senza ID, per retrocompatibilità se serve)
    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
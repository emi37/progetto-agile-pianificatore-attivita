package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain;

/**
 * classe che rappresenta un utente base dell'app */
public class Utente {
    
    private String username;
    private String password;
    
    // costruttore 
    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
    }
//getter e setter
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
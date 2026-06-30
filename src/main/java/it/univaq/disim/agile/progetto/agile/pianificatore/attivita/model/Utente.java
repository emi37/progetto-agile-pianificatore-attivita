package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model;


public class Utente {
    private int idUtente;
    private String username;
    private String password;


    public Utente() {}

    public Utente(int idUtente, String username, String password) {
        this.idUtente = idUtente;
        this.username = username;
        this.password = password;
    }
    public int getIdUtente() { 
        return idUtente; 
    }
    public void setIdUtente(int idUtente) { 
        this.idUtente = idUtente; 
    }

    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPassword() { 
        return password ;
        
    }
    public void setPassword(String password) { 
        this.password = password; 
    }
    
}

            
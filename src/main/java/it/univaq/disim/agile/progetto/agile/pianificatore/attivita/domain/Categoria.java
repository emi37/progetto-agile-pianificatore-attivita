package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain;

/**
 * classe base per le categorie*/

public class Categoria {

    private int id;
    
    private String nomeCategoria;
    
    private int idUtente;

    public Categoria(int id, String nomeCategoria, int idUtente) {
        
        this.id = id;
        
        this.nomeCategoria = nomeCategoria;
        
        this.idUtente = idUtente;
        
    }

    public int getId() {
        
        return id;
        
    }

    public void setId(int id) {
        
        this.id = id;
        
    }

    public String getNomeCategoria() {

        return nomeCategoria;

    }

    public void setNomeCategoria(String nomeCategoria) {

        this.nomeCategoria = nomeCategoria;

    }

    public int getIdUtente() {
        
        return idUtente;
        
    }

    public void setIdUtente(int idUtente) {
        
        this.idUtente = idUtente;
        
    }

}
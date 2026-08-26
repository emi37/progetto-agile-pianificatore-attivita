package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain;

public class Priorita {

    private int id;
    private String livello;

    public Priorita(int id, String livello) {
        this.id = id;
        this.livello = livello;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLivello() {
        return livello;
    }

    public void setLivello(String livello) {
        this.livello = livello;
    }
}
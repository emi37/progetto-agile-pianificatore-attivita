package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model;

public class Priorita {

    private int idPriorita;
    private String livello;

    public Priorita(int idPriorita, String livello) {
        this.idPriorita = idPriorita;
        this.livello = livello;
    }

    public Integer getIdPriorita() {
        return idPriorita;
    }

    public void setIdPriorita(Integer idPriorita) {
        this.idPriorita = idPriorita;
    }

    public String getLivello() {
        return livello;
    }

    public void setLivello(String livello) {
        this.livello = livello;
    }
}

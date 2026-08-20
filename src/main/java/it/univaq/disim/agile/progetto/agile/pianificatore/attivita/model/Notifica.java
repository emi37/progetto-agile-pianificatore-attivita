package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model;

public class Notifica {

    private int idNotifica;
    private String messaggio;
    private String stato;

    public Notifica(int idNotifica, String messaggio, String stato) {
        this.idNotifica = idNotifica;
        this.messaggio = messaggio;
        this.stato = stato;
    }

    public Integer getIdNotifica() {
        return idNotifica;
    }

    public void setIdNotifica(Integer idNotifica) {
        this.idNotifica = idNotifica;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

}

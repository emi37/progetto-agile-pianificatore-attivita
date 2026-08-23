package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain;
 
import java.time.LocalDateTime;
 
public class Notifica {

    private String messaggio;

    private String stato;

    private LocalDateTime dataInvio;
 
    public Notifica(String messaggio, String stato, LocalDateTime dataInvio) {

        this.messaggio = messaggio;

        this.stato = stato;

        this.dataInvio = dataInvio;

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
 
    public LocalDateTime getDataInvio() {

        return dataInvio;

    }
 
    public void setDataInvio(LocalDateTime dataInvio) {

        this.dataInvio = dataInvio;

    }

}
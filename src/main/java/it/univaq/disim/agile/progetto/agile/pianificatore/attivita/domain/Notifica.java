package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain;

import java.time.LocalDateTime;

public class Notifica {

    // Mappatura esatta delle colonne del DB
    private int idNotifica;
    private String messaggio;
    private String stato;
    private LocalDateTime dataInvio;
    
    // Relazione con l'entità principale
    private Attivita attivita;

    // Costruttore completo (solitamente usato in fase di estrazione dati dal DAO)
    public Notifica(int idNotifica, String messaggio, String stato, LocalDateTime dataInvio, Attivita attivita) {
        this.idNotifica = idNotifica;
        this.messaggio = messaggio;
        this.stato = stato;
        this.dataInvio = dataInvio;
        this.attivita = attivita;
    }

    // Costruttore per la creazione di nuove notifiche dall'interfaccia (l'ID lo deciderà MySQL)
    public Notifica(String messaggio, String stato, LocalDateTime dataInvio, Attivita attivita) {
        this.messaggio = messaggio;
        this.stato = stato;
        this.dataInvio = dataInvio;
        this.attivita = attivita;
    }

    // --- Getter e Setter ---

    public int getIdNotifica() {
        return idNotifica;
    }

    public void setIdNotifica(int idNotifica) {
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

    public LocalDateTime getDataInvio() {
        return dataInvio;
    }

    public void setDataInvio(LocalDateTime dataInvio) {
        this.dataInvio = dataInvio;
    }

    public Attivita getAttivita() {
        return attivita;
    }

    public void setAttivita(Attivita attivita) {
        this.attivita = attivita;
    }
}
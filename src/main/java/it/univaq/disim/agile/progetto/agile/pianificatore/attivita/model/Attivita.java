package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model;

import java.time.LocalDate;

public class Attivita {

    private int idAttivita;
    private String descrizione;
    private LocalDate dataScadenza;
    private LocalDate dataCompletamento;

    public Attivita(int idAttivita, String descrizione, LocalDate dataScadenza, LocalDate dataCompletamento) {
        this.idAttivita = idAttivita;
        this.descrizione = descrizione;
        this.dataScadenza = dataScadenza;
        this.dataCompletamento = dataCompletamento;
    }

    public int getIdAttivita() {
        return idAttivita;
    }

    public void setIdAttivita(int idAttivita) {
        this.idAttivita = idAttivita;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public LocalDate getDataCompletamento() {
        return dataCompletamento;
    }

    public void setDataCompletamento(LocalDate dataCompletamento) {
        this.dataCompletamento = dataCompletamento;
    }

}

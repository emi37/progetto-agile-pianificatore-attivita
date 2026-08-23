package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain;
 
import java.time.LocalDate;
 
public class Attivita {

    private String titolo;

    private String descrizione;

    private LocalDate dataScadenza;

    private LocalDate dataCompletamento;

    private boolean completata;
 
    public Attivita(String titolo, String descrizione, LocalDate dataScadenza, LocalDate dataCompletamento, boolean completata) {

        this.titolo = titolo;

        this.descrizione = descrizione;

        this.dataScadenza = dataScadenza;

        this.dataCompletamento = dataCompletamento;

        this.completata = completata;

    }
 
    public String getTitolo() {

        return titolo;

    }
 
    public void setTitolo(String titolo) {

        this.titolo = titolo;

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
 
    public boolean isCompletata() {

        return completata;

    }
 
    public void setCompletata(boolean completata) {

        this.completata = completata;

    }

}
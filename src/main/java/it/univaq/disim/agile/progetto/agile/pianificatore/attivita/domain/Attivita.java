package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.domain;

import java.time.LocalDate;

/**
 * Classe di dominio che rappresenta un'attività (Task) pianificata dall'utente.
 * Aggiornata per mappare fedelmente tutte le Foreign Keys del DB (inclusione di Utente, Categoria e Priorità).
 */
public class Attivita {

    private int id;
    
    private String titolo;
    
    private String descrizione;
    
    private LocalDate dataScadenza;
    
    private LocalDate dataCompletamento;
    
    private boolean completata;
    
    private Utente utente;
    
    private Categoria categoria;
    
    private Priorita priorita;

    /**
     * Costruttore completo per istanziare un'attività.
     * Utilizza il riferimento 'this' per garantire l'Information Hiding.
     */
    public Attivita(int id, String titolo, String descrizione, LocalDate dataScadenza, 
                    LocalDate dataCompletamento, boolean completata, Utente utente, Categoria categoria, Priorita priorita) {
        
        this.id = id;
        
        this.titolo = titolo;
        
        this.descrizione = descrizione;
        
        this.dataScadenza = dataScadenza;
        
        this.dataCompletamento = dataCompletamento;
        
        this.completata = completata;
        
        this.utente = utente;
        
        this.categoria = categoria;
        
        this.priorita = priorita;
        
    }

    // --- Metodi Getter ---

    public int getId() {
        
        return id;
        
    }

    public String getTitolo() {
        
        return titolo;
        
    }

    public String getDescrizione() {
        
        return descrizione;
        
    }

    public LocalDate getDataScadenza() {
        
        return dataScadenza;
        
    }

    public LocalDate getDataCompletamento() {
        
        return dataCompletamento;
        
    }

    public boolean isCompletata() {
        
        return completata;
        
    }
    
    public Utente getUtente() {
        
        return utente;
        
    }

    public Categoria getCategoria() {
        
        return categoria;
        
    }

    public Priorita getPriorita() {
        
        return priorita;
        
    }

    // --- Metodi Setter ---

    public void setId(int id) {
        
        this.id = id;
        
    }

    public void setTitolo(String titolo) {
        
        this.titolo = titolo;
        
    }

    public void setDescrizione(String descrizione) {
        
        this.descrizione = descrizione;
        
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        
        this.dataScadenza = dataScadenza;
        
    }

    public void setDataCompletamento(LocalDate dataCompletamento) {
        
        this.dataCompletamento = dataCompletamento;
        
    }

    public void setCompletata(boolean completata) {
        
        this.completata = completata;
        
    }
    
    public void setUtente(Utente utente) {
        
        this.utente = utente;
        
    }

    public void setCategoria(Categoria categoria) {
        
        this.categoria = categoria;
        
    }

    public void setPriorita(Priorita priorita) {
        
        this.priorita = priorita;
        
    }
}
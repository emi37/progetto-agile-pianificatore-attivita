package it.univaq.disim.agile.progetto.agile.pianificatore.attivita.model;

import java.time.LocalDateTime;

public class Attivita {
    private int idAtt; 
    private String tit; private String desc;
    private LocalDateTime scad; private LocalDateTime comp;
    private int compl; 
    private int idUt; private Integer idCat; private int idPrio;

    public Attivita() {}

    public Attivita(int idAtt, String tit, String desc, LocalDateTime scad, LocalDateTime comp, int compl, int idUt, Integer idCat, int idPrio ) 
    {
        this.idAtt = idAtt; this.tit = tit; 
        this.desc = desc;
        this.scad = scad; 
        this.comp = comp;
        this.compl = compl;
        this.idUt = idUt;
        this.idCat = idCat; 
        this.idPrio = idPrio;
    }


    public int getIdAttivita(){
        return idAtt; 
    }
    public void setIdAttivita(int idAttivita) 
    {
        this.idAtt = idAttivita; 
    
    }
    public String getTitolo() { 
        return tit;
    }
    public void setTitolo(String titolo) { 
        this.tit = titolo; }
    public String getDescrizione() { return desc; 
    }
    public void setDescrizione(String descrizione) { 
        this.desc = descrizione; }
    public LocalDateTime getDataScadenza() { 
        return scad;
    
    }
    public void setDataScadenza(LocalDateTime dataScadenza) { this.scad = dataScadenza; }
    public LocalDateTime getDataCompletamento() { 
        return comp; 
    
    }
    public void setDataCompletamento(LocalDateTime dataCompletamento) { this.comp = dataCompletamento; }
    public int getCompletata() { 
        return compl; 
    }
    public void setCompletata(int completata) { 
        this.compl = completata; 
    
    }
    public int getIdUtente() { 
        return idUt;
    }
    public void setIdUtente(int idUtente) { this.idUt = idUtente; }
    public Integer getIdCategoria() { 
        return idCat; }
    public void setIdCategoria(Integer idCategoria) { this.idCat = idCategoria; }
    public int getIdPriorita() { 
        return idPrio;
    }
    public void setIdPriorita(int idPriorita) {
        this.idPrio = idPriorita;
        }
}
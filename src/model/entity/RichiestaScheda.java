package model.entity;

import java.io.Serializable;

 //Classe Entity che rappresenta il form di richiesta di una nuova scheda.

public class RichiestaScheda implements Serializable {

    private DatiFisici datiFisici;
    private String obiettivo; // Dimagrimento, Aumento massa, Mantenimento
    private int frequenzaSettimanale;
    private String note;

    // Identificativi per collegare la richiesta agli utenti
    private String clienteEmail;
    private String idPersonalTrainer;

    // Costruttore completo
    public RichiestaScheda(DatiFisici datiFisici, String obiettivo,
                           int frequenzaSettimanale, String note,
                           String clienteEmail, String idPersonalTrainer) {
        this.datiFisici = datiFisici;
        this.obiettivo = obiettivo;
        this.frequenzaSettimanale = frequenzaSettimanale;
        this.note = note;
        this.clienteEmail = clienteEmail;
        this.idPersonalTrainer = idPersonalTrainer;
    }

    public String getSesso() { return datiFisici.getSesso(); }

    public int getEta() { return datiFisici.getEta(); }

    public double getPeso() { return datiFisici.getPeso(); }

    public String getObiettivo() { return obiettivo; }
    public void setObiettivo(String obiettivo) { this.obiettivo = obiettivo; }

    public int getFrequenzaSettimanale() { return frequenzaSettimanale; }
    public void setFrequenzaSettimanale(int frequenza) { this.frequenzaSettimanale = frequenza; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getClienteEmail() { return clienteEmail; }
    public String getIdPersonalTrainer() { return idPersonalTrainer; }

    @Override
    public String toString() {
        // Usiamo il getter getPeso() sull'istanza datiFisici
        return String.format("Richiesta[Email=%s, Obiettivo=%s, Peso=%.2f, Frequenza=%d]",
                clienteEmail,
                obiettivo,
                datiFisici.getPeso(),
                frequenzaSettimanale);
    }
}
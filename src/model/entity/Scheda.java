package model.entity;

import java.io.Serializable;
import java.util.List;

public class Scheda implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String emailCliente;
    private final String emailPT;
    private final List<Esercizio> esercizi;

    public Scheda(String emailCliente, String emailPT, List<Esercizio> esercizi) {
        this.emailCliente = emailCliente;
        this.emailPT = emailPT;
        this.esercizi = esercizi;
    }

    public String getEmailCliente() { return emailCliente; }
    public String getEmailPT() { return emailPT; }
    public List<Esercizio> getEsercizi() { return esercizi; }
}

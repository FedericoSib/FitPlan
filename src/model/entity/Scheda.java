package model.entity;

import java.io.Serializable;
import java.util.List;

public class Scheda implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String emailCliente;
    private final String emailPT;
    private final List<GiornoScheda> giorni;

    public Scheda(String emailCliente, String emailPT, List<GiornoScheda> giorni) {
        this.emailCliente = emailCliente;
        this.emailPT = emailPT;
        this.giorni = giorni;
    }

    public String getEmailCliente() { return emailCliente; }
    public String getEmailPT() { return emailPT; }
    public List<GiornoScheda> getGiorni() { return giorni; }
}

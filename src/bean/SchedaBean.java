package bean;

import java.util.ArrayList;
import java.util.List;

public class SchedaBean {
    private String emailCliente;
    private String emailPT;
    private List<EsercizioBean> esercizi = new ArrayList<>();

    public SchedaBean() {
        //evitiamo il costruttore di default
    }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getEmailPT() { return emailPT; }
    public void setEmailPT(String emailPT) { this.emailPT = emailPT; }

    public List<EsercizioBean> getEsercizi() { return esercizi; }
    public void setEsercizi(List<EsercizioBean> esercizi) { this.esercizi = esercizi; }

    public void aggiungiEsercizio(EsercizioBean e) { this.esercizi.add(e); }
}

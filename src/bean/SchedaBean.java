package bean;

import java.util.ArrayList;
import java.util.List;

public class SchedaBean {
    private String emailCliente;
    private String emailPT;
    private List<GiornoSchedaBean> giorni = new ArrayList<>();

    public SchedaBean() {
        // Costruttore vuoto obbligatorio per i Java Bean
    }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getEmailPT() { return emailPT; }
    public void setEmailPT(String emailPT) { this.emailPT = emailPT; }

    public List<GiornoSchedaBean> getGiorni() { return giorni; }
    public void setGiorni(List<GiornoSchedaBean> giorni) { this.giorni = giorni; }
}

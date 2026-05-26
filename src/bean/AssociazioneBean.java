package bean;

public class AssociazioneBean {
    private String emailCliente;
    private String emailPT;
    private String stato; // Usiamo String per semplicità nel Bean, il Controller la convertirà in Enum

    public AssociazioneBean() {
        // Costruttore vuoto obbligatorio per i Java Bean
    }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getEmailPT() { return emailPT; }
    public void setEmailPT(String emailPT) { this.emailPT = emailPT; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
}

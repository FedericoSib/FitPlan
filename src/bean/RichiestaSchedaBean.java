package bean;

public class RichiestaSchedaBean {
    private String clienteEmail;
    private String idPersonalTrainer;
    private String obiettivo;
    private int frequenzaSettimanale;
    private String note;

    // Dati estratti da DatiFisici per non passare l'Entity
    private String sesso;
    private int eta;
    private double peso;

    public RichiestaSchedaBean() {
        //evitiamo il costruttore di default
    }

    // Getters e Setters
    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }

    public String getIdPersonalTrainer() { return idPersonalTrainer; }
    public void setIdPersonalTrainer(String idPersonalTrainer) { this.idPersonalTrainer = idPersonalTrainer; }

    public String getObiettivo() { return obiettivo; }
    public void setObiettivo(String obiettivo) { this.obiettivo = obiettivo; }

    public int getFrequenzaSettimanale() { return frequenzaSettimanale; }
    public void setFrequenzaSettimanale(int frequenzaSettimanale) { this.frequenzaSettimanale = frequenzaSettimanale; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getSesso() { return sesso; }
    public void setSesso(String sesso) { this.sesso = sesso; }

    public int getEta() { return eta; }
    public void setEta(int eta) { this.eta = eta; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
}

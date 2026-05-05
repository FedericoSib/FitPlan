package bean;

public class ProgressiBean {
    private String emailCliente;
    private String nomeEsercizio;
    private double carico;
    private int ripetizioni;
    private String note;
    private String data; // stringa formattata per la UI

    public ProgressiBean() {}

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getNomeEsercizio() { return nomeEsercizio; }
    public void setNomeEsercizio(String nomeEsercizio) { this.nomeEsercizio = nomeEsercizio; }

    public double getCarico() { return carico; }
    public void setCarico(double carico) { this.carico = carico; }

    public int getRipetizioni() { return ripetizioni; }
    public void setRipetizioni(int ripetizioni) { this.ripetizioni = ripetizioni; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    @Override
    public String toString() {
        return String.format("[%s] %s — %.1f kg x %d rep%s",
                data, nomeEsercizio, carico, ripetizioni,
                (note == null || note.isBlank()) ? "" : " | " + note);
    }
}

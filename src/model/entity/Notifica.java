package model.entity;

public class Notifica {
    private final String emailDestinatario;
    private final String testo;

    public Notifica(String emailDestinatario, String testo) {
        this.emailDestinatario = emailDestinatario;
        this.testo = testo;
    }

    public String getEmailDestinatario() { return emailDestinatario; }
    public String getTesto() { return testo; }
}

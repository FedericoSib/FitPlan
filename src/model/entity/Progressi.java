package model.entity;

import java.io.Serializable;
import java.time.LocalDate;

public class Progressi implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String emailCliente;
    private final LocalDate data;
    private final String nomeEsercizio;
    private final double carico;
    private final int ripetizioni;
    private final String note;

    public Progressi(String emailCliente, String nomeEsercizio,
                     double carico, int ripetizioni, String note) {
        this.emailCliente = emailCliente;
        this.data = LocalDate.now();
        this.nomeEsercizio = nomeEsercizio;
        this.carico = carico;
        this.ripetizioni = ripetizioni;
        this.note = note;
    }

    public String getEmailCliente() { return emailCliente; }
    public LocalDate getData() { return data; }
    public String getNomeEsercizio() { return nomeEsercizio; }
    public double getCarico() { return carico; }
    public int getRipetizioni() { return ripetizioni; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        return String.format("[%s] %s — %.1f kg x %d rep%s",
                data, nomeEsercizio, carico, ripetizioni,
                note.isBlank() ? "" : " | " + note);
    }
}

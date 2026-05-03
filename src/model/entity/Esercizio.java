package model.entity;

import java.io.Serializable;

public class Esercizio implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome;
    private final int serie;
    private final int ripetizioni;
    private final int recuperoSecondi;
    private final String note;

    public Esercizio(String nome, int serie, int ripetizioni, int recuperoSecondi, String note) {
        this.nome = nome;
        this.serie = serie;
        this.ripetizioni = ripetizioni;
        this.recuperoSecondi = recuperoSecondi;
        this.note = note;
    }

    public String getNome() { return nome; }
    public int getSerie() { return serie; }
    public int getRipetizioni() { return ripetizioni; }
    public int getRecuperoSecondi() { return recuperoSecondi; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        return String.format("%s — %dx%d (rec: %ds) %s",
                nome, serie, ripetizioni, recuperoSecondi,
                note.isBlank() ? "" : "| " + note);
    }
}


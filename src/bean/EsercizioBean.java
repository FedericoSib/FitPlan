package bean;

public class EsercizioBean {
    private String nome;
    private int serie;
    private int ripetizioni;
    private int recuperoSecondi;
    private String note;

    public EsercizioBean() {
        //evitiamo il costruttore di default

    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getSerie() { return serie; }
    public void setSerie(int serie) { this.serie = serie; }

    public int getRipetizioni() { return ripetizioni; }
    public void setRipetizioni(int ripetizioni) { this.ripetizioni = ripetizioni; }

    public int getRecuperoSecondi() { return recuperoSecondi; }
    public void setRecuperoSecondi(int recuperoSecondi) { this.recuperoSecondi = recuperoSecondi; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return String.format("%s — %dx%d (rec: %ds)", nome, serie, ripetizioni, recuperoSecondi);
    }
}

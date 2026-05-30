package bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PersonalTrainerBean implements Serializable {
    private String id;
    private String nome;
    private String cognome;
    private String email;
    private String nomeCompleto;

    private int richiesteAssociazionePending;
    private List<String> ultimeRichiesteAssociazione = new ArrayList<>();
    private int richiesteSchedePending;
    private int schedeInLavorazione;

    public PersonalTrainerBean() {
        // Costruttore vuoto obbligatorio
    }

    public PersonalTrainerBean(String id, String nome, String cognome, String email) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.nomeCompleto = nome + " " + cognome;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public int getRichiesteAssociazionePending() { return richiesteAssociazionePending; }
    public void setRichiesteAssociazionePending(int richiesteAssociazionePending) {
        this.richiesteAssociazionePending = richiesteAssociazionePending;
    }

    public List<String> getUltimeRichiesteAssociazione() { return ultimeRichiesteAssociazione; }
    public void setUltimeRichiesteAssociazione(List<String> ultimeRichiesteAssociazione) {
        this.ultimeRichiesteAssociazione = ultimeRichiesteAssociazione;
    }

    public int getRichiesteSchedePending() { return richiesteSchedePending; }
    public void setRichiesteSchedePending(int richiesteSchedePending) {
        this.richiesteSchedePending = richiesteSchedePending;
    }

    public int getSchedeInLavorazione() { return schedeInLavorazione; }
    public void setSchedeInLavorazione(int schedeInLavorazione) {
        this.schedeInLavorazione = schedeInLavorazione;
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " (" + id + ")";
    }
}

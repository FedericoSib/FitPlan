package bean;

public class UtenteBean {
    private String email;
    private String password;
    private String nome;
    private String cognome;
    private String id;
    private int ruolo;

    public UtenteBean() {
        // Costruttore vuoto obbligatorio per i Java Bean
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getRuolo() { return ruolo; }
    public void setRuolo(int ruolo) { this.ruolo = ruolo; }
}
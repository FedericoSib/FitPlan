package bean;

import model.exception.RegistrazioneException;

public class RegistrazioneBean {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private int ruolo;

    public RegistrazioneBean() {
        //evitiamo il costruttore di default
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = (email != null) ? email.toLowerCase().trim() : null;
    }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getRuolo() { return ruolo; }
    public void setRuolo(int ruolo) { this.ruolo = ruolo; }
    // Validazione sintattica di base
    public void valida() throws RegistrazioneException {
        if (nome == null || nome.isEmpty() || cognome == null || cognome.isEmpty() ||
                email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new RegistrazioneException("Tutti i campi sono obbligatori.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new RegistrazioneException("Formato email non valido.");
        }
    }
}
package bean;

import java.io.Serializable;

/**
 * Bean per il trasporto dei dati del Personal Trainer verso la UI.
 * Implementa Serializable per permettere l'eventuale passaggio tra scene.
 */
public class PersonalTrainerBean implements Serializable {
    private String id;
    private String nome;
    private String cognome;
    private String email;

    // Costruttore vuoto obbligatorio per i Java Bean
    public PersonalTrainerBean() {}

    // Costruttore di comodità per il Controller logico
    public PersonalTrainerBean(String id, String nome, String cognome, String email) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * Utile se vuoi mostrare il nome completo direttamente in una ListView
     * senza dover definire una CellFactory complessa.
     */
    @Override
    public String toString() {
        return nome + " " + cognome + " (" + id + ")";
    }
}

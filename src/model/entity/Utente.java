package model.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Utente {
    protected String id; // Es: C-MR-10031425 (Cliente, Mario Rossi, 10 Marzo ore 14:25)
    protected String email;
    protected String password;
    protected String nome;
    protected String cognome;
    protected int ruolo; // 1: Cliente, 2: PT

    protected Utente(String nome, String cognome, String email, String password, int ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.id = generaID(); // L'ID viene generato al momento della creazione dell'oggetto
    }

    protected Utente(String id, String nome, String cognome,
                     String email, String password, int ruolo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
    }

    private String generaID() {
        // Prefisso in base al ruolo
        String prefisso = (ruolo == 1) ? "C" : "PT";

        // Iniziali (prendiamo il primo carattere di nome e cognome, in maiuscolo)
        String iniziali = (nome.substring(0, 1) + cognome.substring(0, 1)).toUpperCase();

        // Timestamp: GiornoMeseOreMinuti (Esempio: 10031425)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMHHmm");
        String timestamp = LocalDateTime.now().format(formatter);

        return prefisso + "-" + iniziali + "-" + timestamp;
    }

    // Getter per l'ID (senza setter, l'ID non deve cambiare!)

    public String getNome() {return nome ;}
    public String getCognome() {return cognome; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public int getRuolo() { return ruolo; }
}
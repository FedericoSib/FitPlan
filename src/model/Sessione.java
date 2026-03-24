package model;

import model.entity.Utente;

public class Sessione {
    private static Sessione instance = null;
    private Utente utenteLoggato;

    private Sessione() {}

    public static Sessione getInstance() {
        if (instance == null) {
            instance = new Sessione();
        }
        return instance;
    }

    public void setUtente(Utente u) { this.utenteLoggato = u; }
    public Utente getUtente() { return utenteLoggato; }
}
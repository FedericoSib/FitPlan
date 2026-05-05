package model;

import model.entity.Utente;

public class Sessione {

    private Utente utenteLoggato;

    private Sessione() {}

    private static class SessioneHolder {
        private static final Sessione INSTANCE = new Sessione();
    }

    public static Sessione getInstance() {
        return SessioneHolder.INSTANCE;
    }

    public void setUtente(Utente u) { this.utenteLoggato = u; }
    public Utente getUtente() { return utenteLoggato; }
}
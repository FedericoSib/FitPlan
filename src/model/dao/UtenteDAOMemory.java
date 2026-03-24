package model.dao;

import model.entity.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAOMemory implements UtenteDAO {
    // Lista statica per simulare il database
    private static final List<Utente> utenti = new ArrayList<>();

    public UtenteDAOMemory() {
        // Possiamo aggiungere un utente "pre-caricato" per testare il login subito
        if (utenti.isEmpty()) {
            utenti.add(new Cliente("Mario", "Rossi", "mario@test.it", "pass123"));
            utenti.add(new PersonalTrainer("Coach", "Zanna", "coach@test.it", "pass123", "Bodybuilding"));
        }
    }

    @Override
    public void salvaNuovoUtente(Utente u) {
        utenti.add(u);
        System.out.println("[MEMORY] Utente salvato: " + u.getEmail());
    }

    @Override
    public Utente trovaUtente(String email, String password) {
        for (Utente u : utenti) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public Utente trovaUtentePerEmail(String email) {
        for (Utente u : utenti) {
            if (u.getEmail().equals(email)) {
                return u;
            }
        }
        return null;
    }
}

package model.dao;

import model.entity.Utente;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.exception.DAOException;
import model.exception.UserNotFoundException;
import util.LogManager;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAOMemory implements UtenteDAO {
    private static final List<Utente> utenti = new ArrayList<>();

    public UtenteDAOMemory() {
        if (utenti.isEmpty()) {
            utenti.add(new Cliente("C-MR-DEMO01","Mario", "Rossi", "mario@test.it", "pass123"));
            PersonalTrainer coachZanna = new PersonalTrainer("PT-CZ-DEMO01","Coach", "Zanna", "coach@test.it", "pass123");
            utenti.add(coachZanna);
            PersonalTrainerDAOMemory.aggiungiPT(coachZanna);
        }
    }

    @Override
    public void salvaNuovoUtente(Utente u) throws DAOException {
        if (u == null) {
            throw new DAOException("Impossibile salvare un utente nullo.");
        }
        utenti.add(u);
        LogManager.info("Utente salvato: "+ u.getEmail());

        if (u instanceof PersonalTrainer pt) {
            PersonalTrainerDAOMemory.aggiungiPT(pt);
        }
    }

    @Override
    public Utente trovaUtente(String email, String password) throws UserNotFoundException {
        for (Utente u : utenti) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        throw new UserNotFoundException("Credenziali errate per l'utente: " + email);
    }

    @Override
    public Utente trovaUtentePerEmail(String email) throws UserNotFoundException {
        for (Utente u : utenti) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        throw new UserNotFoundException("Nessun utente trovato con email: " + email);
    }
}

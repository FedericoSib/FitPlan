package controller.graphic;

import util.LogManager;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.entity.Utente;
import model.dao.DAOFactory;
import model.dao.UtenteDAO;
import model.exception.RegistrazioneException;

public class RegistrazioneController {
    public void registraNuovoUtente(String nome, String cognome, String email, String pass, int ruolo)
            throws RegistrazioneException {

        Utente nuovoUtente;

        // Creazione dell'istanza specifica
        if (ruolo == 1) {
            if (nome == null || nome.isEmpty() || cognome == null || cognome.isEmpty()) {
                throw new RegistrazioneException("Dati incompleti: nome e cognome sono obbligatori.");
            }
            nuovoUtente = new Cliente(nome, cognome, email, pass);
        } else {
            if (nome == null || nome.isEmpty() || cognome == null || cognome.isEmpty()) {
                throw new RegistrazioneException("Dati incompleti: nome e cognome sono obbligatori.");
            }
            nuovoUtente = new PersonalTrainer(nome, cognome, email, pass);
        }

        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();

            try {
                // Proviamo a cercare l'utente
                dao.trovaUtentePerEmail(email);

                // Se arriviamo qui, significa che l'utente ESISTE (non è stata lanciata l'eccezione)
                LogManager.warn("Tentativo di registrazione con email duplicata: " + email);
                throw new RegistrazioneException("Email già registrata.");

            } catch (model.exception.UserNotFoundException _) {
                // SCENARIO CORRETTO: L'utente non esiste, quindi possiamo salvarlo
                dao.salvaNuovoUtente(nuovoUtente);
                LogManager.info("Registrazione completata. ID: " + nuovoUtente.getId());
            }

        } catch (model.exception.DAOException e) {
            // SCENARIO ERRORE TECNICO: Problemi al database o al file
            LogManager.error("Errore persistenza registrazione", e);
            throw new RegistrazioneException("Errore tecnico durante il salvataggio. Riprova più tardi.");
        }
    }
}
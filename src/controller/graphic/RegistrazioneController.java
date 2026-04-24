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
            nuovoUtente = new Cliente(nome, cognome, email, pass);
        } else {
            nuovoUtente = new PersonalTrainer(nome, cognome, email, pass);
        }

        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();

            // Controllo se l'email esiste già
            if (dao.trovaUtentePerEmail(email) != null) {
                LogManager.warn("Tentativo di registrazione con email duplicata: " + email);
                throw new RegistrazioneException("Email già registrata.");
            }
            dao.salvaNuovoUtente(nuovoUtente);
            LogManager.info("Registrazione completata con successo. Creato utente con ID: " + nuovoUtente.getId());
        } catch (Exception e) {
            LogManager.error("Errore critico durante la registrazione", e);
            throw new RegistrazioneException("Errore interno del sistema. Riprova più tardi.");
        }
    }
}
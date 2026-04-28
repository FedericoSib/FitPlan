package controller.graphic;

import model.Sessione;
import model.dao.UtenteDAO;
import model.dao.DAOFactory;
import model.entity.Utente;
import model.exception.UserNotFoundException;
import model.exception.LoginException;
import model.exception.DAOException;
import util.LogManager;

public class LoginController {

    public void autentica(String email, String password) throws LoginException {
        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();
            Utente utente = dao.trovaUtente(email, password);

            Sessione.getInstance().setUtente(utente);
            LogManager.info("Login effettuato con successo per: " + email);

        } catch (UserNotFoundException e) {
            LogManager.warn("Autenticazione fallita: " + e.getMessage());
            throw new LoginException("Credenziali non valide. Riprova.");

        } catch (DAOException e) {
            LogManager.error("Errore persistenza durante login", e);
            throw new LoginException("Servizio momentaneamente non disponibile.");
        }
    }
}

package controller.graphic;

import model.Sessione;
import model.dao.UtenteDAO;
import model.dao.DAOFactory;
import model.entity.Utente;
import model.exception.LoginException;
import util.LogManager;

public class LoginController {

    public void autentica(String email, String password) throws LoginException {
        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();
            Utente utente = dao.trovaUtente(email, password);

            if (utente == null) {
                LogManager.warn("Tentativo di accesso con email o password errata");
                throw new LoginException("Email o Password errati. Riprova.");
            }

            Sessione.getInstance().setUtente(utente);
        } catch (Exception e) {
            LogManager.error("Errore critico durante l'accesso", e);
            throw new LoginException("Errore tecnico durante l'accesso: " + e.getMessage());
        }
    }
}

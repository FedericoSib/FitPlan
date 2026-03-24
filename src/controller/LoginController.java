package controller;

import model.Sessione;
import model.dao.UtenteDAO;
import model.dao.DAOFactory;
import model.entity.Utente;
import model.entity.Cliente;
import model.exception.LoginException;

public class LoginController {

    public void autentica(String email, String password) throws LoginException {
        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();
            Utente utente = dao.trovaUtente(email, password);

            if (utente == null) {
                throw new LoginException("Email o Password errati. Riprova.");
            }

            Sessione.getInstance().setUtente(utente);
        } catch (Exception e) {
            throw new LoginException("Errore tecnico durante l'accesso: " + e.getMessage());
        }
    }
}

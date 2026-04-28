package controller.graphic;

import model.Sessione;
import model.dao.*;
import model.entity.*;
import model.exception.UserNotFoundException;
import model.exception.LoginException;
import model.exception.DAOException;
import util.LogManager;

public class LoginController {

    public void autentica(String email, String password) throws LoginException {
        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();
            Utente utente = dao.trovaUtente(email, password);

            if (utente instanceof Cliente cliente) {
                AssociazioneDAO associaDAO = DAOFactory.getAssociazioneDAO();

                // Recuperiamo lo stato dal file/memoria delle associazioni
                StatoAssociazione stato = associaDAO.getStato(cliente.getEmail());
                cliente.setStatoAssociazione(stato);

                // Se c'è una richiesta (pending o associato), carichiamo anche l'email del PT
                if (stato != StatoAssociazione.NESSUNA) {
                    String emailPT = associaDAO.getEmailPTAssociato(cliente.getEmail());
                    cliente.setIdPersonalTrainer(emailPT);
                }
            }

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

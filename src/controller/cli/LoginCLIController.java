package controller.cli;

import bean.LoginBean;
import model.Sessione;
import model.dao.AssociazioneDAO;
import model.dao.DAOFactory;
import model.entity.Cliente;
import model.entity.StatoAssociazione;
import model.entity.Utente;
import model.exception.DAOException;
import model.exception.LoginException;
import model.exception.UserNotFoundException;
import util.LogManager;

public class LoginCLIController {

    public void autentica(LoginBean loginBean) throws LoginException {
        if (loginBean.getEmail() == null || loginBean.getEmail().isEmpty()) {
            throw new LoginException("Email obbligatoria.");
        }

        try {
            var dao = DAOFactory.getUtenteDAO();
            Utente utente = dao.trovaUtente(loginBean.getEmail(), loginBean.getPassword());

            if (utente instanceof Cliente cliente) {
                AssociazioneDAO associaDAO = DAOFactory.getAssociazioneDAO();
                StatoAssociazione stato = associaDAO.getStato(cliente.getEmail());
                cliente.setStatoAssociazione(stato);

                if (stato != StatoAssociazione.NESSUNA) {
                    String emailPT = associaDAO.getEmailPTAssociato(cliente.getEmail());
                    cliente.setIdPersonalTrainer(emailPT);
                }
            }

            Sessione.getInstance().setUtente(utente);
            LogManager.info("[CLI] Login effettuato per: " + loginBean.getEmail());

        } catch (UserNotFoundException _) {
            throw new LoginException("Credenziali non valide.");
        } catch (DAOException e) {
            throw new LoginException("Errore di sistema: " + e.getMessage());
        }
    }
}

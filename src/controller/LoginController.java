package controller;

import model.Sessione;
import bean.*;
import model.dao.*;
import model.entity.*;
import model.exception.UserNotFoundException;
import model.exception.LoginException;
import model.exception.DAOException;
import util.LogManager;

import java.util.List;

public class LoginController {

    public void autentica(LoginBean loginBean) throws LoginException {
        if (loginBean.getEmail() == null || loginBean.getEmail().isEmpty()) {
            throw new LoginException("Email obbligatoria.");
        }

        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();
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
            if (Sessione.getInstance().getUtente() instanceof Cliente clienteInSessione) {
                sincronizzaStatoRichiesta(clienteInSessione);
            }
            LogManager.info("Login effettuato con successo per: " + loginBean.getEmail());

        } catch (UserNotFoundException _) {
            throw new LoginException("Credenziali non valide.");
        } catch (DAOException e) {
            throw new LoginException("Errore di sistema: " + e.getMessage());
        }
    }

    private void sincronizzaStatoRichiesta(Cliente cliente) {
        try {
            if (!DAOFactory.getSchedaDAO()
                    .getSchedePerCliente(cliente.getEmail()).isEmpty()) {
                ((Cliente) Sessione.getInstance().getUtente())
                        .setStatoRichiesta(StatoRichiesta.COMPLETATA);
                return;
            }
            var tutteLeRichieste = DAOFactory.getRichiestaDAO()
                    .prendiTutteLeRichieste();

            LogManager.info("Richieste trovate al login: " + tutteLeRichieste.size());

            tutteLeRichieste.stream()
                    .filter(r -> r.getClienteEmail()
                            .equalsIgnoreCase(cliente.getEmail()))
                    .findFirst()
                    .ifPresent(r -> {
                        LogManager.info("Stato trovato: " + r.getStato());
                        Cliente clienteInSessione =
                                (Cliente) Sessione.getInstance().getUtente();
                        clienteInSessione.setStatoRichiesta(r.getStato());
                        LogManager.info("Stato dopo set: " +
                                clienteInSessione.getStatoRichiesta());
                    });

        } catch (Exception e) {
            LogManager.error("Errore sincronizzazione stato richiesta al login", e);
        }
    }

    public List<String> getNotifichePendenti() {
        util.observer.NotificaManager manager = new util.observer.NotificaManager(model.dao.DAOFactory.getNotificaDAO());
        return manager.ottieniMessaggiPendenti(this.getUtenteLoggato().getEmail());
    }

    public bean.UtenteBean getUtenteLoggato() {
        model.entity.Utente u = model.Sessione.getInstance().getUtente();
        if (u == null) return null;

        bean.UtenteBean utenteBean = new bean.UtenteBean();
        utenteBean.setNome(u.getNome());
        utenteBean.setCognome(u.getCognome());
        utenteBean.setEmail(u.getEmail());
        utenteBean.setId(u.getId());
        utenteBean.setRuolo(u.getRuolo());

        return utenteBean;
    }
}

package controller.cli;

import bean.RegistrazioneBean;
import model.dao.DAOFactory;
import model.dao.UtenteDAO;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.entity.Utente;
import model.exception.DAOException;
import model.exception.RegistrazioneException;
import model.exception.UserNotFoundException;
import util.LogManager;

public class RegistrazioneCLIController {

    public void registraNuovoUtente(RegistrazioneBean bean) throws RegistrazioneException {
        bean.valida();

        Utente nuovoUtente;
        if (bean.getRuolo() == 1) {
            nuovoUtente = new Cliente(bean.getNome(), bean.getCognome(),
                    bean.getEmail(), bean.getPassword());
        } else {
            nuovoUtente = new PersonalTrainer(bean.getNome(), bean.getCognome(),
                    bean.getEmail(), bean.getPassword());
        }

        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();
            verificaEmailNonEsistente(dao, bean.getEmail());
            dao.salvaNuovoUtente(nuovoUtente);
            LogManager.info("[CLI] Registrazione completata per: " + nuovoUtente.getEmail());

        } catch (DAOException _) {
            throw new RegistrazioneException("Errore tecnico durante il salvataggio.");
        }
    }

    private void verificaEmailNonEsistente(UtenteDAO dao, String email)
            throws RegistrazioneException, DAOException {
        try {
            dao.trovaUtentePerEmail(email);
            throw new RegistrazioneException("L'email inserita è già associata a un account.");
        } catch (UserNotFoundException _) {
            LogManager.info("[CLI] Email disponibile: " + email);
        }
    }
}

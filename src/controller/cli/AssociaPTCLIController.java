package controller.cli;

import bean.AssociazioneBean;
import bean.PersonalTrainerBean;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import model.exception.TrainerNotFoundException;
import util.LogManager;
import util.observer.NotificaObservableBase;

import java.util.ArrayList;
import java.util.List;

public class AssociaPTCLIController extends NotificaObservableBase {

    public List<PersonalTrainerBean> cercaTrainer(String ricerca) throws TrainerNotFoundException {
        var dao = DAOFactory.getPersonalTrainerDAO();
        List<PersonalTrainer> entitaTrovate = new ArrayList<>();

        if (ricerca.toUpperCase().startsWith("PT-")) {
            PersonalTrainer pt = dao.getPTById(ricerca);
            if (pt != null) entitaTrovate.add(pt);
        } else if (ricerca.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            PersonalTrainer pt = dao.getPTByEmail(ricerca.toLowerCase().trim());
            if (pt != null) entitaTrovate.add(pt);
        } else {
            List<PersonalTrainer> trovati = dao.getPTByName(ricerca);
            if (trovati != null) entitaTrovate.addAll(trovati);
        }

        if (entitaTrovate.isEmpty()) {
            throw new TrainerNotFoundException("Nessun Personal Trainer trovato per: " + ricerca);
        }

        List<PersonalTrainerBean> risultati = new ArrayList<>();
        for (PersonalTrainer pt : entitaTrovate) {
            PersonalTrainerBean bean = new PersonalTrainerBean();
            bean.setNome(pt.getNome());
            bean.setCognome(pt.getCognome());
            bean.setEmail(pt.getEmail());
            bean.setId(pt.getId());
            risultati.add(bean);
        }
        return risultati;
    }

    public void inviaRichiestaAssociazione(AssociazioneBean bean) throws DAOException {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();
        var dao = DAOFactory.getAssociazioneDAO();
        dao.salvaRichiesta(bean.getEmailCliente(), bean.getEmailPT());

        cliente.setStatoAssociazione(StatoAssociazione.PENDING);
        cliente.setIdPersonalTrainer(bean.getEmailPT());

        notificaObserver(bean.getEmailPT(),
                "Il cliente " + bean.getEmailCliente() + " ha richiesto l'associazione.");

        LogManager.info("[CLI] Richiesta associazione inviata da: " + bean.getEmailCliente());
    }
}

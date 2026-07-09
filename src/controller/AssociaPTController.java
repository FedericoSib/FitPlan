package controller;

import bean.*;
import model.dao.*;
import model.entity.*;
import model.Sessione;
import model.exception.*;
import util.observer.*;
import util.LogManager;
import java.util.List;
import java.util.ArrayList;


public class AssociaPTController extends NotificaObservableBase{

    public List<PersonalTrainerBean> cercaTrainer(String ricerca) throws TrainerNotFoundException {
        PersonalTrainerDAO dao = DAOFactory.getPersonalTrainerDAO();
        List<PersonalTrainer> entitaTrovate = new ArrayList<>();

        if (ricerca.toUpperCase().startsWith("PT-")) {
            PersonalTrainer pT = dao.getPTById(ricerca);
            if (pT != null) entitaTrovate.add(pT);
        } else if (ricerca.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            PersonalTrainer pT = dao.getPTByEmail(ricerca.toLowerCase().trim());
            if (pT != null) entitaTrovate.add(pT);
        } else {
            List<PersonalTrainer> trovatiPerNome = dao.getPTByName(ricerca);
            if (trovatiPerNome != null) entitaTrovate.addAll(trovatiPerNome);
        }

        if (entitaTrovate.isEmpty()) {
            throw new TrainerNotFoundException("Nessun Personal Trainer trovato per: " + ricerca);
        }

        List<PersonalTrainerBean> risultatiBean = new ArrayList<>();
        for (PersonalTrainer pt : entitaTrovate) {
            PersonalTrainerBean bean = new PersonalTrainerBean();
            bean.setNome(pt.getNome());
            bean.setCognome(pt.getCognome());
            bean.setEmail(pt.getEmail());
            bean.setId(pt.getId());
            risultatiBean.add(bean);
        }

        return risultatiBean;
    }

    public void inviaRichiestaAssociazione(AssociazioneBean bean) throws DAOException {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();
        AssociazioneDAO dao = DAOFactory.getAssociazioneDAO();
        dao.salvaRichiesta(bean.getEmailCliente(), bean.getEmailPT());

        cliente.setStatoAssociazione(StatoAssociazione.PENDING);
        cliente.setIdPersonalTrainer(bean.getEmailPT());

        notificaObserver(bean.getEmailPT(),
                "Il cliente " + bean.getEmailCliente() + " ha richiesto l'associazione.");

        LogManager.info("Stato aggiornato in sessione a PENDING per: " + bean.getEmailCliente());
    }

    public void configuraObserverNotifiche() {
        util.observer.NotificaManager manager = new util.observer.NotificaManager(model.dao.DAOFactory.getNotificaDAO());
        this.aggiungiObserver(manager);
    }

    public ClienteBean getClienteCorrente() {
        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        ClienteBean bean = new ClienteBean();
        bean.setEmail(c.getEmail());
        return bean;
    }
}

package controller.cli;

import bean.AssociazioneBean;
import model.dao.DAOFactory;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import util.observer.NotificaObservableBase;

import java.util.ArrayList;
import java.util.List;

public class GestisciRichiestePTCLIController extends NotificaObservableBase {

    public List<AssociazioneBean> getRichiesteSospese(String emailPT) throws DAOException {
        var dao = DAOFactory.getAssociazioneDAO();
        List<String> clienti = dao.getRichiestePerPT(emailPT);

        List<AssociazioneBean> beanList = new ArrayList<>();
        for (String emailCliente : clienti) {
            AssociazioneBean bean = new AssociazioneBean();
            bean.setEmailCliente(emailCliente);
            bean.setEmailPT(emailPT);
            bean.setStato(StatoAssociazione.PENDING.name());
            beanList.add(bean);
        }
        return beanList;
    }

    public void accettaAssociazione(AssociazioneBean bean) throws DAOException {
        DAOFactory.getAssociazioneDAO().aggiornaStato(
                bean.getEmailCliente(), StatoAssociazione.ASSOCIATO);

        notificaObserver(bean.getEmailCliente(),
                "Il tuo Personal Trainer ha accettato la tua richiesta di associazione.");
    }

    public void rifiutaAssociazione(AssociazioneBean bean) throws DAOException {
        DAOFactory.getAssociazioneDAO().aggiornaStato(
                bean.getEmailCliente(), StatoAssociazione.NESSUNA);

        notificaObserver(bean.getEmailCliente(),
                "Il tuo Personal Trainer ha rifiutato la tua richiesta di associazione.");
    }
}

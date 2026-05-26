package controller;

import bean.AssociazioneBean;
import model.entity.Notifica;
import model.dao.AssociazioneDAO;
import model.dao.DAOFactory;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import util.observer.*;
import java.util.ArrayList;
import java.util.List;

public class GestisciRichiestePTController extends NotificaObservableBase{
    private final List<NotificaObserver> observers = new ArrayList<>();

    @Override
    public void aggiungiObserver(NotificaObserver observer) {
        observers.add(observer);
    }

    @Override
    public void rimuoviObserver(NotificaObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notificaObserver(String emailDestinatario, String testo) {
        for (NotificaObserver o : observers) {
            o.onNotifica(new Notifica(emailDestinatario, testo));
        }
    }

    public List<AssociazioneBean> getRichiesteSospese(String emailPT) throws DAOException {
        AssociazioneDAO dao = DAOFactory.getAssociazioneDAO();
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
        AssociazioneDAO dao = DAOFactory.getAssociazioneDAO();
        dao.aggiornaStato(bean.getEmailCliente(), StatoAssociazione.ASSOCIATO);

        notificaObserver(bean.getEmailCliente(),
                "Il tuo Personal Trainer ha accettato la tua richiesta di associazione.");
    }

    public void rifiutaAssociazione(AssociazioneBean bean) throws DAOException {
        AssociazioneDAO dao = DAOFactory.getAssociazioneDAO();
        dao.aggiornaStato(bean.getEmailCliente(), StatoAssociazione.NESSUNA);

        notificaObserver(bean.getEmailCliente(),
                "Il tuo Personal Trainer ha rifiutato la tua richiesta di associazione.");
    }
}
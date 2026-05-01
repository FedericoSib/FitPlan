package controller.graphic;

import bean.AssociazioneBean;
import model.dao.AssociazioneDAO;
import model.dao.DAOFactory;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import java.util.ArrayList;
import java.util.List;

public class GestisciRichiestePTController {

    public List<AssociazioneBean> getRichiesteSospese(String emailPT) throws DAOException {
        AssociazioneDAO dao = DAOFactory.getAssociazioneDAO();
        // Nota: Dovresti aggiungere un metodo nel DAO per filtrare per PT
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
        // Aggiorna lo stato nel file/memoria
        dao.aggiornaStato(bean.getEmailCliente(), StatoAssociazione.ASSOCIATO);
    }

    public void rifiutaAssociazione(AssociazioneBean bean) throws DAOException {
        AssociazioneDAO dao = DAOFactory.getAssociazioneDAO();
        // Aggiorna lo stato nel file/memoria
        dao.aggiornaStato(bean.getEmailCliente(), StatoAssociazione.NESSUNA);
    }
}
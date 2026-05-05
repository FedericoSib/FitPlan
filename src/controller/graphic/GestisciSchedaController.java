package controller.graphic;

import bean.ProgressiBean;
import bean.SchedaBean;
import bean.GiornoSchedaBean;
import bean.EsercizioBean;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.*;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import util.LogManager;

import java.util.ArrayList;
import java.util.List;

public class GestisciSchedaController {

    public SchedaBean getSchedaCliente() throws DAOException {
        String email = Sessione.getInstance().getUtente().getEmail();
        List<Scheda> schede = DAOFactory.getSchedaDAO().getSchedePerCliente(email);

        if (schede.isEmpty()) return null;

        // Prendiamo l'ultima scheda assegnata
        Scheda scheda = schede.get(schede.size() - 1);

        SchedaBean bean = new SchedaBean();
        bean.setEmailCliente(scheda.getEmailCliente());
        bean.setEmailPT(scheda.getEmailPT());

        for (GiornoScheda gs : scheda.getGiorni()) {
            GiornoSchedaBean giornoBean = new GiornoSchedaBean(gs.getNome());
            for (Esercizio e : gs.getEsercizi()) {
                EsercizioBean eb = new EsercizioBean();
                eb.setNome(e.getNome());
                eb.setSerie(e.getSerie());
                eb.setRipetizioni(e.getRipetizioni());
                eb.setRecuperoSecondi(e.getRecuperoSecondi());
                eb.setNote(e.getNote());
                giornoBean.aggiungiEsercizio(eb);
            }
            bean.getGiorni().add(giornoBean);
        }
        return bean;
    }

    public List<ProgressiBean> getStorico() throws DAOException {
        String email = Sessione.getInstance().getUtente().getEmail();
        List<Progressi> lista = DAOFactory.getProgressiDAO()
                .getProgressiPerCliente(email);

        List<ProgressiBean> beans = new ArrayList<>();
        for (Progressi p : lista) {
            ProgressiBean bean = new ProgressiBean();
            bean.setEmailCliente(p.getEmailCliente());
            bean.setNomeEsercizio(p.getNomeEsercizio());
            bean.setCarico(p.getCarico());
            bean.setRipetizioni(p.getRipetizioni());
            bean.setNote(p.getNote());
            bean.setData(p.getData().toString());
            beans.add(bean);
        }
        return beans;
    }

    public void registraProgressi(ProgressiBean bean)
            throws DAOException, InvalidFormException {
        if (bean.getNomeEsercizio() == null || bean.getNomeEsercizio().isBlank()) {
            throw new InvalidFormException("Seleziona un esercizio.");
        }
        if (bean.getCarico() < 0) {
            throw new InvalidFormException("Il carico non può essere negativo.");
        }
        if (bean.getRipetizioni() <= 0) {
            throw new InvalidFormException("Le ripetizioni devono essere almeno 1.");
        }

        Progressi entity = new Progressi(
                bean.getEmailCliente(),
                bean.getNomeEsercizio(),
                bean.getCarico(),
                bean.getRipetizioni(),
                bean.getNote() != null ? bean.getNote() : ""
        );

        DAOFactory.getProgressiDAO().salvaProgressi(entity);
        LogManager.info("Progressi salvati per: " + bean.getEmailCliente());
    }
}

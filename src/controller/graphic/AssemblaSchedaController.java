package controller.graphic;

import bean.*;
import model.dao.DAOFactory;
import model.dao.RichiestaDAO;
import model.dao.SchedaDAO;
import model.entity.Esercizio;
import model.entity.RichiestaScheda;
import model.entity.Scheda;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import util.LogManager;
import util.observer.NotificaObservableBase;

import java.util.ArrayList;
import java.util.List;

public class AssemblaSchedaController extends NotificaObservableBase {

    public List<RichiestaSchedaBean> getRichiestePerPT(String emailPT) throws DAOException {
        RichiestaDAO dao = DAOFactory.getRichiestaDAO();
        List<RichiestaScheda> entita = dao.prendiRichiestePerPT(emailPT);

        List<RichiestaSchedaBean> beans = new ArrayList<>();
        for (RichiestaScheda r : entita) {
            RichiestaSchedaBean bean = new RichiestaSchedaBean();
            bean.setClienteEmail(r.getClienteEmail());
            bean.setIdPersonalTrainer(r.getIdPersonalTrainer());
            bean.setSesso(r.getSesso());
            bean.setEta(r.getEta());
            bean.setPeso(r.getPeso());
            bean.setObiettivo(r.getObiettivo());
            bean.setFrequenzaSettimanale(r.getFrequenzaSettimanale());
            bean.setNote(r.getNote());
            beans.add(bean);
        }
        return beans;
    }

    public void inviaScheda(SchedaBean schedaBean) throws DAOException, InvalidFormException {
        if (schedaBean.getEsercizi().isEmpty()) {
            throw new InvalidFormException("La scheda deve contenere almeno un esercizio.");
        }

        // Trasformazione Bean → Entity
        List<Esercizio> esercizi = new ArrayList<>();
        for (EsercizioBean eb : schedaBean.getEsercizi()) {
            esercizi.add(new Esercizio(
                    eb.getNome(),
                    eb.getSerie(),
                    eb.getRipetizioni(),
                    eb.getRecuperoSecondi(),
                    eb.getNote() != null ? eb.getNote() : ""
            ));
        }

        Scheda scheda = new Scheda(
                schedaBean.getEmailCliente(),
                schedaBean.getEmailPT(),
                esercizi
        );

        SchedaDAO dao = DAOFactory.getSchedaDAO();
        dao.salvaScheda(scheda);

        // Notifica al cliente
        notificaObserver(schedaBean.getEmailCliente(),
                "Il tuo Personal Trainer ha assemblato e inviato la tua scheda di allenamento!");

        // Rimuovi la richiesta soddisfatta
        rimuoviRichiesta(schedaBean.getEmailCliente(), schedaBean.getEmailPT());

        LogManager.info("Scheda inviata al cliente: " + schedaBean.getEmailCliente());
    }

    private void rimuoviRichiesta(String emailCliente, String emailPT) {
        try {
            RichiestaDAO dao = DAOFactory.getRichiestaDAO();
            List<RichiestaScheda> tutte = dao.prendiTutteLeRichieste();
            tutte.stream()
                    .filter(r -> r.getClienteEmail().equals(emailCliente) &&
                                 r.getIdPersonalTrainer().equals(emailPT))
                    .findFirst()
                    .ifPresent(r -> {
                        try { dao.cancellaRichiesta(r); }
                        catch (Exception e) {
                            LogManager.error("Errore rimozione richiesta", e);
                        }
                    });
        } catch (Exception e) {
            LogManager.error("Errore accesso DAO richiesta", e);
        }
    }
}

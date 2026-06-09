package controller;

import bean.*;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.*;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import util.LogManager;
import util.observer.NotificaObservableBase;

import java.util.ArrayList;
import java.util.List;

public class AssemblaSchedaController extends NotificaObservableBase {

    public List<RichiestaSchedaBean> getRichiestePerPT(String emailPT) throws DAOException {
        return toBeanList(DAOFactory.getRichiestaDAO()
                .prendiRichiestePerPTEStato(emailPT, StatoRichiesta.PENDING));
    }

    public List<RichiestaSchedaBean> getRichiesteInLavorazione(String emailPT) throws DAOException {
        return toBeanList(DAOFactory.getRichiestaDAO()
                .prendiRichiestePerPTEStato(emailPT, StatoRichiesta.IN_LAVORAZIONE));
    }

    public void segnaInLavorazione(String emailCliente) throws DAOException {
        DAOFactory.getRichiestaDAO()
                .aggiornaStato(emailCliente, StatoRichiesta.IN_LAVORAZIONE);
        try {
            Utente utenteCliente = DAOFactory.getUtenteDAO().trovaUtentePerEmail(emailCliente);
            if (utenteCliente instanceof Cliente cliente) {
                cliente.setStatoRichiesta(StatoRichiesta.IN_LAVORAZIONE);
            }
        } catch (model.exception.UserNotFoundException _) {
            util.LogManager.warn("Richiesta in lavorazione, ma impossibile aggiornare l'oggetto Cliente in RAM: Utente non trovato.");
        }
    }

    private List<RichiestaSchedaBean> toBeanList(List<RichiestaScheda> entita) {
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
        if (schedaBean.getGiorni().isEmpty()) {
            throw new InvalidFormException("La scheda deve contenere almeno un giorno.");
        }
        for (GiornoSchedaBean gb : schedaBean.getGiorni()) {
            if (gb.getEsercizi().isEmpty()) {
                throw new InvalidFormException("Il giorno '" + gb.getNome() +
                        "' non contiene esercizi.");
            }
        }

        List<GiornoScheda> giorni = new ArrayList<>();
        for (GiornoSchedaBean gb : schedaBean.getGiorni()) {
            GiornoScheda giorno = new GiornoScheda(gb.getNome());
            for (EsercizioBean eb : gb.getEsercizi()) {
                giorno.aggiungiEsercizio(new Esercizio(
                        eb.getNome(), eb.getSerie(), eb.getRipetizioni(),
                        eb.getRecuperoSecondi(),
                        eb.getNote() != null ? eb.getNote() : ""
                ));
            }
            giorni.add(giorno);
        }

        Scheda scheda = new Scheda(schedaBean.getEmailCliente(),
                schedaBean.getEmailPT(), giorni);
        DAOFactory.getSchedaDAO().salvaScheda(scheda);

        notificaObserver(schedaBean.getEmailCliente(),
                "Il tuo Personal Trainer ha assemblato e inviato la tua scheda di allenamento!");

        rimuoviRichiesta(schedaBean.getEmailCliente());
        LogManager.info("Scheda inviata al cliente: " + schedaBean.getEmailCliente());
    }

    private void rimuoviRichiesta(String emailCliente) {
        try {
            DAOFactory.getRichiestaDAO()
                    .aggiornaStato(emailCliente, StatoRichiesta.COMPLETATA);
            Utente utenteCliente = DAOFactory.getUtenteDAO().trovaUtentePerEmail(emailCliente);
            if (utenteCliente instanceof Cliente cliente) {
                cliente.setStatoRichiesta(StatoRichiesta.COMPLETATA);
            }
        } catch (Exception e) {
            LogManager.error("Errore aggiornamento stato richiesta", e);
        }
    }

    public PersonalTrainerBean getPT() {
        PersonalTrainer pt = (PersonalTrainer) Sessione.getInstance().getUtente();

        PersonalTrainerBean bean = new PersonalTrainerBean();
        bean.setEmail(pt.getEmail());

        return bean;
    }
}

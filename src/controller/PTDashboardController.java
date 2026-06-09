package controller;

import bean.PersonalTrainerBean;
import model.Sessione;
import model.entity.Utente;
import model.entity.StatoRichiesta;
import model.dao.DAOFactory;
import util.LogManager;
import java.util.List;

public class PTDashboardController {
    public PersonalTrainerBean getDatiDashboard() {
        Utente pt = Sessione.getInstance().getUtente();
        PersonalTrainerBean bean = new PersonalTrainerBean();
        bean.setId(pt.getId());
        bean.setNome(pt.getNome());
        bean.setCognome(pt.getCognome());
        bean.setEmail(pt.getEmail());
        try {
            List<String> clientiPendenti = DAOFactory.getAssociazioneDAO()
                    .getRichiestePerPT(pt.getEmail());

            bean.setRichiesteAssociazionePending(clientiPendenti.size());
            bean.setUltimeRichiesteAssociazione(
                    clientiPendenti.stream().limit(3).toList());

            var tutteRichieste = DAOFactory.getRichiestaDAO()
                    .prendiRichiestePerPT(pt.getEmail());

            long schedePending = tutteRichieste.stream()
                    .filter(r -> r.getStato() == StatoRichiesta.PENDING)
                    .count();
            bean.setRichiesteSchedePending((int) schedePending);

            long schedeLavorazione = tutteRichieste.stream()
                    .filter(r -> r.getStato() == StatoRichiesta.IN_LAVORAZIONE)
                    .count();
            bean.setSchedeInLavorazione((int) schedeLavorazione);

        } catch (Exception e) {
            LogManager.error("Errore durante il recupero dei dati per la dashboard del PT", e);
        }

        return bean;
    }

    public void effettuaLogout() {
        Sessione.getInstance().setUtente(null);
        LogManager.info("Logout effettuato. Dati di sessione rimossi.");
    }
}
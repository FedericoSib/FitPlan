package controller;

import bean.PersonalTrainerBean;
import model.Sessione;
import model.entity.Utente;
import model.entity.StatoRichiesta;
import model.entity.StatoAssociazione;
import model.dao.DAOFactory;
import model.dao.AssociazioneDAO;
import util.LogManager;
import java.util.ArrayList;
import java.util.List;

public class PTDashboardController {
    public PersonalTrainerBean getDatiDashboard() {
        Utente pt = Sessione.getInstance().getUtente();
        PersonalTrainerBean bean = new PersonalTrainerBean(
                pt.getEmail(), // Usa l'email come ID
                pt.getNome(),
                pt.getCognome(),
                pt.getEmail()
        );
        try {
            AssociazioneDAO associazioneDAO = DAOFactory.getAssociazioneDAO();

            List<String> tuttiClientiDelPT = associazioneDAO.getRichiestePerPT(pt.getEmail());
            List<String> clientiPendenti = new ArrayList<>();

            for (String emailCliente : tuttiClientiDelPT) {
                if (associazioneDAO.getStato(emailCliente) == StatoAssociazione.PENDING) {
                    clientiPendenti.add(emailCliente);
                }
            }

            bean.setRichiesteAssociazionePending(clientiPendenti.size());

            List<String> ultimeRichieste = clientiPendenti.stream()
                    .limit(3)
                    .toList();
            bean.setUltimeRichiesteAssociazione(ultimeRichieste);

            long schedePending = DAOFactory.getRichiestaDAO().prendiTutteLeRichieste().stream()
                    .filter(r -> r.getIdPersonalTrainer().equalsIgnoreCase(pt.getEmail()))
                    .filter(r -> r.getStato() == StatoRichiesta.PENDING)
                    .count();
            bean.setRichiesteSchedePending((int) schedePending);

            long schedeLavorazione = DAOFactory.getRichiestaDAO().prendiTutteLeRichieste().stream()
                    .filter(r -> r.getIdPersonalTrainer().equalsIgnoreCase(pt.getEmail()))
                    .filter(r -> r.getStato() == StatoRichiesta.IN_LAVORAZIONE)
                    .count();
            bean.setSchedeInLavorazione((int) schedeLavorazione);

        } catch (Exception e) {
            LogManager.error("Errore durante il recupero dei dati per la dashboard del PT", e);
        }

        return bean;
    }
}
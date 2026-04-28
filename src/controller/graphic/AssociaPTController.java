package controller.graphic;

import model.dao.*;
import model.entity.*;
import model.Sessione;
import model.exception.*;
import util.LogManager;
import java.util.List;
import java.util.ArrayList;


public class AssociaPTController {

    public List<PersonalTrainer> cercaTrainer(String ricerca) throws TrainerNotFoundException {
        PersonalTrainerDAO dao = DAOFactory.getPersonalTrainerDAO();
        List<PersonalTrainer> risultati = new ArrayList<>();

        if (ricerca.toUpperCase().startsWith("PT-")) {
            PersonalTrainer pT = dao.getPTById(ricerca);
            if (pT != null) risultati.add(pT);
        } else if (ricerca.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            PersonalTrainer pT = dao.getPTByEmail(ricerca);
            if (pT != null) risultati.add(pT);
        }
        else {
            List<PersonalTrainer> trovatiPerNome = dao.getPTByName(ricerca);
            if (trovatiPerNome != null) {
                risultati.addAll(trovatiPerNome);
            }
        }
        if (risultati.isEmpty()) {
            throw new TrainerNotFoundException("Nessun Personal Trainer trovato per: " + ricerca);
        }

        return risultati;
    }

    public void inviaRichiestaAssociazione(PersonalTrainer pt) throws DAOException {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();

        // 1. Salviamo la richiesta nel DAO delle associazioni
        AssociazioneDAO dao = DAOFactory.getAssociazioneDAO();
        dao.salvaRichiesta(cliente.getEmail(), pt.getEmail());

        // 2. Aggiorniamo l'oggetto in sessione
        cliente.setStatoAssociazione(StatoAssociazione.PENDING);
        cliente.setIdPersonalTrainer(pt.getEmail());

        LogManager.info("Richiesta inviata: " + cliente.getEmail() + " -> " + pt.getEmail());
    }

}

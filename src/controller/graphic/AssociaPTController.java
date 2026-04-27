package controller.graphic;

import model.dao.DAOFactory;
import model.dao.PersonalTrainerDAO;
import model.entity.PersonalTrainer;
import model.entity.Cliente;
import model.Sessione;
import model.exception.TrainerNotFoundException;
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
            risultati.addAll(dao.getPTByName(ricerca));
        }
        if (risultati.isEmpty()) {
            throw new TrainerNotFoundException("Nessun Personal Trainer trovato per: " + ricerca);
        }

        return risultati;
    }

    public void handleAssocia(PersonalTrainer pt) {
        // Recuperiamo il cliente dalla sessione
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();

        // Aggiornamento dello stato dell'Entity
        cliente.setIdPersonalTrainer(pt.getEmail());
        cliente.setAssociated(true);
        LogManager.info("Associazione completata: Cliente [" + cliente.getEmail() +
                "] collegato a PT [" + pt.getEmail() + "]");
    }
}

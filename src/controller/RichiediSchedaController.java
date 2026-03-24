package controller;

import model.dao.DAOFactory;
import model.dao.RichiestaDAO;
import model.entity.Cliente;
import model.Sessione;
import model.entity.RichiestaScheda;
import model.exception.InvalidFormException;
import model.exception.TrainerNotAssociatedException;

public class RichiediSchedaController {
    public void elaboraRichiesta(RichiestaScheda richiesta) throws InvalidFormException {
        // Validazione della logica del form
        if (richiesta.getPeso() <= 0 || richiesta.getPeso() > 200) {
            throw new InvalidFormException("Il peso inserito non è valido.");
        }

        if (richiesta.getEta() < 10 || richiesta.getEta() > 100) {
            throw new InvalidFormException("L'età deve essere compresa tra 10 e 100 anni.");
        }

        // Utilizzo della Factory e del DAO
        try {
            RichiestaDAO dao = DAOFactory.getRichiestaDAO();
            dao.salvaRichiesta(richiesta);
        } catch (Exception e) {
            System.err.println("[ERRORE SISTEMA] Impossibile salvare la richiesta: " + e.getMessage());
        }
    }
    public void verificaAssociazionePT() throws TrainerNotAssociatedException {
        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        if (!c.isAssociated()) {
            throw new TrainerNotAssociatedException("Devi essere associato a un PT per richiedere una scheda.");
        }
    }
}

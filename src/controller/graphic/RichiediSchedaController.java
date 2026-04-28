package controller.graphic;

import bean.*;
import model.dao.*;
import model.entity.*;
import model.Sessione;
import model.entity.RichiestaScheda;
import model.exception.InvalidFormException;
import model.exception.TrainerNotAssociatedException;
import util.LogManager;

public class RichiediSchedaController {
    public void elaboraRichiesta(RichiestaSchedaBean bean) throws InvalidFormException {
        // 1. Validazione tramite i dati del Bean
        if (bean.getPeso() <= 0 || bean.getPeso() > 200) {
            throw new InvalidFormException("Il peso inserito non è valido.");
        }
        if (bean.getEta() < 10 || bean.getEta() > 100) {
            throw new InvalidFormException("L'età deve essere compresa tra 10 e 100 anni.");
        }

        // 2. Trasformazione: Bean -> Entity
        // Creiamo prima l'entity DatiFisici
        DatiFisici df = new DatiFisici(bean.getSesso(), bean.getEta(), bean.getPeso());

        // Creiamo l'entity RichiestaScheda
        RichiestaScheda entity = new RichiestaScheda(
                df,
                bean.getObiettivo(),
                bean.getFrequenzaSettimanale(),
                bean.getNote(),
                bean.getClienteEmail(),
                bean.getIdPersonalTrainer()
        );

        // 3. Salvataggio tramite DAO
        try {
            RichiestaDAO dao = DAOFactory.getRichiestaDAO();
            dao.salvaRichiesta(entity);
            LogManager.info("Richiesta salvata per cliente: " + bean.getClienteEmail());
        } catch (Exception e) {
            LogManager.error("Errore nel salvataggio della richiesta scheda", e);
            throw new InvalidFormException("Errore tecnico nel salvataggio.");
        }
    }

    public void verificaAssociazionePT() throws TrainerNotAssociatedException {
        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        // Nota: isAssociated() deve controllare se lo stato è ASSOCIATO
        if (c.getStatoAssociazione() != StatoAssociazione.ASSOCIATO) {
            throw new TrainerNotAssociatedException("Devi essere associato a un PT per richiedere una scheda.");
        }
    }
}

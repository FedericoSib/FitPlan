package controller.cli;

import bean.RichiestaSchedaBean;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.Cliente;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import model.exception.TrainerNotAssociatedException;
import model.entity.DatiFisici;
import model.entity.RichiestaScheda;
import util.LogManager;

public class RichiediSchedaCLIController {

    public void verificaAssociazionePT() throws TrainerNotAssociatedException {
        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        if (c.getStatoAssociazione() != StatoAssociazione.ASSOCIATO) {
            throw new TrainerNotAssociatedException(
                    "Devi essere associato a un PT per richiedere una scheda.");
        }
    }

    public void elaboraRichiesta(RichiestaSchedaBean bean)
            throws InvalidFormException, DAOException {

        // Verifica richiesta attiva
        var dao = DAOFactory.getRichiestaDAO();
        if (dao.esisteRichiestaAttiva(bean.getClienteEmail())) {
            throw new InvalidFormException(
                    "Hai già una richiesta in attesa di valutazione.");
        }

        // Verifica scheda già assegnata
        if (!DAOFactory.getSchedaDAO()
                .getSchedePerCliente(bean.getClienteEmail()).isEmpty()) {
            throw new InvalidFormException(
                    "Hai già una scheda assegnata.");
        }

        // Validazione
        if (bean.getPeso() <= 0 || bean.getPeso() > 200) {
            throw new InvalidFormException("Il peso inserito non è valido.");
        }
        if (bean.getEta() < 10 || bean.getEta() > 100) {
            throw new InvalidFormException(
                    "L'età deve essere compresa tra 10 e 100 anni.");
        }

        // Trasformazione Bean → Entity
        DatiFisici df = new DatiFisici(
                bean.getSesso(), bean.getEta(), bean.getPeso());

        RichiestaScheda entity = new RichiestaScheda(
                df,
                bean.getObiettivo(),
                bean.getFrequenzaSettimanale(),
                bean.getNote(),
                bean.getClienteEmail(),
                bean.getIdPersonalTrainer()
        );

        dao.salvaRichiesta(entity);
        LogManager.info("[CLI] Richiesta scheda salvata per: " + bean.getClienteEmail());
    }
}

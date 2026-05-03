package controller.graphic;

import bean.*;
import model.dao.*;
import model.entity.*;
import model.Sessione;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import model.exception.TrainerNotAssociatedException;
import util.LogManager;

public class RichiediSchedaController {

    public void elaboraRichiesta(RichiestaSchedaBean bean) throws InvalidFormException {
        // Spostiamo la dichiarazione del DAO fuori per usarlo nel try
        RichiestaDAO dao = DAOFactory.getRichiestaDAO();

        try {
            // 1. Controllo duplicati (ora è nel blocco try, quindi l'errore è gestito)
            if (dao.esisteRichiestaAttiva(bean.getClienteEmail())) {
                throw new InvalidFormException("Attenzione: hai già una richiesta in attesa di valutazione.");
            }

            // 2. Validazione form (logica di business, non serve il try per queste)
            validazioneSemplice(bean);

            // 3. Trasformazione: Bean -> Entity
            DatiFisici df = new DatiFisici(bean.getSesso(), bean.getEta(), bean.getPeso());
            RichiestaScheda entity = new RichiestaScheda(
                    df,
                    bean.getObiettivo(),
                    bean.getFrequenzaSettimanale(),
                    bean.getNote(),
                    bean.getClienteEmail(),
                    bean.getIdPersonalTrainer()
            );

            // 4. Salvataggio
            dao.salvaRichiesta(entity);
            LogManager.info("Richiesta salvata per cliente: " + bean.getClienteEmail());

        } catch (DAOException e) {
            // Qui catturiamo sia l'errore di esisteRichiestaAttiva che di salvaRichiesta
            LogManager.error("Errore persistenza durante elaborazione richiesta", e);
            throw new InvalidFormException("Servizio momentaneamente non disponibile. Riprova più tardi.");
        }
    }

    private void validazioneSemplice(RichiestaSchedaBean bean) throws InvalidFormException {
        if (bean.getPeso() <= 0 || bean.getPeso() > 200) {
            throw new InvalidFormException("Il peso inserito non è valido.");
        }
        if (bean.getEta() < 10 || bean.getEta() > 100) {
            throw new InvalidFormException("L'età deve essere compresa tra 10 e 100 anni.");
        }
    }

    public void verificaAssociazionePT() throws TrainerNotAssociatedException {
        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        if (c.getStatoAssociazione() != StatoAssociazione.ASSOCIATO) {
            throw new TrainerNotAssociatedException("Devi essere associato a un PT per richiedere una scheda.");
        }
    }

    public boolean verificaPresenzaRichiesta(String emailCliente) {
        try {
            return DAOFactory.getRichiestaDAO().esisteRichiestaAttiva(emailCliente);
        } catch (DAOException e) {
            LogManager.error("Errore durante il controllo preventivo richiesta", e);
            return false; // In caso di errore, permettiamo comunque il tentativo per non bloccare l'utente
        }
    }
}

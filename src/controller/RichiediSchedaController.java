package controller;

import bean.*;
import model.dao.*;
import model.entity.*;
import model.Sessione;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import model.exception.TrainerNotAssociatedException;
import util.LogManager;

public class RichiediSchedaController {

public void elaboraRichiesta(RichiestaSchedaBean bean) throws InvalidFormException,DAOException {
        RichiestaDAO dao = DAOFactory.getRichiestaDAO();
        SchedaDAO schedaDao = DAOFactory.getSchedaDAO();
        try {
            if (!schedaDao.getSchedePerCliente(bean.getClienteEmail()).isEmpty()) {
                throw new InvalidFormException("Hai già una scheda assegnata. Per richiederne una nuova contatta il tuo PT.");
            }
            if (dao.esisteRichiestaAttiva(bean.getClienteEmail())) {
                throw new InvalidFormException("Attenzione: hai già una richiesta in attesa di valutazione.");
            }
            validazioneSemplice(bean);
            DatiFisici df = new DatiFisici(bean.getSesso(), bean.getEta(), bean.getPeso());
            RichiestaScheda entity = new RichiestaScheda(
                    df,
                    bean.getObiettivo(),
                    bean.getFrequenzaSettimanale(),
                    bean.getNote(),
                    bean.getClienteEmail(),
                    bean.getIdPersonalTrainer()
            );
            dao.salvaRichiesta(entity);
            LogManager.info("Richiesta salvata per cliente: " + bean.getClienteEmail());

            Utente utenteAttuale = Sessione.getInstance().getUtente();
            if (utenteAttuale instanceof Cliente clienteLoggato) {
                clienteLoggato.setStatoRichiesta(StatoRichiesta.PENDING);
                LogManager.info("Stato del cliente in sessione aggiornato a PENDING.");
            }

        } catch (DAOException e) {
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

    public ClienteBean getDatiClienteCorrente() {
        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        ClienteBean bean = new ClienteBean();
        bean.setEmail(c.getEmail());
        bean.setNome(c.getNome());
        bean.setCognome(c.getCognome());
        bean.setNomePT(c.getIdPersonalTrainer());
        bean.setIdPersonalTrainer(c.getIdPersonalTrainer());
        return bean;
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
            return false;
        }
    }
}

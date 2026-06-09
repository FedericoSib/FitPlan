package controller;

import bean.ClienteBean;
import model.Sessione;
import model.entity.Cliente;
import model.entity.StatoRichiesta;
import model.dao.DAOFactory;
import util.LogManager;

public class ClienteDashboardController {

    public ClienteBean getDatiDashboard() {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();

        sincronizzaStatoCliente(cliente);

        ClienteBean bean = new ClienteBean();
        bean.setNome(cliente.getNome());
        bean.setCognome(cliente.getCognome());
        bean.setEmail(cliente.getEmail());
        bean.setStatoAssociazione(cliente.getStatoAssociazione().name());
        bean.setStatoRichiesta(cliente.getStatoRichiesta().name());
        bean.setNomePT(cliente.getIdPersonalTrainer() != null ? cliente.getIdPersonalTrainer() : "");
        bean.setIdPersonalTrainer(cliente.getIdPersonalTrainer());

        try {
            var schede = DAOFactory.getSchedaDAO().getSchedePerCliente(cliente.getEmail());
            bean.setHaSchedeDisponibili(!schede.isEmpty());

            if (bean.isHaSchedeDisponibili()) {
                bean.setNumeroGiorniScheda(schede.get(schede.size() - 1).getGiorni().size());
            } else if (cliente.getStatoRichiesta() == StatoRichiesta.PENDING) {
                DAOFactory.getRichiestaDAO().prendiTutteLeRichieste().stream()
                        .filter(r -> r.getClienteEmail().equalsIgnoreCase(cliente.getEmail()))
                        .findFirst()
                        .ifPresent(r -> {
                            bean.setObiettivoRichiesta(r.getObiettivo());
                            bean.setFrequenzaRichiesta(r.getFrequenzaSettimanale());
                        });
            }
        } catch (Exception e) {
            LogManager.error("Errore recupero dati aggiuntivi per dashboard", e);
        }

        return bean;
    }

    private void sincronizzaStatoCliente(Cliente cliente) {
        try {
            var schede = DAOFactory.getSchedaDAO()
                    .getSchedePerCliente(cliente.getEmail());

            if (!schede.isEmpty()) {
                cliente.setStatoRichiesta(StatoRichiesta.COMPLETATA);
                return;
            }
            DAOFactory.getRichiestaDAO().prendiTutteLeRichieste().stream()
                    .filter(r -> r.getClienteEmail()
                            .equalsIgnoreCase(cliente.getEmail()))
                    .findFirst()
                    .ifPresentOrElse(
                            r -> cliente.setStatoRichiesta(r.getStato()),
                            () -> cliente.setStatoRichiesta(StatoRichiesta.NESSUNA)
                    );
        } catch (Exception e) {
            LogManager.error("Errore sincronizzazione stato cliente", e);
        }
    }

    public void effettuaLogout() {
        Sessione.getInstance().setUtente(null);
        LogManager.info("Logout effettuato. Dati di sessione rimossi.");
    }
}
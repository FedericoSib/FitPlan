package controller;

import model.Sessione;
import bean.*;
import model.dao.*;
import model.entity.*;
import model.exception.UserNotFoundException;
import model.exception.LoginException;
import model.exception.DAOException;
import util.LogManager;

public class LoginController {

    public void autentica(LoginBean loginBean) throws LoginException {
        // 1. Validazione sintattica nel Bean
        if (loginBean.getEmail() == null || loginBean.getEmail().isEmpty()) {
            throw new LoginException("Email obbligatoria.");
        }

        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();
            // Utilizziamo i dati del Bean per la ricerca
            Utente utente = dao.trovaUtente(loginBean.getEmail(), loginBean.getPassword());

            // 2. Logica di arricchimento dell'Entity Cliente (Stato Associazione)
            if (utente instanceof Cliente cliente) {
                AssociazioneDAO associaDAO = DAOFactory.getAssociazioneDAO();
                StatoAssociazione stato = associaDAO.getStato(cliente.getEmail());
                cliente.setStatoAssociazione(stato);

                if (stato != StatoAssociazione.NESSUNA) {
                    String emailPT = associaDAO.getEmailPTAssociato(cliente.getEmail());
                    cliente.setIdPersonalTrainer(emailPT);
                }
            }

            // 3. Salvataggio in sessione dell'Entity completa
            Sessione.getInstance().setUtente(utente);
            if (Sessione.getInstance().getUtente() instanceof Cliente clienteInSessione) {
                sincronizzaStatoRichiesta(clienteInSessione);
            }
            LogManager.info("Login effettuato con successo per: " + loginBean.getEmail());

        } catch (UserNotFoundException _) {
            throw new LoginException("Credenziali non valide.");
        } catch (DAOException e) {
            throw new LoginException("Errore di sistema: " + e.getMessage());
        }
    }

    private void sincronizzaStatoRichiesta(Cliente cliente) {
        try {
            // 1. Ha già una scheda → COMPLETATA
            if (!DAOFactory.getSchedaDAO()
                    .getSchedePerCliente(cliente.getEmail()).isEmpty()) {
                // Aggiorna sempre l'oggetto in sessione
                ((Cliente) Sessione.getInstance().getUtente())
                        .setStatoRichiesta(StatoRichiesta.COMPLETATA);
                return;
            }

            // 2. Cerca tra tutte le richieste
            var tutteLeRichieste = DAOFactory.getRichiestaDAO()
                    .prendiTutteLeRichieste();

            LogManager.info("Richieste trovate al login: " + tutteLeRichieste.size());

            tutteLeRichieste.stream()
                    .filter(r -> r.getClienteEmail()
                            .equalsIgnoreCase(cliente.getEmail()))
                    .findFirst()
                    .ifPresent(r -> {
                        LogManager.info("Stato trovato: " + r.getStato());
                        // Aggiorna SEMPRE l'oggetto attualmente in sessione
                        // (potrebbe essere diverso da quello passato come parametro)
                        Cliente clienteInSessione =
                                (Cliente) Sessione.getInstance().getUtente();
                        clienteInSessione.setStatoRichiesta(r.getStato());
                        LogManager.info("Stato dopo set: " +
                                clienteInSessione.getStatoRichiesta());
                    });

        } catch (Exception e) {
            LogManager.error("Errore sincronizzazione stato richiesta al login", e);
        }
    }
}

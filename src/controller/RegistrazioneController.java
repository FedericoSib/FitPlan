package controller;

import bean.*;
import util.LogManager;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.entity.Utente;
import model.dao.DAOFactory;
import model.dao.UtenteDAO;
import model.exception.RegistrazioneException;

public class RegistrazioneController {

    public void registraNuovoUtente(RegistrazioneBean bean) throws RegistrazioneException {

        // 1. Validazione dei dati tramite il Bean
        bean.valida();

        // 2. Creazione dell'Entity partendo dai dati del Bean
        Utente nuovoUtente;
        if (bean.getRuolo() == 1) {
            nuovoUtente = new Cliente(bean.getNome(), bean.getCognome(), bean.getEmail(), bean.getPassword());
        } else {
            nuovoUtente = new PersonalTrainer(bean.getNome(), bean.getCognome(), bean.getEmail(), bean.getPassword());
        }

        try {
            UtenteDAO dao = DAOFactory.getUtenteDAO();

            // 3. Verifica duplicati
            verificaEmailNonEsistente(dao, bean.getEmail());

            // 4. Salvataggio
            dao.salvaNuovoUtente(nuovoUtente);
            LogManager.info("Registrazione completata con successo. ID Generato: " + nuovoUtente.getId());

        } catch (model.exception.DAOException e) {
            LogManager.error("Errore persistenza registrazione", e);
            throw new RegistrazioneException("Errore tecnico durante il salvataggio. Riprova più tardi.");
        }
    }

    private void verificaEmailNonEsistente(UtenteDAO dao, String email)
            throws RegistrazioneException, model.exception.DAOException {
        try {
            dao.trovaUtentePerEmail(email);
            LogManager.warn("Tentativo di registrazione con email duplicata: " + email);
            throw new RegistrazioneException("L'email inserita è già associata a un account.");
        } catch (model.exception.UserNotFoundException _) {
            LogManager.info("Email disponibile: " + email);
        }
    }
}
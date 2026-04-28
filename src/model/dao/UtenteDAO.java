package model.dao;

import model.entity.Utente;
import model.exception.DAOException;
import model.exception.UserNotFoundException;

public interface UtenteDAO {
    // Può fallire perché l'utente non c'è o per un errore tecnico (DAOException)
    Utente trovaUtente(String email, String password) throws UserNotFoundException, DAOException;

    Utente trovaUtentePerEmail(String email) throws UserNotFoundException, DAOException;

    void salvaNuovoUtente(Utente utente) throws DAOException;
}
package model.dao;

import model.entity.Utente;
import model.exception.DAOException;
import model.exception.UserNotFoundException;

public interface UtenteDAO {
    Utente trovaUtente(String email, String password) throws UserNotFoundException, DAOException;

    Utente trovaUtentePerEmail(String email) throws UserNotFoundException, DAOException;

    void salvaNuovoUtente(Utente utente) throws DAOException;
}
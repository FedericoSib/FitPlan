package model.dao;

import model.entity.Utente;

public interface UtenteDAO {
    Utente trovaUtente(String email, String password) throws Exception;
    Utente trovaUtentePerEmail(String email) throws Exception;
    void salvaNuovoUtente(Utente utente) throws Exception;
}
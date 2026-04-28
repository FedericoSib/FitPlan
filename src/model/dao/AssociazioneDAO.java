package model.dao;

import model.entity.StatoAssociazione;
import model.exception.DAOException;

public interface AssociazioneDAO {
    void salvaRichiesta(String emailCliente, String emailPT) throws DAOException;
    StatoAssociazione getStato(String emailCliente) throws DAOException;
    String getEmailPTAssociato(String emailCliente) throws DAOException;
    void aggiornaStato(String emailCliente, StatoAssociazione nuovoStato) throws DAOException;
}

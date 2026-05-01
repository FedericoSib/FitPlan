package model.dao;

import model.entity.StatoAssociazione;
import model.exception.DAOException;
import java.util.List;

public interface AssociazioneDAO {
    void salvaRichiesta(String emailCliente, String emailPT) throws DAOException;
    StatoAssociazione getStato(String emailCliente) throws DAOException;
    String getEmailPTAssociato(String emailCliente) throws DAOException;
    void aggiornaStato(String emailCliente, StatoAssociazione nuovoStato) throws DAOException;
    List<String> getRichiestePerPT(String emailPT) throws DAOException;
}

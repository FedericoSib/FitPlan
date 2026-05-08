package model.dao;

import model.entity.StatoRichiesta;
import model.exception.DAOException;
import model.entity.RichiestaScheda;
import java.io.*;
import java.util.List;

public interface RichiestaDAO {

    void salvaRichiesta(RichiestaScheda richiesta) throws DAOException;

    List<RichiestaScheda> prendiTutteLeRichieste() throws DAOException;

    List<RichiestaScheda> prendiRichiestePerPT(String idPersonalTrainer) throws DAOException;

    void cancellaRichiesta(RichiestaScheda richiesta) throws DAOException, IOException;

    boolean esisteRichiestaAttiva(String emailCliente) throws DAOException;

    void aggiornaStato(String emailCliente, StatoRichiesta stato) throws DAOException;
    List<RichiestaScheda> prendiRichiestePerPTEStato(String emailPT, StatoRichiesta stato) throws DAOException;
}
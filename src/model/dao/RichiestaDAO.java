package model.dao;

import model.entity.StatoRichiesta;
import model.exception.DAOException;
import model.entity.RichiestaScheda;
import java.io.*;
import java.util.List;

public interface RichiestaDAO {

    // Metodo per salvare una richiesta
    void salvaRichiesta(RichiestaScheda richiesta) throws DAOException;

    // Recupera tutte le richieste
    List<RichiestaScheda> prendiTutteLeRichieste() throws DAOException;

    // Metodo per recuperare tutte le richieste associate a un PT
    List<RichiestaScheda> prendiRichiestePerPT(String idPersonalTrainer) throws DAOException;

    // Metodo per rimuovere una richiesta
    void cancellaRichiesta(RichiestaScheda richiesta) throws DAOException, IOException;

    boolean esisteRichiestaAttiva(String emailCliente) throws DAOException;

    void aggiornaStato(String emailCliente, StatoRichiesta stato) throws DAOException;
    List<RichiestaScheda> prendiRichiestePerPTEStato(String emailPT, StatoRichiesta stato) throws DAOException;
}
package model.dao;

import model.entity.RichiestaScheda;
import model.entity.StatoRichiesta;
import util.LogManager;
import model.exception.*;
import java.util.ArrayList;
import java.util.List;

public class RichiestaDAOMemory implements RichiestaDAO {
    private static final List<RichiestaScheda> storage = new ArrayList<>();

    @Override
    public void salvaRichiesta(RichiestaScheda richiesta) throws DAOException {
        if (richiesta == null) {
            throw new DAOException("Impossibile salvare una richiesta nulla.");
        }
        storage.add(richiesta);
        LogManager.info("Richiesta salvata con successo per il PT: " + richiesta.getIdPersonalTrainer());
    }

    @Override
    public List<RichiestaScheda> prendiTutteLeRichieste() throws DAOException {
        return new ArrayList<>(storage);
    }

    @Override
    public List<RichiestaScheda> prendiRichiestePerPT(String idPersonalTrainer){
        if (idPersonalTrainer == null) return new ArrayList<>();

        return storage.stream()
                .filter(r -> r.getIdPersonalTrainer().equals(idPersonalTrainer))
                .toList();
    }

    @Override
    public void cancellaRichiesta(RichiestaScheda richiesta) throws DAOException {
        if (richiesta == null) {
            throw new DAOException("Impossibile cancellare una richiesta nulla.");
        }
        boolean rimosso = storage.removeIf(r ->
                r.getClienteEmail().equals(richiesta.getClienteEmail()) &&
                        r.getObiettivo().equals(richiesta.getObiettivo())
        );

        if (rimosso) {
            LogManager.info("Richiesta rimossa con successo dalla memoria.");
        } else {
            LogManager.warn("Tentativo di rimozione di una richiesta non esistente.");
        }
    }

    @Override
    public boolean esisteRichiestaAttiva(String emailCliente) throws DAOException {
        return storage.stream()
                .anyMatch(r -> r.getClienteEmail().equalsIgnoreCase(emailCliente));
    }

    @Override
    public void aggiornaStato(String emailCliente, StatoRichiesta stato) throws DAOException {
        storage.stream()
                .filter(r -> r.getClienteEmail().equalsIgnoreCase(emailCliente))
                .findFirst()
                .ifPresent(r -> r.setStato(stato));
    }

    @Override
    public List<RichiestaScheda> prendiRichiestePerPTEStato(String emailPT, StatoRichiesta stato) {
        return storage.stream()
                .filter(r -> r.getIdPersonalTrainer().equalsIgnoreCase(emailPT)
                        && r.getStato() == stato)
                .toList();
    }

}

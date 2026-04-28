package model.dao;

import model.entity.RichiestaScheda;
import model.exception.DAOException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RichiestaDAOFile implements RichiestaDAO {
    private static final String FILE_NAME = "richieste_fitplan.dat";

    @Override
    public void salvaRichiesta(RichiestaScheda richiesta) throws DAOException {
        try {
            List<RichiestaScheda> listaAttuale = prendiTutteLeRichieste();
            listaAttuale.add(richiesta);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                oos.writeObject(listaAttuale);
            }
        } catch (IOException e) {
            throw new DAOException("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @Override
    public List<RichiestaScheda> prendiRichiestePerPT(String idPersonalTrainer) throws DAOException {
        List<RichiestaScheda> tutte = prendiTutteLeRichieste();
        List<RichiestaScheda> filtrate = new ArrayList<>();

        for (RichiestaScheda r : tutte) {
            if (r.getIdPersonalTrainer().equals(idPersonalTrainer)) {
                filtrate.add(r);
            }
        }
        return filtrate;
    }

    @Override
    public void cancellaRichiesta(RichiestaScheda richiesta) throws DAOException, IOException {
        List<RichiestaScheda> lista = prendiTutteLeRichieste();
        // Rimuoviamo la richiesta basandoci sull'uguaglianza dei dati
        lista.removeIf(r -> r.getClienteEmail().equals(richiesta.getClienteEmail()) &&
                r.getObiettivo().equals(richiesta.getObiettivo()));

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lista);
        }
    }

    @Override
    public List<RichiestaScheda> prendiTutteLeRichieste() throws DAOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<RichiestaScheda>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new DAOException("Errore nel caricamento delle richieste da file: " + e.getMessage());
        }
    }
}

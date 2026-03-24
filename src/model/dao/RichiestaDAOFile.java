package model.dao;

import model.entity.RichiestaScheda;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RichiestaDAOFile implements RichiestaDAO {
    private static final String FILE_NAME = "richieste_fitplan.dat";

    @Override
    public void salvaRichiesta(RichiestaScheda richiesta) throws IOException, ClassNotFoundException {
        // Leggiamo prima le richieste esistenti per non sovrascriverle
        List<RichiestaScheda> listaAttuale = prendiTutteLeRichieste();
        listaAttuale.add(richiesta);

        // Scriviamo l'intera lista aggiornata sul file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(listaAttuale);
            System.out.println("[FULL-FILE] Richiesta salvata correttamente su disco.");
        }
    }

    @Override
    public List<RichiestaScheda> prendiRichiestePerPT(String idPersonalTrainer) throws IOException, ClassNotFoundException {
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
    public void cancellaRichiesta(RichiestaScheda richiesta) throws IOException, ClassNotFoundException {
        List<RichiestaScheda> lista = prendiTutteLeRichieste();
        // Rimuoviamo la richiesta basandoci sull'uguaglianza dei dati
        lista.removeIf(r -> r.getClienteEmail().equals(richiesta.getClienteEmail()) &&
                r.getObiettivo().equals(richiesta.getObiettivo()));

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(lista);
        }
    }

    public List<RichiestaScheda> prendiTutteLeRichieste() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<RichiestaScheda>) ois.readObject();
        } catch (EOFException e) {
            return new ArrayList<>();
        }
    }
}

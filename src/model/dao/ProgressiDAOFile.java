package model.dao;

import model.entity.Progressi;
import model.exception.DAOException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProgressiDAOFile implements ProgressiDAO {
    private static final String FILE_NAME = "progressi_fitplan.dat";

    @Override
    public void salvaProgressi(Progressi progressi) throws DAOException {
        List<Progressi> lista = caricaTutti();
        lista.add(progressi);
        salvaTutto(lista);
    }

    @Override
    public List<Progressi> getProgressiPerCliente(String emailCliente) throws DAOException {
        List<Progressi> filtrati = new ArrayList<>();
        for (Progressi p : caricaTutti()) {
            if (p.getEmailCliente().equalsIgnoreCase(emailCliente)) {
                filtrati.add(p);
            }
        }
        return filtrati;
    }

    private List<Progressi> caricaTutti() throws DAOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {

            Object rawObject = ois.readObject();
            List<Progressi> progressiSicuri = new ArrayList<>();

            //Verifica che l'oggetto letto sia una Lista
            if (rawObject instanceof List<?> listaGrezza) {
                for (Object elemento : listaGrezza) {
                    if (elemento instanceof Progressi progressi) {
                        progressiSicuri.add(progressi);
                    }
                }
            }

            return progressiSicuri;

        } catch (IOException | ClassNotFoundException e) {
            throw new DAOException("Errore caricamento progressi: " + e.getMessage());
        }
    }

    private void salvaTutto(List<Progressi> lista) throws DAOException {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(
                             new FileOutputStream(FILE_NAME, false))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio progressi: " + e.getMessage());
        }
    }
}

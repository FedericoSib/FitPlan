package model.dao;

import model.entity.Scheda;
import model.exception.DAOException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SchedaDAOFile implements SchedaDAO {
    private static final String FILE_NAME = "schede_fitplan.dat";

    @Override
    public void salvaScheda(Scheda scheda) throws DAOException {
        try {
            List<Scheda> listaAttuale = caricaTutte();
            listaAttuale.add(scheda);
            try (ObjectOutputStream oos =
                         new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                oos.writeObject(listaAttuale);
            }
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio scheda: " + e.getMessage());
        }
    }

    @Override
    public List<Scheda> getSchedePerCliente(String emailCliente) throws DAOException {
        List<Scheda> filtrate = new ArrayList<>();
        for (Scheda s : caricaTutte()) {
            if (s.getEmailCliente().equalsIgnoreCase(emailCliente)) {
                filtrate.add(s);
            }
        }
        return filtrate;
    }

    private List<Scheda> caricaTutte() throws DAOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {

            Object rawObject = ois.readObject();
            List<Scheda> schedeSicure = new ArrayList<>();

            //Verifica che sia una Lista
            if (rawObject instanceof List<?> listaGrezza) {
                for (Object elemento : listaGrezza) {
                    if (elemento instanceof Scheda scheda) {
                        schedeSicure.add(scheda);
                    }
                }
            }

            return schedeSicure;

        } catch (IOException | ClassNotFoundException e) {
            throw new DAOException("Errore caricamento schede: " + e.getMessage());
        }
    }
}

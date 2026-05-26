package model.dao;

import model.entity.Notifica;
import model.exception.DAOException;
import util.LogManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NotificaDAOFile implements NotificaDAO {

    private static final String CARTELLA = "notifiche";

    private File getFile(String email) {
        new File(CARTELLA).mkdirs();
        return new File(CARTELLA + "/notifiche_" + email + ".csv");
    }

    @Override
    public void salvaNotifica(Notifica notifica) throws DAOException {
        File file = getFile(notifica.getEmailDestinatario());
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            out.println(notifica.getTesto());
        } catch (IOException e) {
            throw new DAOException("Errore salvataggio notifica: " + e.getMessage());
        }
    }

    @Override
    public List<String> caricaECancellaNotifiche(String emailUtente) throws DAOException {
        File file = getFile(emailUtente);
        List<String> notifiche = new ArrayList<>();
        if (!file.exists()) return notifiche;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) notifiche.add(line);
            }
        } catch (IOException e) {
            throw new DAOException("Errore lettura notifiche: " + e.getMessage());
        }
        try {
            java.nio.file.Files.delete(file.toPath());
        } catch (IOException _) {
            LogManager.warn("Impossibile eliminare il file notifiche: " + file.getName());
        }
        return notifiche;
    }
}
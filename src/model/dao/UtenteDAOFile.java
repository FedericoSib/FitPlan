package model.dao;

import model.entity.*;
import model.exception.UserNotFoundException;
import model.exception.DAOException;
import util.LogManager;
import java.io.*;

public class UtenteDAOFile implements UtenteDAO {
    private static final String FILE_NAME = "utenti_fitplan.dat";

    @Override
    public void salvaNuovoUtente(Utente u) throws DAOException {
        // Formato: ID;RUOLO;NOME;COGNOME;EMAIL;PASSWORD;ASSOCIATED;ID_PT
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
            StringBuilder sb = new StringBuilder();
            sb.append(u.getId()).append(";")
                    .append(u.getRuolo()).append(";")
                    .append(u.getNome()).append(";")
                    .append(u.getCognome()).append(";")
                    .append(u.getEmail()).append(";")
                    .append(u.getPassword());
            out.println(sb.toString());
            LogManager.info("Utente salvato su file: " + u.getEmail());

        } catch (IOException e) {
            throw new DAOException("Errore critico durante la scrittura dell'utente: " + e.getMessage());
        }
    }

    @Override
    public Utente trovaUtente(String email, String password) throws UserNotFoundException, DAOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            throw new DAOException("File utenti non trovato nel percorso: " + FILE_NAME);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] d = riga.split(";");
                // d[4] è email, d[5] è password
                if (d[4].equalsIgnoreCase(email) && d[5].equals(password)) {
                    return ricomponiUtente(d);
                }
            }
        } catch (IOException e) {
            throw new DAOException("Errore di lettura del database testuale: " + e.getMessage());
        }
        throw new UserNotFoundException("Credenziali non valide per l'utente: " + email);
    }

    @Override
    public Utente trovaUtentePerEmail(String email) throws UserNotFoundException, DAOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] d = riga.split(";");
                if (d[4].equalsIgnoreCase(email)) {
                    return ricomponiUtente(d);
                }
            }
        } catch (FileNotFoundException _) {
            return null;
        } catch (IOException e) {
            throw new DAOException("Errore durante la ricerca email: " + e.getMessage());
        }

        throw new UserNotFoundException("Nessun utente registrato con email: " + email);
    }

    private Utente ricomponiUtente(String[] d) {
        int ruolo = Integer.parseInt(d[1]);
        if (ruolo == 1) {
            return new Cliente(d[0], d[2], d[3], d[4], d[5]);
        } else {
            return new PersonalTrainer(d[0], d[2], d[3], d[4], d[5]);
        }
    }
}
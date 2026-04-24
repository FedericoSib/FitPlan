package model.dao;

import model.entity.*;
import java.io.*;

public class UtenteDAOFile implements UtenteDAO {
    private static final String FILE_NAME = "utenti_fitplan.dat";

    @Override
    public void salvaNuovoUtente(Utente u) throws Exception {
        // Formato: ID;RUOLO;NOME;COGNOME;EMAIL;PASSWORD;ASSOCIATED;ID_PT
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
            String riga = u.getId() + ";" + u.getRuolo() + ";" + u.getNome() + ";" +
                    u.getCognome() + ";" + u.getEmail() + ";" + u.getPassword();

            if (u instanceof Cliente) {
                Cliente c = (Cliente) u;
                riga += ";" + c.isAssociated() + ";" + (c.getIdPersonalTrainer() != null ? c.getIdPersonalTrainer() : "null");
            } else {
                riga += ";false;null"; // Campi extra per PT (non usati per ora)
            }
            out.println(riga);
        }
    }

    @Override
    public Utente trovaUtente(String email, String password) throws Exception {
        File file = new File(FILE_NAME);
        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] d = riga.split(";");
                // d[4] è email, d[5] è password
                if (d[4].equals(email) && d[5].equals(password)) {
                    return ricomponiUtente(d);
                }
            }
        }
        return null;
    }

    @Override
    public Utente trovaUtentePerEmail(String email) throws Exception {
        // Simile a trovaUtente ma controlla solo l'email (usato in registrazione)
        return null; // Da implementare con lo stesso ciclo sopra
    }

    private Utente ricomponiUtente(String[] d) {
        int ruolo = Integer.parseInt(d[1]);
        if (ruolo == 1) {
            Cliente c = new Cliente(d[2], d[3], d[4], d[5]);
            // Qui dobbiamo forzare l'ID originale e lo stato associazione
            // In un progetto reale servirebbe un costruttore specifico o dei setter
            c.setAssociated(Boolean.parseBoolean(d[6]));
            c.setIdPersonalTrainer(d[7].equals("null") ? null : d[7]);
            return c;
        } else {
            return new PersonalTrainer(d[2], d[3], d[4], d[5]);
        }
    }
}
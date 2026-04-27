package model.dao;

import model.entity.PersonalTrainer;
import util.LogManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalTrainerDAOFile implements PersonalTrainerDAO {

    private List<PersonalTrainer> caricaTutti() {
        List<PersonalTrainer> listaPT = new ArrayList<>();
        // Leggiamo dal file dove tieni TUTTI gli utenti
        try (BufferedReader br = new BufferedReader(new FileReader("utenti_fitplan.dat"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] parti = linea.split(";");
                // Se il ruolo (indice 1) è "2", è un Personal Trainer
                if (parti[1].equals("2")) {
                    // Creiamo l'oggetto PT partendo dalla riga (ID, Nome, Cognome, Email, etc.)
                    PersonalTrainer pt = new PersonalTrainer(parti[2], parti[3], parti[4], parti[5]);
                    listaPT.add(pt);
                }
            }
        } catch (IOException e) {
            LogManager.error("Errore lettura file utenti per ricerca PT", e);
        }
        return listaPT;
    }

    @Override
    public PersonalTrainer getPTByEmail(String email) {
        return caricaTutti().stream()
                .filter(pt -> pt.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public PersonalTrainer getPTById(String id) {
        return caricaTutti().stream()
                .filter(pt -> pt.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<PersonalTrainer> getPTByName(String name) {
        // Modificato: ora filtra tutti i PT che hanno quel nome o cognome
        // e li restituisce come lista
        return caricaTutti().stream()
                .filter(pt -> pt.getNome().equalsIgnoreCase(name) ||
                        pt.getCognome().equalsIgnoreCase(name)).toList();
    }

    @Override
    public List<PersonalTrainer> getAllPT() {
        return caricaTutti();
    }
}

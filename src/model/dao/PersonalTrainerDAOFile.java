package model.dao;

import model.entity.PersonalTrainer;
import util.LogManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalTrainerDAOFile implements PersonalTrainerDAO {
    private static final String FILE_NAME = "utenti_fitplan.dat";

    private List<PersonalTrainer> caricaTutti() {
        List<PersonalTrainer> listaPT = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] parti = linea.split(";");
                if (parti[1].equals("2")) {
                    PersonalTrainer pt = new PersonalTrainer(parti[0], parti[2], parti[3], parti[4], parti[5]);
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
        String ricercaNormalizzata = name.toLowerCase().trim();
        return caricaTutti().stream()
                .filter(pt -> pt.getNome().toLowerCase().startsWith(ricercaNormalizzata) ||
                        pt.getCognome().toLowerCase().startsWith(ricercaNormalizzata))
                .toList();
    }

    @Override
    public List<PersonalTrainer> getAllPT() {
        return caricaTutti();
    }
}

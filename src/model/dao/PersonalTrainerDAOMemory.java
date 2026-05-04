package model.dao;

import model.entity.PersonalTrainer;
import java.util.ArrayList;
import java.util.List;

public class PersonalTrainerDAOMemory implements PersonalTrainerDAO {
    private static final List<PersonalTrainer> listaPT = new ArrayList<>();

    public static void aggiungiPT(PersonalTrainer pt) {
        listaPT.add(pt);
    }

    @Override
    public PersonalTrainer getPTByEmail(String email) {
        for (PersonalTrainer pt : listaPT) {
            if (pt.getEmail().equalsIgnoreCase(email)) {
                return pt;
            }
        }
        return null;
    }

    @Override
    public PersonalTrainer getPTById(String id) {
        for (PersonalTrainer pt : listaPT) {
            if (pt.getId() != null && pt.getId().equalsIgnoreCase(id)) {
                return pt;
            }
        }
        return null;
    }

    @Override
    public List<PersonalTrainer> getPTByName(String name) {
        List<PersonalTrainer> trovati = new ArrayList<>();
        String ricercaNormalizzata = name.toLowerCase().trim();
        for (PersonalTrainer pt : listaPT) {
            if (pt.getNome().toLowerCase().startsWith(ricercaNormalizzata) ||
                    pt.getCognome().toLowerCase().startsWith(ricercaNormalizzata)) {
                trovati.add(pt);
            }
        }
        return trovati;
    }

    @Override
    public List<PersonalTrainer> getAllPT() {
        return listaPT;
    }
}
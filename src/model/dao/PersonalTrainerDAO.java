package model.dao;

import model.entity.PersonalTrainer;
import java.util.List;

public interface PersonalTrainerDAO {
    PersonalTrainer getPTByEmail(String email);
    PersonalTrainer getPTById(String id);
    List<PersonalTrainer> getPTByName(String name);
    List<PersonalTrainer> getAllPT();
}

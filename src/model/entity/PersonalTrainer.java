package model.entity;

public class PersonalTrainer extends Utente {

    public PersonalTrainer(String nome, String cognome, String email, String password, String specializzazione) {
        super(nome, cognome, email, password, 2); // Passiamo 2 per il ruolo PT
    }
}

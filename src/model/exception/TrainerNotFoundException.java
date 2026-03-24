package model.exception;

public class TrainerNotFoundException extends Exception {
    public TrainerNotFoundException(String id) {
        super("Errore: Il Personal Trainer con codice '" + id + "' non è presente nel sistema.");
    }
}

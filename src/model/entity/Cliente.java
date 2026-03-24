package model.entity;

public class Cliente extends Utente {
    private String idPersonalTrainer; // L'ID del PT a cui è associato
    private boolean associated;

    public Cliente(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password, 1); // Passiamo 1 per il ruolo Cliente
        this.associated = false;
        this.idPersonalTrainer = null;
    }
    public void associaPT(String ptId) {
        this.idPersonalTrainer = ptId;
        this.associated = true;
    }
    public String getIdPersonalTrainer() { return idPersonalTrainer; }
    public void setIdPersonalTrainer(String idPersonalTrainer) { this.idPersonalTrainer = idPersonalTrainer; }
    public boolean isAssociated() { return associated; }
    public void setAssociated(boolean associated) { this.associated = associated; }
}
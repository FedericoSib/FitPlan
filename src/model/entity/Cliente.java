package model.entity;

public class Cliente extends Utente {
    private StatoAssociazione statoAssociazione = StatoAssociazione.NESSUNA;
    private StatoRichiesta statoRichiesta = StatoRichiesta.NESSUNA;
    private String idPersonalTrainer; // L'ID del PT a cui è associato

    public Cliente(String nome, String cognome, String email, String password) {
        super(nome, cognome, email, password, 1); // Passiamo 1 per il ruolo Cliente
        this.idPersonalTrainer = null;
    }
    public Cliente(String id, String nome, String cognome, String email, String password) {
        super(id, nome, cognome, email, password, 1);
    }
    public String getIdPersonalTrainer() {
        return idPersonalTrainer;
    }
    public void setIdPersonalTrainer(String idPersonalTrainer) {
        this.idPersonalTrainer = idPersonalTrainer;
    }
    public StatoAssociazione getStatoAssociazione() {
        return statoAssociazione;
    }
    public void setStatoAssociazione(StatoAssociazione statoA) {
        this.statoAssociazione = statoA;
    }
    public StatoRichiesta getStatoRichiesta() {
        return statoRichiesta;
    }
    public void setStatoRichiesta(StatoRichiesta statoR) { this.statoRichiesta = statoR;}
}
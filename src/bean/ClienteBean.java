package bean;

public class ClienteBean {
    private String nome;
    private String cognome;
    private String email;
    private String statoAssociazione;
    private String statoRichiesta;
    private String nomePT;
    private String idPersonalTrainer;
    private boolean haSchedeDisponibili;
    private String obiettivoRichiesta;
    private int frequenzaRichiesta;
    private int numeroGiorniScheda;

    // --- GETTER E SETTER ---
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatoAssociazione() { return statoAssociazione; }
    public void setStatoAssociazione(String statoAssociazione) { this.statoAssociazione = statoAssociazione; }

    public String getStatoRichiesta() { return statoRichiesta; }
    public void setStatoRichiesta(String statoRichiesta) { this.statoRichiesta = statoRichiesta; }

    public String getNomePT() { return nomePT; }
    public void setNomePT(String nomePT) { this.nomePT = nomePT; }

    public String getIdPersonalTrainer() { return idPersonalTrainer; }
    public void setIdPersonalTrainer(String idPersonalTrainer) { this.idPersonalTrainer = idPersonalTrainer; }

    public boolean isHaSchedeDisponibili() { return haSchedeDisponibili; }
    public void setHaSchedeDisponibili(boolean haSchedeDisponibili) { this.haSchedeDisponibili = haSchedeDisponibili; }

    public String getObiettivoRichiesta() { return obiettivoRichiesta; }
    public void setObiettivoRichiesta(String obiettivoRichiesta) { this.obiettivoRichiesta = obiettivoRichiesta; }

    public int getFrequenzaRichiesta() { return frequenzaRichiesta; }
    public void setFrequenzaRichiesta(int frequenzaRichiesta) { this.frequenzaRichiesta = frequenzaRichiesta; }

    public int getNumeroGiorniScheda() { return numeroGiorniScheda; }
    public void setNumeroGiorniScheda(int numeroGiorniScheda) { this.numeroGiorniScheda = numeroGiorniScheda; }
}

package bean;

import model.entity.StatoAssociazione;
import model.entity.StatoRichiesta;

public class ClienteBean {
    private String nomeCompleto;
    private String email;
    private StatoAssociazione statoAssociazione;
    private StatoRichiesta statoRichiesta;
    private String nomePT;
    private boolean haSchedeDisponibili;
    private String obiettivoRichiesta;
    private int frequenzaRichiesta;
    private int numeroGiorniScheda;

    // --- GETTER E SETTER ---
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public StatoAssociazione getStatoAssociazione() { return statoAssociazione; }
    public void setStatoAssociazione(StatoAssociazione statoAssociazione) { this.statoAssociazione = statoAssociazione; }

    public StatoRichiesta getStatoRichiesta() { return statoRichiesta; }
    public void setStatoRichiesta(StatoRichiesta statoRichiesta) { this.statoRichiesta = statoRichiesta; }

    public String getNomePT() { return nomePT; }
    public void setNomePT(String nomePT) { this.nomePT = nomePT; }

    public boolean isHaSchedeDisponibili() { return haSchedeDisponibili; }
    public void setHaSchedeDisponibili(boolean haSchedeDisponibili) { this.haSchedeDisponibili = haSchedeDisponibili; }

    public String getObiettivoRichiesta() { return obiettivoRichiesta; }
    public void setObiettivoRichiesta(String obiettivoRichiesta) { this.obiettivoRichiesta = obiettivoRichiesta; }

    public int getFrequenzaRichiesta() { return frequenzaRichiesta; }
    public void setFrequenzaRichiesta(int frequenzaRichiesta) { this.frequenzaRichiesta = frequenzaRichiesta; }

    public int getNumeroGiorniScheda() { return numeroGiorniScheda; }
    public void setNumeroGiorniScheda(int numeroGiorniScheda) { this.numeroGiorniScheda = numeroGiorniScheda; }
}

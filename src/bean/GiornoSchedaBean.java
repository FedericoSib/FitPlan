package bean;

import java.util.ArrayList;
import java.util.List;

public class GiornoSchedaBean {
    private String nome;
    private List<EsercizioBean> esercizi = new ArrayList<>();

    public GiornoSchedaBean(String nome) { this.nome = nome; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<EsercizioBean> getEsercizi() { return esercizi; }
    public void aggiungiEsercizio(EsercizioBean e) { esercizi.add(e); }
}
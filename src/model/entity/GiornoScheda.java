package model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GiornoScheda implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private final List<Esercizio> esercizi = new ArrayList<>();

    public GiornoScheda(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Esercizio> getEsercizi() { return esercizi; }
    public void aggiungiEsercizio(Esercizio e) { esercizi.add(e); }
}
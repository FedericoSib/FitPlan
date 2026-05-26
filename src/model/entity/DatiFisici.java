package model.entity;

import java.io.Serializable;

public class DatiFisici implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String sesso;
    private final int eta;
    private final double peso;

    public DatiFisici(String sesso, int eta, double peso) {
        this.sesso = sesso;
        this.eta = eta;
        this.peso = peso;
    }

    public String getSesso() { return sesso; }
    public int getEta() { return eta; }
    public double getPeso() { return peso; }
}
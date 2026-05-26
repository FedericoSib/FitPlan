package model.dao;

import model.entity.StatoAssociazione;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class AssociazioneDAOMemory implements AssociazioneDAO {
    private static final Map<String, String> richiestePT = new HashMap<>();
    private static final Map<String, StatoAssociazione> stati = new HashMap<>();

    @Override
    public void salvaRichiesta(String emailCliente, String emailPT) {
        richiestePT.put(emailCliente, emailPT);
        stati.put(emailCliente, StatoAssociazione.PENDING);
    }

    @Override
    public StatoAssociazione getStato(String emailCliente) {
        return stati.getOrDefault(emailCliente, StatoAssociazione.NESSUNA);
    }

    @Override
    public String getEmailPTAssociato(String emailCliente) {
        return richiestePT.get(emailCliente);
    }

    @Override
    public void aggiornaStato(String emailCliente, StatoAssociazione nuovoStato) {
        stati.put(emailCliente, nuovoStato);
    }

    @Override
    public List<String> getRichiestePerPT(String emailPT) {
        List<String> risultati = new ArrayList<>();
        for (Map.Entry<String, String> entry : richiestePT.entrySet()) {
            if (entry.getValue().equals(emailPT) && stati.get(entry.getKey()) == StatoAssociazione.PENDING) {
                risultati.add(entry.getKey());
            }
        }
        return risultati;
    }

    @Override
    public List<String> rimuoviRichiesteScadute(long limiteMs) {
        return new ArrayList<>();
    }
}
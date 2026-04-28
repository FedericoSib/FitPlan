package model.dao;

import model.entity.StatoAssociazione;
import java.util.HashMap;
import java.util.Map;

public class AssociazioneDAOMemory implements AssociazioneDAO {
    // Mappa: EmailCliente -> [EmailPT, Stato]
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
}
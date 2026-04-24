package model.dao;

import model.entity.RichiestaScheda;
import util.LogManager;

import java.util.ArrayList;
import java.util.List;

public class RichiestaDAOMemory implements RichiestaDAO {
    private static List<RichiestaScheda> storage = new ArrayList<>();

    @Override
    public void salvaRichiesta(RichiestaScheda richiesta){
        storage.add(richiesta);
        LogManager.info("Richiesta salvata con successo per il PT: " + richiesta.getIdPersonalTrainer());
    }

    @Override
    public List<RichiestaScheda> prendiTutteLeRichieste() {
        return new ArrayList<>(storage);
    }

    @Override
    public List<RichiestaScheda> prendiRichiestePerPT(String idPersonalTrainer){
        return storage.stream()
                .filter(r -> r.getIdPersonalTrainer().equals(idPersonalTrainer))
                .toList();
    }

    @Override
    public void cancellaRichiesta(RichiestaScheda richiesta) {
        storage.remove(richiesta);
        LogManager.info("Richiesta rimossa con successo.");
    }
}

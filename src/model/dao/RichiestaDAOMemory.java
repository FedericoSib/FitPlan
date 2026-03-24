package model.dao;

import model.entity.RichiestaScheda;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RichiestaDAOMemory implements RichiestaDAO {
    private static List<RichiestaScheda> storage = new ArrayList<>();

    @Override
    public void salvaRichiesta(RichiestaScheda richiesta){
        storage.add(richiesta);
        System.out.println("[DEMO-MEMORIA] Richiesta salvata con successo per il PT: "
                + richiesta.getIdPersonalTrainer());
    }

    @Override
    public List<RichiestaScheda> prendiTutteLeRichieste() {
        return new ArrayList<>(storage);
    }

    @Override
    public List<RichiestaScheda> prendiRichiestePerPT(String idPersonalTrainer){
        return storage.stream()
                .filter(r -> r.getIdPersonalTrainer().equals(idPersonalTrainer))
                .collect(Collectors.toList());
    }

    @Override
    public void cancellaRichiesta(RichiestaScheda richiesta) {
        storage.remove(richiesta);
        System.out.println("[DEMO-MEMORIA] Richiesta rimossa.");
    }
}

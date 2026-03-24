package model.dao;

import model.entity.RichiestaScheda;
import java.util.List;

public interface RichiestaDAO {

    // Metodo per salvare una richiesta
    void salvaRichiesta(RichiestaScheda richiesta) throws Exception;

    // Recupera tutte le richieste
    List<RichiestaScheda> prendiTutteLeRichieste() throws Exception;

    // Metodo per recuperare tutte le richieste associate a un PT
    List<RichiestaScheda> prendiRichiestePerPT(String idPersonalTrainer) throws Exception;

    // Metodo per rimuovere una richiesta
    void cancellaRichiesta(RichiestaScheda richiesta) throws Exception;
}
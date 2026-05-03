package model.dao;

import model.entity.Scheda;
import model.exception.DAOException;
import java.util.ArrayList;
import java.util.List;

public class SchedaDAOMemory implements SchedaDAO {
    private static final List<Scheda> storage = new ArrayList<>();

    @Override
    public void salvaScheda(Scheda scheda) throws DAOException {
        if (scheda == null) throw new DAOException("Impossibile salvare una scheda nulla.");
        storage.add(scheda);
    }

    @Override
    public List<Scheda> getSchedePerCliente(String emailCliente) {
        if (emailCliente == null) return new ArrayList<>();
        return storage.stream()
                .filter(s -> s.getEmailCliente().equalsIgnoreCase(emailCliente))
                .toList();
    }
}

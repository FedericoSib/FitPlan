package model.dao;

import model.entity.Progressi;
import model.exception.DAOException;

import java.util.ArrayList;
import java.util.List;

public class ProgressiDAOMemory implements ProgressiDAO {
    private static final List<Progressi> storage = new ArrayList<>();

    @Override
    public void salvaProgressi(Progressi progressi) throws DAOException {
        if (progressi == null) throw new DAOException("Progressi nulli.");
        storage.add(progressi);
    }

    @Override
    public List<Progressi> getProgressiPerCliente(String emailCliente) {
        if (emailCliente == null) return new ArrayList<>();
        return storage.stream()
                .filter(p -> p.getEmailCliente().equalsIgnoreCase(emailCliente))
                .toList();
    }
}

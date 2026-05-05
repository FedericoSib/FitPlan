package model.dao;

import model.entity.Progressi;
import model.exception.DAOException;
import java.util.List;

public interface ProgressiDAO {
    void salvaProgressi(Progressi progressi) throws DAOException;
    List<Progressi> getProgressiPerCliente(String emailCliente) throws DAOException;
}

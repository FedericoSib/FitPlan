package model.dao;

import model.entity.Scheda;
import model.exception.DAOException;
import java.util.List;

public interface SchedaDAO {
    void salvaScheda(Scheda scheda) throws DAOException;
    List<Scheda> getSchedePerCliente(String emailCliente) throws DAOException;
}

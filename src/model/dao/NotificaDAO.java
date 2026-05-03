package model.dao;

import model.entity.Notifica;
import model.exception.DAOException;
import java.util.List;

public interface NotificaDAO {
    void salvaNotifica(Notifica notifica) throws DAOException;
    List<String> caricaECancellaNotifiche(String emailUtente) throws DAOException;
}

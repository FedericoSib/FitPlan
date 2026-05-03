package model.dao;

import model.entity.Notifica;
import java.util.*;

public class NotificaDAOMemory implements NotificaDAO {
    private static final Map<String, List<String>> storage = new HashMap<>();

    @Override
    public void salvaNotifica(Notifica notifica) {
        storage.computeIfAbsent(
                notifica.getEmailDestinatario().toLowerCase(),
                k -> new ArrayList<>()
        ).add(notifica.getTesto());
    }

    @Override
    public List<String> caricaECancellaNotifiche(String emailUtente) {
        return storage.remove(emailUtente.toLowerCase()) != null
                ? storage.getOrDefault(emailUtente.toLowerCase(), new ArrayList<>())
                : new ArrayList<>();
    }
}

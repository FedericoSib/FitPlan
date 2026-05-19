package util.observer;

import model.entity.Notifica;
import java.util.ArrayList;
import java.util.List;

public abstract class NotificaObservableBase{

    private final List<NotificaObserver> observers = new ArrayList<>();

    public void aggiungiObserver(NotificaObserver observer) {
        observers.add(observer);
    }

    public void rimuoviObserver(NotificaObserver observer) {
        observers.remove(observer);
    }

    public void notificaObserver(String emailDestinatario, String testo) {
        for (NotificaObserver o : observers) {
            o.onNotifica(new Notifica(emailDestinatario, testo));
        }
    }
}

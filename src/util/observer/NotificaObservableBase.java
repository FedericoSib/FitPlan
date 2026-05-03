package util.observer;

import model.entity.Notifica;
import java.util.ArrayList;
import java.util.List;

public abstract class NotificaObservableBase implements NotificaObservable {

    private final List<NotificaObserver> observers = new ArrayList<>();

    @Override
    public void aggiungiObserver(NotificaObserver observer) {
        observers.add(observer);
    }

    @Override
    public void rimuoviObserver(NotificaObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notificaObserver(String emailDestinatario, String testo) {
        for (NotificaObserver o : observers) {
            o.onNotifica(new Notifica(emailDestinatario, testo));
        }
    }
}

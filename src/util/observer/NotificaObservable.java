package util.observer;

public interface NotificaObservable {
    void aggiungiObserver(NotificaObserver observer);
    void rimuoviObserver(NotificaObserver observer);
    void notificaObserver(String emailDestinatario, String testo);
}

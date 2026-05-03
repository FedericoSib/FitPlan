package util.observer;

import model.Sessione;
import model.dao.DAOFactory;
import model.dao.NotificaDAO;
import model.entity.Notifica;
import model.entity.Utente;
import util.LogManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;

public class NotificaManager implements NotificaObserver {

    private static NotificaManager instance;

    private NotificaManager() {}

    public static NotificaManager getInstance() {
        if (instance == null) {
            instance = new NotificaManager();
        }
        return instance;
    }

    @Override
    public void onNotifica(Notifica notifica) {
        // 1. Salva sempre su file
        try {
            NotificaDAO dao = DAOFactory.getNotificaDAO();
            dao.salvaNotifica(notifica);
        } catch (Exception e) {
            LogManager.error("Errore salvataggio notifica", e);
        }

        // 2. Se il destinatario è loggato, mostra il popup in real-time
        Utente utenteLoggato = Sessione.getInstance().getUtente();
        if (utenteLoggato != null &&
                utenteLoggato.getEmail().equalsIgnoreCase(notifica.getEmailDestinatario())) {
            Platform.runLater(() -> mostraPopup(notifica.getTesto()));
        }
    }

    private void mostraPopup(String testo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifica");
        alert.setHeaderText(null);
        alert.setContentText(testo);
        alert.show();
    }

    public void mostraNotifichePendenti(String emailUtente) {
        try {
            NotificaDAO dao = DAOFactory.getNotificaDAO();
            List<String> notifiche = dao.caricaECancellaNotifiche(emailUtente);
            for (String testo : notifiche) {
                Platform.runLater(() -> mostraPopup(testo));
            }
        } catch (Exception e) {
            LogManager.error("Errore caricamento notifiche pendenti", e);
        }
    }
}
package util.observer;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import model.Sessione;
import model.dao.NotificaDAO;
import model.entity.Notifica;
import model.entity.Utente;
import model.exception.DAOException;
import util.LogManager;

import java.util.List;

public class NotificaManager implements NotificaObserver {

    private final NotificaDAO dao;

    public NotificaManager(NotificaDAO dao) {
        this.dao = dao;
    }

    @Override
    public void onNotifica(Notifica notifica) {
        try {
            dao.salvaNotifica(notifica);
        } catch (DAOException e) {
            LogManager.error("Errore salvataggio notifica", e);
        }

        Utente utenteLoggato = Sessione.getInstance().getUtente();
        if (utenteLoggato != null &&
                utenteLoggato.getEmail().equalsIgnoreCase(notifica.getEmailDestinatario())) {
            Platform.runLater(() -> mostraPopup(notifica.getTesto()));
        }
    }

    public void mostraNotifichePendenti(String emailUtente) {
        try {
            List<String> notifiche = dao.caricaECancellaNotifiche(emailUtente);
            for (String testo : notifiche) {
                Platform.runLater(() -> mostraPopup(testo));
            }
        } catch (DAOException e) {
            LogManager.error("Errore caricamento notifiche pendenti", e);
        }
    }

    private void mostraPopup(String testo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifica");
        alert.setHeaderText(null);
        alert.setContentText(testo);
        alert.show();
    }
}
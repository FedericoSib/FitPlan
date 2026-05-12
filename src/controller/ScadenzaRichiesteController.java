package controller;

import model.dao.AssociazioneDAO;
import model.dao.DAOFactory;
import model.exception.DAOException;
import util.LogManager;
import util.observer.NotificaObservableBase;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScadenzaRichiesteController extends NotificaObservableBase {

    private static final boolean DEMO_MODE = true;
    private static final long SCADENZA_MS = DEMO_MODE
            ? TimeUnit.MINUTES.toMillis(2)
            : TimeUnit.DAYS.toMillis(7);
    private static final long INTERVALLO_SECONDI = DEMO_MODE ? 30 : 3600;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public void avvia() {
        scheduler.scheduleAtFixedRate(
                this::controllaScadenze,
                0,
                INTERVALLO_SECONDI,
                TimeUnit.SECONDS
        );
        LogManager.info("ScadenzaRichiesteController avviato. Scadenza: " +
                (DEMO_MODE ? "2 minuti (DEMO)" : "7 giorni"));
    }

    public void ferma() {
        scheduler.shutdownNow();
    }

    private void controllaScadenze() {
        try {
            AssociazioneDAO dao = DAOFactory.getAssociazioneDAO();
            List<String> scaduti = dao.rimuoviRichiesteScadute(SCADENZA_MS);

            for (String emailCliente : scaduti) {
                LogManager.warn("Richiesta scaduta per cliente: " + emailCliente);
                notificaObserver(emailCliente,
                        "La tua richiesta di associazione è scaduta. Puoi inviarne una nuova.");
            }
        } catch (DAOException e) {
            LogManager.error("Errore nel controllo scadenze", e);
        }
    }
}
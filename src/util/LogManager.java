package util;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class LogManager {
    private static final Logger GLOBAL_LOGGER = Logger.getLogger("FitPlanLogger");

    // Costruttore privato per evitare istanziazioni (classe di utility)
    private LogManager() {}

    static {
        try {
            // Configura il logger per scrivere su un file chiamato "fitplan_logs.log"
            // Il parametro 'true' serve per aggiungere i log in coda senza sovrascrivere
            FileHandler fh = new FileHandler("fitplan_logs.log", true);
            GLOBAL_LOGGER.addHandler(fh);

            // Imposta un formato semplice e leggibile
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            GLOBAL_LOGGER.setUseParentHandlers(false);

        } catch (IOException | SecurityException e) {
            GLOBAL_LOGGER.log(Level.SEVERE, "Impossibile inizializzare il file di log", e);
        }
    }

    // Metodo per loggare informazioni generiche
    public static void info(String message) {
        GLOBAL_LOGGER.log(Level.INFO, message);
    }

    // Metodo per loggare avvisi
    public static void warn(String message) {
        GLOBAL_LOGGER.log(Level.WARNING, message);
    }

    // Metodo per loggare errori gravi con eccezione
    public static void error(String message, Throwable throwable) {
        GLOBAL_LOGGER.log(Level.SEVERE, message, throwable);
    }
}
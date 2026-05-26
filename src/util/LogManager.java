package util;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class LogManager {
    private static final Logger GLOBAL_LOGGER = Logger.getLogger("FitPlanLogger");

    private LogManager() {}

    static {
        try {
            FileHandler fh = new FileHandler("fitplan_logs.log", true);
            GLOBAL_LOGGER.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            GLOBAL_LOGGER.setUseParentHandlers(false);


        } catch (IOException | SecurityException e) {
            GLOBAL_LOGGER.log(Level.SEVERE, "Impossibile inizializzare il file di log", e);
        }
    }
    public static void info(String message) {
        GLOBAL_LOGGER.log(Level.INFO, message);
    }

    public static void warn(String message) {
        GLOBAL_LOGGER.log(Level.WARNING, message);
    }

    public static void error(String message, Throwable throwable) {
        GLOBAL_LOGGER.log(Level.SEVERE, message, throwable);
    }
}
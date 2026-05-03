package model.dao;

import util.LogManager;

public class DAOFactory {

    private static boolean demoMode = false;

    private DAOFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static void setMode(int scelta) {
        // 1 = Demo, 2 = Full
        if (scelta == 1){
            demoMode = true;
        }
        String modo = demoMode ? "DEMO (In-Memory)" : "FULL (File System)";
        LogManager.info("Sistema avviato in modalità: " + modo);
    }

    public static RichiestaDAO getRichiestaDAO() {
        if (demoMode) {
            return new RichiestaDAOMemory(); //Versione DEMO
        } else {
            return new RichiestaDAOFile(); //Versione FULL
        }
    }
    public static UtenteDAO getUtenteDAO() {
        if (demoMode) {
            return new UtenteDAOMemory(); //Versione DEMO
        } else {
            return new UtenteDAOFile(); //Versione FULL
        }
    }
    public static PersonalTrainerDAO getPersonalTrainerDAO() {
        if (demoMode) {
            return new PersonalTrainerDAOMemory(); //Versione DEMO
        } else {
            return new PersonalTrainerDAOFile(); //Versione FULL
        }
    }

    public static AssociazioneDAO getAssociazioneDAO() {
        if (demoMode) {
            return new AssociazioneDAOMemory();
        } else {
            return new AssociazioneDAOFile();
        }
    }

    public static NotificaDAO getNotificaDAO() {
        if (demoMode) {
            return new NotificaDAOMemory();
        } else {
            return new NotificaDAOFile();
        }
    }
}
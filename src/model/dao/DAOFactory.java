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
            return new RichiestaDAOMemory();
        } else {
            return new RichiestaDAOFile();
        }
    }
    public static UtenteDAO getUtenteDAO() {
        if (demoMode) {
            return new UtenteDAOMemory();
        } else {
            return new UtenteDAOFile();
        }
    }
    public static PersonalTrainerDAO getPersonalTrainerDAO() {
        if (demoMode) {
            return new PersonalTrainerDAOMemory();
        } else {
            return new PersonalTrainerDAOFile();
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

    public static SchedaDAO getSchedaDAO() {
        if (demoMode) return new SchedaDAOMemory();
        else return new SchedaDAOFile();
    }
    public static ProgressiDAO getProgressiDAO() {
        if (demoMode) return new ProgressiDAOMemory();
        else return new ProgressiDAOFile();
    }
}
package model.dao;

public class DAOFactory {

    private static boolean DEMO_MODE = false;

    public static void setMode(int scelta) {
        // 1 = Demo, 2 = Full
        if (scelta == 1){
            DEMO_MODE = true;
        }
        String modo = DEMO_MODE ? "DEMO (In-Memory)" : "FULL (File System)";
        System.out.println("[CONFIG] Sistema avviato in modalità: " + modo);
    }

    public static RichiestaDAO getRichiestaDAO() {
        if (DEMO_MODE) {
            return new RichiestaDAOMemory(); //Versione DEMO
        } else {
            return new RichiestaDAOFile(); //Versione FULL
        }
    }
    public static UtenteDAO getUtenteDAO() {
        if (DEMO_MODE) {
            return new UtenteDAOMemory(); //Versione DEMO
        } else {
            return new UtenteDAOFile(); //Versione FULL
        }
    }
}
package main;

import controller.graphic.ScadenzaRichiesteController;
import model.dao.DAOFactory;
import util.LogManager;
import util.observer.NotificaManager;
import view.cli.MenuPrincipaleCLI;
import view.fxml.Navigator;

import java.util.Scanner;

public class MainApp extends javafx.application.Application {
    private static final String ICON_PATH = "/view/immages/logo.png";
    private static ScadenzaRichiesteController scadenzaController;
    private static final int MAX_TENTATIVI = 3;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int modalitaUI = leggiScelta(scanner,
                "=== FitPlan ===\n1. Avvia interfaccia grafica (GUI)\n2. Avvia interfaccia testuale (CLI)",
                1, 2);

        int modalitaDAO = leggiScelta(scanner,
                "Modalità dati:\n1. DEMO (In-Memory)\n2. FULL (File)",
                1, 2);

        DAOFactory.setMode(modalitaDAO);
        avviaScadenzaController();

        if (modalitaUI == 2) {
            new MenuPrincipaleCLI(scanner).avvia();
            scadenzaController.ferma();
        } else {
            launch(args);
        }
    }

    private static int leggiScelta(Scanner scanner, String prompt, int min, int max) {
        System.out.println(prompt);
        int tentativi = 0;

        while (tentativi < MAX_TENTATIVI) {
            System.out.print("Scelta (" + min + "-" + max + "): ");
            String input = scanner.nextLine().trim();

            try {
                int scelta = Integer.parseInt(input);
                if (scelta >= min && scelta <= max) {
                    return scelta;
                }
                System.out.println("Inserisci un numero tra " + min + " e " + max + ".");
            } catch (NumberFormatException _) {
                System.out.println("Input non valido. Inserisci un numero.");
            }

            tentativi++;
            int rimasti = MAX_TENTATIVI - tentativi;
            if (rimasti > 0) {
                System.out.println("Tentativi rimanenti: " + rimasti);
            }
        }

        System.out.println("Troppi tentativi falliti. Chiusura applicazione.");
        System.exit(1);
        return -1;
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {
        Navigator.setPrimaryStage(primaryStage);
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/fxml/Login.fxml"));
        javafx.scene.Parent root = loader.load();
        primaryStage.setTitle("FitPlan");
        try {
            primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream(ICON_PATH)));
        } catch (Exception e) {
            LogManager.error("Impossibile caricare l'icona dell'applicazione.", e);
        }
        primaryStage.setScene(new javafx.scene.Scene(root));
        primaryStage.show();
        primaryStage.requestFocus();
        primaryStage.toFront();
        primaryStage.setIconified(false);
    }

    @Override
    public void stop() {
        if (scadenzaController != null) scadenzaController.ferma();
    }

    private static void avviaScadenzaController() {
        scadenzaController = new ScadenzaRichiesteController();
        NotificaManager manager = new NotificaManager(DAOFactory.getNotificaDAO());
        scadenzaController.aggiungiObserver(manager);
        scadenzaController.avvia();
        LogManager.info("ScadenzaRichiesteController avviato.");
    }
}
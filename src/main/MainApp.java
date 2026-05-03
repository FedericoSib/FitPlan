package main;

import controller.graphic.ScadenzaRichiesteController;
import model.dao.DAOFactory;
import util.LogManager;
import util.observer.NotificaManager;
import view.cli.MenuPrincipaleCLI;

import java.util.Scanner;

public class MainApp extends javafx.application.Application {

    private static ScadenzaRichiesteController scadenzaController;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== FitPlan ===");
        System.out.println("1. Avvia interfaccia grafica (GUI)");
        System.out.println("2. Avvia interfaccia testuale (CLI)");
        System.out.print("Scelta: ");

        String scelta = scanner.nextLine().trim();

        // Setup comune a entrambe le modalità
        DAOFactory.setMode(sceltaModalitaDAO(scanner));
        avviaScadenzaController();

        if (scelta.equals("2")) {
            new MenuPrincipaleCLI(scanner).avvia();
            scadenzaController.ferma();
        } else {
            launch(args);
        }
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {
        javafx.fxml.FXMLLoader loader =
                new javafx.fxml.FXMLLoader(getClass().getResource("/view/Login.fxml"));
        javafx.scene.Parent root = loader.load();
        primaryStage.setTitle("FitPlan");
        primaryStage.setScene(new javafx.scene.Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (scadenzaController != null) scadenzaController.ferma();
    }

    private static int sceltaModalitaDAO(Scanner scanner) {
        System.out.println("Modalità dati:");
        System.out.println("1. DEMO (In-Memory)");
        System.out.println("2. FULL (File)");
        System.out.print("Scelta: ");
        String s = scanner.nextLine().trim();
        return s.equals("1") ? 1 : 2;
    }

    private static void avviaScadenzaController() {
        scadenzaController = new ScadenzaRichiesteController();
        NotificaManager manager = new NotificaManager(model.dao.DAOFactory.getNotificaDAO());
        scadenzaController.aggiungiObserver(manager);
        scadenzaController.avvia();
        LogManager.info("ScadenzaRichiesteController avviato.");
    }
}

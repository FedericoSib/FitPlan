package view.cli;

import controller.PTDashboardController;

import java.util.Scanner;

public class MenuPTCLI {

    private final Scanner scanner;
    private final PTDashboardController controller = new PTDashboardController();

    public MenuPTCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        boolean esci = false;
        while (!esci) {
            System.out.println("\n=== Menu Personal Trainer ===");
            System.out.println("0. Logout");
            System.out.println("1. Gestisci richieste di associazione");
            System.out.println("2. Assembla Scheda");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "0" -> {
                    controller.effettuaLogout();
                    esci = true;
                    System.out.println("Logout effettuato.");
                }
                case "1" -> new GestisciRichiesteCLIBoundary(scanner).avvia();
                case "2" -> new AssemblaSchedaCLIBoundary(scanner).avvia();
                default -> System.out.println("Scelta non valida.");
            }
        }
    }
}

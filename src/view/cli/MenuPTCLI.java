package view.cli;

import model.Sessione;

import java.util.Scanner;

public class MenuPTCLI {

    private final Scanner scanner;

    public MenuPTCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        boolean esci = false;
        while (!esci) {
            System.out.println("\n=== Menu Personal Trainer ===");
            System.out.println("1. Gestisci richieste di associazione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1" -> new GestisciRichiesteCLIBoundary(scanner).avvia();
                case "0" -> {
                    Sessione.getInstance().setUtente(null);
                    esci = true;
                    System.out.println("Logout effettuato.");
                }
                default -> System.out.println("Scelta non valida.");
            }
        }
    }
}

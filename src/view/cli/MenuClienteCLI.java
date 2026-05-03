package view.cli;

import model.Sessione;

import java.util.Scanner;

public class MenuClienteCLI {

    private final Scanner scanner;

    public MenuClienteCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        boolean esci = false;
        while (!esci) {
            System.out.println("\n=== Menu Cliente ===");
            System.out.println("1. Cerca e associa Personal Trainer");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1" -> new AssociaPTCLIBoundary(scanner).avvia();
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

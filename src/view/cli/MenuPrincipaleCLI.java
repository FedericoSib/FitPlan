package view.cli;

import java.util.Scanner;

public class MenuPrincipaleCLI {

    private final Scanner scanner;

    public MenuPrincipaleCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        boolean esci = false;
        while (!esci) {
            System.out.println("\n=== FitPlan CLI ===");
            System.out.println("1. Login");
            System.out.println("2. Registrati");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1" -> new LoginCLIBoundary(scanner).avvia();
                case "2" -> new RegistrazioneCLIBoundary(scanner).avvia();
                case "0" -> esci = true;
                default  -> System.out.println("Scelta non valida.");
            }
        }
        System.out.println("Arrivederci!");
    }
}

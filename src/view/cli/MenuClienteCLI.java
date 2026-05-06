package view.cli;

import model.Sessione;
import model.dao.DAOFactory;
import model.entity.Cliente;
import model.entity.StatoRichiesta;

import java.util.Scanner;

public class MenuClienteCLI {

    private final Scanner scanner;

    public MenuClienteCLI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        sincronizzaStatoCliente();

        // --- 2. AVVIA IL MENU ---
        boolean esci = false;
        while (!esci) {
            System.out.println("\n=== Menu Cliente ===");
            System.out.println("0. Logout");
            System.out.println("1. Cerca e associa Personal Trainer");
            System.out.println("2. Richiedi Scheda");
            System.out.println("3. Gestisci scheda");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "0" -> {
                    Sessione.getInstance().setUtente(null);
                    esci = true;
                    System.out.println("Logout effettuato.");
                }
                case "1" -> new AssociaPTCLIBoundary(scanner).avvia();
                case "2" -> new RichiediSchedaCLIBoundary(scanner).avvia();
                case "3" -> new GestisciSchedaCLIBoundary(scanner).avvia();
                default -> System.out.println("Scelta non valida.");
            }
        }
    }
    private void sincronizzaStatoCliente() {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();
        try {
            // 1. Ha già una scheda → COMPLETATA
            if (!DAOFactory.getSchedaDAO()
                    .getSchedePerCliente(cliente.getEmail()).isEmpty()) {
                cliente.setStatoRichiesta(StatoRichiesta.COMPLETATA);
                return;
            }

            // 2. Cerca la richiesta tra tutte — legge lo stato salvato su file
            DAOFactory.getRichiestaDAO()
                    .prendiTutteLeRichieste()
                    .stream()
                    .filter(r -> r.getClienteEmail()
                            .equalsIgnoreCase(cliente.getEmail()))
                    .findFirst()
                    .ifPresent(r -> cliente.setStatoRichiesta(r.getStato()));

        } catch (Exception e) {
            System.out.println("Errore sincronizzazione stato: " + e.getMessage());
        }
    }
}

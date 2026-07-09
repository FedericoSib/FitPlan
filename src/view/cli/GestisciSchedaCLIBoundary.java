package view.cli;

import bean.*;
import controller.GestisciSchedaController;
import controller.ClienteDashboardController;
import model.exception.InvalidFormException;
import util.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestisciSchedaCLIBoundary {

    private final Scanner scanner;
    private final GestisciSchedaController controller = new GestisciSchedaController();
    private final ClienteDashboardController dashboardController = new ClienteDashboardController();

    public static final String STRINGAERRORE = "Errore: ";

    // Costanti per sostituire gli Enum
    private static final String STATO_ASSOCIATO = "ASSOCIATO";
    private static final String STATO_NESSUNA = "NESSUNA";
    private static final String STATO_COMPLETATA = "COMPLETATA";
    private static final String STATO_PENDING = "PENDING";
    private static final String STATO_INLAVORAZIONE = "IN_LAVORAZIONE";

    public GestisciSchedaCLIBoundary(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        ClienteBean datiDashboard = dashboardController.getDatiDashboard();

        if (!datiDashboard.getStatoAssociazione().equals(STATO_ASSOCIATO)) {
            System.out.println("\nDevi essere associato a un Personal Trainer per poter gestire una scheda.");
            return;
        }

        String stato = datiDashboard.getStatoRichiesta();
        if (stato.equals(STATO_NESSUNA)) {
            System.out.println("\nNon hai ancora richiesto una scheda al trainer.");
            return;
        } else if (!stato.equals(STATO_COMPLETATA)) {
            if (stato.equals(STATO_PENDING)) {
                System.out.println("\nIl trainer non ha ancora visualizzato la tua richiesta di scheda.");
            } else if (stato.equals(STATO_INLAVORAZIONE)) {
                System.out.println("\nIl trainer non ha ancora completato la tua scheda.");
            }
            return;
        }

        // --- MENU GESTISCI SCHEDA ---
        boolean esci = false;
        while (!esci) {
            System.out.println("\n--- Gestisci Scheda ---");
            System.out.println("1. Visualizza la mia scheda");
            System.out.println("2. Registra sessione");
            System.out.println("3. Visualizza storico");
            System.out.println("0. Torna al menu");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1" -> visualizzaScheda();
                case "2" -> registraSessione();
                case "3" -> visualizzaStorico();
                case "0" -> esci = true;
                default  -> System.out.println("Scelta non valida.");
            }
        }
    }

    private void visualizzaScheda() {
        try {
            SchedaBean scheda = controller.getSchedaCliente();
            if (scheda == null) {
                System.out.println("\nNessuna scheda assegnata.");
                return;
            }

            System.out.println("\n=== La tua scheda ===");
            for (GiornoSchedaBean giorno : scheda.getGiorni()) {
                System.out.println("\n--- " + giorno.getNome() + " ---");
                for (EsercizioBean e : giorno.getEsercizi()) {
                    System.out.printf("  • %s  |  %dx%d  |  rec: %ds%s%n",
                            e.getNome(),
                            e.getSerie(),
                            e.getRipetizioni(),
                            e.getRecuperoSecondi(),
                            e.getNote().isBlank() ? "" : "  |  " + e.getNote());
                }
            }

        } catch (Exception e) {
            LogManager.error("[CLI] Errore caricamento scheda cliente", e);
            System.out.println(STRINGAERRORE + "Impossibile caricare la scheda in questo momento.");
        }
    }

    private void registraSessione() {
        try {
            SchedaBean scheda = controller.getSchedaCliente();
            if (scheda == null) {
                System.out.println("\nNessuna scheda assegnata.");
                return;
            }

            List<String> tuttiEsercizi = new ArrayList<>();
            for (GiornoSchedaBean g : scheda.getGiorni()) {
                for (EsercizioBean e : g.getEsercizi()) {
                    if (!tuttiEsercizi.contains(e.getNome())) {
                        tuttiEsercizi.add(e.getNome());
                    }
                }
            }

            System.out.println("\n--- Registra Sessione ---");
            System.out.println("Esercizi disponibili:");
            for (int i = 0; i < tuttiEsercizi.size(); i++) {
                System.out.println((i + 1) + ". " + tuttiEsercizi.get(i));
            }

            System.out.print("Seleziona esercizio (0 per annullare): ");
            int indice = parseSelezione(scanner.nextLine().trim());
            if (indice == -2 || indice < 0 || indice >= tuttiEsercizi.size()) {
                System.out.println("Operazione annullata.");
                return;
            }

            String nomeEsercizio = tuttiEsercizi.get(indice);

            System.out.print("Carico utilizzato (kg, es. 80.5): ");
            double carico = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            if (carico < 0) {
                System.out.println("Il carico non può essere negativo.");
                return;
            }

            System.out.print("Ripetizioni effettuate: ");
            int ripetizioni = Integer.parseInt(scanner.nextLine().trim());
            if (ripetizioni <= 0) {
                System.out.println("Le ripetizioni devono essere almeno 1.");
                return;
            }

            System.out.print("Note (Invio per saltare): ");
            String note = scanner.nextLine().trim();

            ProgressiBean bean = new ProgressiBean();
            // L'email NON viene più passata dalla View! Ci pensa il Controller via Sessione.
            bean.setNomeEsercizio(nomeEsercizio);
            bean.setCarico(carico);
            bean.setRipetizioni(ripetizioni);
            bean.setNote(note);

            controller.registraProgressi(bean);
            System.out.println("Progressi salvati!");

        } catch (NumberFormatException _) {
            System.out.println("Valore non valido.");
        } catch (InvalidFormException e) {
            System.out.println(STRINGAERRORE + e.getMessage());
        } catch (Exception e) {
            LogManager.error("[CLI] Errore salvataggio progressi", e);
            System.out.println(STRINGAERRORE + "Impossibile salvare i progressi in questo momento.");
        }
    }

    private void visualizzaStorico() {
        try {
            List<ProgressiBean> storico = controller.getStorico();
            if (storico.isEmpty()) {
                System.out.println("\nNessuna sessione registrata.");
                return;
            }

            System.out.println("\n=== Storico sessioni ===");
            for (ProgressiBean p : storico) {
                System.out.println("  " + p);
            }

        } catch (Exception e) {
            LogManager.error("[CLI] Errore caricamento storico", e);
            System.out.println(STRINGAERRORE + "Impossibile caricare lo storico in questo momento.");
        }
    }

    private int parseSelezione(String scelta) {
        try {
            return Integer.parseInt(scelta) - 1;
        } catch (NumberFormatException _) {
            System.out.println("Scelta non valida.");
            return -2;
        }
    }
}

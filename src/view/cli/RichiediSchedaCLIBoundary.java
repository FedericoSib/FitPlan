package view.cli;

import bean.ClienteBean;
import bean.RichiestaSchedaBean;
import controller.ClienteDashboardController;
import controller.RichiediSchedaController;
import model.exception.InvalidFormException;
import model.exception.TrainerNotAssociatedException;
import util.LogManager;

import java.util.Scanner;

public class RichiediSchedaCLIBoundary {

    public static final String ERR = "Errore: ";
    private final Scanner scanner;
    private final RichiediSchedaController controller = new RichiediSchedaController();
    private final ClienteDashboardController dashboardController = new ClienteDashboardController();

    public RichiediSchedaCLIBoundary(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        ClienteBean datiDashboard = dashboardController.getDatiDashboard();

        switch (datiDashboard.getStatoRichiesta()) {
            case "PENDING" -> {
                System.out.println("\nHai già una richiesta in attesa di valutazione.");
                return;
            }
            case "IN_LAVORAZIONE" -> {
                System.out.println("\nHai già una richiesta in corso. Attendi che il PT completi la scheda.");
                return;
            }
            case "COMPLETATA" -> {
                System.out.println("\nIl tuo PT ti ha inviato la scheda. Visualizzala nel menù.");
                return;
            }
            default -> {
                //Stato: NESSUNA si può procedere al caso d'uso.
            }
        }

        try {
            controller.verificaAssociazionePT();
        } catch (TrainerNotAssociatedException e) {
            System.out.println(ERR + e.getMessage());
            return;
        }

        System.out.println("\n--- Richiedi Scheda ---");
        System.out.println("\nCompila il form per richiedere la tua scheda:");

        String sesso = scegliSesso();
        if (sesso == null) return;

        int eta = leggiIntero("Inserisci la tua età: ", 10, 100);
        if (eta == -1) return;

        double peso = leggiDouble("Inserisci il tuo peso (kg): ", 1, 200);
        if (peso == -1) return;

        String obiettivo = scegliObiettivo();
        if (obiettivo == null) return;

        int frequenza = leggiIntero("Quante volte a settimana vuoi allenarti? (1-7): ", 1, 7);
        if (frequenza == -1) return;

        System.out.print("Note aggiuntive (premi Invio per saltare): ");
        String note = scanner.nextLine().trim();

        RichiestaSchedaBean bean = new RichiestaSchedaBean();
        bean.setClienteEmail(datiDashboard.getEmail());
        bean.setIdPersonalTrainer(datiDashboard.getIdPersonalTrainer());
        bean.setSesso(sesso);
        bean.setEta(eta);
        bean.setPeso(peso);
        bean.setObiettivo(obiettivo);
        bean.setFrequenzaSettimanale(frequenza);
        bean.setNote(note);

        try {
            controller.elaboraRichiesta(bean);
            System.out.println("\nRichiesta inviata con successo al tuo Personal Trainer!");
        } catch (InvalidFormException e) {
            System.out.println(ERR + e.getMessage());
        } catch (Exception e) {
            LogManager.error("[CLI] Errore imprevisto durante l'invio della richiesta scheda", e);
            System.out.println(ERR + "Impossibile inviare la richiesta in questo momento. Riprova più tardi.");
        }
    }

    private String scegliSesso() {
        System.out.println("\nSesso:");
        System.out.println("1. Maschio");
        System.out.println("2. Femmina");
        System.out.print("Scelta: ");
        String s = scanner.nextLine().trim();
        return switch (s) {
            case "1" -> "Maschio";
            case "2" -> "Femmina";
            default -> {
                System.out.println("Scelta non valida.");
                yield null;
            }
        };
    }

    private String scegliObiettivo() {
        System.out.println("\nObiettivo:");
        System.out.println("1. Aumento Massa");
        System.out.println("2. Definizione");
        System.out.println("3. Perdita Peso");
        System.out.println("4. Mantenimento");
        System.out.print("Scelta: ");
        String s = scanner.nextLine().trim();
        return switch (s) {
            case "1" -> "Aumento Massa";
            case "2" -> "Definizione";
            case "3" -> "Perdita Peso";
            case "4" -> "Mantenimento";
            default -> {
                System.out.println("Scelta non valida.");
                yield null;
            }
        };
    }

    private int leggiIntero(String prompt, int min, int max) {
        System.out.print(prompt);
        try {
            int valore = Integer.parseInt(scanner.nextLine().trim());
            if (valore < min || valore > max) {
                System.out.println("Valore fuori range (" + min + "-" + max + ").");
                return -1;
            }
            return valore;
        } catch (NumberFormatException _) {
            System.out.println("Inserisci un numero valido.");
            return -1;
        }
    }

    private double leggiDouble(String prompt, double min, double max) {
        System.out.print(prompt);
        try {
            double valore = Double.parseDouble(
                    scanner.nextLine().trim().replace(",", "."));
            if (valore < min || valore > max) {
                System.out.println("Valore fuori range (" + min + "-" + max + ").");
                return -1;
            }
            return valore;
        } catch (NumberFormatException _) {
            System.out.println("Inserisci un numero valido (es. 70.5).");
            return -1;
        }
    }
}

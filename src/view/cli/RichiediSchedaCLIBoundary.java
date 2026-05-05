package view.cli;

import bean.RichiestaSchedaBean;
import controller.cli.RichiediSchedaCLIController;
import model.Sessione;
import model.entity.Cliente;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import model.exception.TrainerNotAssociatedException;

import java.util.Scanner;

public class RichiediSchedaCLIBoundary {

    private final Scanner scanner;
    private final RichiediSchedaCLIController controller =
            new RichiediSchedaCLIController();

    public RichiediSchedaCLIBoundary(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        System.out.println("\n--- Richiedi Scheda ---");

        // Verifica associazione PT
        try {
            controller.verificaAssociazionePT();
        } catch (TrainerNotAssociatedException e) {
            System.out.println("Errore: " + e.getMessage());
            return;
        }

        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();

        // Raccolta dati step by step
        System.out.println("\nCompila il form per richiedere la tua scheda:");

        // Sesso
        String sesso = scegliSesso();
        if (sesso == null) return;

        // Età
        int eta = leggiIntero("Inserisci la tua età: ", 10, 100);
        if (eta == -1) return;

        // Peso
        double peso = leggiDouble("Inserisci il tuo peso (kg): ", 1, 200);
        if (peso == -1) return;

        // Obiettivo
        String obiettivo = scegliObiettivo();
        if (obiettivo == null) return;

        // Frequenza settimanale
        int frequenza = leggiIntero("Quante volte a settimana vuoi allenarti? (1-7): ", 1, 7);
        if (frequenza == -1) return;

        // Note
        System.out.print("Note aggiuntive (premi Invio per saltare): ");
        String note = scanner.nextLine().trim();

        // Costruzione Bean
        RichiestaSchedaBean bean = new RichiestaSchedaBean();
        bean.setClienteEmail(cliente.getEmail());
        bean.setIdPersonalTrainer(cliente.getIdPersonalTrainer());
        bean.setSesso(sesso);
        bean.setEta(eta);
        bean.setPeso(peso);
        bean.setObiettivo(obiettivo);
        bean.setFrequenzaSettimanale(frequenza);
        bean.setNote(note);

        // Invio
        try {
            controller.elaboraRichiesta(bean);
            System.out.println("\nRichiesta inviata con successo al tuo Personal Trainer!");
        } catch (InvalidFormException | DAOException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    //  HELPER
    // ─────────────────────────────────────────────

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

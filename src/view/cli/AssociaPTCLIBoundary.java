package view.cli;

import bean.AssociazioneBean;
import bean.PersonalTrainerBean;
import bean.ClienteBean;
import controller.AssociaPTController;
import controller.ClienteDashboardController;
import model.exception.TrainerNotFoundException;
import util.LogManager;

import java.util.List;
import java.util.Scanner;

public class AssociaPTCLIBoundary {

    private final Scanner scanner;
    private final AssociaPTController controller;
    private final ClienteDashboardController dashboardController;

    private static final String STATO_ASSOCIATO = "ASSOCIATO";
    private static final String STATO_PENDING = "PENDING";

    public AssociaPTCLIBoundary(Scanner scanner) {
        this.scanner = scanner;
        this.controller = new AssociaPTController();
        this.dashboardController = new ClienteDashboardController();
        controller.configuraObserverNotifiche();
    }

    public void avvia() {
        ClienteBean datiDashboard = dashboardController.getDatiDashboard();
        if (datiDashboard.getStatoAssociazione().equals(STATO_ASSOCIATO)) {
            System.out.println("\nSei già associato a un Personal Trainer.");
            return;
        }
        if (datiDashboard.getStatoAssociazione().equals(STATO_PENDING)) {
            System.out.println("\nHai già una richiesta di associazione in attesa.");
            return;
        }

        System.out.println("\n--- Cerca Personal Trainer ---");
        System.out.println("Puoi cercare per ID (PT-xxx), email o nome/cognome.");
        System.out.print("Ricerca: ");
        String query = scanner.nextLine().trim();

        if (query.isBlank()) {
            System.out.println("Ricerca vuota.");
            return;
        }

        try {
            List<PersonalTrainerBean> risultati = controller.cercaTrainer(query);

            System.out.println("\nRisultati trovati:");
            for (int i = 0; i < risultati.size(); i++) {
                PersonalTrainerBean pt = risultati.get(i);
                System.out.printf("%d. %s %s [%s] - %s%n",
                        i + 1, pt.getNome(), pt.getCognome(), pt.getId(), pt.getEmail());
            }

            System.out.print("\nSeleziona il numero del PT da associare (0 per annullare): ");
            String scelta = scanner.nextLine().trim();
            int indice = parseSelezione(scelta);
            if (indice == -2) return;
            if (indice < 0 || indice >= risultati.size()) {
                System.out.println("Operazione annullata.");
                return;
            }

            PersonalTrainerBean selezionato = risultati.get(indice);

            AssociazioneBean bean = new AssociazioneBean();

            bean.setEmailCliente(datiDashboard.getEmail());
            bean.setEmailPT(selezionato.getEmail());

            controller.inviaRichiestaAssociazione(bean);
            System.out.println("Richiesta inviata a " + selezionato.getNome() +
                    " " + selezionato.getCognome() + ". Sei in stato PENDING.");

        } catch (TrainerNotFoundException e) {
            System.out.println("Nessun PT trovato: " + e.getMessage());
        } catch (Exception e) {
            LogManager.error("[CLI] Errore invio richiesta associazione", e);
            System.out.println("Errore tecnico durante l'invio della richiesta.");
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
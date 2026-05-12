package view.cli;

import bean.AssociazioneBean;
import controller.GestisciRichiestePTController;
import model.Sessione;
import model.dao.DAOFactory;
import model.exception.DAOException;
import util.observer.NotificaManager;

import java.util.List;
import java.util.Scanner;

public class GestisciRichiesteCLIBoundary {

    private final Scanner scanner;
    private final GestisciRichiestePTController controller;

    public GestisciRichiesteCLIBoundary(Scanner scanner) {
        this.scanner = scanner;
        this.controller = new GestisciRichiestePTController();
        controller.aggiungiObserver(new NotificaManager(DAOFactory.getNotificaDAO()));
    }
    public void avvia() {
        String emailPT = Sessione.getInstance().getUtente().getEmail();

        try {
            List<AssociazioneBean> richieste = controller.getRichiesteSospese(emailPT);

            if (richieste.isEmpty()) {
                System.out.println("\nNessuna richiesta di associazione in attesa.");
                return;
            }

            System.out.println("\n--- Richieste di Associazione in Attesa ---");
            for (int i = 0; i < richieste.size(); i++) {
                System.out.printf("%d. Cliente: %s%n", i + 1,
                        richieste.get(i).getEmailCliente());
            }

            System.out.print("\nSeleziona il numero della richiesta da gestire (0 per tornare): ");
            String scelta = scanner.nextLine().trim();
            int indice = parseSelezione(scelta);
            if (indice == -2) return;
            if (indice < 0 || indice >= richieste.size()) {
                System.out.println("Operazione annullata.");
                return;
            }

            AssociazioneBean selezionata = richieste.get(indice);
            System.out.println("\nRichiesta di: " + selezionata.getEmailCliente());
            System.out.println("1. Accetta");
            System.out.println("2. Rifiuta");
            System.out.println("0. Annulla");
            System.out.print("Scelta: ");

            String azione = scanner.nextLine().trim();
            switch (azione) {
                case "1" -> {
                    controller.accettaAssociazione(selezionata);
                    System.out.println("Richiesta accettata.");
                }
                case "2" -> {
                    controller.rifiutaAssociazione(selezionata);
                    System.out.println("Richiesta rifiutata.");
                }
                default -> System.out.println("Operazione annullata.");
            }

        } catch (DAOException e) {
            System.out.println("Errore: " + e.getMessage());
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

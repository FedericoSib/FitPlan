package view.cli;

import bean.AssociazioneBean;
import bean.PersonalTrainerBean;
import controller.cli.AssociaPTCLIController;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.Cliente;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import model.exception.TrainerNotFoundException;
import util.observer.NotificaManager;

import java.util.List;
import java.util.Scanner;

public class AssociaPTCLIBoundary {

    private final Scanner scanner;
    private final AssociaPTCLIController controller;

    public AssociaPTCLIBoundary(Scanner scanner) {
        this.scanner = scanner;
        this.controller = new AssociaPTCLIController();
        controller.aggiungiObserver(new NotificaManager(DAOFactory.getNotificaDAO()));
    }

    public void avvia() {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();
        if (cliente.getStatoAssociazione() == StatoAssociazione.ASSOCIATO) {
            System.out.println("\nSei già associato a un Personal Trainer.");
            return;
        }
        if (cliente.getStatoAssociazione() == StatoAssociazione.PENDING) {
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
            String emailCliente = Sessione.getInstance().getUtente().getEmail();

            AssociazioneBean bean = new AssociazioneBean();
            bean.setEmailCliente(emailCliente);
            bean.setEmailPT(selezionato.getEmail());

            controller.inviaRichiestaAssociazione(bean);
            System.out.println("Richiesta inviata a " + selezionato.getNome() +
                    " " + selezionato.getCognome() + ". Sei in stato PENDING.");

        } catch (TrainerNotFoundException e) {
            System.out.println("Nessun PT trovato: " + e.getMessage());
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

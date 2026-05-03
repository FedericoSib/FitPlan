package view.cli;

import bean.LoginBean;
import controller.cli.LoginCLIController;
import model.Sessione;
import model.entity.PersonalTrainer;
import model.exception.LoginException;

import java.util.Scanner;

public class LoginCLIBoundary {

    private final Scanner scanner;
    private final LoginCLIController controller = new LoginCLIController();

    public LoginCLIBoundary(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        System.out.println("\n--- Login ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        LoginBean bean = new LoginBean();
        bean.setEmail(email);
        bean.setPassword(password);

        try {
            controller.autentica(bean);
            System.out.println("Benvenuto, " +
                    Sessione.getInstance().getUtente().getNome() + "!");

            // Mostra notifiche pendenti CLI
            mostraNotifichePendenti(email);

            // Smista al menu corretto
            if (Sessione.getInstance().getUtente() instanceof PersonalTrainer) {
                new MenuPTCLI(scanner).avvia();
            } else {
                new MenuClienteCLI(scanner).avvia();
            }

        } catch (LoginException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void mostraNotifichePendenti(String email) {
        try {
            var dao = model.dao.DAOFactory.getNotificaDAO();
            var notifiche = dao.caricaECancellaNotifiche(email);
            if (!notifiche.isEmpty()) {
                System.out.println("\n--- Notifiche ---");
                notifiche.forEach(n -> System.out.println("  • " + n));
                System.out.println("-----------------");
            }
        } catch (Exception _) {
            // Notifiche non bloccanti
        }
    }
}

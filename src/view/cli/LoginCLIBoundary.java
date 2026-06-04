package view.cli;

import bean.LoginBean;
import bean.UtenteBean;
import controller.LoginController;
import model.dao.DAOFactory;
import model.exception.LoginException;
import util.observer.NotificaManager;

import java.util.List;
import java.util.Scanner;

public class LoginCLIBoundary {

    private final Scanner scanner;
    private final LoginController controller = new LoginController();

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

            UtenteBean utenteLoggato = controller.getUtenteLoggato();
            System.out.println("Benvenuto, " + utenteLoggato.getNome() + "!");

            mostraNotifichePendenti(email);

            if (utenteLoggato.getRuolo() == 2) {
                new MenuPTCLI(scanner).avvia();
            } else {
                new MenuClienteCLI(scanner).avvia();
            }

        } catch (LoginException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void mostraNotifichePendenti(String email) {
        NotificaManager manager = new NotificaManager(DAOFactory.getNotificaDAO());

        List<String> notifiche = manager.ottieniMessaggiPendenti(email);

        if (!notifiche.isEmpty()) {
            System.out.println("\n--- Notifiche ---");
            for (String n : notifiche) {
                System.out.println("  • " + n);
            }
            System.out.println("-----------------");
        }
    }
}

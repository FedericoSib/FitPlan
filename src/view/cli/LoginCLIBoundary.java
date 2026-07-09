package view.cli;

import bean.LoginBean;
import bean.UtenteBean;
import controller.LoginController;
import model.exception.LoginException;
import util.LogManager;

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

            mostraNotifichePendenti();

            if (utenteLoggato.getRuolo() == 2) {
                new MenuPTCLI(scanner).avvia();
            } else {
                new MenuClienteCLI(scanner).avvia();
            }

        } catch (LoginException e) {
            System.out.println("Errore: " + e.getMessage());
        } catch (Exception e) {
            LogManager.error("[CLI] Errore imprevisto durante il login", e);
            System.out.println("Errore tecnico durante l'accesso al sistema.");
        }

    }

    private void mostraNotifichePendenti() {
        try {
            List<String> notifiche = controller.getNotifichePendenti();
            if (notifiche != null && !notifiche.isEmpty()) {
                System.out.println("\n--- Notifiche ---");
                for (String n : notifiche) {
                    System.out.println("  • " + n);
                }
                System.out.println("-----------------");
            }
        } catch (Exception e) {
            LogManager.error("[CLI] Errore recupero notifiche pendenti", e);
        }
    }
}

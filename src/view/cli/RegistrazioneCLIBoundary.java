package view.cli;

import bean.RegistrazioneBean;
import controller.RegistrazioneController;
import model.exception.RegistrazioneException;
import util.LogManager;

import java.util.Scanner;

public class RegistrazioneCLIBoundary {

    private final Scanner scanner;
    private final RegistrazioneController controller = new RegistrazioneController();

    public RegistrazioneCLIBoundary(Scanner scanner) {
        this.scanner = scanner;
    }

    public void avvia() {
        System.out.println("\n--- Registrazione ---");

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Cognome: ");
        String cognome = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.println("Ruolo:");
        System.out.println("1. Cliente");
        System.out.println("2. Personal Trainer");
        System.out.print("Scelta: ");
        String ruoloStr = scanner.nextLine().trim();
        int ruolo = ruoloStr.equals("2") ? 2 : 1;

        RegistrazioneBean bean = new RegistrazioneBean();
        bean.setNome(nome);
        bean.setCognome(cognome);
        bean.setEmail(email);
        bean.setPassword(password);
        bean.setRuolo(ruolo);

        try {
            controller.registraNuovoUtente(bean);
            System.out.println("Registrazione completata! Ora puoi effettuare il login.");
        } catch (RegistrazioneException e) {
            System.out.println("Errore: " + e.getMessage());
        } catch (Exception e) {
            LogManager.error("[CLI] Errore imprevisto durante la registrazione", e);
            System.out.println("Errore tecnico durante la registrazione. Riprova più tardi.");
        }
    }
}

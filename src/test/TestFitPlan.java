package test;

import controller.RichiediSchedaController;
import model.dao.DAOFactory;
import model.entity.RichiestaScheda;
import model.exception.InvalidFormException;

import java.util.Scanner;

public class TestFitPlan {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RichiediSchedaController controller = new RichiediSchedaController();

        // 1. Configurazione Iniziale
        System.out.println("=== TEST SISTEMA FITPLAN ===");
        System.out.println("1) Modalità DEMO (RAM)\n2) Modalità FULL (File)");
        System.out.print("Scegli: ");
        int scelta = scanner.nextInt();
        DAOFactory.setMode(scelta);

        // 2. Simulazione inserimento dati form
        // Creiamo un oggetto RichiestaScheda (normalmente fatto dalla GUI)
        RichiestaScheda nuovaRichiesta = new RichiestaScheda(
                "M", 25, 80.5, "Aumento massa", 4,
                "Nessuna nota", "atleta@email.it", "PT_MARCO_01"
        );

        try {
            System.out.println("\nTentativo di invio richiesta...");
            controller.elaboraRichiesta(nuovaRichiesta);
            System.out.println("OPERAZIONE COMPLETATA CON SUCCESSO!");

        } catch (InvalidFormException e) {
            // Qui catturiamo l'eccezione personalizzata (Requisito 5)
            System.err.println("ERRORE DI VALIDAZIONE: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERRORE GENERICO: " + e.getMessage());
        }

        // 3. Test della "Error Logic" (Proviamo a inserire un peso assurdo)
        System.out.println("\n--- Test Eccezione (Peso Errato) ---");
        RichiestaScheda richiestaErrata = new RichiestaScheda(
                "F", 22, -10, "Dimagrimento", 3, "", "test@email.it", "PT_TEST"
        );

        try {
            controller.elaboraRichiesta(richiestaErrata);
        } catch (InvalidFormException e) {
            System.out.println("Eccezione catturata correttamente: " + e.getMessage());
        }
    }
}
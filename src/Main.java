import model.dao.DAOFactory;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== FitPlan Application ===");
        System.out.println("Seleziona la modalità di esecuzione:");
        System.out.println("1) Demo-version (Dati in memoria)");
        System.out.println("2) Full-version (Dati su file)");
        System.out.print("Scelta: ");

        int scelta = scanner.nextInt();

        // Inizializziamo la factory con la scelta dell'utente
        DAOFactory.setMode(scelta);

        // Ora l'applicazione può procedere...
        // Esempio: far partire la GUI o la CLI
        System.out.println("Applicazione pronta!");
    }
}
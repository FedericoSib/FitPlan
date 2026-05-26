package view.cli;

import bean.*;
import controller.AssemblaSchedaController;
import model.Sessione;
import model.dao.DAOFactory;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import util.LogManager;
import util.observer.NotificaManager;

import java.util.List;
import java.util.Scanner;

public class AssemblaSchedaCLIBoundary {

    private final Scanner scanner;
    private final AssemblaSchedaController controller;
    public static final String SCELTANONVALIDA = "Scelta non valida.";

    public AssemblaSchedaCLIBoundary(Scanner scanner) {
        this.scanner = scanner;
        this.controller = new AssemblaSchedaController();
        controller.aggiungiObserver(new NotificaManager(DAOFactory.getNotificaDAO()));
    }

    public void avvia() {
        boolean esci = false;
        while (!esci) {
            System.out.println("\n--- Assembla Scheda ---");
            System.out.println("1. Nuove richieste");
            System.out.println("2. Richieste in lavorazione");
            System.out.println("0. Torna al menu");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1" -> gestisciRichieste(false);
                case "2" -> gestisciRichieste(true);
                case "0" -> esci = true;
                default  -> System.out.println(SCELTANONVALIDA);
            }
        }
    }

    private void gestisciRichieste(boolean inLavorazione) {
        String emailPT = Sessione.getInstance().getUtente().getEmail();

        try {
            List<RichiestaSchedaBean> richieste = inLavorazione
                    ? controller.getRichiesteInLavorazione(emailPT)
                    : controller.getRichiestePerPT(emailPT);

            if (richieste.isEmpty()) {
                System.out.println("\nNessuna richiesta " +
                        (inLavorazione ? "in lavorazione" : "in attesa") + ".");
                return;
            }

            // Mostra lista richieste
            System.out.println("\n--- Richieste " +
                    (inLavorazione ? "in lavorazione" : "in attesa") + " ---");
            for (int i = 0; i < richieste.size(); i++) {
                RichiestaSchedaBean r = richieste.get(i);
                System.out.printf("%d. %s — %s | %s | %dgg/sett%n",
                        i + 1,
                        r.getClienteEmail(),
                        r.getObiettivo(),
                        r.getSesso(),
                        r.getFrequenzaSettimanale());
            }

            System.out.print("\nSeleziona richiesta (0 per tornare): ");
            int indice = parseSelezione(scanner.nextLine().trim());
            if (indice == -2 || indice < 0 || indice >= richieste.size()) {
                System.out.println("Operazione annullata.");
                return;
            }

            RichiestaSchedaBean selezionata = richieste.get(indice);
            mostraDettagli(selezionata);

            // Menu azioni
            System.out.println("\n1. Assembla subito");
            if (!inLavorazione) System.out.println("2. Assembla in seguito");
            System.out.println("0. Annulla");
            System.out.print("Scelta: ");

            String azione = scanner.nextLine().trim();
            switch (azione) {
                case "1" -> assemblaScheda(selezionata);
                case "2" -> {
                    if (!inLavorazione) {
                        controller.segnaInLavorazione(selezionata.getClienteEmail());
                        System.out.println("Richiesta spostata in lavorazione.");
                    } else {
                        System.out.println(SCELTANONVALIDA);
                    }
                }
                default -> System.out.println("Operazione annullata.");
            }

        } catch (DAOException e) {
            LogManager.error("[CLI] Errore caricamento richieste scheda", e);
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private void mostraDettagli(RichiestaSchedaBean r) {
        System.out.println("\n--- Dettagli richiesta ---");
        System.out.println("Cliente:    " + r.getClienteEmail());
        System.out.println("Sesso:      " + r.getSesso());
        System.out.println("Età:        " + r.getEta() + " anni");
        System.out.println("Peso:       " + r.getPeso() + " kg");
        System.out.println("Obiettivo:  " + r.getObiettivo());
        System.out.println("Frequenza:  " + r.getFrequenzaSettimanale() + " volte/settimana");
        System.out.println("Note:       " +
                (r.getNote().isBlank() ? "—" : r.getNote()));
    }

    private void assemblaScheda(RichiestaSchedaBean richiesta) {
        System.out.println("\n--- Assembla Scheda per " + richiesta.getClienteEmail() + " ---");
        System.out.println("Frequenza settimanale: " + richiesta.getFrequenzaSettimanale() + " giorni");

        SchedaBean schedaBean = costruisciSchedaBean(richiesta);
        inviaSchedaAssemblata(schedaBean, richiesta.getClienteEmail());
    }

    private SchedaBean costruisciSchedaBean(RichiestaSchedaBean richiesta) {
        SchedaBean schedaBean = new SchedaBean();
        schedaBean.setEmailCliente(richiesta.getClienteEmail());
        schedaBean.setEmailPT(Sessione.getInstance().getUtente().getEmail());

        int totalGiorni = richiesta.getFrequenzaSettimanale();
        for (int i = 1; i <= totalGiorni; i++) {
            System.out.println("\n=== Giorno " + i + " di " + totalGiorni + " ===");
            GiornoSchedaBean giorno = creaGiorno(i);
            schedaBean.getGiorni().add(giorno);
        }
        return schedaBean;
    }

    private GiornoSchedaBean creaGiorno(int numeroGiorno) {
        System.out.print("Nome giorno (Invio per 'Giorno " + numeroGiorno + "'): ");
        String nomeGiorno = scanner.nextLine().trim();
        if (nomeGiorno.isBlank()) nomeGiorno = "Giorno " + numeroGiorno;

        GiornoSchedaBean giorno = new GiornoSchedaBean(nomeGiorno);
        gestisciEserciziGiorno(giorno);
        return giorno;
    }

    private void gestisciEserciziGiorno(GiornoSchedaBean giorno) {
        boolean continua = true;
        while (continua) {
            mostraEserciziGiorno(giorno);
            continua = gestisciAzioneEsercizio(giorno);
        }
    }

    private void mostraEserciziGiorno(GiornoSchedaBean giorno) {
        System.out.println("\n  Esercizi in " + giorno.getNome() + ":");
        List<EsercizioBean> esercizi = giorno.getEsercizi();
        if (esercizi.isEmpty()) {
            System.out.println("  (nessun esercizio)");
        } else {
            for (int j = 0; j < esercizi.size(); j++) {
                System.out.println("  " + (j + 1) + ". " + esercizi.get(j));
            }
        }
        System.out.println("\n  1. Aggiungi esercizio");
        if (!esercizi.isEmpty()) System.out.println("  2. Rimuovi esercizio");
        System.out.println("  0. Passa al giorno successivo");
        System.out.print("  Scelta: ");
    }

    private boolean gestisciAzioneEsercizio(GiornoSchedaBean giorno) {
        String scelta = scanner.nextLine().trim();
        return switch (scelta) {
            case "1" -> {
                EsercizioBean e = raccogliEsercizio();
                if (e != null) giorno.aggiungiEsercizio(e);
                yield true;
            }
            case "2" -> {
                if (!giorno.getEsercizi().isEmpty()) rimuoviEsercizio(giorno);
                yield true;
            }
            case "0" -> {
                if (giorno.getEsercizi().isEmpty()) {
                    System.out.println("  Aggiungi almeno un esercizio prima di continuare.");
                    yield true;
                }
                yield false;
            }
            default -> {
                System.out.println("  Scelta non valida.");
                yield true;
            }
        };
    }

    private void inviaSchedaAssemblata(SchedaBean schedaBean, String emailCliente) {
        try {
            controller.inviaScheda(schedaBean);
            System.out.println("\nScheda inviata con successo a " + emailCliente + "!");
        } catch (InvalidFormException | DAOException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private EsercizioBean raccogliEsercizio() {
        System.out.print("  Nome esercizio: ");
        String nome = scanner.nextLine().trim();
        if (nome.isBlank()) {
            System.out.println("  Nome obbligatorio.");
            return null;
        }

        int serie = leggiIntero("  Serie (1-20): ", 1, 20);
        if (serie == -1) return null;

        int ripetizioni = leggiIntero("  Ripetizioni (1-50): ", 1, 50);
        if (ripetizioni == -1) return null;

        int recupero = leggiIntero("  Recupero in secondi (15-300): ", 15, 300);
        if (recupero == -1) return null;

        System.out.print("  Note (Invio per saltare): ");
        String note = scanner.nextLine().trim();

        EsercizioBean bean = new EsercizioBean();
        bean.setNome(nome);
        bean.setSerie(serie);
        bean.setRipetizioni(ripetizioni);
        bean.setRecuperoSecondi(recupero);
        bean.setNote(note);
        return bean;
    }

    private void rimuoviEsercizio(GiornoSchedaBean giorno) {
        List<EsercizioBean> esercizi = giorno.getEsercizi();
        System.out.print("  Numero esercizio da rimuovere: ");
        int indice = parseSelezione(scanner.nextLine().trim());
        if (indice >= 0 && indice < esercizi.size()) {
            esercizi.remove(indice);
            System.out.println("  Esercizio rimosso.");
        } else {
            System.out.println("  Selezione non valida.");
        }
    }

    private int parseSelezione(String scelta) {
        try {
            return Integer.parseInt(scelta) - 1;
        } catch (NumberFormatException _) {
            System.out.println(SCELTANONVALIDA);
            return -2;
        }
    }

    private int leggiIntero(String prompt, int min, int max) {
        System.out.print(prompt);
        try {
            int valore = Integer.parseInt(scanner.nextLine().trim());
            if (valore < min || valore > max) {
                System.out.println("  Valore fuori range (" + min + "-" + max + ").");
                return -1;
            }
            return valore;
        } catch (NumberFormatException _) {
            System.out.println("  Inserisci un numero valido.");
            return -1;
        }
    }
}

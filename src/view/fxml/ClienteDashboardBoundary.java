package view.fxml;

import config.*;
import controller.graphic.RichiediSchedaController;
import javafx.scene.shape.Circle;
import model.dao.DAOFactory;
import util.observer.*;
import bean.UtenteBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import java.io.IOException;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.Sessione;
import model.entity.*;
import util.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class ClienteDashboardBoundary {

    private RichiediSchedaController richiestaController = new RichiediSchedaController();
    private static final String CSS_PATH_KEY = "style.css.path";
    String cssPath = AppConfig.get(CSS_PATH_KEY);
    @FXML private Label lblNomeUtente;
    @FXML private Label lblMeseAnno;
    @FXML private GridPane gridCalendario;
    @FXML private ScrollPane scrollPrincipale;
    @FXML private ImageView imgFitplan;
    @FXML private ImageView imgGestisciScheda;
    @FXML private ImageView imgRichiediScheda;
    @FXML private ImageView imgAssociaPT;
    @FXML private ImageView imgGestisciScheda2;
    @FXML private ImageView imgRichiediScheda2;
    @FXML private ImageView imgAssociaPT2;
    @FXML private Circle cerchioStato;
    @FXML private Label lblStatoAssociazione;
    @FXML private Label lblNomePT;
    @FXML private Label lblStatoRichiesta;
    @FXML private Label lblObiettivoRichiesta;
    @FXML private Label lblDataRichiesta;
    @FXML private Label lblSchedaInfo;
    @FXML private Label lblSchedaDettaglio;


    @FXML
    public void initialize() {
        final String IMAGE_LOAD_ERROR = "Impossibile caricare immagine: ";
        // 1. Recupero dati utente dalla sessione
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();
        lblNomeUtente.setText(cliente.getNome() + " " + cliente.getCognome());
        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
             imgFitplan.setImage(logo);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ", e);
        }
        try {
            Image  associaPT= new Image(getClass().getResourceAsStream("/view/Immages/AssociaPT.png"));
            imgAssociaPT.setImage(associaPT);
            imgAssociaPT2.setImage(associaPT);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR, e);
        }
        try {
            Image gestisciScheda = new Image(getClass().getResourceAsStream("/view/Immages/GestisciScheda.png"));
            imgGestisciScheda.setImage(gestisciScheda);
            imgGestisciScheda2.setImage(gestisciScheda);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR, e);
        }
        try {
            Image richiediScheda = new Image(getClass().getResourceAsStream("/view/Immages/RichiediScheda.png"));
            imgRichiediScheda.setImage(richiediScheda);
            imgRichiediScheda2.setImage(richiediScheda);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR, e);
        }

        // 2. Setup dinamico del calendario
        costruisciCalendarioDinamico();
        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        NotificaManager manager = new NotificaManager(DAOFactory.getNotificaDAO());
        manager.mostraNotifichePendenti(c.getEmail());

        popolaInfoCard();
    }

    private void costruisciCalendarioDinamico() {
        gridCalendario.getChildren().clear(); // Pulizia per ricaricamento

        LocalDate oggi = LocalDate.now();
        YearMonth meseCorrente = YearMonth.now();

        // Imposta la Label superiore (es. MARZO 2026)
        String titoloMese = oggi.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN).toUpperCase();
        lblMeseAnno.setText(titoloMese + " " + oggi.getYear());

        // Calcoliamo dove inizia il mese (1 = Lunedì, 7 = Domenica)
        int primoGiornoDelMese = meseCorrente.atDay(1).getDayOfWeek().getValue();
        int giorniNelMese = meseCorrente.lengthOfMonth();

        // Riempimento Griglia
        int riga = 0;
        // Iniziamo a scrivere dal giorno corretto (offset basato sul primo giorno)
        for (int giorno = 1; giorno <= giorniNelMese; giorno++) {
            int colonna = (giorno + primoGiornoDelMese - 2) % 7;
            riga = (giorno + primoGiornoDelMese - 2) / 7;

            Label lblGiorno = new Label(String.valueOf(giorno));
            lblGiorno.setPrefSize(40, 40);
            lblGiorno.setAlignment(Pos.CENTER);

            // Se è il giorno di oggi, applichiamo lo stile speciale CSS
            if (giorno == oggi.getDayOfMonth()) {
                lblGiorno.getStyleClass().add("giorno-attuale");
            }

            gridCalendario.add(lblGiorno, colonna, riga);
        }
    }

    private void mostraPopupErroreNoPT() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/SchermataErroreNoPT.fxml"));
        Parent root = loader.load();

        Stage popupStage = new Stage();
        // Assicurati che imgFitplan sia accessibile (o usa un altro nodo della scena)
        popupStage.initOwner(imgFitplan.getScene().getWindow());
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        scene.getStylesheets().add(
                getClass().getResource(AppConfig.get(CSS_PATH_KEY)).toExternalForm()
        );

        popupStage.setScene(scene);
        popupStage.show();
    }

    private void popolaInfoCard() {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();
        popolaCardAssociazione(cliente);
        popolaCardRichiesta(cliente);
        popolaCardScheda(cliente);
    }
    private void popolaCardAssociazione(Cliente cliente) {
        StatoAssociazione stato = cliente.getStatoAssociazione();

        switch (stato) {
            case ASSOCIATO -> {
                cerchioStato.setFill(javafx.scene.paint.Color.web("#00b894")); // verde
                lblStatoAssociazione.setText("Associato");
                lblNomePT.setText("Personal Trainer: " + cliente.getIdPersonalTrainer());
            }
            case PENDING -> {
                cerchioStato.setFill(javafx.scene.paint.Color.web("#fdcb6e")); // giallo
                lblStatoAssociazione.setText("Richiesta in attesa...");
                lblNomePT.setText("In attesa di conferma dal PT");
            }
            default -> {
                cerchioStato.setFill(javafx.scene.paint.Color.web("#d63031")); // rosso
                lblStatoAssociazione.setText("Nessun Personal Trainer");
                lblNomePT.setText("Clicca per cercare un PT");
            }
        }
    }

    private void popolaCardRichiesta(Cliente cliente) {
        try {
            var dao = model.dao.DAOFactory.getRichiestaDAO();
            boolean haRichiesta = dao.esisteRichiestaAttiva(cliente.getEmail());

            if (haRichiesta) {
                // Cerca la richiesta attiva per mostrare i dettagli
                var richieste = dao.prendiRichiestePerPT(cliente.getIdPersonalTrainer());
                richieste.stream()
                        .filter(r -> r.getClienteEmail().equalsIgnoreCase(cliente.getEmail()))
                        .findFirst()
                        .ifPresent(r -> {
                            lblStatoRichiesta.setText("⏳ Richiesta in attesa");
                            lblStatoRichiesta.setTextFill(javafx.scene.paint.Color.web("#fdcb6e"));
                            lblObiettivoRichiesta.setText("Obiettivo: " + r.getObiettivo());
                            lblDataRichiesta.setText("Frequenza: " + r.getFrequenzaSettimanale() + " giorni/settimana");
                        });
            } else {
                lblStatoRichiesta.setText("Nessuna richiesta attiva");
                lblStatoRichiesta.setTextFill(javafx.scene.paint.Color.web("#636e72"));
                lblObiettivoRichiesta.setText(
                        cliente.getStatoAssociazione() == StatoAssociazione.ASSOCIATO
                                ? "Clicca per richiedere la tua scheda"
                                : "Prima associati a un PT");
                lblDataRichiesta.setText("");
            }
        } catch (Exception e) {
            util.LogManager.error("Errore caricamento info richiesta", e);
        }
    }

    private void popolaCardScheda(Cliente cliente) {
        try {
            var schede = model.dao.DAOFactory.getSchedaDAO()
                    .getSchedePerCliente(cliente.getEmail());

            if (schede.isEmpty()) {
                lblSchedaInfo.setText("Nessuna scheda ancora disponibile");
                lblSchedaInfo.setTextFill(javafx.scene.paint.Color.web("#d63031"));
                lblSchedaDettaglio.setText("Richiedila ora al tuo Personal Trainer!");
            } else {
                var scheda = schede.get(schede.size() - 1);
                lblSchedaInfo.setText("✓ Scheda disponibile");
                lblSchedaInfo.setTextFill(javafx.scene.paint.Color.web("#00b894"));
                lblSchedaDettaglio.setText(scheda.getGiorni().size() + " giorni di allenamento • Clicca per visualizzarla");
            }
        } catch (Exception e) {
            util.LogManager.error("Errore caricamento info scheda", e);
        }
    }


    // --- LOGICA DI NAVIGAZIONE (SCROLL) ---

    @FXML
    public void scrollaGestione() {
        // Vvalue 0.0 è la parte superiore della ScrollPane
        scrollPrincipale.setVvalue(0.0);
    }

    @FXML
    public void scrollaAssocia() {
        // Vvalue 0.5 è la metà (assicurati che il pannello "Associa" sia al centro)
        scrollPrincipale.setVvalue(0.5);
    }

    @FXML
    public void scrollaRichiedi() {
        // Vvalue 1.0 è il fondo della pagina
        scrollPrincipale.setVvalue(1.0);
    }

    // --- AZIONI DEI BOTTONI ---

    @FXML
    public void apriGestisciScheda() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/fxml/ClienteGestisciScheda.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(lblNomeUtente.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) {
            LogManager.error("Errore apertura gestisci scheda", e);
        }
    }

    @FXML
    public void avviaRichiestaScheda() {
        // Recupero dell'utente dalla sessione
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();
        StatoAssociazione stato = cliente.getStatoAssociazione();

        try {
            switch (stato) {
                case NESSUNA -> mostraPopupErroreNoPT();
                case PENDING -> AlertManager.mostra("Il tuo Trainer non ha ancora accettato la richiesta.");
                case ASSOCIATO -> {
                    if (richiestaController.verificaPresenzaRichiesta(cliente.getEmail())) {
                        AlertManager.mostra("Hai già una richiesta di scheda in sospeso. Attendi la risposta del PT.");
                    } else {
                        caricaInterfacciaRichiesta();
                    }
                }
                default -> LogManager.warn("Stato associazione non gestito: " + stato);
            }
        } catch (IOException e) {
            LogManager.error("Errore durante l'avvio della richiesta scheda", e);
        }
    }
    private void caricaInterfacciaRichiesta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/RichiestaScheda.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(lblNomeUtente.getScene().getWindow());
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.show();

        } catch (IOException e) {
            LogManager.error("Errore generico apertura pop-up richieste", e);
        }
    }

    @FXML
    public void apriProfiloPersonale() {
        Utente utenteCorrente = Sessione.getInstance().getUtente();

        UtenteBean bean = new UtenteBean();
        bean.setNome(utenteCorrente.getNome());
        bean.setCognome(utenteCorrente.getCognome());
        bean.setEmail(utenteCorrente.getEmail());
        bean.setRuolo(utenteCorrente.getRuolo());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/ProfiloPersonaleCliente.fxml"));
            Parent root = loader.load();

            // Otteniamo il controller generico
            ProfiloPersonaleBoundary controller = loader.getController();
            controller.setDatiUtente(bean); // Passiamo l'utente (Atleta o PT)

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(lblNomeUtente.getScene().getWindow()); // Usa una label qualsiasi della dashboard
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.show();

        } catch (IOException e) {
            LogManager.error("Errore generico apertura pop-up profilo personale", e);
        }
    }

    @FXML
    public void apriRicercaPT() {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();

        if (cliente.getStatoAssociazione() == StatoAssociazione.PENDING) {
            AlertManager.mostra("Hai una richiesta pendente. Attendi la risposta del Trainer.");
            return;
        }

        if (cliente.getStatoAssociazione() == StatoAssociazione.ASSOCIATO) {
            AlertManager.mostra("Sei già associato a un Trainer!");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/ClienteRicercaPT.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();

            popupStage.initOwner(lblNomeUtente.getScene().getWindow());
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(
                    getClass().getResource(AppConfig.get(CSS_PATH_KEY)).toExternalForm()
            );

            popupStage.setScene(scene);
            popupStage.show();

            LogManager.info("Popup Ricerca PT aperto correttamente.");

        } catch (IOException e) {
            LogManager.error("Impossibile aprire il popup di ricerca PT", e);
        }
    }

    @FXML
    public void handleLogout() {
        // 1. Pulizia della sessione: impostiamo l'utente a null
        // Questo è fondamentale per la sicurezza e per i check dei controller
        Sessione.getInstance().setUtente(null);

        // 2. Feedback (opzionale ma consigliato)
        LogManager.info("Logout effettuato con successo. Reindirizzamento al login...");

        // 3. Navigazione verso la schermata di Login
        // Usiamo il tuo Navigator per cambiare scena
        Navigator.pushScene("/view/fxml/Login.fxml", "FitPlan - Login");
    }
}
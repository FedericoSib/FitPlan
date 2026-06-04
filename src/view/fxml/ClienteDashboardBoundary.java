package view.fxml;

import config.AppConfig;
import controller.ClienteDashboardController;
import controller.RichiediSchedaController;
import bean.ClienteBean;
import bean.UtenteBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import model.Sessione;
import model.entity.StatoAssociazione;
import model.entity.StatoRichiesta;
import model.entity.Utente;
import util.LogManager;
import model.dao.DAOFactory;
import util.observer.NotificaManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class ClienteDashboardBoundary {

    private final ClienteDashboardController dashboardController = new ClienteDashboardController();
    private final RichiediSchedaController richiestaController = new RichiediSchedaController();
    private ClienteBean datiDashboard;

    private static final String CSS_PATH_KEY = "style.css.path";
    private static final String GIALLO = "#fdcb6e";
    private static final String VERDE = "#00b894";
    public static final String ATT = "Attenzione";

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
        caricaImmagini();
        costruisciCalendarioDinamico();
        aggiornaSchermata();
    }

    private void aggiornaSchermata() {
        this.datiDashboard = dashboardController.getDatiDashboard();
        lblNomeUtente.setText(datiDashboard.getNomeCompleto());
        NotificaManager manager = new NotificaManager(DAOFactory.getNotificaDAO());
        manager.mostraNotifichePendenti(datiDashboard.getEmail());

        popolaInfoCard();
    }

    private void caricaImmagini() {
        final String IMAGE_LOAD_ERROR = "Impossibile caricare immagine: ";
        try {
            imgFitplan.setImage(new Image(getClass().getResourceAsStream("/view/Immages/logo.png")));
            Image associaPT = new Image(getClass().getResourceAsStream("/view/Immages/AssociaPT.png"));
            imgAssociaPT.setImage(associaPT); imgAssociaPT2.setImage(associaPT);
            Image gestisciScheda = new Image(getClass().getResourceAsStream("/view/Immages/GestisciScheda.png"));
            imgGestisciScheda.setImage(gestisciScheda); imgGestisciScheda2.setImage(gestisciScheda);
            Image richiediScheda = new Image(getClass().getResourceAsStream("/view/Immages/RichiediScheda.png"));
            imgRichiediScheda.setImage(richiediScheda); imgRichiediScheda2.setImage(richiediScheda);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR, e);
        }
    }

    private void costruisciCalendarioDinamico() {
        gridCalendario.getChildren().clear();
        LocalDate oggi = LocalDate.now(ZoneId.systemDefault());
        YearMonth meseCorrente = YearMonth.now(ZoneId.systemDefault());

        lblMeseAnno.setText(oggi.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN).toUpperCase() + " " + oggi.getYear());
        int primoGiorno = meseCorrente.atDay(1).getDayOfWeek().getValue();

        for (int giorno = 1; giorno <= meseCorrente.lengthOfMonth(); giorno++) {
            int colonna = (giorno + primoGiorno - 2) % 7;
            int riga = (giorno + primoGiorno - 2) / 7;

            Label lblGiorno = new Label(String.valueOf(giorno));
            lblGiorno.setPrefSize(40, 40);
            lblGiorno.setAlignment(Pos.CENTER);
            if (giorno == oggi.getDayOfMonth()) lblGiorno.getStyleClass().add("giorno-attuale");

            gridCalendario.add(lblGiorno, colonna, riga);
        }
    }

    private void popolaInfoCard() {
        popolaCardAssociazione();
        popolaCardRichiesta();
        popolaCardScheda();
    }

    private void popolaCardAssociazione() {
        switch (datiDashboard.getStatoAssociazione()) {
            case ASSOCIATO -> {
                cerchioStato.setFill(Color.web(VERDE));
                lblStatoAssociazione.setText("Associato");
                lblNomePT.setText("Personal Trainer: " + datiDashboard.getNomePT());
            }
            case PENDING -> {
                cerchioStato.setFill(Color.web(GIALLO));
                lblStatoAssociazione.setText("Richiesta in attesa...");
                lblNomePT.setText("In attesa di conferma dal PT");
            }
            default -> {
                cerchioStato.setFill(Color.web("#d63031"));
                lblStatoAssociazione.setText("Nessun Personal Trainer");
                lblNomePT.setText("Clicca per cercare un PT");
            }
        }
    }

    private void popolaCardRichiesta() {
        if (datiDashboard.isHaSchedeDisponibili()) {
            lblStatoRichiesta.setText("✓ Scheda ricevuta");
            lblStatoRichiesta.setTextFill(Color.web(VERDE));
            lblObiettivoRichiesta.setText("Vai su Gestisci Scheda per visualizzarla");
            lblDataRichiesta.setText("");
            return;
        }

        switch (datiDashboard.getStatoRichiesta()) {
            case PENDING -> {
                lblStatoRichiesta.setText("⏳ Richiesta in attesa");
                lblStatoRichiesta.setTextFill(Color.web(GIALLO));
                lblObiettivoRichiesta.setText("Obiettivo: " + datiDashboard.getObiettivoRichiesta());
                lblDataRichiesta.setText("Frequenza: " + datiDashboard.getFrequenzaRichiesta() + " gg/sett");
            }
            case IN_LAVORAZIONE -> {
                lblStatoRichiesta.setText("🔧 In lavorazione");
                lblStatoRichiesta.setTextFill(Color.web("#0984e3"));
                lblObiettivoRichiesta.setText("Il tuo PT sta assemblando la scheda");
                lblDataRichiesta.setText("");
            }
            default -> {
                lblStatoRichiesta.setText("Nessuna richiesta attiva");
                lblStatoRichiesta.setTextFill(Color.web("#636e72"));
                lblObiettivoRichiesta.setText(datiDashboard.getStatoAssociazione() == StatoAssociazione.ASSOCIATO ? "Clicca per richiedere la tua scheda" : "Prima associati a un Personal Trainer");
                lblDataRichiesta.setText("");
            }
        }
    }

    private void popolaCardScheda() {
        if (datiDashboard.isHaSchedeDisponibili()) {
            lblSchedaInfo.setText("✓ Scheda disponibile");
            lblSchedaInfo.setTextFill(Color.web(VERDE));
            lblSchedaDettaglio.setText(datiDashboard.getNumeroGiorniScheda() + " giorni di allenamento • Clicca per visualizzarla");
            return;
        }

        switch (datiDashboard.getStatoRichiesta()) {
            case PENDING -> {
                lblSchedaInfo.setText("⏳ Richiesta inviata");
                lblSchedaInfo.setTextFill(Color.web(GIALLO));
                lblSchedaDettaglio.setText("Il tuo PT non ha ancora visualizzato la richiesta.");
            }
            case IN_LAVORAZIONE -> {
                lblSchedaInfo.setText("🔧 Scheda in lavorazione");
                lblSchedaInfo.setTextFill(Color.web("#0984e3"));
                lblSchedaDettaglio.setText("Il tuo PT sta assemblando la tua scheda!");
            }
            default -> {
                lblSchedaInfo.setText("Nessuna scheda ancora disponibile");
                lblSchedaInfo.setTextFill(Color.web("#d63031"));
                lblSchedaDettaglio.setText(datiDashboard.getStatoAssociazione() == StatoAssociazione.ASSOCIATO ? "Richiedila ora al tuo Personal Trainer!" : "Prima associati a un Personal Trainer.");
            }
        }
    }

    @FXML public void scrollaGestione() { scrollPrincipale.setVvalue(0.0); }
    @FXML public void scrollaAssocia() { scrollPrincipale.setVvalue(0.5); }
    @FXML public void scrollaRichiedi() { scrollPrincipale.setVvalue(1.0); }

    @FXML
    public void apriGestisciScheda() {
        verificaAssociazionePTUtente(0);
        if (datiDashboard.getStatoAssociazione() != StatoAssociazione.ASSOCIATO) return;
        StatoRichiesta stato = datiDashboard.getStatoRichiesta();
        if (stato == StatoRichiesta.NESSUNA) {
            mostraAlert(Alert.AlertType.INFORMATION, ATT, "Non hai ancora richiesto una scheda al trainer");
            return;
        } else if (stato != StatoRichiesta.COMPLETATA) {
            mostraAlert(Alert.AlertType.INFORMATION, ATT, stato == StatoRichiesta.PENDING ?
                    "Il trainer non ha ancora visualizzato la richiesta di scheda" : "Il trainer non ha ancora lavorato la richiesta di scheda");
            return;
        }

        try {
            apriPopup("/view/fxml/ClienteGestisciScheda.fxml");
            aggiornaSchermata(); // Aggiorna i dati quando il popup si chiude
        } catch (IOException e) {
            LogManager.error("Errore apertura gestisci scheda", e);
        }
    }

    @FXML
    public void avviaRichiestaScheda() { verificaAssociazionePTUtente(1); }

    public void verificaAssociazionePTUtente(int mode) {
        try {
            switch (datiDashboard.getStatoAssociazione()) {
                case NESSUNA -> mostraPopupErroreNoPT();
                case PENDING -> mostraAlert(Alert.AlertType.INFORMATION, ATT, mode == 1 ? "Il tuo Trainer non ha ancora accettato la richiesta." : "Il tuo Trainer non ha ancora accettato la richiesta di associazione.");

                case StatoAssociazione s when s == StatoAssociazione.ASSOCIATO && mode == 1 -> {
                    if (richiestaController.verificaPresenzaRichiesta(datiDashboard.getEmail())) {
                        mostraAlert(Alert.AlertType.INFORMATION, ATT, "Hai già una richiesta di scheda in sospeso. Attendi la risposta del PT.");
                    } else {
                        apriPopup("/view/fxml/RichiestaScheda.fxml");
                        aggiornaSchermata();
                    }
                }
                case ASSOCIATO -> {
                    //se lo stato è associato ma il mode non è 1, non fa nulla.
                }
            }
        } catch (IOException e) {
            LogManager.error("Errore durante l'avvio della richiesta scheda", e);
        }
    }

    private void mostraPopupErroreNoPT() throws IOException {
        Stage popupStage = new Stage();
        popupStage.initOwner(imgFitplan.getScene().getWindow());
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/view/fxml/SchermataErroreNoPT.fxml")));
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource(AppConfig.get(CSS_PATH_KEY)).toExternalForm());
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void apriPopup(String fxmlPath) throws IOException {
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(lblNomeUtente.getScene().getWindow());
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxmlPath)));
        scene.setFill(Color.TRANSPARENT);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    @FXML
    public void apriProfiloPersonale() {
        Utente utenteCorrente = Sessione.getInstance().getUtente(); // Ammesso per il routing globale del profilo
        UtenteBean bean = new UtenteBean();
        bean.setNome(utenteCorrente.getNome());
        bean.setCognome(utenteCorrente.getCognome());
        bean.setEmail(utenteCorrente.getEmail());
        bean.setRuolo(utenteCorrente.getRuolo());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/ProfiloPersonaleCliente.fxml"));
            Parent root = loader.load();
            ProfiloPersonaleBoundary controller = loader.getController();
            controller.setDatiUtente(bean);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(lblNomeUtente.getScene().getWindow());
            popupStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.show();
        } catch (IOException e) {
            LogManager.error("Errore apertura profilo personale", e);
        }
    }

    @FXML
    public void apriRicercaPT() {
        if (datiDashboard.getStatoAssociazione() == StatoAssociazione.PENDING) {
            mostraAlert(Alert.AlertType.INFORMATION, ATT, "Hai una richiesta pendente. Attendi la risposta del Trainer.");
            return;
        }
        if (datiDashboard.getStatoAssociazione() == StatoAssociazione.ASSOCIATO) {
            mostraAlert(Alert.AlertType.INFORMATION, ATT, "Sei già associato a un Trainer!");
            return;
        }
        try {
            Stage popupStage = new Stage();
            popupStage.initOwner(lblNomeUtente.getScene().getWindow());
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/view/fxml/ClienteRicercaPT.fxml")));
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource(AppConfig.get(CSS_PATH_KEY)).toExternalForm());
            popupStage.setScene(scene);
            popupStage.showAndWait();
            aggiornaSchermata();
        } catch (IOException e) {
            LogManager.error("Impossibile aprire il popup di ricerca PT", e);
        }
    }

    @FXML
    public void handleLogout() {
        Sessione.getInstance().setUtente(null);
        LogManager.info("Logout effettuato con successo. Reindirizzamento al login...");
        Navigator.pushScene("/view/fxml/Login.fxml", "FitPlan - Login");
    }

    private void mostraAlert(Alert.AlertType tipo, String titolo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
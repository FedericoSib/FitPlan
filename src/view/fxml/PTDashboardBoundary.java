package view.fxml;

import bean.AssociazioneBean;
import bean.PersonalTrainerBean;
import bean.UtenteBean;
import controller.PTDashboardController;
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
import util.LogManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class PTDashboardBoundary {

    @FXML private Label lblNomeUtente;
    @FXML private Label lblMeseAnno;
    @FXML private GridPane gridCalendario;
    @FXML private ScrollPane scrollPrincipale;
    @FXML private ImageView imgFitplan;
    @FXML private ImageView imgGestioneClienti;
    @FXML private ImageView imgNotifiche;
    @FXML private ImageView imgAssemblaScheda;
    @FXML private ImageView imgGestioneClienti2;
    @FXML private ImageView imgNotifiche2;
    @FXML private ImageView imgAssemblaScheda2;
    @FXML private ListView<AssociazioneBean> lvRichieste;
    @FXML private Label lblBenvenuto;
    @FXML private Label lblRichiesteAssociazione;
    @FXML private Label lblRichiesta1;
    @FXML private Label lblRichiesta2;
    @FXML private Label lblRichiesta3;
    @FXML private Label lblRichiesteSchedePending;
    @FXML private Label lblSchedeInLavorazione;

    private final PTDashboardController dashboardController = new PTDashboardController();
    private PersonalTrainerBean datiDashboard;

    @FXML
    public void initialize() {
        caricaImmagini();
        costruisciCalendarioDinamico();
        aggiornaSchermata();
    }

    private void aggiornaSchermata() {
        datiDashboard = dashboardController.getDatiDashboard();
        lblNomeUtente.setText(datiDashboard.getNome() + " " + datiDashboard.getCognome());
        dashboardController.gestisciNotifichePendenti();
        popolaInfoCard();
    }

    private void caricaImmagini() {
        final String IMAGE_LOAD_ERROR = "Impossibile caricare immagine: ";
        try {
            imgFitplan.setImage(new Image(
                    getClass().getResourceAsStream("/view/Immages/logo.png")));
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ", e);
        }
        try {
            Image gestioneClienti = new Image(
                    getClass().getResourceAsStream("/view/Immages/GestioneClienti.png"));
            imgGestioneClienti.setImage(gestioneClienti);
            imgGestioneClienti2.setImage(gestioneClienti);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR, e);
        }
        try {
            Image notifiche = new Image(
                    getClass().getResourceAsStream("/view/Immages/Notifiche.png"));
            imgNotifiche.setImage(notifiche);
            imgNotifiche2.setImage(notifiche);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR, e);
        }
        try {
            Image assemblaScheda = new Image(
                    getClass().getResourceAsStream("/view/Immages/AssemblaScheda.png"));
            imgAssemblaScheda.setImage(assemblaScheda);
            imgAssemblaScheda2.setImage(assemblaScheda);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR, e);
        }
    }

    private void costruisciCalendarioDinamico() {
        gridCalendario.getChildren().clear();
        LocalDate oggi = LocalDate.now(ZoneId.systemDefault());
        YearMonth meseCorrente = YearMonth.now(ZoneId.systemDefault());

        lblMeseAnno.setText(oggi.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ITALIAN).toUpperCase()
                + " " + oggi.getYear());

        int primoGiornoDelMese = meseCorrente.atDay(1).getDayOfWeek().getValue();

        for (int giorno = 1; giorno <= meseCorrente.lengthOfMonth(); giorno++) {
            int colonna = (giorno + primoGiornoDelMese - 2) % 7;
            int riga    = (giorno + primoGiornoDelMese - 2) / 7;

            Label lblGiorno = new Label(String.valueOf(giorno));
            lblGiorno.setPrefSize(40, 40);
            lblGiorno.setAlignment(Pos.CENTER);
            if (giorno == oggi.getDayOfMonth()) {
                lblGiorno.getStyleClass().add("giorno-attuale");
            }
            gridCalendario.add(lblGiorno, colonna, riga);
        }
    }

    private void popolaInfoCard() {
        popolaCardGestioneClienti();
        popolaCardRichiesteSchede();
        popolaCardAssemblaScheda();
    }

    private void popolaCardGestioneClienti() {
        int count = datiDashboard.getRichiesteAssociazionePending();
        String singoplu = (count == 1) ? "a" : "e";
        lblRichiesteAssociazione.setText(count == 0
                ? "Nessuna richiesta in attesa"
                : count + " richiest" + singoplu + " in attesa");

        List<String> ultime = datiDashboard.getUltimeRichiesteAssociazione();
        List<Label> labels = List.of(lblRichiesta1, lblRichiesta2, lblRichiesta3);
        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).setText(i < ultime.size() ? "• " + ultime.get(i) : "");
        }
    }

    private void popolaCardRichiesteSchede() {
        int count = datiDashboard.getRichiesteSchedePending();
        String testoRichiesta = (count == 1) ? "nuova richiesta" : "nuove richieste";
        lblRichiesteSchedePending.setText(count == 0
                ? "Nessuna nuova richiesta scheda"
                : count + " " + testoRichiesta + " in attesa");
    }

    private void popolaCardAssemblaScheda() {
        int count = datiDashboard.getSchedeInLavorazione();
        String parolaScheda = (count == 1) ? "scheda" : "schede";
        lblSchedeInLavorazione.setText(count == 0
                ? "Nessuna scheda in lavorazione"
                : count + " " + parolaScheda + " in lavorazione");
    }

    @FXML public void scrollaGestione() { scrollPrincipale.setVvalue(0.0); }
    @FXML public void scrollaAssembla()  { scrollPrincipale.setVvalue(0.5); }
    @FXML public void scrollaRichieste() { scrollPrincipale.setVvalue(1.0); }

    @FXML
    public void apriRichiesteClienti() {
        try {
            apriPopupEAggiorna("/view/fxml/PTRichiestePendenti.fxml");
        } catch (IOException e) {
            LogManager.error("Errore apertura pop-up richieste", e);
        }
    }

    @FXML
    public void apriRichiesteScheda() {
        try {
            apriPopupEAggiorna("/view/fxml/PTRichiesteScheda.fxml");
        } catch (IOException e) {
            LogManager.error("Errore apertura richieste scheda", e);
        }
    }

    @FXML
    public void apriAssemblaScheda() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/fxml/PTRichiesteScheda.fxml"));
            Parent root = loader.load();

            PTRichiesteSchedaBoundary boundary = loader.getController();
            boundary.setModalita(PTRichiesteSchedaBoundary.Modalita.IN_LAVORAZIONE);
            boundary.caricaRichieste();

            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(lblNomeUtente.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.showAndWait();
            aggiornaSchermata();

        } catch (IOException e) {
            LogManager.error("Errore apertura assembla scheda", e);
        }
    }

    @FXML
    public void apriProfiloPersonale() {
        UtenteBean bean = new UtenteBean();
        bean.setNome(datiDashboard.getNome());
        bean.setCognome(datiDashboard.getCognome());
        bean.setEmail(datiDashboard.getEmail());

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/fxml/ProfiloPersonalePT.fxml"));
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
    public void tornaAllaDashboard() {
        Navigator.pushScene("/view/fxml/PTDashboard.fxml", "FitPlan - PersonalTrainerDashboard");
    }

    @FXML
    public void handleLogout() {
        dashboardController.effettuaLogout();
        Navigator.pushScene("/view/fxml/Login.fxml", "FitPlan - Login");
    }

    private void apriPopupEAggiorna(String fxmlPath) throws IOException {
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(lblNomeUtente.getScene().getWindow());
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxmlPath)));
        scene.setFill(Color.TRANSPARENT);
        popupStage.setScene(scene);
        popupStage.showAndWait();
        aggiornaSchermata();
    }
}
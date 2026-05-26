package view.fxml;

import model.dao.DAOFactory;
import util.observer.*;
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
import model.entity.PersonalTrainer;
import model.entity.Utente;
import bean.*;
import util.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class PTDashboardBoundary {

    @FXML private Label lblNomeUtente;
    @FXML private Label lblMeseAnno;
    @FXML private Label lblCodiceUnivocoPT;
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


    @FXML
    public void initialize() {
        final String IMAGE_LOAD_ERROR = "Impossibile caricare immagine: ";
        PersonalTrainer pT = (PersonalTrainer) Sessione.getInstance().getUtente();
        lblNomeUtente.setText(pT.getNome() + " " + pT.getCognome());
        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
            imgFitplan.setImage(logo);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ",e);
        }
        try {
            Image gestioneClienti = new Image(getClass().getResourceAsStream("/view/Immages/GestioneClienti.png"));
            imgGestioneClienti.setImage(gestioneClienti);
            imgGestioneClienti2.setImage(gestioneClienti);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR ,e);
        }
        try {
            Image notifiche = new Image(getClass().getResourceAsStream("/view/Immages/Notifiche.png"));
            imgNotifiche.setImage(notifiche);
            imgNotifiche2.setImage(notifiche);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR ,e);
        }
        try {
            Image assemblaScheda = new Image(getClass().getResourceAsStream("/view/Immages/AssemblaScheda.png"));
            imgAssemblaScheda.setImage(assemblaScheda);
            imgAssemblaScheda2.setImage(assemblaScheda);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR ,e);
        }

        costruisciCalendarioDinamico();
        PersonalTrainer pt = (PersonalTrainer) Sessione.getInstance().getUtente();
        NotificaManager manager = new NotificaManager(DAOFactory.getNotificaDAO());
        manager.mostraNotifichePendenti(pt.getEmail());

        popolaInfoCard();
    }

    private void costruisciCalendarioDinamico() {
        gridCalendario.getChildren().clear();

        LocalDate oggi = LocalDate.now();
        YearMonth meseCorrente = YearMonth.now();

        String titoloMese = oggi.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN).toUpperCase();
        lblMeseAnno.setText(titoloMese + " " + oggi.getYear());

        int primoGiornoDelMese = meseCorrente.atDay(1).getDayOfWeek().getValue();
        int giorniNelMese = meseCorrente.lengthOfMonth();

        int riga = 0;
        for (int giorno = 1; giorno <= giorniNelMese; giorno++) {
            int colonna = (giorno + primoGiornoDelMese - 2) % 7;
            riga = (giorno + primoGiornoDelMese - 2) / 7;

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
        PersonalTrainer pt = (PersonalTrainer) Sessione.getInstance().getUtente();
        popolaCardGestioneClienti(pt);
        popolaCardRichiesteSchede(pt);
        popolaCardAssemblaScheda(pt);
    }

    private void popolaCardGestioneClienti(PersonalTrainer pt) {
        try {
            var dao = model.dao.DAOFactory.getAssociazioneDAO();
            java.util.List<String> richieste = dao.getRichiestePerPT(pt.getEmail());
            int count = richieste.size();
            String singoplu = (count == 1) ? "a" : "e";
            String messaggio = (count == 0)
                    ? "Nessuna richiesta in attesa"
                    : count + " richiest" + singoplu + " in attesa";
            lblRichiesteAssociazione.setText(messaggio);

            // Ultime 3 email
            java.util.List<Label> labels = java.util.List.of(lblRichiesta1, lblRichiesta2, lblRichiesta3);
            for (int i = 0; i < labels.size(); i++) {
                if (i < richieste.size()) {
                    labels.get(i).setText("• " + richieste.get(i));
                } else {
                    labels.get(i).setText("");
                }
            }
        } catch (Exception e) {
            util.LogManager.error("Errore caricamento richieste associazione", e);
        }
    }

    private void popolaCardRichiesteSchede(PersonalTrainer pt) {
        try {
            var richieste = model.dao.DAOFactory.getRichiestaDAO()
                    .prendiRichiestePerPTEStato(pt.getEmail(), model.entity.StatoRichiesta.PENDING);
            int count = richieste.size();
            String testoRichiesta = (count == 1) ? "nuova richiesta" : "nuove richieste";
            String messaggio = (count == 0)
                    ? "Nessuna nuova richiesta scheda"
                    : count + " " + testoRichiesta + " in attesa";
            lblRichiesteSchedePending.setText(messaggio);
        } catch (Exception e) {
            util.LogManager.error("Errore caricamento richieste schede", e);
        }
    }

    private void popolaCardAssemblaScheda(PersonalTrainer pt) {
        try {
            var inLavorazione = model.dao.DAOFactory.getRichiestaDAO()
                    .prendiRichiestePerPTEStato(pt.getEmail(), model.entity.StatoRichiesta.IN_LAVORAZIONE);
            int count = inLavorazione.size();
            String parolaScheda = (count == 1) ? "scheda" : "schede";
            String messaggio = (count == 0)
                    ? "Nessuna scheda in lavorazione"
                    : count + " " + parolaScheda + " in lavorazione";
            lblSchedeInLavorazione.setText(messaggio);
        } catch (Exception e) {
            util.LogManager.error("Errore caricamento schede in lavorazione", e);
        }
    }

    @FXML
    public void scrollaGestione() {
        scrollPrincipale.setVvalue(0.0);
    }

    @FXML
    public void scrollaAssembla() {
        scrollPrincipale.setVvalue(0.5);
    }

    @FXML
    public void scrollaRichieste() {
        scrollPrincipale.setVvalue(1.0);
    }

    @FXML
    public void apriRichiesteClienti() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/PTRichiestePendenti.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(lblNomeUtente.getScene().getWindow());

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.showAndWait();
            popolaInfoCard();

        } catch (IOException e) {
            LogManager.error("Errore apertura pop-up richieste (IO)", e);
        } catch (Exception e) {
            LogManager.error("Errore generico apertura pop-up richieste", e);
        }
    }

    @FXML
    public void apriRichiesteScheda() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/PTRichiesteScheda.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(lblNomeUtente.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.showAndWait();
            popolaInfoCard();
        } catch (IOException e) {
            LogManager.error("Errore apertura richieste scheda", e);
        }
    }

    @FXML
    public void apriAssemblaScheda() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/PTRichiesteScheda.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            PTRichiesteSchedaBoundary boundary = loader.getController();
            boundary.setModalita(PTRichiesteSchedaBoundary.Modalita.IN_LAVORAZIONE);
            boundary.caricaRichieste();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.showAndWait();
            popolaInfoCard();
        } catch (IOException e) {
            LogManager.error("Errore apertura assembla scheda", e);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/ProfiloPersonalePT.fxml"));
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
            LogManager.error("Errore generico apertura pop-up profilo personale", e);
        }
    }

    @FXML
    public void tornaAllaDashboard() {
        Navigator.pushScene("/view/fxml/PTDashboard.fxml", "FitPlan - PersonalTrainerDashboard");
    }

    @FXML
    public void handleLogout() {
        Sessione.getInstance().setUtente(null);
        LogManager.info("Logout effettuato con successo. Reindirizzamento al login...");
        Navigator.pushScene("/view/fxml/Login.fxml", "FitPlan - Login");
    }
}
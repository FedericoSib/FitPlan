package view;

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
import javafx.scene.layout.VBox;
import model.Sessione;
import model.entity.PersonalTrainer;
import model.entity.Utente;
import model.entity.Cliente;
import util.LogManager;

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
    @FXML private ImageView imgAvatar;
    @FXML private ImageView imgCopia;

    @FXML
    public void initialize() {
        final String IMAGE_LOAD_ERROR = "Impossibile caricare immagine: ";
        // 1. Recupero dati utente dalla sessione
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
        try {
            Image avatarBase = new Image(getClass().getResourceAsStream("/view/Immages/AvatarBase.png"));
            imgAvatar.setImage(avatarBase);
        } catch (Exception e) {
            LogManager.error(IMAGE_LOAD_ERROR ,e);
        }

        // 2. Setup dinamico del calendario
        costruisciCalendarioDinamico();
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
        // Caricherà la schermata con la lista delle richieste pendenti
        Navigator.pushScene("/view/PTRichiestePendenti.fxml", "FitPlan - Richieste Scheda");
    }

    @FXML
    public void apriListaAtleti() {
        // Caricherà la lista dei clienti associati
        Navigator.pushScene("/view/PTMieiAtleti.fxml", "FitPlan - I Miei Atleti");
    }

    @FXML
    public void apriProfiloPersonale() {
        Utente utenteCorrente = Sessione.getInstance().getUtente();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfiloPersonalePT.fxml"));
            Parent root = loader.load();

            // Otteniamo il controller generico
            ProfiloPersonaleBoundary controller = loader.getController();
            controller.setDatiUtente(utenteCorrente); // Passiamo l'utente (Atleta o PT)

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(lblNomeUtente.getScene().getWindow()); // Usa una label qualsiasi della dashboard
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void tornaAllaDashboard() {
        Navigator.pushScene("/view/PTDashboard.fxml", "FitPlan - PersonalTrainerDashboard");
    }
}
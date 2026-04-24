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
import model.entity.Utente;
import model.entity.Cliente;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class ClienteDashboardBoundary {

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
    @FXML private ImageView imgAvatar;

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
            System.err.println("Impossibile caricare il logo: " + e.getMessage());
        }
        try {
            Image AssociaPT = new Image(getClass().getResourceAsStream("/view/Immages/AssociaPT.png"));
            imgAssociaPT.setImage(AssociaPT);
            imgAssociaPT2.setImage(AssociaPT);
        } catch (Exception e) {
            System.err.println(IMAGE_LOAD_ERROR + e.getMessage());
        }
        try {
            Image GestisciScheda = new Image(getClass().getResourceAsStream("/view/Immages/GestisciScheda.png"));
            imgGestisciScheda.setImage(GestisciScheda);
            imgGestisciScheda2.setImage(GestisciScheda);
        } catch (Exception e) {
            System.err.println(IMAGE_LOAD_ERROR + e.getMessage());
        }
        try {
            Image RichiediScheda = new Image(getClass().getResourceAsStream("/view/Immages/RichiediScheda.png"));
            imgRichiediScheda.setImage(RichiediScheda);
            imgRichiediScheda2.setImage(RichiediScheda);
        } catch (Exception e) {
            System.err.println(IMAGE_LOAD_ERROR + e.getMessage());
        }
        try {
            Image AvatarBase = new Image(getClass().getResourceAsStream("/view/Immages/AvatarBase.png"));
            imgAvatar.setImage(AvatarBase);
        } catch (Exception e) {
            System.err.println(IMAGE_LOAD_ERROR + e.getMessage());
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
    public void avviaRichiestaScheda() {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();

        if (!cliente.isAssociated()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataErroreNoPT.fxml"));
                Parent root = loader.load();

                Stage popupStage = new Stage();
                popupStage.initOwner(imgFitplan.getScene().getWindow());
                popupStage.initModality(Modality.WINDOW_MODAL);

                popupStage.initStyle(StageStyle.TRANSPARENT);

                Scene scene = new Scene(root);

                scene.setFill(Color.TRANSPARENT);

                scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

                popupStage.setScene(scene);
                popupStage.initStyle(StageStyle.TRANSPARENT);
                popupStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Navigator.pushScene("/view/RichiestaScheda.fxml", "FitPlan - Nuova Richiesta");
        }
    }

    @FXML
    public void apriProfiloPersonale() {
        Utente utenteCorrente = Sessione.getInstance().getUtente();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProfiloPersonaleCliente.fxml"));
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
    public void logout() {
        Sessione.getInstance().setUtente(null);
        Navigator.pushScene("/view/Login.fxml", "FitPlan - Login");
    }
}
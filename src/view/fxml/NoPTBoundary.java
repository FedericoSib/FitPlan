package view.fxml;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import util.LogManager;
import java.io.IOException;

public class NoPTBoundary {

    @FXML private ImageView imgFitplan;

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
            imgFitplan.setImage(logo);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ",e);
        }
    }

    @FXML
    public void vaiAssociaPT(ActionEvent event) {
        // 1. Chiudi il popup di errore
        Stage popupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        popupStage.close();

        // 2. Apri il caso d'uso come popup sopra la dashboard
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/fxml/ClienteRicercaPT.fxml"));
            Parent root = loader.load();

            Stage ricercaStage = new Stage();
            ricercaStage.initModality(Modality.WINDOW_MODAL);
            ricercaStage.initStyle(StageStyle.TRANSPARENT);

            // Usa il Navigator per ottenere il primaryStage come owner
            ricercaStage.initOwner(Navigator.getPrimaryStage());

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/view/fxml/style.css").toExternalForm());

            ricercaStage.setScene(scene);
            ricercaStage.show();

        } catch (IOException e) {
            LogManager.error("Impossibile aprire il popup di ricerca PT", e);
        }
    }

    @FXML
    public void tornaAllaDashboard(ActionEvent event) {
        chiudiFinestra(event);
    }

    // Metodo privato di utility per non ripetere il codice
    private void chiudiFinestra(ActionEvent event) {
        // Risaliamo dal bottone (source) fino alla finestra (Stage) per chiuderla
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

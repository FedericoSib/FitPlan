package view;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import util.LogManager;

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
        Stage popupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        popupStage.close();
        Navigator.pushScene("/view/ClienteRicercaPT.fxml", "FitPlan - Cerca il tuo Trainer");
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

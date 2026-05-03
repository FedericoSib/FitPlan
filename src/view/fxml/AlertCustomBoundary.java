package view.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import util.LogManager;

public class AlertCustomBoundary {

    @FXML private Label lblMessaggio;
    @FXML private ImageView imgFitplan;

    @FXML
    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
            imgFitplan.setImage(logo);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ", e);
        }
    }

    public void setMessaggio(String messaggio) {
        lblMessaggio.setText(messaggio);
    }

    @FXML
    public void tornaAllaDashboard() {
        Stage stage = (Stage) lblMessaggio.getScene().getWindow();
        stage.close();
    }
}
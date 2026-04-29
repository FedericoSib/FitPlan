package view;

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

        // Quando la larghezza della label è nota, calcola l'altezza necessaria
        lblMessaggio.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                // Forza il ricalcolo del layout
                lblMessaggio.setPrefHeight(Label.USE_COMPUTED_SIZE);
                lblMessaggio.getParent().layout();

                // Ridimensiona lo stage
                Stage stage = (Stage) lblMessaggio.getScene().getWindow();
                if (stage != null) {
                    stage.sizeToScene();
                }
            }
        });
    }

    @FXML
    public void tornaAllaDashboard() {
        Stage stage = (Stage) lblMessaggio.getScene().getWindow();
        stage.close();
    }
}
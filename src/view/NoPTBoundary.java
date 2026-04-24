/*package view;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NoPTBoundary {
    @FXML private ImageView imgFitplan;

    public void initialize() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
            imgFitplan.setImage(logo);
        } catch (Exception e) {
            System.err.println("Errore caricamento logo login: " + e.getMessage());
        }
    }
    @FXML
    public void vaiAssociaPT() {
        Navigator.pushScene("/view/ClienteDashboard.fxml", "FitPlan - Dashboard");
    }

    @FXML
    public void tornaAllaDashboard() {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}*/

package view;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;           // Import fondamentale
import javafx.scene.Node;            // Import fondamentale
import javafx.event.ActionEvent;     // ATTENZIONE: deve essere javafx.event, non java.awt.event
import javafx.scene.control.Button;
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
        chiudiFinestra(event);
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

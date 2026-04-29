package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import view.AlertCustomBoundary;

public class AlertManager {

    private AlertManager() {}

    public static void mostra(String messaggio) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AlertManager.class.getResource("/view/AlertCustom.fxml")
            );
            Parent root = loader.load();

            AlertCustomBoundary controller = loader.getController();
            controller.setMessaggio(messaggio);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);

            // Crea la scena senza dimensioni fisse
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            // Dopo che è mostrata, adatta la dimensione al contenuto
            stage.setOnShown(e -> {
                root.applyCss();
                root.layout();
                stage.sizeToScene();
            });

            stage.showAndWait();

        } catch (Exception e) {
            LogManager.error("Impossibile caricare alert custom", e);
        }
    }
}

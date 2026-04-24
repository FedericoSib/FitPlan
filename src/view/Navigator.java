package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.LogManager;

import java.io.IOException;

public class Navigator {

    private static Stage primaryStage;

    private Navigator(){}

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void pushScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);

            // Opzionale: centra la finestra e la rende non ridimensionabile se preferisci
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (IOException e) {
            LogManager.error("ERRORE NAVIGATORE: Impossibile caricare " + fxmlPath, e);
        }
    }
}

package main;

import javafx.application.Application;
import javafx.stage.Stage;
import view.Navigator;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Diamo lo Stage al Navigatore
        Navigator.setPrimaryStage(primaryStage);

        // 2. Avviamo la prima Storyboard
        // Nota: lo slash iniziale "/" parte dalla cartella resources
        Navigator.pushScene("/view/Login.fxml", "FitPlan - Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
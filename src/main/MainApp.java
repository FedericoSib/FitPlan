package main;

import javafx.application.Application;
import javafx.stage.Stage;
import controller.graphic.*;
import model.dao.DAOFactory;
import util.observer.NotificaManager;
import view.Navigator;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        ScadenzaRichiesteController scadenzaController = new ScadenzaRichiesteController();
        NotificaManager manager = new NotificaManager(DAOFactory.getNotificaDAO());
        scadenzaController.aggiungiObserver(manager);
        scadenzaController.avvia();
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
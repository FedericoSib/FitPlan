package view.fxml;

import controller.LoginController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.exception.LoginException;
import model.Sessione;
import bean.*;
import model.entity.Utente;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import util.LogManager;

public class LoginBoundary {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPass;
    @FXML private ImageView imgFitplan;

    private LoginController loginController = new LoginController();

    public void initialize() {
        // Carichiamo il logo all'avvio (stessa cartella dell'altra boundary)
        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
            imgFitplan.setImage(logo);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ",e);
        }
    }

    @FXML
    public void gestisciAccesso() {
        LoginBean loginBean = new LoginBean();
        loginBean.setEmail(txtEmail.getText());
        loginBean.setPassword(txtPass.getText());

        if (!loginBean.isValid()) {
            mostraAlert(Alert.AlertType.WARNING, "Campi Vuoti", "Inserisci email e password per accedere.");
            return;
        }

        try {
            loginController.autentica(loginBean);
            procediAllaDashboard();

        } catch (LoginException e) {
            mostraAlert(Alert.AlertType.ERROR, "Problema di Accesso", e.getMessage());
        }
    }

    @FXML
    public void gestisciRegistrazione() {
        Navigator.pushScene("/view/fxml/Registrazione.fxml", "FitPlan - Crea il tuo Account");
    }

    private void procediAllaDashboard() {
        Utente loggato = Sessione.getInstance().getUtente();
        mostraAlert(Alert.AlertType.INFORMATION, "Benvenuto", "Accesso effettuato come: " + loggato.getId());
        if (loggato instanceof Cliente) {
            Navigator.pushScene("/view/fxml/ClienteDashboard.fxml", "FitPlan - Dashboard Cliente");
        } else if (loggato instanceof PersonalTrainer) {
            // Qui caricherai la dashboard specifica per il PT che creeremo
            Navigator.pushScene("/view/fxml/PTDashboard.fxml", "FitPlan - Dashboard Trainer");
        }
    }

    private void mostraAlert(Alert.AlertType tipo, String titolo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

package view.fxml;

import bean.*;
import controller.RegistrazioneController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.exception.RegistrazioneException;
import util.LogManager;

public class RegistrazioneBoundary {

    @FXML private TextField txtNome;
    @FXML private TextField txtCognome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPass;
    @FXML private PasswordField txtConfermaPass;
    @FXML private ChoiceBox<String> cbRuolo;
    @FXML private ImageView imgFitplan;

    private RegistrazioneController controller = new RegistrazioneController();

    @FXML
    public void initialize() {
        mostraFormRegistrazione();
    }

    public void mostraFormRegistrazione() {
        cbRuolo.getItems().addAll("Atleta", "Personal Trainer");
        cbRuolo.setValue("Atleta"); // Valore di default

        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
            imgFitplan.setImage(logo);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ",e);
        }
    }

    @FXML

    public void finalizzaRegistrazione() {
        // 1. Raccolta dati dalla UI
        String pass = txtPass.getText();
        String conferma = txtConfermaPass.getText();
        String ruoloSelezionato = cbRuolo.getValue();

        // 2. Controllo specifico della Boundary (conferma password)
        // Questo rimane qui perché è un controllo di "interfaccia"
        if (!pass.equals(conferma)) {
            mostraAlert("Errore Password", "Le password non coincidono!");
            return;
        }

        try {
            // 3. Creazione e popolamento del Bean
            RegistrazioneBean bean = new RegistrazioneBean();
            bean.setNome(txtNome.getText());
            bean.setCognome(txtCognome.getText());
            bean.setEmail(txtEmail.getText());
            bean.setPassword(pass);

            // Conversione logica del ruolo
            int ruolo = "Personal Trainer".equals(ruoloSelezionato) ? 2 : 1;
            bean.setRuolo(ruolo);

            // 4. Delega al controller (che ora accetta solo il Bean)
            controller.registraNuovoUtente(bean);

            mostraAlert("Successo", "Registrazione completata! Ora puoi effettuare il login.");
            tornaAlLogin();

        } catch (RegistrazioneException e) {
            // Qui catturiamo sia gli errori di validazione del Bean che quelli del DB
            mostraAlert("Errore Registrazione", e.getMessage());
        } catch (Exception e) {
            LogManager.error("Errore generico in registrazione", e);
            mostraAlert("Errore Sistema", "Si è verificato un errore imprevisto.");
        }
    }

    @FXML
    public void annullaRegistrazione() {
        Navigator.pushScene("/view/fxml/Login.fxml", "FitPlan - Login");
    }

    private void mostraAlert(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void tornaAlLogin() {
        Navigator.pushScene("/view/fxml/Login.fxml", "FitPlan - Login");
    }
}
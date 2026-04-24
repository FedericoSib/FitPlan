package view;

import controller.graphic.RegistrazioneController;
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
        String nome = txtNome.getText();
        String cognome = txtCognome.getText();
        String email = txtEmail.getText();
        String pass = txtPass.getText();
        String conferma = txtConfermaPass.getText();
        String ruoloSelezionato = cbRuolo.getValue();

        // 1. Validazione sintattica base
        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || pass.isEmpty() || ruoloSelezionato == null) {
            mostraAlert("Errore", "Tutti i campi sono obbligatori.");
            return;
        }

        if (!pass.equals(conferma)) {
            mostraAlert("Errore Password", "Le password non coincidono!");
            return;
        }

        try {
            // Convertiamo la selezione della ChoiceBox in ruolo numerico
            int ruolo = ruoloSelezionato.equals("Personal Trainer") ? 2 : 1;

            // 2. Chiamata al controller
            controller.registraNuovoUtente(nome, cognome, email, pass, ruolo);

            mostraAlert("Successo", "Registrazione completata! Ora puoi effettuare il login.");
            tornaAlLogin();

        } catch (RegistrazioneException e) {
            mostraAlert("Errore Registrazione", e.getMessage());
        } catch (Exception e) {
            mostraAlert("Errore Sistema", "Si è verificato un errore imprevisto.");
        }
    }

    @FXML
    public void annullaRegistrazione() {
        Navigator.pushScene("/view/Login.fxml", "FitPlan - Login");
    }

    private void mostraAlert(String titolo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void tornaAlLogin() {
        Navigator.pushScene("/view/Login.fxml", "FitPlan - Login");
    }
}
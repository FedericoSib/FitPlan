package view.fxml;

import bean.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.exception.*;
import util.LogManager;
import controller.AssociaPTController;

import java.util.List;

public class ClienteRicercaPTBoundary {

    @FXML private TextField txtSearch;
    @FXML private Button btnAssocia;
    @FXML private Button btnCerca;
    @FXML private Button btnChiudi;
    @FXML private Button btnAnnulla;
    @FXML private ListView<PersonalTrainerBean> listaPT;

    private final AssociaPTController controller = new AssociaPTController();

    @FXML
    public void initialize() {
        controller.configuraObserverNotifiche();
        listaPT.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PersonalTrainerBean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome() + " " + item.getCognome() + " [ID: " + item.getId() + "]");
                }
            }
        });

        btnAssocia.setDisable(true);
        listaPT.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                btnAssocia.setDisable(newVal == null)
        );
    }

    @FXML
    public void handleCerca() {
        String query = txtSearch.getText();
        listaPT.getItems().clear();

        if (query == null || query.isBlank()) {
            LogManager.warn("Campo ricerca vuoto");
            return;
        }

        try {
            List<PersonalTrainerBean> risultati = controller.cercaTrainer(query);

            ObservableList<PersonalTrainerBean> data = FXCollections.observableArrayList(risultati);
            listaPT.setItems(data);

            LogManager.info("Ricerca completata per: " + query);

        } catch (TrainerNotFoundException _) {
            listaPT.getItems().clear();
            LogManager.warn("Nessun Personal Trainer trovato per: " + query);
            mostraAlert("Attenzione", "Nessun trainer trovato per i criteri inseriti.");
        }
    }

    @FXML
    public void handleAssocia() {
        PersonalTrainerBean selezionato = listaPT.getSelectionModel().getSelectedItem();

        try {
            AssociazioneBean associazioneBean = new AssociazioneBean();
            String emailCliente = controller.getClienteCorrente().getEmail();

            associazioneBean.setEmailCliente(emailCliente);
            associazioneBean.setEmailPT(selezionato.getEmail());

            LogManager.info("Invio richiesta tramite Bean da: " + emailCliente + " a: " + selezionato.getEmail());

            controller.inviaRichiestaAssociazione(associazioneBean);

            // 3. Feedback e chiusura
            ((Stage) btnAssocia.getScene().getWindow()).close();
            mostraAlert("Inviata", "Richiesta inviata correttamente! Ora sei in stato PENDING.");

        } catch (Exception e) {
            LogManager.error("Errore durante l'invio della richiesta", e);
            mostraAlert("Errore", "Impossibile inviare la richiesta: " + e.getMessage());
        }
    }

    @FXML
    public void tornaAllaDashboard(ActionEvent event) {
        chiudiFinestra(event);
    }

    private void chiudiFinestra(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void mostraAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.initOwner(null);
        alert.showAndWait();
    }
}
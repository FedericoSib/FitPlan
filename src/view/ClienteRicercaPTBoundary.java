/*package view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.entity.PersonalTrainer;
import model.exception.TrainerNotFoundException;
import util.LogManager;
import controller.graphic.AssociaPTController;


public class ClienteRicercaPTBoundary {

    @FXML private TextField txtSearch;
    @FXML private Button btnAssocia;
    @FXML private ListView<PersonalTrainer> listaPT;

    @FXML
    public void handleCerca() {
        String stringaDiRicerca = txtSearch.getText();
        if (stringaDiRicerca == null || stringaDiRicerca.isEmpty()) {
            LogManager.warn("Campo ricerca vuoto");
            return;
        }

        try {
            AssociaPTController controller = new AssociaPTController();
            PersonalTrainer trovato = controller.cercaTrainer(stringaDiRicerca);

            // Aggiorna la UI (es. mostra una Label col nome del PT trovato)
            lblRisultato.setText("Trovato: " + trovato.getNome() + " " + trovato.getCognome());
            btnAssocia.setVisible(true);

        } catch (TrainerNotFoundException e) {
            lblRisultato.setText("Nessun Personal Trainer trovato.");
            btnAssocia.setVisible(false);
        }
    }
}
*/

package view;

import bean.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;
import model.exception.*;
import util.LogManager;
import controller.graphic.AssociaPTController;

import java.util.List;

public class ClienteRicercaPTBoundary {

    @FXML private TextField txtSearch;
    @FXML private Button btnAssocia;
    @FXML private Button btnCerca;
    @FXML private Button btnChiudi;
    @FXML private Button btnAnnulla;
    @FXML private ListView<PersonalTrainerBean> listaPT;

    // Controller logico (Sarebbe meglio iniettarlo o averlo come attributo)
    private final AssociaPTController controller = new AssociaPTController();

    @FXML
    public void initialize() {
        // 1. Configura come i PT appaiono nella lista (Nome Cognome [ID])
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

        // 2. Disabilita il tasto associa finché non viene selezionato un PT
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
            // Supponiamo che il controller restituisca una lista di risultati
            // o un singolo PT incapsulato in una lista
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
            // 1. Creazione e popolamento del Bean
            AssociazioneBean associazioneBean = new AssociazioneBean();

            // Prendiamo l'email del cliente dalla sessione
            String emailCliente = Sessione.getInstance().getUtente().getEmail();
            associazioneBean.setEmailCliente(emailCliente);

            // Prendiamo l'email del PT selezionato dalla riga della lista
            associazioneBean.setEmailPT(selezionato.getEmail());

            LogManager.info("Invio richiesta tramite Bean da: " + emailCliente + " a: " + selezionato.getEmail());

            // 2. Chiamata al controller logico passando il BEAN
            controller.inviaRichiestaAssociazione(associazioneBean);

            // 3. Feedback e chiusura
            ((Stage) btnAssocia.getScene().getWindow()).close();
            mostraAlert("Inviata", "Richiesta inviata correttamente! Ora sei in stato PENDING.");

        } catch (DAOException e) {
            LogManager.error("Errore durante l'invio della richiesta", e);
            mostraAlert("Errore", "Impossibile inviare la richiesta: " + e.getMessage());
        }
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

    private void mostraAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
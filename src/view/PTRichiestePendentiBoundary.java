package view;

import bean.AssociazioneBean;
import controller.graphic.GestisciRichiestePTController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;
import model.Sessione;
import model.exception.DAOException;
import util.AlertManager;

public class PTRichiestePendentiBoundary {

    @FXML private Button btnAccetta;
    @FXML private Button btnChiudi;
    @FXML private Button btnRifiuta;
    @FXML private ListView<AssociazioneBean> lvRichieste;

    private GestisciRichiestePTController controller = new GestisciRichiestePTController();

    @FXML
    public void initialize() {
        lvRichieste.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        lvRichieste.setCellFactory(lv -> new ListCell<AssociazioneBean>() {
            @Override
            protected void updateItem(AssociazioneBean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("Richiesta di associazione da: " + item.getEmailCliente());
                    getStyleClass().add("richiesta-cell");
                }
            }
        });

        caricaRichieste();
    }

    private void caricaRichieste() {
        try {
            String emailPT = Sessione.getInstance().getUtente().getEmail();
            lvRichieste.getItems().setAll(controller.getRichiesteSospese(emailPT));
            btnAccetta.setDisable(true);
            btnRifiuta.setDisable(true);
            lvRichieste.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                    btnAccetta.setDisable(newVal == null)
            );
            lvRichieste.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                    btnRifiuta.setDisable(newVal == null)
            );
        } catch (DAOException _) {
            AlertManager.mostra("Impossibile caricare le richieste.");
        }
    }

    @FXML
    public void handleAccetta() {
        List<AssociazioneBean> selezionate = lvRichieste.getSelectionModel().getSelectedItems();
        try {
            for (AssociazioneBean bean : selezionate) {
                controller.accettaAssociazione(bean);
            }
            AlertManager.mostra("Cliente associato con successo!");
            caricaRichieste();
        } catch (DAOException _) {
            AlertManager.mostra("Impossibile accettare la richiesta.");
        }
    }

    @FXML
    public void handleRifiuta() {
        List<AssociazioneBean> selezionate = lvRichieste.getSelectionModel().getSelectedItems();
        try {
            for (AssociazioneBean bean : selezionate) {
                controller.rifiutaAssociazione(bean);
            }
            AlertManager.mostra("Cliente rifiutato con successo!");
            caricaRichieste();
        } catch (DAOException _) {
            AlertManager.mostra("Impossibile rifiutare la richiesta.");
        }
    }

    @FXML
    public void tornaAllaDashboard(ActionEvent event) {
        chiudiFinestra(event);
    }

    private void chiudiFinestra(ActionEvent event) {
        // Risaliamo dal bottone (source) fino alla finestra (Stage) per chiuderla
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSelezionaTutti() {
        lvRichieste.getSelectionModel().selectAll();
    }

}
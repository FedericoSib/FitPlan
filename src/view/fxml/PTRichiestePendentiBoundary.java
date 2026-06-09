package view.fxml;

import bean.AssociazioneBean;
import controller.GestisciRichiestePTController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;
import model.dao.DAOFactory;
import model.exception.DAOException;
import util.observer.NotificaManager;

public class PTRichiestePendentiBoundary {

    @FXML private Button btnAccetta;
    @FXML private Button btnChiudi;
    @FXML private Button btnRifiuta;
    @FXML private ListView<AssociazioneBean> lvRichieste;
    public static final String ERR = "Errore";
    public static final String SUCC = "Successo";


    private GestisciRichiestePTController controller = new GestisciRichiestePTController();

    @FXML
    public void initialize() {
        NotificaManager manager = new NotificaManager(DAOFactory.getNotificaDAO());
        controller.aggiungiObserver(manager);
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
            String emailPT = controller.getPT().getEmail();
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
            mostraAlert(Alert.AlertType.ERROR, ERR,
                    "impossibile caricare le richieste");
        }
    }

    @FXML
    public void handleAccetta() {
        List<AssociazioneBean> selezionate = lvRichieste.getSelectionModel().getSelectedItems();
        try {
            for (AssociazioneBean bean : selezionate) {
                controller.accettaAssociazione(bean);
            }
            mostraAlert(Alert.AlertType.INFORMATION, SUCC,
                    "Cliente associato con successo!");
            caricaRichieste();
        } catch (DAOException _) {
            mostraAlert(Alert.AlertType.ERROR, ERR,
                    "impossibile caricare le richieste");
        }
    }

    @FXML
    public void handleRifiuta() {
        List<AssociazioneBean> selezionate = lvRichieste.getSelectionModel().getSelectedItems();
        try {
            for (AssociazioneBean bean : selezionate) {
                controller.rifiutaAssociazione(bean);
            }
            mostraAlert(Alert.AlertType.INFORMATION, SUCC,
                    "Cliente rifiutato con successo!");
            caricaRichieste();
        } catch (DAOException _) {
            mostraAlert(Alert.AlertType.ERROR, ERR,
                    "Impossibile rifiutare la richiesta.");
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

    @FXML
    public void handleSelezionaTutti() {
        lvRichieste.getSelectionModel().selectAll();
    }

    private void mostraAlert(Alert.AlertType tipo, String titolo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
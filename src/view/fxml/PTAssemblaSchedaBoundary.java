package view.fxml;

import bean.EsercizioBean;
import bean.RichiestaSchedaBean;
import bean.SchedaBean;
import controller.graphic.AssemblaSchedaController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Sessione;
import model.dao.DAOFactory;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import util.LogManager;
import util.observer.NotificaManager;

public class PTAssemblaSchedaBoundary {

    @FXML private Label lblDestinatario;
    @FXML private TextField txtNome;
    @FXML private Spinner<Integer> spSerie;
    @FXML private Spinner<Integer> spRipetizioni;
    @FXML private Spinner<Integer> spRecupero;
    @FXML private TextField txtNoteEsercizio;
    @FXML private ListView<EsercizioBean> lvEsercizi;

    private final AssemblaSchedaController controller = new AssemblaSchedaController();
    private final ObservableList<EsercizioBean> esercizi = FXCollections.observableArrayList();
    private RichiestaSchedaBean richiesta;

    @FXML
    public void initialize() {
        controller.aggiungiObserver(new NotificaManager(DAOFactory.getNotificaDAO()));

        spSerie.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3));
        spRipetizioni.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        spRecupero.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 300, 90));

        lvEsercizi.setItems(esercizi);
        lvEsercizi.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(EsercizioBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
    }

    public void setRichiesta(RichiestaSchedaBean richiesta) {
        this.richiesta = richiesta;
        lblDestinatario.setText("Scheda per: " + richiesta.getClienteEmail() +
                " — Obiettivo: " + richiesta.getObiettivo());
    }

    @FXML
    public void handleAggiungiEsercizio() {
        String nome = txtNome.getText().trim();
        if (nome.isBlank()) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione", "Il nome dell'esercizio è obbligatorio.");
            return;
        }

        EsercizioBean esercizio = new EsercizioBean();
        esercizio.setNome(nome);
        esercizio.setSerie(spSerie.getValue());
        esercizio.setRipetizioni(spRipetizioni.getValue());
        esercizio.setRecuperoSecondi(spRecupero.getValue());
        esercizio.setNote(txtNoteEsercizio.getText().trim());

        esercizi.add(esercizio);

        // Reset campi
        txtNome.clear();
        txtNoteEsercizio.clear();
        spSerie.getValueFactory().setValue(3);
        spRipetizioni.getValueFactory().setValue(10);
        spRecupero.getValueFactory().setValue(90);
    }

    @FXML
    public void handleRimuoviEsercizio() {
        EsercizioBean selezionato = lvEsercizi.getSelectionModel().getSelectedItem();
        if (selezionato != null) esercizi.remove(selezionato);
    }

    @FXML
    public void handleInviaScheda() {
        if (richiesta == null) return;

        SchedaBean schedaBean = new SchedaBean();
        schedaBean.setEmailCliente(richiesta.getClienteEmail());
        schedaBean.setEmailPT(Sessione.getInstance().getUtente().getEmail());
        schedaBean.setEsercizi(new java.util.ArrayList<>(esercizi));

        try {
            controller.inviaScheda(schedaBean);
            mostraAlert(Alert.AlertType.INFORMATION, "Successo",
                    "Scheda inviata con successo a " + richiesta.getClienteEmail() + "!");
            ((Stage) lvEsercizi.getScene().getWindow()).close();
        } catch (InvalidFormException e) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione", e.getMessage());
        } catch (DAOException e) {
            LogManager.error("Errore invio scheda", e);
            mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore tecnico durante l'invio.");
        }
    }

    @FXML
    public void handleAnnulla() {
        ((Stage) lvEsercizi.getScene().getWindow()).close();
    }

    private void mostraAlert(Alert.AlertType tipo, String titolo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

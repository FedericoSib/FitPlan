package view.fxml;

import bean.*;
import controller.GestisciSchedaController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import model.Sessione;
import util.LogManager;

import java.util.ArrayList;
import java.util.List;

public class ClienteGestisciSchedaBoundary {

    @FXML private ChoiceBox<String> cbGiorni;
    @FXML private ListView<String> lvEsercizi;
    @FXML private ChoiceBox<String> cbEsercizi;
    @FXML private TextField txtCarico;
    @FXML private Spinner<Integer> spRipetizioni;
    @FXML private TextField txtNote;
    @FXML private ListView<String> lvStorico;

    private final GestisciSchedaController controller = new GestisciSchedaController();
    private SchedaBean scheda;

    @FXML
    public void initialize() {
        spRipetizioni.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        caricaScheda();
        caricaStorico();

        cbGiorni.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> {
                    if (newVal != null && scheda != null) {
                        aggiornaEsercizi(cbGiorni.getSelectionModel().getSelectedIndex());
                    }
                });
    }

    private void caricaScheda() {
        try {
            scheda = controller.getSchedaCliente();
            if (scheda == null || scheda.getGiorni().isEmpty()) {
                cbGiorni.setItems(FXCollections.observableArrayList("Nessuna scheda assegnata"));
                return;
            }

            // Popola ChoiceBox giorni
            List<String> nomiGiorni = new ArrayList<>();
            List<String> nomiEsercizi = new ArrayList<>();

            for (GiornoSchedaBean g : scheda.getGiorni()) {
                nomiGiorni.add(g.getNome());
                for (EsercizioBean e : g.getEsercizi()) {
                    if (!nomiEsercizi.contains(e.getNome())) {
                        nomiEsercizi.add(e.getNome());
                    }
                }
            }

            cbGiorni.setItems(FXCollections.observableArrayList(nomiGiorni));
            cbEsercizi.setItems(FXCollections.observableArrayList(nomiEsercizi));
            cbGiorni.getSelectionModel().selectFirst();
            aggiornaEsercizi(0);

        } catch (DAOException e) {
            LogManager.error("Errore caricamento scheda", e);
        }
    }

    private void aggiornaEsercizi(int giornoIndex) {
        GiornoSchedaBean giorno = scheda.getGiorni().get(giornoIndex);
        List<String> righe = new ArrayList<>();
        for (EsercizioBean e : giorno.getEsercizi()) {
            righe.add(String.format("%s  |  %dx%d  |  rec: %ds%s",
                    e.getNome(),
                    e.getSerie(),
                    e.getRipetizioni(),
                    e.getRecuperoSecondi(),
                    e.getNote().isBlank() ? "" : "  |  " + e.getNote()));
        }
        lvEsercizi.setItems(FXCollections.observableArrayList(righe));
    }

    private void caricaStorico() {
        try {
            List<ProgressiBean> storico = controller.getStorico();
            List<String> righe = new ArrayList<>();
            for (ProgressiBean p : storico) {
                righe.add(p.toString());
            }
            lvStorico.setItems(FXCollections.observableArrayList(righe));
        } catch (DAOException e) {
            LogManager.error("Errore caricamento storico", e);
        }
    }

    @FXML
    public void handleSalvaProgressi() {
        String esercizio = cbEsercizi.getValue();
        if (esercizio == null) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione",
                    "Seleziona un esercizio.");
            return;
        }

        double carico;
        try {
            carico = Double.parseDouble(
                    txtCarico.getText().trim().replace(",", "."));
        } catch (NumberFormatException _) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione",
                    "Inserisci un valore numerico per il carico (es. 80.5).");
            return;
        }

        ProgressiBean bean = new ProgressiBean();
        bean.setEmailCliente(Sessione.getInstance().getUtente().getEmail());
        bean.setNomeEsercizio(esercizio);
        bean.setCarico(carico);
        bean.setRipetizioni(spRipetizioni.getValue());
        bean.setNote(txtNote.getText().trim());

        try {
            controller.registraProgressi(bean);
            mostraAlert(Alert.AlertType.INFORMATION, "Successo",
                    "Progressi salvati!");

            // Reset campi
            txtCarico.clear();
            txtNote.clear();
            spRipetizioni.getValueFactory().setValue(1);

            // Aggiorna storico
            caricaStorico();

        } catch (InvalidFormException | DAOException e) {
            mostraAlert(Alert.AlertType.ERROR, "Errore", e.getMessage());
        }
    }

    @FXML
    public void handleChiudi() {
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

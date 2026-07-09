package view.fxml;

import bean.*;
import controller.AssemblaSchedaController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.exception.InvalidFormException;
import util.LogManager;

public class PTAssemblaSchedaBoundary {

    @FXML private Label lblDestinatario;
    @FXML private Label lblGiornoCorrente;
    @FXML private TextField txtNome;
    @FXML private Spinner<Integer> spSerie;
    @FXML private Spinner<Integer> spRipetizioni;
    @FXML private Spinner<Integer> spRecupero;
    @FXML private TextField txtNoteEsercizio;
    @FXML private ListView<EsercizioBean> lvEsercizi;
    @FXML private TextField txtNomeGiorno;
    @FXML private Label lblProgressoGiorni;

    private final AssemblaSchedaController controller = new AssemblaSchedaController();
    private final ObservableList<EsercizioBean> esercizioGiornoCorrente =
            FXCollections.observableArrayList();

    private RichiestaSchedaBean richiesta;
    private SchedaBean schedaBean;
    private int giornoCorrenteIndex = 0;
    private int totalGiorni;

    @FXML
    public void initialize() {
        controller.configuraObserverNotifiche();

        spSerie.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3));
        spRipetizioni.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        spRecupero.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 300, 90));

        lvEsercizi.setItems(esercizioGiornoCorrente);
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
        this.totalGiorni = richiesta.getFrequenzaSettimanale();
        String emailPT = controller.getPT().getEmail();

        schedaBean = new SchedaBean();
        schedaBean.setEmailCliente(richiesta.getClienteEmail());
        schedaBean.setEmailPT(emailPT);

        for (int i = 1; i <= totalGiorni; i++) {
            schedaBean.getGiorni().add(new GiornoSchedaBean("Giorno " + i));
        }

        lblDestinatario.setText("Scheda per: " + richiesta.getClienteEmail() +
                " — Obiettivo: " + richiesta.getObiettivo());

        aggiornaVistaGiorno();
    }

    private void aggiornaVistaGiorno() {
        GiornoSchedaBean giornoCorrente = schedaBean.getGiorni().get(giornoCorrenteIndex);

        txtNomeGiorno.setText(giornoCorrente.getNome());

        lblProgressoGiorni.setText("Giorno " + (giornoCorrenteIndex + 1) +
                " di " + totalGiorni);

        esercizioGiornoCorrente.setAll(giornoCorrente.getEsercizi());

        // Reset campi input
        txtNome.clear();
        txtNoteEsercizio.clear();
        spSerie.getValueFactory().setValue(3);
        spRipetizioni.getValueFactory().setValue(10);
        spRecupero.getValueFactory().setValue(90);
    }

    @FXML
    public void handleNomeGiornoChanged() {
        String nuovoNome = txtNomeGiorno.getText().trim();
        if (!nuovoNome.isBlank()) {
            schedaBean.getGiorni().get(giornoCorrenteIndex).setNome(nuovoNome);
        }
    }

    @FXML
    public void handleAggiungiEsercizio() {
        String nome = txtNome.getText().trim();
        if (nome.isBlank()) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione",
                    "Il nome dell'esercizio è obbligatorio.");
            return;
        }

        EsercizioBean esercizio = new EsercizioBean();
        esercizio.setNome(nome);
        esercizio.setSerie(spSerie.getValue());
        esercizio.setRipetizioni(spRipetizioni.getValue());
        esercizio.setRecuperoSecondi(spRecupero.getValue());
        esercizio.setNote(txtNoteEsercizio.getText().trim());
        schedaBean.getGiorni().get(giornoCorrenteIndex).aggiungiEsercizio(esercizio);
        esercizioGiornoCorrente.add(esercizio);
        txtNome.clear();
        txtNoteEsercizio.clear();
        spSerie.getValueFactory().setValue(3);
        spRipetizioni.getValueFactory().setValue(10);
        spRecupero.getValueFactory().setValue(90);
    }

    @FXML
    public void handleRimuoviEsercizio() {
        EsercizioBean selezionato = lvEsercizi.getSelectionModel().getSelectedItem();
        if (selezionato != null) {
            schedaBean.getGiorni().get(giornoCorrenteIndex)
                    .getEsercizi().remove(selezionato);
            esercizioGiornoCorrente.remove(selezionato);
        }
    }

    @FXML
    public void handleGiornoPrecedente() {
        salvaNomeGiornoCorrente();
        if (giornoCorrenteIndex > 0) {
            giornoCorrenteIndex--;
            aggiornaVistaGiorno();
        }
    }

    @FXML
    public void handleGiornoSuccessivo() {
        salvaNomeGiornoCorrente();
        if (giornoCorrenteIndex < totalGiorni - 1) {
            giornoCorrenteIndex++;
            aggiornaVistaGiorno();
        }
    }

    private void salvaNomeGiornoCorrente() {
        String nome = txtNomeGiorno.getText().trim();
        if (!nome.isBlank()) {
            schedaBean.getGiorni().get(giornoCorrenteIndex).setNome(nome);
        }
    }

    @FXML
    public void handleInviaScheda() {
        salvaNomeGiornoCorrente();

        try {
            controller.inviaScheda(schedaBean);
            mostraAlert(Alert.AlertType.INFORMATION, "Successo",
                    "Scheda inviata con successo a " +
                            richiesta.getClienteEmail() + "!");
            ((Stage) lvEsercizi.getScene().getWindow()).close();
        } catch (InvalidFormException e) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione", e.getMessage());
        } catch (Exception e) {
            LogManager.error("Errore invio scheda", e);
            mostraAlert(Alert.AlertType.ERROR, "Errore",
                    "Errore tecnico durante l'invio.");
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
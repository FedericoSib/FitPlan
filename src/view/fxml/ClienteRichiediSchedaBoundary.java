package view.fxml;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import util.LogManager;
import controller.RichiediSchedaController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.entity.Cliente;
import model.Sessione;
import bean.*;
import model.exception.InvalidFormException;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class ClienteRichiediSchedaBoundary {

    @FXML private ChoiceBox<String> cbSesso;
    @FXML private ChoiceBox<String> cbObiettivo;
    @FXML private TextField txtEta;
    @FXML private TextField txtPeso;
    @FXML private Spinner<Integer> spFrequenza;
    @FXML private TextArea txtNote;
    @FXML private ImageView imgFitplan;

    private RichiediSchedaController controller = new RichiediSchedaController();

    @FXML
    public void initialize() {
        mostraFormRichiesta();
    }

    public void mostraFormRichiesta() {
        cbSesso.getItems().addAll("Maschio", "Femmina");
        cbObiettivo.getItems().addAll("Aumento Massa", "Definizione", "Perdita Peso", "Mantenimento");

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 7, 3);
        spFrequenza.setValueFactory(valueFactory);

        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
            imgFitplan.setImage(logo);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ",e);
        }
    }

    public RichiestaSchedaBean raccogliDatiRichiesta() {
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();

        RichiestaSchedaBean bean = new RichiestaSchedaBean();
        bean.setClienteEmail(cliente.getEmail());
        bean.setIdPersonalTrainer(cliente.getIdPersonalTrainer());
        bean.setSesso(cbSesso.getValue());
        bean.setEta(Integer.parseInt(txtEta.getText()));
        bean.setPeso(Double.parseDouble(txtPeso.getText()));
        bean.setObiettivo(cbObiettivo.getValue());
        bean.setFrequenzaSettimanale(spFrequenza.getValue());
        bean.setNote(txtNote.getText());

        return bean;
    }

    public void mostraMessaggioConferma(String messaggio) {
        mostraAlert(Alert.AlertType.INFORMATION, "Successo", messaggio);
    }

    public void mostraMessaggioErrore(String errore) {
        mostraAlert(Alert.AlertType.ERROR, "Errore", errore);
    }

    public void evidenziaCampiMancanti(List<String> campi) {
        String elenco = String.join(", ", campi);
        mostraAlert(Alert.AlertType.WARNING, "Dati Mancanti",
                "Per favore, compila i seguenti campi obbligatori: " + elenco);
    }

    // --- LOGICA DI COORDINAMENTO (Handler del bottone) ---

    @FXML
    public void gestisciInvio() {
        try {
            List<String> campiVuoti = verificaInputTestuali();
            if (!campiVuoti.isEmpty()) {
                evidenziaCampiMancanti(campiVuoti);
                return;
            }
            RichiestaSchedaBean bean = raccogliDatiRichiesta();
            bean.setClienteEmail(bean.getClienteEmail().toLowerCase());
            controller.elaboraRichiesta(bean);
            mostraMessaggioConferma("Richiesta inviata correttamente!");
            Stage stage = (Stage) imgFitplan.getScene().getWindow();
            stage.close();
        } catch (InvalidFormException e) {
            mostraMessaggioErrore(e.getMessage());
        } catch (NumberFormatException _) {
            mostraMessaggioErrore("Inserisci numeri validi per Età e Peso.");
        } catch (Exception e) {
            LogManager.error("Errore critico invio scheda", e);
            mostraMessaggioErrore("Errore tecnico imprevisto.");
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

    private List<String> verificaInputTestuali() {
        List<String> mancanti = new ArrayList<>();
        if (cbSesso.getValue() == null) mancanti.add("Sesso");
        if (cbObiettivo.getValue() == null) mancanti.add("Obiettivo");
        if (txtEta.getText().isEmpty()) mancanti.add("Età");
        if (txtPeso.getText().isEmpty()) mancanti.add("Peso");
        return mancanti;
    }

    private void mostraAlert(Alert.AlertType tipo, String titolo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
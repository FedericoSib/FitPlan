package view;

import util.LogManager;
import controller.graphic.RichiediSchedaController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.entity.RichiestaScheda;
import model.entity.Cliente;
import model.entity.DatiFisici;
import model.Sessione;
import model.exception.InvalidFormException;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class ClienteRichiediSchedaBoundary {

    // Componenti FXML (fx:id)
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
        // Popolamento ChoiceBox
        cbSesso.getItems().addAll("Maschio", "Femmina");
        cbObiettivo.getItems().addAll("Aumento Massa", "Definizione", "Perdita Peso", "Mantenimento");

        // Configurazione Spinner
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 7, 3);
        spFrequenza.setValueFactory(valueFactory);

        // Caricamento Logo
        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
            imgFitplan.setImage(logo);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ",e);
        }
    }

    /*public RichiestaScheda raccogliDatiRichiesta() {
        Utente utenteLoggato = Sessione.getInstance().getUtente();
        String emailCliente = "";
        String idPT = "";
        return new RichiestaScheda(
                cbSesso.getValue(),
                Integer.parseInt(txtEta.getText()),
                Double.parseDouble(txtPeso.getText()),
                cbObiettivo.getValue(),
                spFrequenza.getValue(),
                txtNote.getText(),
                emailCliente,
                idPT       // Temporaneo: andrebbe preso dal profilo atleta
        );
    }*/

    public RichiestaScheda raccogliDatiRichiesta() {
        // 1. Recuperiamo l'utente dalla sessione (sappiamo che è un Cliente grazie ai controlli preventivi)
        Cliente cliente = (Cliente) Sessione.getInstance().getUtente();

        // 2. Creiamo il Value Object DatiFisici raggruppando i 3 parametri
        DatiFisici datiFisici = new DatiFisici(
                cbSesso.getValue(),
                Integer.parseInt(txtEta.getText()),
                Double.parseDouble(txtPeso.getText())
        );

        // 3. Creiamo l'oggetto RichiestaScheda passando l'oggetto datiFisici (totale 6 parametri)
        return new RichiestaScheda(
                datiFisici,
                cbObiettivo.getValue(),
                spFrequenza.getValue(),
                txtNote.getText(),
                cliente.getEmail(),
                cliente.getIdPersonalTrainer()
        );
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
            // 1. Validazione Formale (Campi testuali vuoti?)
            List<String> campiVuoti = verificaInputTestuali();
            if (!campiVuoti.isEmpty()) {
                evidenziaCampiMancanti(campiVuoti);
                return;
            }

            // 2. Raccolta Dati (Ora siamo sicuri che il Cliente ha un PT)
            // Il metodo raccogliDatiRichiesta() userà l'ID del PT reale dalla sessione
            RichiestaScheda richiesta = raccogliDatiRichiesta();

            // 3. Delegazione al Controller di Business
            // Questo metodo interagirà con il DAO per salvare la richiesta nel DB/Memoria
            controller.elaboraRichiesta(richiesta);

            // 4. Feedback di Successo e Ritorno alla Dashboard
            mostraMessaggioConferma("La tua richiesta è stata inviata con successo al tuo Personal Trainer!");

            // Opzionale: Dopo l'invio, riportiamo l'utente alla Home
            Navigator.pushScene("/view/ClienteDashboard.fxml", "FitPlan - Dashboard");

        } catch (NumberFormatException _) {
            // Errore se l'utente scrive lettere dove servono numeri (Età/Peso)
            mostraMessaggioErrore("Assicurati che Età e Peso siano numeri validi (es: 25, 70.5).");
        } catch (InvalidFormException e) {
            // Eccezione di business (es: età negativa, peso assurdo)
            mostraMessaggioErrore(e.getMessage());
        } catch (Exception e) {
            // Paracadute per errori imprevisti (es: database offline)
            e.printStackTrace(); // Utile in fase di sviluppo
            mostraMessaggioErrore("Si è verificato un errore imprevisto durante l'invio.");
        }
    }

    @FXML
    public void tornaAllaDashboard() {
        Navigator.pushScene("/view/ClienteDashboard.fxml", "FitPlan - ClienteDashboard");
    }

    // Metodo di utility interna per validare i campi prima della raccolta
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
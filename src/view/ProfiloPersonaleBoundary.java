package view;

import bean.UtenteBean;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import model.Sessione;
import util.LogManager;

public class ProfiloPersonaleBoundary {

    @FXML private ImageView imgFitplan;
    @FXML private ImageView imgCopia;
    @FXML private ImageView imgAvatar;
    @FXML private Label lblCodiceUnivoco;
    @FXML private Label lblNome;
    @FXML private Label lblCognome;

    public void setDatiUtente(UtenteBean bean) {
        if (bean == null) return;
        try {
            Image logo = new Image(getClass().getResourceAsStream("/view/Immages/logo.png"));
            imgFitplan.setImage(logo);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare il logo: ", e);
        }
        try {
            Image copia = new Image(getClass().getResourceAsStream("/view/Immages/Copia.png"));
            imgCopia.setImage(copia);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare l'immagine copia: ", e);
        }
        try {
            Image avatar = new Image(getClass().getResourceAsStream("/view/Immages/AvatarBase.png"));
            imgAvatar.setImage(avatar);
        } catch (Exception e) {
            LogManager.error("Impossibile caricare l'immagine dell'avatar: ", e);
        }
        // Impostiamo i dati comuni
        lblNome.setText(bean.getNome());
        lblCognome.setText(bean.getCognome() );
        lblCodiceUnivoco.setText(Sessione.getInstance().getUtente().getId());
    }

    @FXML
    public void handleCopiaCodice() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(lblCodiceUnivoco.getText());
        clipboard.setContent(content);

        // Opzionale: un piccolo feedback visivo sulla label
        lblCodiceUnivoco.setOpacity(0.5);
        LogManager.info("Codice copiato: " + lblCodiceUnivoco.getText());
    }

    @FXML
    public void tornaAllaDashboard(ActionEvent event) {
        // Chiude semplicemente il banner sovrapposto
        chiudiFinestra(event);
    }

    // Metodo privato di utility per non ripetere il codice
    private void chiudiFinestra(ActionEvent event) {
        // Risaliamo dal bottone (source) fino alla finestra (Stage) per chiuderla
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
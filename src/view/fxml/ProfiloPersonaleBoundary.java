package view.fxml;

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
        lblNome.setText(bean.getNome());
        lblCognome.setText(bean.getCognome());
        lblCodiceUnivoco.setText(bean.getId());
    }

    @FXML
    public void handleCopiaCodice() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(lblCodiceUnivoco.getText());
        clipboard.setContent(content);

        lblCodiceUnivoco.setOpacity(0.5);
        LogManager.info("Codice copiato: " + lblCodiceUnivoco.getText());
    }

    @FXML
    public void tornaAllaDashboard(ActionEvent event) {
        chiudiFinestra(event);
    }

    private void chiudiFinestra(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
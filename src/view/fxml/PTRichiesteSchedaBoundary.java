package view.fxml;

import bean.RichiestaSchedaBean;
import controller.graphic.AssemblaSchedaController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.PersonalTrainer;
import model.exception.DAOException;
import util.LogManager;
import util.observer.NotificaManager;

import java.io.IOException;
import java.util.List;

public class PTRichiesteSchedaBoundary {

    @FXML private ListView<RichiestaSchedaBean> lvRichieste;
    @FXML private VBox vboxDettagli;
    @FXML private Label lblCliente;
    @FXML private Label lblSesso;
    @FXML private Label lblEta;
    @FXML private Label lblPeso;
    @FXML private Label lblObiettivo;
    @FXML private Label lblFrequenza;
    @FXML private Label lblNote;
    @FXML private Button btnAssemblaSubito;
    @FXML private Button btnDopoAssembla;

    public enum Modalita { NUOVE, IN_LAVORAZIONE }

    private Modalita modalita = Modalita.NUOVE;

    public void setModalita(Modalita modalita) {
        this.modalita = modalita;
    }

    private final AssemblaSchedaController controller = new AssemblaSchedaController();

    @FXML
    public void initialize() {
        // Collega observer per notifiche
        controller.aggiungiObserver(new NotificaManager(DAOFactory.getNotificaDAO()));

        // Configura cella ListView
        lvRichieste.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(RichiestaSchedaBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getClienteEmail() + " — " + item.getObiettivo());
            }
        });

        // Carica le richieste
        caricaRichieste();

        // Listener selezione → mostra dettagli
        lvRichieste.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    if (selected != null) {
                        mostraDettagli(selected);
                        btnAssemblaSubito.setDisable(false);
                        btnDopoAssembla.setDisable(false);
                    }
                });
    }

    private void mostraDettagli(RichiestaSchedaBean r) {
        lblCliente.setText("Cliente: " + r.getClienteEmail());
        lblSesso.setText("Sesso: " + r.getSesso());
        lblEta.setText("Età: " + r.getEta() + " anni");
        lblPeso.setText("Peso: " + r.getPeso() + " kg");
        lblObiettivo.setText("Obiettivo: " + r.getObiettivo());
        lblFrequenza.setText("Frequenza: " + r.getFrequenzaSettimanale() + " volte/settimana");
        lblNote.setText("Note: " + (r.getNote().isBlank() ? "—" : r.getNote()));

        vboxDettagli.setVisible(true);
        vboxDettagli.setManaged(true);
    }

    @FXML
    public void handleAssemblaSubito() {
        RichiestaSchedaBean selezionata = lvRichieste.getSelectionModel().getSelectedItem();
        if (selezionata == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/fxml/PTAssemblaScheda.fxml"));
            Parent root = loader.load();

            PTAssemblaSchedaBoundary assemblaController = loader.getController();
            assemblaController.setRichiesta(selezionata);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(lvRichieste.getScene().getWindow());

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.showAndWait();

            // Ricarica dopo il ritorno
            caricaRichieste();
            vboxDettagli.setVisible(false);
            vboxDettagli.setManaged(false);
            btnAssemblaSubito.setDisable(true);
            btnDopoAssembla.setDisable(true);

        } catch (IOException e) {
            LogManager.error("Errore apertura assembla scheda", e);
        }
    }

    @FXML
    public void handleDopoAssembla() {
        RichiestaSchedaBean selezionata = lvRichieste.getSelectionModel().getSelectedItem();
        if (selezionata == null) return;

        try {
            controller.segnaInLavorazione(selezionata.getClienteEmail());
            // Rimuove dalla lista visuale
            lvRichieste.getItems().remove(selezionata);
            vboxDettagli.setVisible(false);
            vboxDettagli.setManaged(false);
            btnAssemblaSubito.setDisable(true);
            btnDopoAssembla.setDisable(true);
        } catch (DAOException e) {
            LogManager.error("Errore aggiornamento stato richiesta", e);
        }
    }

    public void caricaRichieste() {
        PersonalTrainer pt = (PersonalTrainer) Sessione.getInstance().getUtente();
        try {
            List<RichiestaSchedaBean> richieste = modalita == Modalita.IN_LAVORAZIONE
                    ? controller.getRichiesteInLavorazione(pt.getEmail())
                    : controller.getRichiestePerPT(pt.getEmail());
            lvRichieste.setItems(FXCollections.observableArrayList(richieste));

            // Nasconde "Assembla in Seguito" se siamo già in lavorazione
            if (modalita == Modalita.IN_LAVORAZIONE) {
                btnDopoAssembla.setVisible(false);
                btnDopoAssembla.setManaged(false);
            }
        } catch (DAOException e) {
            LogManager.error("Errore caricamento richieste scheda", e);
        }
    }

    @FXML
    public void handleChiudi() {
        ((Stage) lvRichieste.getScene().getWindow()).close();
    }
}

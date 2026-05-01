package test;

import bean.AssociazioneBean;
import bean.PersonalTrainerBean;
import bean.RegistrazioneBean;
import controller.graphic.AssociaPTController;
import controller.graphic.GestisciRichiestePTController;
import controller.graphic.RegistrazioneController;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.*;
import model.exception.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssociaPTControllerTest {

    private AssociaPTController controller;
    private PersonalTrainer ptDiTest;

    @BeforeEach
    void setUp() throws Exception {
        DAOFactory.setMode(1);
        controller = new AssociaPTController();
        Sessione.getInstance().setUtente(null);

        // Registra un PT nel DAO in-memory da usare nelle ricerche
        RegistrazioneController reg = new RegistrazioneController();
        RegistrazioneBean b = new RegistrazioneBean();
        b.setNome("Luca");
        b.setCognome("Neri");
        b.setEmail("luca@pt.it");
        b.setPassword("Pwd1!");
        b.setRuolo(2);
        reg.registraNuovoUtente(b);

        // Recupera il PT per avere l'ID generato
        ptDiTest = (PersonalTrainer) DAOFactory.getUtenteDAO()
                .trovaUtentePerEmail("luca@pt.it");
    }

    @AfterEach
    void tearDown() {
        Sessione.getInstance().setUtente(null);
    }

    // ── RICERCA PER ID ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Ricerca per ID esistente → restituisce 1 bean")
    void cercaTrainer_perId_trovato() throws TrainerNotFoundException {
        String id = ptDiTest.getId();
        List<PersonalTrainerBean> risultati = controller.cercaTrainer(id);

        assertEquals(1, risultati.size());
        assertEquals("Luca", risultati.get(0).getNome());
        assertEquals("luca@pt.it", risultati.get(0).getEmail());
    }

    @Test
    @DisplayName("Ricerca per ID inesistente → TrainerNotFoundException")
    void cercaTrainer_perId_nonTrovato() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("PT-9999"));
    }

    // ── RICERCA PER EMAIL ────────────────────────────────────────────────────

    @Test
    @DisplayName("Ricerca per email esistente → restituisce 1 bean")
    void cercaTrainer_perEmail_trovato() throws TrainerNotFoundException {
        List<PersonalTrainerBean> risultati = controller.cercaTrainer("luca@pt.it");

        assertEquals(1, risultati.size());
        assertEquals("Luca", risultati.get(0).getNome());
    }

    @Test
    @DisplayName("Ricerca per email inesistente → TrainerNotFoundException")
    void cercaTrainer_perEmail_nonTrovata() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("nessuno@pt.it"));
    }

    // ── RICERCA PER NOME ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Ricerca per nome → restituisce almeno 1 bean")
    void cercaTrainer_perNome_trovato() throws TrainerNotFoundException {
        List<PersonalTrainerBean> risultati = controller.cercaTrainer("Luca");

        assertFalse(risultati.isEmpty());
        assertEquals("Luca", risultati.get(0).getNome());
    }

    @Test
    @DisplayName("Ricerca per cognome → restituisce almeno 1 bean")
    void cercaTrainer_perCognome_trovato() throws TrainerNotFoundException {
        List<PersonalTrainerBean> risultati = controller.cercaTrainer("Neri");
        assertFalse(risultati.isEmpty());
    }

    @Test
    @DisplayName("Ricerca per nome inesistente → TrainerNotFoundException")
    void cercaTrainer_perNome_nonTrovato() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("Inesistente"));
    }

    // ── MAPPING Entity → Bean ────────────────────────────────────────────────

    @Test
    @DisplayName("Tutti i campi copiati correttamente nel Bean")
    void cercaTrainer_mappingBean() throws TrainerNotFoundException {
        List<PersonalTrainerBean> risultati = controller.cercaTrainer("luca@pt.it");
        PersonalTrainerBean bean = risultati.get(0);

        assertEquals("Luca", bean.getNome());
        assertEquals("Neri", bean.getCognome());
        assertEquals("luca@pt.it", bean.getEmail());
        assertNotNull(bean.getId());
    }

    // ── inviaRichiestaAssociazione ───────────────────────────────────────────

    @Test
    @DisplayName("Invio richiesta → stato cliente in sessione diventa PENDING")
    void inviaRichiesta_statoDiventaPending() throws Exception {
        Cliente cliente = new Cliente("Anna", "Bianchi", "anna@test.it", "pwd");
        Sessione.getInstance().setUtente(cliente);

        AssociazioneBean bean = new AssociazioneBean();
        bean.setEmailCliente("anna@test.it");
        bean.setEmailPT("luca@pt.it");

        controller.inviaRichiestaAssociazione(bean);

        assertEquals(StatoAssociazione.PENDING, cliente.getStatoAssociazione());
        assertEquals("luca@pt.it", cliente.getIdPersonalTrainer());
    }

    @Test
    @DisplayName("Invio richiesta → visibile nelle richieste sospese del PT")
    void inviaRichiesta_visibileAlPT() throws Exception {
        Cliente cliente = new Cliente("Anna", "Bianchi", "anna@test.it", "pwd");
        Sessione.getInstance().setUtente(cliente);

        AssociazioneBean bean = new AssociazioneBean();
        bean.setEmailCliente("anna@test.it");
        bean.setEmailPT("luca@pt.it");

        controller.inviaRichiestaAssociazione(bean);

        GestisciRichiestePTController ptController = new GestisciRichiestePTController();
        List<AssociazioneBean> richieste = ptController.getRichiesteSospese("luca@pt.it");

        assertEquals(1, richieste.size());
        assertEquals("anna@test.it", richieste.get(0).getEmailCliente());
    }
}
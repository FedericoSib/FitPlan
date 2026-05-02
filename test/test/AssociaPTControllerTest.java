package test;

import controller.graphic.*;
import bean.AssociazioneBean;
import bean.PersonalTrainerBean;
import model.Sessione;
import model.dao.DAOFactory;
import model.dao.PersonalTrainerDAOMemory;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import model.exception.TrainerNotFoundException;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per AssociaPTController.
 *
 * Ogni test che necessita di un PT nella lista lo inserisce autonomamente
 * tramite helper, garantendo indipendenza dall'ordine di esecuzione.
 */
class AssociaPTControllerTest {

    private AssociaPTController controller;

    private static final String PT_EMAIL      = "trainer@gym.it";
    private static final String PT_NOME       = "Giorgio";
    private static final String PT_COGN       = "Ferro";
    private static final String CLIENTE_EMAIL = "cliente@test.it";

    @BeforeAll
    static void setupDAO() {
        DAOFactory.setMode(1);
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new AssociaPTController();

        // Lista statica sempre pulita: ogni test parte da zero
        svuotaListaPT();

        // Sessione con un Cliente fresco
        Cliente c = new Cliente("Test", "Cliente", CLIENTE_EMAIL, "pwd");
        c.setStatoAssociazione(StatoAssociazione.NESSUNA);
        Sessione.getInstance().setUtente(c);
    }

    // ─────────────────────────────────────────────
    //  RICERCA — lista vuota
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Ricerca per ID inesistente → TrainerNotFoundException")
    void testCercaPerIdNonEsistente() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("PT-999"));
    }

    @Test
    @DisplayName("Ricerca per Email inesistente → TrainerNotFoundException")
    void testCercaPerEmailNonEsistente() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("nessuno@gym.it"));
    }

    @Test
    @DisplayName("Ricerca per Nome inesistente → TrainerNotFoundException")
    void testCercaPerNomeNonEsistente() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("NomeInesistente"));
    }

    // ─────────────────────────────────────────────
    //  BRANCH COVERAGE — discriminazione tipo ricerca
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Prefisso 'pt-' minuscolo → branch ID (toUpperCase)")
    void testCercaConPrefissoPtMinuscolo() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("pt-001"));
    }

    @Test
    @DisplayName("Stringa con formato email → branch email")
    void testCercaBranchEmail() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("qualcuno@dominio.com"));
    }

    @Test
    @DisplayName("Stringa senza '@' e senza 'PT-' → branch nome")
    void testCercaBranchNome() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("SoloNome"));
    }

    // ─────────────────────────────────────────────
    //  RICERCA POSITIVA — ogni test popola la lista da solo
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Ricerca per Email esistente → lista con 1 bean")
    void testCercaPerEmailEsistente() throws Exception {
        aggiungiPTInLista(new PersonalTrainer(PT_NOME, PT_COGN, PT_EMAIL, "pwd"));

        List<PersonalTrainerBean> risultati = controller.cercaTrainer(PT_EMAIL);

        assertEquals(1, risultati.size());
        assertEquals(PT_EMAIL, risultati.get(0).getEmail());
    }

    @Test
    @DisplayName("Ricerca per Nome esistente → lista non vuota")
    void testCercaPerNomeEsistente() throws Exception {
        aggiungiPTInLista(new PersonalTrainer(PT_NOME, PT_COGN, PT_EMAIL, "pwd"));

        List<PersonalTrainerBean> risultati = controller.cercaTrainer(PT_NOME);

        assertFalse(risultati.isEmpty());
        assertEquals(PT_NOME, risultati.get(0).getNome());
    }

    @Test
    @DisplayName("Ricerca per Cognome esistente → lista non vuota")
    void testCercaPerCognomeEsistente() throws Exception {
        aggiungiPTInLista(new PersonalTrainer(PT_NOME, PT_COGN, PT_EMAIL, "pwd"));

        List<PersonalTrainerBean> risultati = controller.cercaTrainer(PT_COGN);

        assertFalse(risultati.isEmpty());
        assertEquals(PT_COGN, risultati.get(0).getCognome());
    }

    @Test
    @DisplayName("Ricerca per ID esistente → bean con ID corretto")
    void testCercaPerIdEsistente() throws Exception {
        PersonalTrainer pt = new PersonalTrainer(PT_NOME, PT_COGN, PT_EMAIL, "pwd");
        aggiungiPTInLista(pt);

        // L'id è generato automaticamente nel costruttore: lo leggiamo dall'entity
        String idReale = pt.getId();
        List<PersonalTrainerBean> risultati = controller.cercaTrainer(idReale);

        assertFalse(risultati.isEmpty());
        assertEquals(idReale, risultati.get(0).getId());
    }

    // ─────────────────────────────────────────────
    //  INVIO RICHIESTA ASSOCIAZIONE
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("inviaRichiestaAssociazione → stato sessione diventa PENDING")
    void testInviaRichiestaAssociazione() throws DAOException {
        AssociazioneBean bean = new AssociazioneBean();
        bean.setEmailCliente(CLIENTE_EMAIL);
        bean.setEmailPT(PT_EMAIL);

        controller.inviaRichiestaAssociazione(bean);

        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        assertEquals(StatoAssociazione.PENDING, c.getStatoAssociazione());
        assertEquals(PT_EMAIL, c.getIdPersonalTrainer());
    }

    // ─────────────────────────────────────────────
    //  HELPER
    // ─────────────────────────────────────────────

    private void svuotaListaPT() throws Exception {
        getListaPT().clear();
    }

    private void aggiungiPTInLista(PersonalTrainer pt) throws Exception {
        getListaPT().add(pt);
    }

    @SuppressWarnings("unchecked")
    private List<PersonalTrainer> getListaPT() throws Exception {
        Field field = PersonalTrainerDAOMemory.class.getDeclaredField("listaPT");
        field.setAccessible(true);
        return (List<PersonalTrainer>) field.get(null);
    }
}

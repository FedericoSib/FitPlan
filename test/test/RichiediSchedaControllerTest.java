package test;

import bean.RichiestaSchedaBean;
import controller.graphic.RichiediSchedaController;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.*;
import model.exception.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class RichiediSchedaControllerTest {

    private RichiediSchedaController controller;

    @BeforeEach
    void setUp() {
        DAOFactory.setMode(1);
        controller = new RichiediSchedaController();
    }

    @AfterEach
    void tearDown() {
        Sessione.getInstance().setUtente(null);
    }

    // ── verificaAssociazionePT ───────────────────────────────────────────────

    @Test
    @DisplayName("Stato NESSUNA → TrainerNotAssociatedException")
    void verifica_statoNessuna() {
        setClienteInSessione(StatoAssociazione.NESSUNA);
        assertThrows(TrainerNotAssociatedException.class,
                () -> controller.verificaAssociazionePT());
    }

    @Test
    @DisplayName("Stato PENDING → TrainerNotAssociatedException")
    void verifica_statoPending() {
        setClienteInSessione(StatoAssociazione.PENDING);
        assertThrows(TrainerNotAssociatedException.class,
                () -> controller.verificaAssociazionePT());
    }

    @Test
    @DisplayName("Stato ASSOCIATO → nessuna eccezione")
    void verifica_statoAssociato() {
        setClienteInSessione(StatoAssociazione.ASSOCIATO);
        assertDoesNotThrow(() -> controller.verificaAssociazionePT());
    }

    // ── VALIDAZIONE PESO (boundary values) ──────────────────────────────────

    @Test
    @DisplayName("Peso = 0 → InvalidFormException")
    void elabora_pesoZero() {
        RichiestaSchedaBean bean = buildBeanValido();
        bean.setPeso(0);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @DisplayName("Peso negativo → InvalidFormException")
    void elabora_pesoNegativo() {
        RichiestaSchedaBean bean = buildBeanValido();
        bean.setPeso(-10);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @DisplayName("Peso = 201 → InvalidFormException")
    void elabora_pesoTroppoAlto() {
        RichiestaSchedaBean bean = buildBeanValido();
        bean.setPeso(201);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @DisplayName("Peso = 1 (minimo valido) → OK")
    void elabora_pesoMinimoValido() {
        RichiestaSchedaBean bean = buildBeanValido();
        bean.setPeso(1);
        assertDoesNotThrow(() -> controller.elaboraRichiesta(bean));
    }

    @Test
    @DisplayName("Peso = 200 (massimo valido) → OK")
    void elabora_pesoMassimoValido() {
        RichiestaSchedaBean bean = buildBeanValido();
        bean.setPeso(200);
        assertDoesNotThrow(() -> controller.elaboraRichiesta(bean));
    }

    // ── VALIDAZIONE ETÀ (boundary values) ───────────────────────────────────

    @Test
    @DisplayName("Età = 9 → InvalidFormException")
    void elabora_etaTroppoGiovane() {
        RichiestaSchedaBean bean = buildBeanValido();
        bean.setEta(9);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @DisplayName("Età = 101 → InvalidFormException")
    void elabora_etaTroppoAlta() {
        RichiestaSchedaBean bean = buildBeanValido();
        bean.setEta(101);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @DisplayName("Età = 10 (minimo valido) → OK")
    void elabora_etaMinimaValida() {
        RichiestaSchedaBean bean = buildBeanValido();
        bean.setEta(10);
        assertDoesNotThrow(() -> controller.elaboraRichiesta(bean));
    }

    @Test
    @DisplayName("Età = 100 (massimo valido) → OK")
    void elabora_etaMassimaValida() {
        RichiestaSchedaBean bean = buildBeanValido();
        bean.setEta(100);
        assertDoesNotThrow(() -> controller.elaboraRichiesta(bean));
    }

    // ── FLUSSO COMPLETO ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Dati validi → richiesta salvata senza eccezioni")
    void elabora_ok() {
        assertDoesNotThrow(() -> controller.elaboraRichiesta(buildBeanValido()));
    }

    @Test
    @DisplayName("Richiesta salvata → recuperabile dal DAO per PT")
    void elabora_richiestaRecuperabile() throws Exception {
        controller.elaboraRichiesta(buildBeanValido());

        var richieste = DAOFactory.getRichiestaDAO()
                .prendiRichiestePerPT("pt@fitplan.it");

        assertFalse(richieste.isEmpty());
        assertEquals("mario@test.it", richieste.get(0).getClienteEmail());
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private void setClienteInSessione(StatoAssociazione stato) {
        Cliente c = new Cliente("Test", "User", "test@test.it", "pwd");
        c.setStatoAssociazione(stato);
        Sessione.getInstance().setUtente(c);
    }

    private RichiestaSchedaBean buildBeanValido() {
        RichiestaSchedaBean b = new RichiestaSchedaBean();
        b.setPeso(75);
        b.setEta(30);
        b.setSesso("M");
        b.setObiettivo("Dimagrimento");
        b.setFrequenzaSettimanale(3);
        b.setNote("nessuna");
        b.setClienteEmail("mario@test.it");
        b.setIdPersonalTrainer("pt@fitplan.it");
        return b;
    }
}
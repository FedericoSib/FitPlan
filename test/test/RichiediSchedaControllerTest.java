package controller.graphic;

import bean.RichiestaSchedaBean;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.Cliente;
import model.entity.StatoAssociazione;
import model.exception.InvalidFormException;
import model.exception.TrainerNotAssociatedException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per RichiediSchedaController.
 *
 * Casi coperti:
 *  - verificaAssociazionePT: cliente NON associato → eccezione
 *  - verificaAssociazionePT: cliente PENDING → eccezione
 *  - verificaAssociazionePT: cliente ASSOCIATO → nessuna eccezione
 *  - elaboraRichiesta: peso non valido (≤ 0, > 200)
 *  - elaboraRichiesta: età non valida (< 10, > 100)
 *  - elaboraRichiesta: dati validi → salvataggio OK
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RichiediSchedaControllerTest {

    private RichiediSchedaController controller;
    private static final String PT_EMAIL      = "pt@gym.it";
    private static final String CLIENTE_EMAIL = "atleta@test.it";

    @BeforeAll
    static void setupDAO() {
        DAOFactory.setMode(1);
    }

    @BeforeEach
    void setUp() {
        controller = new RichiediSchedaController();
    }

    // ─────────────────────────────────────────────
    //  VERIFICA ASSOCIAZIONE PT
    // ─────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Cliente con stato NESSUNA → TrainerNotAssociatedException")
    void testVerificaAssociazione_StatoNessuna() {
        impostaSessione(StatoAssociazione.NESSUNA);
        assertThrows(TrainerNotAssociatedException.class,
                () -> controller.verificaAssociazionePT());
    }

    @Test
    @Order(2)
    @DisplayName("Cliente con stato PENDING → TrainerNotAssociatedException")
    void testVerificaAssociazione_StatoPending() {
        impostaSessione(StatoAssociazione.PENDING);
        assertThrows(TrainerNotAssociatedException.class,
                () -> controller.verificaAssociazionePT());
    }

    @Test
    @Order(3)
    @DisplayName("Cliente con stato ASSOCIATO → nessuna eccezione")
    void testVerificaAssociazione_StatoAssociato() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        assertDoesNotThrow(() -> controller.verificaAssociazionePT());
    }

    // ─────────────────────────────────────────────
    //  VALIDAZIONE PESO
    // ─────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Peso = 0 → InvalidFormException")
    void testElaboraRichiesta_PesoZero() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        bean.setPeso(0);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @Order(5)
    @DisplayName("Peso negativo → InvalidFormException")
    void testElaboraRichiesta_PesoNegativo() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        bean.setPeso(-5);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @Order(6)
    @DisplayName("Peso > 200 → InvalidFormException")
    void testElaboraRichiesta_PesoEccessivo() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        bean.setPeso(201);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @Order(7)
    @DisplayName("Peso limite inferiore valido (1) → nessuna eccezione di peso")
    void testElaboraRichiesta_PesoLimiteInferiore() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        bean.setPeso(1);
        // Potrebbe lanciare eccezione per altri motivi, ma NON per il peso
        try {
            controller.elaboraRichiesta(bean);
        } catch (InvalidFormException e) {
            assertFalse(e.getMessage().toLowerCase().contains("peso"),
                    "Non dovrebbe fallire per il peso con valore 1");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Peso limite superiore valido (200) → nessuna eccezione di peso")
    void testElaboraRichiesta_PesoLimiteSuperiore() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        bean.setPeso(200);
        try {
            controller.elaboraRichiesta(bean);
        } catch (InvalidFormException e) {
            assertFalse(e.getMessage().toLowerCase().contains("peso"),
                    "Non dovrebbe fallire per il peso con valore 200");
        }
    }

    // ─────────────────────────────────────────────
    //  VALIDAZIONE ETÀ
    // ─────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("Età < 10 → InvalidFormException")
    void testElaboraRichiesta_EtaMinore() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        bean.setEta(9);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @Order(10)
    @DisplayName("Età > 100 → InvalidFormException")
    void testElaboraRichiesta_EtaMaggiore() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        bean.setEta(101);
        assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
    }

    @Test
    @Order(11)
    @DisplayName("Età limite inferiore (10) → nessuna eccezione di età")
    void testElaboraRichiesta_EtaLimiteInferiore() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        bean.setEta(10);
        try {
            controller.elaboraRichiesta(bean);
        } catch (InvalidFormException e) {
            assertFalse(e.getMessage().toLowerCase().contains("età"),
                    "Non dovrebbe fallire per l'età con valore 10");
        }
    }

    @Test
    @Order(12)
    @DisplayName("Età limite superiore (100) → nessuna eccezione di età")
    void testElaboraRichiesta_EtaLimiteSuperiore() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        bean.setEta(100);
        try {
            controller.elaboraRichiesta(bean);
        } catch (InvalidFormException e) {
            assertFalse(e.getMessage().toLowerCase().contains("età"),
                    "Non dovrebbe fallire per l'età con valore 100");
        }
    }

    // ─────────────────────────────────────────────
    //  HAPPY PATH
    // ─────────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("Dati validi → elaboraRichiesta senza eccezioni")
    void testElaboraRichiesta_DatiValidi() {
        impostaSessione(StatoAssociazione.ASSOCIATO);
        RichiestaSchedaBean bean = creaBeanValido();
        assertDoesNotThrow(() -> controller.elaboraRichiesta(bean));
    }

    // ─────────────────────────────────────────────
    //  HELPER
    // ─────────────────────────────────────────────

    private void impostaSessione(StatoAssociazione stato) {
        Cliente c = new Cliente("Test", "Atleta", CLIENTE_EMAIL, "pwd");
        c.setStatoAssociazione(stato);
        if (stato == StatoAssociazione.ASSOCIATO || stato == StatoAssociazione.PENDING) {
            c.setIdPersonalTrainer(PT_EMAIL);
        }
        Sessione.getInstance().setUtente(c);
    }

    private RichiestaSchedaBean creaBeanValido() {
        RichiestaSchedaBean bean = new RichiestaSchedaBean();
        bean.setPeso(75);
        bean.setEta(25);
        bean.setSesso("M");
        bean.setObiettivo("Dimagrimento");
        bean.setFrequenzaSettimanale(3);
        bean.setNote("Nessuna nota");
        bean.setClienteEmail(CLIENTE_EMAIL);
        bean.setIdPersonalTrainer(PT_EMAIL);
        return bean;
    }
}

package controller.graphic;

import bean.LoginBean;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.entity.StatoAssociazione;
import model.exception.LoginException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per LoginController.
 *
 * Strategia: usa la modalità DEMO (UtenteDAOMemory) che pre-carica
 * mario@test.it / pass123  (Cliente)
 * coach@test.it / pass123  (PersonalTrainer)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginControllerTest {

    private LoginController controller;

    @BeforeAll
    static void setupDAO() {
        // Avviamo in modalità DEMO: tutti i DAO usano le implementazioni In-Memory
        DAOFactory.setMode(1);
    }

    @BeforeEach
    void setUp() {
        controller = new LoginController();
        // Resettiamo la sessione prima di ogni test
        Sessione.getInstance().setUtente(null);
    }

    // ─────────────────────────────────────────────
    //  HAPPY PATH
    // ─────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Login Cliente con credenziali corrette → sessione valorizzata")
    void testLoginClienteOk() throws LoginException {
        LoginBean bean = new LoginBean();
        bean.setEmail("mario@test.it");
        bean.setPassword("pass123");

        controller.autentica(bean);

        assertNotNull(Sessione.getInstance().getUtente());
        assertInstanceOf(Cliente.class, Sessione.getInstance().getUtente());
        assertEquals("mario@test.it", Sessione.getInstance().getUtente().getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("Login PersonalTrainer con credenziali corrette → sessione valorizzata")
    void testLoginPTOk() throws LoginException {
        LoginBean bean = new LoginBean();
        bean.setEmail("coach@test.it");
        bean.setPassword("pass123");

        controller.autentica(bean);

        assertNotNull(Sessione.getInstance().getUtente());
        assertInstanceOf(PersonalTrainer.class, Sessione.getInstance().getUtente());
    }

    @Test
    @Order(3)
    @DisplayName("Login Cliente → stato associazione viene caricato (NESSUNA di default)")
    void testLoginClienteCaricaStatoAssociazione() throws LoginException {
        LoginBean bean = new LoginBean();
        bean.setEmail("mario@test.it");
        bean.setPassword("pass123");

        controller.autentica(bean);

        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        // In memoria non ci sono associazioni pre-esistenti → NESSUNA
        assertNotNull(c.getStatoAssociazione());
    }

    // ─────────────────────────────────────────────
    //  ERRORI ATTESI
    // ─────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Login con password sbagliata → LoginException")
    void testLoginPasswordErrata() {
        LoginBean bean = new LoginBean();
        bean.setEmail("mario@test.it");
        bean.setPassword("passwordSbagliata");

        assertThrows(LoginException.class, () -> controller.autentica(bean));
    }

    @Test
    @Order(5)
    @DisplayName("Login con email inesistente → LoginException")
    void testLoginEmailInesistente() {
        LoginBean bean = new LoginBean();
        bean.setEmail("nonEsisto@test.it");
        bean.setPassword("pass123");

        assertThrows(LoginException.class, () -> controller.autentica(bean));
    }

    @Test
    @Order(6)
    @DisplayName("Login con email null → LoginException con messaggio 'obbligatoria'")
    void testLoginEmailNull() {
        LoginBean bean = new LoginBean();
        bean.setEmail(null);
        bean.setPassword("pass123");

        LoginException ex = assertThrows(LoginException.class, () -> controller.autentica(bean));
        assertTrue(ex.getMessage().toLowerCase().contains("obbligatoria"));
    }

    @Test
    @Order(7)
    @DisplayName("Login con email vuota → LoginException")
    void testLoginEmailVuota() {
        LoginBean bean = new LoginBean();
        bean.setEmail("");
        bean.setPassword("pass123");

        assertThrows(LoginException.class, () -> controller.autentica(bean));
    }
}

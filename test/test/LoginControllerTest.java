package test;

import controller.graphic.LoginController;
import bean.LoginBean;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
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
class LoginControllerTest {

    private LoginController controller;

    @BeforeAll
    static void setupDAO() {
        DAOFactory.setMode(1);
    }

    @BeforeEach
    void setUp() {
        controller = new LoginController();
        Sessione.getInstance().setUtente(null);
    }

    // ─────────────────────────────────────────────
    //  HAPPY PATH
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Login Cliente con credenziali corrette → sessione valorizzata")
    void testLoginClienteOk() throws LoginException {
        controller.autentica(creaBean("mario@test.it", "pass123"));

        assertNotNull(Sessione.getInstance().getUtente());
        assertInstanceOf(Cliente.class, Sessione.getInstance().getUtente());
        assertEquals("mario@test.it", Sessione.getInstance().getUtente().getEmail());
    }

    @Test
    @DisplayName("Login PersonalTrainer con credenziali corrette → sessione valorizzata")
    void testLoginPTOk() throws LoginException {
        controller.autentica(creaBean("coach@test.it", "pass123"));

        assertNotNull(Sessione.getInstance().getUtente());
        assertInstanceOf(PersonalTrainer.class, Sessione.getInstance().getUtente());
    }

    @Test
    @DisplayName("Login Cliente → stato associazione viene caricato (NESSUNA di default)")
    void testLoginClienteCaricaStatoAssociazione() throws LoginException {
        controller.autentica(creaBean("mario@test.it", "pass123"));

        Cliente c = (Cliente) Sessione.getInstance().getUtente();
        assertNotNull(c.getStatoAssociazione());
    }

    // ─────────────────────────────────────────────
    //  ERRORI ATTESI
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("Login con password sbagliata → LoginException")
    void testLoginPasswordErrata() {
        assertLoginFails("mario@test.it", "passwordSbagliata");
    }

    @Test
    @DisplayName("Login con email inesistente → LoginException")
    void testLoginEmailInesistente() {
        assertLoginFails("nonEsisto@test.it", "pass123");
    }

    @Test
    @DisplayName("Login con email vuota → LoginException")
    void testLoginEmailVuota() {
        assertLoginFails("", "pass123");
    }

    @Test
    @DisplayName("Login con email null → LoginException con messaggio 'obbligatoria'")
    void testLoginEmailNull() {
        LoginBean bean = creaBean(null, "pass123");
        LoginException ex = assertThrows(LoginException.class, () -> controller.autentica(bean));
        assertTrue(ex.getMessage().toLowerCase().contains("obbligatoria"));
    }

    // ─────────────────────────────────────────────
    //  HELPER
    // ─────────────────────────────────────────────

    private LoginBean creaBean(String email, String password) {
        LoginBean bean = new LoginBean();
        bean.setEmail(email);
        bean.setPassword(password);
        return bean;
    }

    private void assertLoginFails(String email, String password) {
        LoginBean bean = creaBean(email, password);
        assertThrows(LoginException.class, () -> controller.autentica(bean));
    }
}

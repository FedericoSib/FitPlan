package test;

import bean.LoginBean;
import bean.RegistrazioneBean;
import controller.graphic.LoginController;
import controller.graphic.RegistrazioneController;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.*;
import model.exception.LoginException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per LoginController.
 * Usa DAOFactory in modalità DEMO (in-memory) — nessun file su disco.
 *
 * Casi coperti:
 *  - Email null              → LoginException "Email obbligatoria"
 *  - Email vuota             → LoginException "Email obbligatoria"
 *  - Utente non registrato   → LoginException "Credenziali non valide"
 *  - Password errata         → LoginException "Credenziali non valide"
 *  - Login OK come Cliente   → sessione impostata correttamente
 *  - Login OK come PT        → sessione impostata correttamente
 */
class LoginControllerTest {

    private LoginController controller;

    @BeforeEach
    void setUp() {
        DAOFactory.setMode(1); // DEMO: usa DAO in-memory
        controller = new LoginController();
        Sessione.getInstance().setUtente(null);
    }

    @AfterEach
    void tearDown() {
        Sessione.getInstance().setUtente(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VALIDAZIONE INPUT
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Email null → LoginException 'Email obbligatoria'")
    void autentica_emailNull() {
        LoginBean bean = new LoginBean();
        bean.setEmail(null);
        bean.setPassword("pwd");

        LoginException ex = assertThrows(LoginException.class, () -> controller.autentica(bean));
        assertTrue(ex.getMessage().contains("Email obbligatoria"));
    }

    @Test
    @DisplayName("Email vuota → LoginException 'Email obbligatoria'")
    void autentica_emailVuota() {
        LoginBean bean = new LoginBean();
        bean.setEmail("");
        bean.setPassword("pwd");

        LoginException ex = assertThrows(LoginException.class, () -> controller.autentica(bean));
        assertTrue(ex.getMessage().contains("Email obbligatoria"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CREDENZIALI ERRATE
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Utente non registrato → LoginException 'Credenziali non valide'")
    void autentica_utenteNonEsistente() {
        LoginBean bean = new LoginBean();
        bean.setEmail("inesistente@test.it");
        bean.setPassword("pwd");

        LoginException ex = assertThrows(LoginException.class, () -> controller.autentica(bean));
        assertTrue(ex.getMessage().contains("Credenziali non valide"));
    }

    @Test
    @DisplayName("Password errata → LoginException 'Credenziali non valide'")
    void autentica_passwordErrata() throws Exception {
        registraCliente("mario@test.it", "passwordGiusta");

        LoginBean bean = new LoginBean();
        bean.setEmail("mario@test.it");
        bean.setPassword("passwordSbagliata");

        LoginException ex = assertThrows(LoginException.class, () -> controller.autentica(bean));
        assertTrue(ex.getMessage().contains("Credenziali non valide"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOGIN CORRETTO
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Login OK come Cliente → sessione impostata")
    void autentica_clienteOk() throws Exception {
        registraCliente("anna@test.it", "pwd123");

        LoginBean bean = new LoginBean();
        bean.setEmail("anna@test.it");
        bean.setPassword("pwd123");

        assertDoesNotThrow(() -> controller.autentica(bean));

        Utente inSessione = Sessione.getInstance().getUtente();
        assertNotNull(inSessione);
        assertInstanceOf(Cliente.class, inSessione);
        assertEquals("anna@test.it", inSessione.getEmail());
    }

    @Test
    @DisplayName("Login OK come PersonalTrainer → sessione impostata")
    void autentica_personalTrainerOk() throws Exception {
        registraPT("carlo@pt.it", "pwd123");

        LoginBean bean = new LoginBean();
        bean.setEmail("carlo@pt.it");
        bean.setPassword("pwd123");

        assertDoesNotThrow(() -> controller.autentica(bean));

        Utente inSessione = Sessione.getInstance().getUtente();
        assertNotNull(inSessione);
        assertInstanceOf(PersonalTrainer.class, inSessione);
        assertEquals("carlo@pt.it", inSessione.getEmail());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────────────────

    private void registraCliente(String email, String password) throws Exception {
        RegistrazioneController reg = new RegistrazioneController();
        RegistrazioneBean b = buildBean(email, password, 1);
        reg.registraNuovoUtente(b);
    }

    private void registraPT(String email, String password) throws Exception {
        RegistrazioneController reg = new RegistrazioneController();
        RegistrazioneBean b = buildBean(email, password, 2);
        reg.registraNuovoUtente(b);
    }

    private RegistrazioneBean buildBean(String email, String password, int ruolo) {
        RegistrazioneBean b = new RegistrazioneBean();
        b.setNome("Test");
        b.setCognome("User");
        b.setEmail(email);
        b.setPassword(password);
        b.setConfermaPassword(password);
        b.setRuolo(ruolo);
        return b;
    }
}

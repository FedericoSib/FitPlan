package test;

import bean.RegistrazioneBean;
import controller.graphic.RegistrazioneController;
import model.dao.DAOFactory;
import model.exception.RegistrazioneException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class RegistrazioneControllerTest {

    private RegistrazioneController controller;

    @BeforeEach
    void setUp() {
        DAOFactory.setMode(1);
        controller = new RegistrazioneController();
    }

    // ── VALIDAZIONE BEAN ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Bean vuoto → RegistrazioneException 'campi obbligatori'")
    void registra_beanVuoto() {
        RegistrazioneBean bean = new RegistrazioneBean();
        assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(bean));
    }

    @Test
    @DisplayName("Nome null → RegistrazioneException")
    void registra_nomeNull() {
        RegistrazioneBean bean = buildBean("mario@test.it", "Pwd1!", 1);
        bean.setNome(null);
        assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(bean));
    }

    @Test
    @DisplayName("Nome vuoto → RegistrazioneException")
    void registra_nomeVuoto() {
        RegistrazioneBean bean = buildBean("mario@test.it", "Pwd1!", 1);
        bean.setNome("");
        assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(bean));
    }

    @Test
    @DisplayName("Email null → RegistrazioneException")
    void registra_emailNull() {
        RegistrazioneBean bean = buildBean("mario@test.it", "Pwd1!", 1);
        bean.setEmail(null);
        assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(bean));
    }

    @Test
    @DisplayName("Email senza @ → RegistrazioneException 'Formato email'")
    void registra_emailNonValida() {
        RegistrazioneBean bean = buildBean("mariotest.it", "Pwd1!", 1);
        RegistrazioneException ex = assertThrows(RegistrazioneException.class,
                () -> controller.registraNuovoUtente(bean));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    @DisplayName("Password null → RegistrazioneException")
    void registra_passwordNull() {
        RegistrazioneBean bean = buildBean("mario@test.it", "Pwd1!", 1);
        bean.setPassword(null);
        assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(bean));
    }

    // ── EMAIL DUPLICATA ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Email già registrata → RegistrazioneException 'già associata'")
    void registra_emailDuplicata() throws Exception {
        RegistrazioneException ex = assertThrows(RegistrazioneException.class,
                () -> controller.registraNuovoUtente(buildBean("mario@test.it", "Pwd1!", 1)));
        assertTrue(ex.getMessage().contains("già associata"));
    }

    // ── REGISTRAZIONE OK ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Registrazione OK come Cliente (ruolo=1)")
    void registra_clienteOk() {
        assertDoesNotThrow(() ->
                controller.registraNuovoUtente(buildBean("cliente@test.it", "Pwd1!", 1)));
    }

    @Test
    @DisplayName("Registrazione OK come PersonalTrainer (ruolo=2)")
    void registra_ptOk() {
        assertDoesNotThrow(() ->
                controller.registraNuovoUtente(buildBean("pt@test.it", "Pwd1!", 2)));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private RegistrazioneBean buildBean(String email, String password, int ruolo) {
        RegistrazioneBean b = new RegistrazioneBean();
        b.setNome("Mario");
        b.setCognome("Rossi");
        b.setEmail(email);
        b.setPassword(password);
        b.setRuolo(ruolo);
        return b;
    }
}
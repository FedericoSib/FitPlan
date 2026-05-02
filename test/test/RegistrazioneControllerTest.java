package controller.graphic;

import bean.RegistrazioneBean;
import model.dao.DAOFactory;
import model.exception.RegistrazioneException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per RegistrazioneController.
 *
 * Casi coperti:
 *  - Registrazione Cliente OK
 *  - Registrazione PersonalTrainer OK
 *  - Email già esistente → RegistrazioneException
 *  - Validazione bean: email malformata, password corta, campi vuoti
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegistrazioneControllerTest {

    private RegistrazioneController controller;

    @BeforeAll
    static void setupDAO() {
        DAOFactory.setMode(1);
    }

    @BeforeEach
    void setUp() {
        controller = new RegistrazioneController();
    }

    // ─────────────────────────────────────────────
    //  HAPPY PATH
    // ─────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Registrazione Cliente con dati validi → nessuna eccezione")
    void testRegistrazioneClienteOk() {
        RegistrazioneBean bean = creaBean("Luca", "Bianchi", "luca.bianchi@test.it", "SecurePass1!", 1);
        assertDoesNotThrow(() -> controller.registraNuovoUtente(bean));
    }

    @Test
    @Order(2)
    @DisplayName("Registrazione PersonalTrainer con dati validi → nessuna eccezione")
    void testRegistrazionePTOk() {
        RegistrazioneBean bean = creaBean("Anna", "Verdi", "anna.pt@test.it", "SecurePass1!", 2);
        assertDoesNotThrow(() -> controller.registraNuovoUtente(bean));
    }

    // ─────────────────────────────────────────────
    //  EMAIL GIÀ ESISTENTE
    // ─────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("Registrazione con email già esistente → RegistrazioneException")
    void testEmailDuplicata() {
        // mario@test.it è pre-caricato in UtenteDAOMemory
        RegistrazioneBean bean = creaBean("Mario", "Duplicato", "mario@test.it", "SecurePass1!", 1);

        RegistrazioneException ex = assertThrows(
                RegistrazioneException.class,
                () -> controller.registraNuovoUtente(bean)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("già associata") ||
                   ex.getMessage().toLowerCase().contains("email"));
    }

    // ─────────────────────────────────────────────
    //  VALIDAZIONE BEAN (dipende da bean.valida())
    // ─────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Nome vuoto → RegistrazioneException da validazione")
    void testNomeVuoto() {
        RegistrazioneBean bean = creaBean("", "Rossi", "nuovo@test.it", "SecurePass1!", 1);
        assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(bean));
    }

    @Test
    @Order(5)
    @DisplayName("Cognome vuoto → RegistrazioneException da validazione")
    void testCognomeVuoto() {
        RegistrazioneBean bean = creaBean("Marco", "", "nuovo2@test.it", "SecurePass1!", 1);
        assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(bean));
    }

    @Test
    @Order(6)
    @DisplayName("Email malformata → RegistrazioneException da validazione")
    void testEmailMalformata() {
        RegistrazioneBean bean = creaBean("Marco", "Neri", "emailSenzaChiocciola", "SecurePass1!", 1);
        assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(bean));
    }

    @Test
    @Order(7)
    @DisplayName("Password troppo corta → RegistrazioneException da validazione")
    void testPasswordTroppoCorta() {
        RegistrazioneBean bean = creaBean("Marco", "Neri", "marco.neri@test.it", "123", 1);
        assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(bean));
    }

    // ─────────────────────────────────────────────
    //  HELPER
    // ─────────────────────────────────────────────

    private RegistrazioneBean creaBean(String nome, String cognome, String email, String password, int ruolo) {
        RegistrazioneBean bean = new RegistrazioneBean();
        bean.setNome(nome);
        bean.setCognome(cognome);
        bean.setEmail(email);
        bean.setPassword(password);
        bean.setRuolo(ruolo);
        return bean;
    }
}

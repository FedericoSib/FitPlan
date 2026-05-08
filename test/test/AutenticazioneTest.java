package test;

import bean.LoginBean;
import bean.RegistrazioneBean;
import controller.graphic.LoginController;
import controller.graphic.RegistrazioneController;
import model.Sessione;
import model.dao.DAOFactory;
import model.dao.UtenteDAOMemory;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.exception.LoginException;
import model.exception.RegistrazioneException;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AutenticazioneTest {

    @Test
    @DisplayName("Suite autenticazione caricata correttamente")
    void testSuiteCaricata() {
        assertTrue(true);
    }

    //  LoginController
    @Nested
    @DisplayName("LoginController")
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
        @DisplayName("Login Cliente → stato associazione viene caricato")
        void testLoginClienteCaricaStatoAssociazione() throws LoginException {
            controller.autentica(creaBean("mario@test.it", "pass123"));
            Cliente c = (Cliente) Sessione.getInstance().getUtente();
            assertNotNull(c.getStatoAssociazione());
        }

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
            LoginException ex = assertThrows(LoginException.class,
                    () -> controller.autentica(bean));
            assertTrue(ex.getMessage().toLowerCase().contains("obbligatoria"));
        }

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
    //  RegistrazioneController
    @Nested
    @DisplayName("RegistrazioneController")
    class RegistrazioneControllerTest {

        private RegistrazioneController controller;

        @BeforeAll
        static void setupDAO() {
            DAOFactory.setMode(1);
        }

        @BeforeEach
        void setUp() throws Exception {
            controller = new RegistrazioneController();
            // Ripristina la lista utenti ai soli dummy pre-caricati
            Field field = UtenteDAOMemory.class.getDeclaredField("utenti");
            field.setAccessible(true);
            ((List<?>) field.get(null)).clear();
            new UtenteDAOMemory(); // ricarica i dummy
        }

        @Test
        @DisplayName("Registrazione Cliente con dati validi → nessuna eccezione")
        void testRegistrazioneClienteOk() {
            assertDoesNotThrow(() -> controller.registraNuovoUtente(
                    creaBean("Luca", "Bianchi", "luca@test.it", "SecurePass1!", 1)));
        }

        @Test
        @DisplayName("Registrazione PersonalTrainer con dati validi → nessuna eccezione")
        void testRegistrazionePTOk() {
            assertDoesNotThrow(() -> controller.registraNuovoUtente(
                    creaBean("Anna", "Verdi", "anna@test.it", "SecurePass1!", 2)));
        }

        @Test
        @DisplayName("Email già esistente → RegistrazioneException")
        void testEmailDuplicata() {
            RegistrazioneException ex = assertThrows(RegistrazioneException.class,
                    () -> controller.registraNuovoUtente(
                            creaBean("Mario", "Dup", "mario@test.it", "SecurePass1!", 1)));
            assertTrue(ex.getMessage().toLowerCase().contains("email") ||
                       ex.getMessage().toLowerCase().contains("già associata"));
        }

        @Test
        @DisplayName("Nome vuoto → RegistrazioneException")
        void testNomeVuoto() {
            assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(
                    creaBean("", "Rossi", "nuovo@test.it", "SecurePass1!", 1)));
        }

        @Test
        @DisplayName("Cognome vuoto → RegistrazioneException")
        void testCognomeVuoto() {
            assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(
                    creaBean("Marco", "", "nuovo2@test.it", "SecurePass1!", 1)));
        }

        @Test
        @DisplayName("Email malformata → RegistrazioneException")
        void testEmailMalformata() {
            assertThrows(RegistrazioneException.class, () -> controller.registraNuovoUtente(
                    creaBean("Marco", "Neri", "emailSenzaChiocciola", "SecurePass1!", 1)));
        }

        private RegistrazioneBean creaBean(String nome, String cognome,
                                           String email, String password, int ruolo) {
            RegistrazioneBean bean = new RegistrazioneBean();
            bean.setNome(nome);
            bean.setCognome(cognome);
            bean.setEmail(email);
            bean.setPassword(password);
            bean.setRuolo(ruolo);
            return bean;
        }
    }
}

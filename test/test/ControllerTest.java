package test;

import bean.*;
import controller.graphic.*;
import model.Sessione;
import model.dao.*;
import model.entity.*;
import model.exception.*;
import util.observer.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite completa di test per tutti i Controller del progetto FitPlan.
 * Usa la modalità DEMO (DAO In-Memory) per garantire isolamento e ripetibilità.
 */
class ControllerTest {

    @Test
    @DisplayName("Suite controller caricata correttamente")
    void testSuiteCaricata() {
        assertTrue(true);
    }

    // ════════════════════════════════════════════════════
    //  LoginController
    // ════════════════════════════════════════════════════

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
            LoginException ex = assertThrows(LoginException.class, () -> controller.autentica(bean));
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

    // ════════════════════════════════════════════════════
    //  RegistrazioneController
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("RegistrazioneController")
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

        @Test
        @DisplayName("Registrazione Cliente con dati validi → nessuna eccezione")
        void testRegistrazioneClienteOk() {
            assertDoesNotThrow(() -> controller.registraNuovoUtente(
                    creaBean("Luca", "Bianchi", "luca.bianchi2@test.it", "SecurePass1!", 1)));
        }

        @Test
        @DisplayName("Registrazione PersonalTrainer con dati validi → nessuna eccezione")
        void testRegistrazionePTOk() {
            assertDoesNotThrow(() -> controller.registraNuovoUtente(
                    creaBean("Anna", "Verdi", "anna.pt2@test.it", "SecurePass1!", 2)));
        }

        @Test
        @DisplayName("Email duplicata → RegistrazioneException")
        void testEmailDuplicata() {
            RegistrazioneException ex = assertThrows(RegistrazioneException.class,
                    () -> controller.registraNuovoUtente(
                            creaBean("Mario", "Duplicato", "mario@test.it", "SecurePass1!", 1)));
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

    // ════════════════════════════════════════════════════
    //  AssociaPTController
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("AssociaPTController")
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
            svuotaListaPT();
            Cliente c = new Cliente("Test", "Cliente", CLIENTE_EMAIL, "pwd");
            c.setStatoAssociazione(StatoAssociazione.NESSUNA);
            Sessione.getInstance().setUtente(c);
        }

        @Test
        @DisplayName("Ricerca per ID inesistente → TrainerNotFoundException")
        void testCercaPerIdNonEsistente() {
            assertThrows(TrainerNotFoundException.class, () -> controller.cercaTrainer("PT-999"));
        }

        @Test
        @DisplayName("Ricerca per Email inesistente → TrainerNotFoundException")
        void testCercaPerEmailNonEsistente() {
            assertThrows(TrainerNotFoundException.class, () -> controller.cercaTrainer("nessuno@gym.it"));
        }

        @Test
        @DisplayName("Ricerca per Nome inesistente → TrainerNotFoundException")
        void testCercaPerNomeNonEsistente() {
            assertThrows(TrainerNotFoundException.class, () -> controller.cercaTrainer("NomeInesistente"));
        }

        @Test
        @DisplayName("Prefisso 'pt-' minuscolo → branch ID")
        void testCercaConPrefissoPtMinuscolo() {
            assertThrows(TrainerNotFoundException.class, () -> controller.cercaTrainer("pt-001"));
        }

        @Test
        @DisplayName("Stringa con formato email → branch email")
        void testCercaBranchEmail() {
            assertThrows(TrainerNotFoundException.class, () -> controller.cercaTrainer("qualcuno@dominio.com"));
        }

        @Test
        @DisplayName("Stringa senza '@' e senza 'PT-' → branch nome")
        void testCercaBranchNome() {
            assertThrows(TrainerNotFoundException.class, () -> controller.cercaTrainer("SoloNome"));
        }

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
        }

        @Test
        @DisplayName("Ricerca per Cognome esistente → lista non vuota")
        void testCercaPerCognomeEsistente() throws Exception {
            aggiungiPTInLista(new PersonalTrainer(PT_NOME, PT_COGN, PT_EMAIL, "pwd"));
            List<PersonalTrainerBean> risultati = controller.cercaTrainer(PT_COGN);
            assertFalse(risultati.isEmpty());
        }

        @Test
        @DisplayName("Ricerca per ID esistente → bean con ID corretto")
        void testCercaPerIdEsistente() throws Exception {
            PersonalTrainer pt = new PersonalTrainer(PT_NOME, PT_COGN, PT_EMAIL, "pwd");
            aggiungiPTInLista(pt);
            List<PersonalTrainerBean> risultati = controller.cercaTrainer(pt.getId());
            assertFalse(risultati.isEmpty());
            assertEquals(pt.getId(), risultati.get(0).getId());
        }

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

    // ════════════════════════════════════════════════════
    //  GestisciRichiestePTController
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("GestisciRichiestePTController")
    class GestisciRichiestePTControllerTest {

        private GestisciRichiestePTController controller;

        private static final String PT_EMAIL       = "pt.gestore@test.it";
        private static final String CLIENTE1_EMAIL  = "cliente1@test.it";
        private static final String CLIENTE2_EMAIL  = "cliente2@test.it";

        @BeforeAll
        static void setupDAO() {
            DAOFactory.setMode(1);
        }

        @BeforeEach
        void setUp() throws Exception {
            controller = new GestisciRichiestePTController();
            pulisciAssociazioneDAO();
        }

        @Test
        @DisplayName("PT senza richieste → lista vuota")
        void testGetRichiesteSospese_ListaVuota() throws DAOException {
            assertTrue(controller.getRichiesteSospese(PT_EMAIL).isEmpty());
        }

        @Test
        @DisplayName("PT con 2 richieste PENDING → lista con 2 elementi")
        void testGetRichiesteSospese_ConRichieste() throws DAOException {
            AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
            dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);
            dao.salvaRichiesta(CLIENTE2_EMAIL, PT_EMAIL);
            assertEquals(2, controller.getRichiesteSospese(PT_EMAIL).size());
        }

        @Test
        @DisplayName("Richiesta di altro PT non viene restituita")
        void testGetRichiesteSospese_FiltroPerPT() throws DAOException {
            AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
            dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);
            dao.salvaRichiesta(CLIENTE2_EMAIL, "altroPT@test.it");
            assertEquals(1, controller.getRichiesteSospese(PT_EMAIL).size());
        }

        @Test
        @DisplayName("accettaAssociazione → stato diventa ASSOCIATO")
        void testAccettaAssociazione() throws DAOException {
            AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
            dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);
            AssociazioneBean bean = creaBean(CLIENTE1_EMAIL, PT_EMAIL);
            controller.accettaAssociazione(bean);
            assertEquals(StatoAssociazione.ASSOCIATO, dao.getStato(CLIENTE1_EMAIL));
        }

        @Test
        @DisplayName("rifiutaAssociazione → stato diventa NESSUNA")
        void testRifiutaAssociazione() throws DAOException {
            AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
            dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);
            AssociazioneBean bean = creaBean(CLIENTE1_EMAIL, PT_EMAIL);
            controller.rifiutaAssociazione(bean);
            assertEquals(StatoAssociazione.NESSUNA, dao.getStato(CLIENTE1_EMAIL));
        }

        @Test
        @DisplayName("Dopo rifiuto la richiesta non appare più tra le PENDING")
        void testRifiutaAssociazione_ScompareRichieste() throws DAOException {
            AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
            dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);
            controller.rifiutaAssociazione(creaBean(CLIENTE1_EMAIL, PT_EMAIL));
            assertTrue(controller.getRichiesteSospese(PT_EMAIL).isEmpty());
        }

        private AssociazioneBean creaBean(String emailCliente, String emailPT) {
            AssociazioneBean bean = new AssociazioneBean();
            bean.setEmailCliente(emailCliente);
            bean.setEmailPT(emailPT);
            return bean;
        }

        private void pulisciAssociazioneDAO() throws Exception {
            pulisciMappa("richiestePT");
            pulisciMappa("stati");
        }

        private void pulisciMappa(String fieldName) throws Exception {
            Field f = AssociazioneDAOMemory.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            ((java.util.Map<?, ?>) f.get(null)).clear();
        }
    }

    // ════════════════════════════════════════════════════
    //  RichiediSchedaController
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("RichiediSchedaController")
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

        @Test
        @DisplayName("Cliente NESSUNA → TrainerNotAssociatedException")
        void testVerificaAssociazione_StatoNessuna() {
            impostaSessione(StatoAssociazione.NESSUNA);
            assertThrows(TrainerNotAssociatedException.class, () -> controller.verificaAssociazionePT());
        }

        @Test
        @DisplayName("Cliente PENDING → TrainerNotAssociatedException")
        void testVerificaAssociazione_StatoPending() {
            impostaSessione(StatoAssociazione.PENDING);
            assertThrows(TrainerNotAssociatedException.class, () -> controller.verificaAssociazionePT());
        }

        @Test
        @DisplayName("Cliente ASSOCIATO → nessuna eccezione")
        void testVerificaAssociazione_StatoAssociato() {
            impostaSessione(StatoAssociazione.ASSOCIATO);
            assertDoesNotThrow(() -> controller.verificaAssociazionePT());
        }

        @Test
        @DisplayName("Peso = 0 → InvalidFormException")
        void testPesoZero() {
            impostaSessione(StatoAssociazione.ASSOCIATO);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setPeso(0);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        @Test
        @DisplayName("Peso negativo → InvalidFormException")
        void testPesoNegativo() {
            impostaSessione(StatoAssociazione.ASSOCIATO);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setPeso(-5);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        @Test
        @DisplayName("Peso > 200 → InvalidFormException")
        void testPesoEccessivo() {
            impostaSessione(StatoAssociazione.ASSOCIATO);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setPeso(201);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        @Test
        @DisplayName("Età < 10 → InvalidFormException")
        void testEtaMinore() {
            impostaSessione(StatoAssociazione.ASSOCIATO);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setEta(9);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        @Test
        @DisplayName("Età > 100 → InvalidFormException")
        void testEtaMaggiore() {
            impostaSessione(StatoAssociazione.ASSOCIATO);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setEta(101);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        @Test
        @DisplayName("Dati validi → nessuna eccezione")
        void testElaboraRichiesta_DatiValidi() {
            impostaSessione(StatoAssociazione.ASSOCIATO);
            assertDoesNotThrow(() -> controller.elaboraRichiesta(creaBeanValido()));
        }

        private void impostaSessione(StatoAssociazione stato) {
            Cliente c = new Cliente("Test", "Atleta", CLIENTE_EMAIL, "pwd");
            c.setStatoAssociazione(stato);
            if (stato != StatoAssociazione.NESSUNA) c.setIdPersonalTrainer(PT_EMAIL);
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

    // ════════════════════════════════════════════════════
    //  NotificaObservableBase
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("NotificaObservableBase")
    class NotificaObservableBaseTest {

        // Implementazione concreta minima per testare la classe astratta
        private static class TestableObservable extends NotificaObservableBase {}

        private TestableObservable observable;
        private List<model.entity.Notifica> notificheRicevute;

        @BeforeEach
        void setUp() {
            observable = new TestableObservable();
            notificheRicevute = new ArrayList<>();
        }

        @Test
        @DisplayName("aggiungiObserver → notifica viene ricevuta")
        void testAggiungiObserver() {
            observable.aggiungiObserver(n -> notificheRicevute.add(n));
            observable.notificaObserver("dest@test.it", "Testo");
            assertEquals(1, notificheRicevute.size());
            assertEquals("Testo", notificheRicevute.get(0).getTesto());
        }

        @Test
        @DisplayName("rimuoviObserver → notifica non viene più ricevuta")
        void testRimuoviObserver() {
            NotificaObserver observer = n -> notificheRicevute.add(n);
            observable.aggiungiObserver(observer);
            observable.rimuoviObserver(observer);
            observable.notificaObserver("dest@test.it", "Testo");
            assertTrue(notificheRicevute.isEmpty());
        }

        @Test
        @DisplayName("2 observer → entrambi ricevono la notifica")
        void testDueObserver() {
            observable.aggiungiObserver(n -> notificheRicevute.add(n));
            observable.aggiungiObserver(n -> notificheRicevute.add(n));
            observable.notificaObserver("dest@test.it", "Testo");
            assertEquals(2, notificheRicevute.size());
        }

        @Test
        @DisplayName("nessun observer → notificaObserver non lancia eccezioni")
        void testNessunObserver() {
            assertDoesNotThrow(() -> observable.notificaObserver("dest@test.it", "Testo"));
        }

        @Test
        @DisplayName("notificaObserver → email destinatario corretta nel bean Notifica")
        void testEmailDestinatarioCorretta() {
            observable.aggiungiObserver(n -> notificheRicevute.add(n));
            observable.notificaObserver("dest@test.it", "Testo");
            assertEquals("dest@test.it", notificheRicevute.get(0).getEmailDestinatario());
        }
    }

    // ════════════════════════════════════════════════════
    //  ScadenzaRichiesteController
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("ScadenzaRichiesteController")
    class ScadenzaRichiesteControllerTest {

        private ScadenzaRichiesteController controller;

        @BeforeAll
        static void setupDAO() {
            DAOFactory.setMode(1);
        }

        @BeforeEach
        void setUp() {
            controller = new ScadenzaRichiesteController();
        }

        @AfterEach
        void tearDown() {
            controller.ferma();
        }

        @Test
        @DisplayName("avvia → scheduler parte senza eccezioni")
        void testAvvia() {
            assertDoesNotThrow(() -> controller.avvia());
        }

        @Test
        @DisplayName("ferma → scheduler termina senza eccezioni")
        void testFerma() {
            controller.avvia();
            assertDoesNotThrow(() -> controller.ferma());
        }

        @Test
        @DisplayName("aggiungiObserver → observer riceve notifica da notificaObserver")
        void testAggiungiObserver() {
            List<model.entity.Notifica> ricevute = new ArrayList<>();
            controller.aggiungiObserver(n -> ricevute.add(n));
            controller.notificaObserver("cli@test.it", "Scaduta");
            assertEquals(1, ricevute.size());
        }

        @Test
        @DisplayName("rimuoviObserver → observer non riceve più notifiche")
        void testRimuoviObserver() {
            List<model.entity.Notifica> ricevute = new ArrayList<>();
            NotificaObserver observer = n -> ricevute.add(n);
            controller.aggiungiObserver(observer);
            controller.rimuoviObserver(observer);
            controller.notificaObserver("cli@test.it", "Scaduta");
            assertTrue(ricevute.isEmpty());
        }
    }
}

package test;

import bean.LoginBean;
import bean.PersonalTrainerBean;
import model.dao.*;
import model.entity.*;
import model.exception.DAOException;
import model.exception.UserNotFoundException;
import org.junit.jupiter.api.*;
import util.observer.NotificaObservableBase;
import util.observer.NotificaObserver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite completa di test per i DAO In-Memory, Bean ed Entity del progetto FitPlan.
 */
class DAOMemoryTest {

    @Test
    @DisplayName("Suite DAO caricata correttamente")
    void testSuiteCaricata() {
        assertTrue(true);
    }

    // ════════════════════════════════════════════════════
    //  DAOFactory
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("DAOFactory")
    class DAOFactoryTest {

        @Test
        @DisplayName("Modalità DEMO → getRichiestaDAO restituisce RichiestaDAOMemory")
        void testDemoModeRichiesta() {
            DAOFactory.setMode(1);
            assertInstanceOf(RichiestaDAOMemory.class, DAOFactory.getRichiestaDAO());
        }

        @Test
        @DisplayName("Modalità DEMO → getUtenteDAO restituisce UtenteDAOMemory")
        void testDemoModeUtente() {
            DAOFactory.setMode(1);
            assertInstanceOf(UtenteDAOMemory.class, DAOFactory.getUtenteDAO());
        }

        @Test
        @DisplayName("Modalità DEMO → getPersonalTrainerDAO restituisce PersonalTrainerDAOMemory")
        void testDemoModePT() {
            DAOFactory.setMode(1);
            assertInstanceOf(PersonalTrainerDAOMemory.class, DAOFactory.getPersonalTrainerDAO());
        }

        @Test
        @DisplayName("Modalità DEMO → getAssociazioneDAO restituisce AssociazioneDAOMemory")
        void testDemoModeAssociazione() {
            DAOFactory.setMode(1);
            assertInstanceOf(AssociazioneDAOMemory.class, DAOFactory.getAssociazioneDAO());
        }

        @Test
        @DisplayName("Modalità DEMO → getNotificaDAO restituisce NotificaDAOMemory")
        void testDemoModeNotifica() {
            DAOFactory.setMode(1);
            assertInstanceOf(NotificaDAOMemory.class, DAOFactory.getNotificaDAO());
        }

        @Test
        @DisplayName("DAOFactory non è istanziabile")
        void testNonInstanziabile() throws Exception {
            java.lang.reflect.Constructor<DAOFactory> c =
                    DAOFactory.class.getDeclaredConstructor();
            c.setAccessible(true);
            assertThrows(java.lang.reflect.InvocationTargetException.class, c::newInstance);
        }
    }

    // ════════════════════════════════════════════════════
    //  UtenteDAOMemory
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("UtenteDAOMemory")
    class UtenteDAOMemoryTest {

        private UtenteDAOMemory dao;

        @BeforeEach
        void setUp() throws Exception {
            Field field = UtenteDAOMemory.class.getDeclaredField("utenti");
            field.setAccessible(true);
            ((List<?>) field.get(null)).clear();
            dao = new UtenteDAOMemory();
        }

        @Test
        @DisplayName("trovaUtente: credenziali corrette → utente restituito")
        void testTrovaUtenteOk() throws UserNotFoundException {
            Utente u = dao.trovaUtente("mario@test.it", "pass123");
            assertNotNull(u);
            assertEquals("mario@test.it", u.getEmail());
        }

        @Test
        @DisplayName("trovaUtente: password errata → UserNotFoundException")
        void testTrovaUtentePasswordErrata() {
            assertThrows(UserNotFoundException.class,
                    () -> dao.trovaUtente("mario@test.it", "sbagliata"));
        }

        @Test
        @DisplayName("trovaUtente: email inesistente → UserNotFoundException")
        void testTrovaUtenteEmailInesistente() {
            assertThrows(UserNotFoundException.class,
                    () -> dao.trovaUtente("nessuno@test.it", "pass123"));
        }

        @Test
        @DisplayName("trovaUtentePerEmail: email esistente → PersonalTrainer restituito")
        void testTrovaPerEmailOk() throws UserNotFoundException {
            Utente u = dao.trovaUtentePerEmail("coach@test.it");
            assertNotNull(u);
            assertInstanceOf(PersonalTrainer.class, u);
        }

        @Test
        @DisplayName("trovaUtentePerEmail: email inesistente → UserNotFoundException")
        void testTrovaPerEmailNonEsistente() {
            assertThrows(UserNotFoundException.class,
                    () -> dao.trovaUtentePerEmail("ghost@test.it"));
        }

        @Test
        @DisplayName("salvaNuovoUtente: utente valido → trovabile dopo salvataggio")
        void testSalvaNuovoUtenteOk() throws DAOException, UserNotFoundException {
            Utente nuovo = new Cliente("Nuovo", "Utente", "nuovo@test.it", "pwd");
            dao.salvaNuovoUtente(nuovo);
            assertEquals("nuovo@test.it", dao.trovaUtentePerEmail("nuovo@test.it").getEmail());
        }

        @Test
        @DisplayName("salvaNuovoUtente: utente null → DAOException")
        void testSalvaNuovoUtenteNull() {
            assertThrows(DAOException.class, () -> dao.salvaNuovoUtente(null));
        }
    }

    // ════════════════════════════════════════════════════
    //  AssociazioneDAOMemory
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("AssociazioneDAOMemory")
    class AssociazioneDAOMemoryTest {

        private AssociazioneDAOMemory dao;

        @BeforeEach
        void setUp() throws Exception {
            pulisciMappa("richiestePT");
            pulisciMappa("stati");
            dao = new AssociazioneDAOMemory();
        }

        @Test
        @DisplayName("getStato: cliente senza richiesta → NESSUNA")
        void testGetStatoDefault() {
            assertEquals(StatoAssociazione.NESSUNA, dao.getStato("sconosciuto@test.it"));
        }

        @Test
        @DisplayName("salvaRichiesta → stato diventa PENDING")
        void testSalvaRichiesta() {
            dao.salvaRichiesta("cli@test.it", "pt@test.it");
            assertEquals(StatoAssociazione.PENDING, dao.getStato("cli@test.it"));
        }

        @Test
        @DisplayName("salvaRichiesta → getEmailPTAssociato restituisce email PT corretta")
        void testGetEmailPTAssociato() {
            dao.salvaRichiesta("cli@test.it", "pt@test.it");
            assertEquals("pt@test.it", dao.getEmailPTAssociato("cli@test.it"));
        }

        @Test
        @DisplayName("aggiornaStato ASSOCIATO → stato aggiornato")
        void testAggiornaStatoAssociato() {
            dao.salvaRichiesta("cli@test.it", "pt@test.it");
            dao.aggiornaStato("cli@test.it", StatoAssociazione.ASSOCIATO);
            assertEquals(StatoAssociazione.ASSOCIATO, dao.getStato("cli@test.it"));
        }

        @Test
        @DisplayName("aggiornaStato NESSUNA → stato aggiornato")
        void testAggiornaStatoNessuna() {
            dao.salvaRichiesta("cli@test.it", "pt@test.it");
            dao.aggiornaStato("cli@test.it", StatoAssociazione.NESSUNA);
            assertEquals(StatoAssociazione.NESSUNA, dao.getStato("cli@test.it"));
        }

        @Test
        @DisplayName("getRichiestePerPT: restituisce solo i PENDING per quel PT")
        void testGetRichiestePerPT() {
            dao.salvaRichiesta("a@test.it", "pt@test.it");
            dao.salvaRichiesta("b@test.it", "pt@test.it");
            dao.salvaRichiesta("c@test.it", "altropt@test.it");
            dao.aggiornaStato("b@test.it", StatoAssociazione.ASSOCIATO);
            List<String> richieste = dao.getRichiestePerPT("pt@test.it");
            assertEquals(1, richieste.size());
            assertEquals("a@test.it", richieste.get(0));
        }

        @Test
        @DisplayName("getRichiestePerPT: PT senza richieste → lista vuota")
        void testGetRichiestePerPT_Vuota() {
            assertTrue(dao.getRichiestePerPT("nessunpt@test.it").isEmpty());
        }

        private void pulisciMappa(String fieldName) throws Exception {
            Field f = AssociazioneDAOMemory.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            ((Map<?, ?>) f.get(null)).clear();
        }
    }

    // ════════════════════════════════════════════════════
    //  RichiestaDAOMemory
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("RichiestaDAOMemory")
    class RichiestaDAOMemoryTest {

        private RichiestaDAOMemory dao;

        @BeforeEach
        void setUp() throws Exception {
            Field field = RichiestaDAOMemory.class.getDeclaredField("storage");
            field.setAccessible(true);
            ((List<?>) field.get(null)).clear();
            dao = new RichiestaDAOMemory();
        }

        @Test
        @DisplayName("salvaRichiesta: richiesta valida → trovata in prendiTutte")
        void testSalvaEPrendiTutte() throws DAOException {
            dao.salvaRichiesta(creaRichiesta("cli@test.it", "pt@test.it", "Massa"));
            assertEquals(1, dao.prendiTutteLeRichieste().size());
        }

        @Test
        @DisplayName("salvaRichiesta: richiesta null → DAOException")
        void testSalvaNull() {
            assertThrows(DAOException.class, () -> dao.salvaRichiesta(null));
        }

        @Test
        @DisplayName("prendiRichiestePerPT: filtra correttamente per PT")
        void testPrendiRichiestePerPT() throws DAOException {
            dao.salvaRichiesta(creaRichiesta("a@test.it", "pt1@test.it", "Forza"));
            dao.salvaRichiesta(creaRichiesta("b@test.it", "pt1@test.it", "Cardio"));
            dao.salvaRichiesta(creaRichiesta("c@test.it", "pt2@test.it", "Massa"));
            assertEquals(2, dao.prendiRichiestePerPT("pt1@test.it").size());
        }

        @Test
        @DisplayName("prendiRichiestePerPT: id null → lista vuota")
        void testPrendiRichiestePerPT_Null() {
            assertTrue(dao.prendiRichiestePerPT(null).isEmpty());
        }

        @Test
        @DisplayName("cancellaRichiesta: richiesta esistente → rimossa")
        void testCancellaRichiesta() throws DAOException {
            RichiestaScheda r = creaRichiesta("cli@test.it", "pt@test.it", "Dimagrimento");
            dao.salvaRichiesta(r);
            dao.cancellaRichiesta(r);
            assertTrue(dao.prendiTutteLeRichieste().isEmpty());
        }

        @Test
        @DisplayName("cancellaRichiesta: richiesta null → DAOException")
        void testCancellaNull() {
            assertThrows(DAOException.class, () -> dao.cancellaRichiesta(null));
        }

        @Test
        @DisplayName("cancellaRichiesta: obiettivo diverso → richiesta originale non rimossa")
        void testCancellaRichiestaNonEsistente() throws DAOException {
            dao.salvaRichiesta(creaRichiesta("cli@test.it", "pt@test.it", "Forza"));
            assertDoesNotThrow(() -> dao.cancellaRichiesta(
                    creaRichiesta("cli@test.it", "pt@test.it", "ObiettivoDiverso")));
            assertEquals(1, dao.prendiTutteLeRichieste().size());
        }

        private RichiestaScheda creaRichiesta(String emailCliente, String emailPT, String obiettivo) {
            return new RichiestaScheda(new DatiFisici("M", 25, 75),
                    obiettivo, 3, "Note", emailCliente, emailPT);
        }

        @Test
        @DisplayName("esisteRichiestaAttiva: cliente con richiesta → true")
        void testEsisteRichiestaAttiva_True() throws DAOException {
            dao.salvaRichiesta(creaRichiesta("cli@test.it", "pt@test.it", "Forza"));
            assertTrue(dao.esisteRichiestaAttiva("cli@test.it"));
        }

        @Test
        @DisplayName("esisteRichiestaAttiva: cliente senza richiesta → false")
        void testEsisteRichiestaAttiva_False() throws DAOException {
            assertFalse(dao.esisteRichiestaAttiva("nessuno@test.it"));
        }

        @Test
        @DisplayName("esisteRichiestaAttiva: case insensitive")
        void testEsisteRichiestaAttiva_CaseInsensitive() throws DAOException {
            dao.salvaRichiesta(creaRichiesta("CLI@TEST.IT", "pt@test.it", "Massa"));
            assertTrue(dao.esisteRichiestaAttiva("cli@test.it"));
        }
    }

    // ════════════════════════════════════════════════════
    //  PersonalTrainerDAOMemory
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("PersonalTrainerDAOMemory")
    class PersonalTrainerDAOMemoryTest {

        private PersonalTrainerDAOMemory dao;

        @BeforeEach
        void setUp() throws Exception {
            Field field = PersonalTrainerDAOMemory.class.getDeclaredField("listaPT");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PersonalTrainer> lista = (List<PersonalTrainer>) field.get(null);
            lista.clear();
            lista.add(new PersonalTrainer("Marco", "Neri", "marco@gym.it", "pwd"));
            lista.add(new PersonalTrainer("Sara", "Blu", "sara@gym.it", "pwd"));
            dao = new PersonalTrainerDAOMemory();
        }

        @Test
        @DisplayName("getPTByEmail: email esistente → PT trovato")
        void testGetPTByEmailOk() {
            assertNotNull(dao.getPTByEmail("marco@gym.it"));
        }

        @Test
        @DisplayName("getPTByEmail: case insensitive")
        void testGetPTByEmailCaseInsensitive() {
            assertNotNull(dao.getPTByEmail("MARCO@GYM.IT"));
        }

        @Test
        @DisplayName("getPTByEmail: email inesistente → null")
        void testGetPTByEmailNonEsistente() {
            assertNull(dao.getPTByEmail("nessuno@gym.it"));
        }

        @Test
        @DisplayName("getPTByName: nome esistente → lista con 1 elemento")
        void testGetPTByNome() {
            assertEquals(1, dao.getPTByName("Marco").size());
        }

        @Test
        @DisplayName("getPTByName: cognome esistente → lista con 1 elemento")
        void testGetPTByCognome() {
            assertEquals(1, dao.getPTByName("Blu").size());
        }

        @Test
        @DisplayName("getPTByName: prefisso parziale → tutti i PT che iniziano con quel prefisso")
        void testGetPTByNamePrefisso() throws Exception {
            Field field = PersonalTrainerDAOMemory.class.getDeclaredField("listaPT");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PersonalTrainer> lista = (List<PersonalTrainer>) field.get(null);
            lista.add(new PersonalTrainer("Marina", "Verdi", "marina@gym.it", "pwd"));
            assertEquals(2, dao.getPTByName("ma").size());
        }

        @Test
        @DisplayName("getPTByName: nome inesistente → lista vuota")
        void testGetPTByNomeInesistente() {
            assertTrue(dao.getPTByName("Fantasma").isEmpty());
        }

        @Test
        @DisplayName("getAllPT → restituisce tutti i PT")
        void testGetAllPT() {
            assertEquals(2, dao.getAllPT().size());
        }

        @Test
        @DisplayName("getPTById: id esistente → PT trovato")
        void testGetPTById() throws Exception {
            Field field = PersonalTrainerDAOMemory.class.getDeclaredField("listaPT");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PersonalTrainer> lista = (List<PersonalTrainer>) field.get(null);
            assertNotNull(dao.getPTById(lista.get(0).getId()));
        }

        @Test
        @DisplayName("getPTById: id inesistente → null")
        void testGetPTByIdNonEsistente() {
            assertNull(dao.getPTById("PT-INESISTENTE"));
        }
    }

    // ════════════════════════════════════════════════════
    //  NotificaDAOMemory
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("NotificaDAOMemory")
    class NotificaDAOMemoryTest {

        private NotificaDAOMemory dao;

        @BeforeEach
        void setUp() throws Exception {
            Field field = NotificaDAOMemory.class.getDeclaredField("storage");
            field.setAccessible(true);
            ((Map<?, ?>) field.get(null)).clear();
            dao = new NotificaDAOMemory();
        }

        @Test
        @DisplayName("salvaNotifica → notifica recuperabile")
        void testSalvaECarica(){
            dao.salvaNotifica(new Notifica("utente@test.it", "Testo notifica"));
            List<String> result = dao.caricaECancellaNotifiche("utente@test.it");
            assertEquals(1, result.size());
            assertEquals("Testo notifica", result.get(0));
        }

        @Test
        @DisplayName("caricaECancella → dopo la lettura la lista è vuota")
        void testCancellaDopoLettura(){
            dao.salvaNotifica(new Notifica("utente@test.it", "Testo"));
            dao.caricaECancellaNotifiche("utente@test.it");
            assertTrue(dao.caricaECancellaNotifiche("utente@test.it").isEmpty());
        }

        @Test
        @DisplayName("utente senza notifiche → lista vuota")
        void testNessunNotifica(){
            assertTrue(dao.caricaECancellaNotifiche("nessuno@test.it").isEmpty());
        }

        @Test
        @DisplayName("notifiche di utenti diversi non si mescolano")
        void testIsolamentoUtenti(){
            dao.salvaNotifica(new Notifica("a@test.it", "Notifica A"));
            dao.salvaNotifica(new Notifica("b@test.it", "Notifica B"));
            List<String> resultA = dao.caricaECancellaNotifiche("a@test.it");
            assertEquals(1, resultA.size());
            assertEquals("Notifica A", resultA.get(0));
        }
    }

    // ════════════════════════════════════════════════════
    //  RichiestaScheda + DatiFisici (Entity)
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("RichiestaScheda e DatiFisici")
    class RichiestaSchedaTest {

        private DatiFisici datiFisici;
        private RichiestaScheda richiesta;

        @BeforeEach
        void setUp() {
            datiFisici = new DatiFisici("M", 25, 75.5);
            richiesta = new RichiestaScheda(datiFisici, "Dimagrimento",
                    3, "Nessuna nota", "cli@test.it", "pt@test.it");
        }

        @Test
        @DisplayName("DatiFisici: getter sesso → corretto")
        void testDatiFisiciSesso() {
            assertEquals("M", datiFisici.getSesso());
        }

        @Test
        @DisplayName("DatiFisici: getter età → corretto")
        void testDatiFisiciEta() {
            assertEquals(25, datiFisici.getEta());
        }

        @Test
        @DisplayName("DatiFisici: getter peso → corretto")
        void testDatiFisiciPeso() {
            assertEquals(75.5, datiFisici.getPeso());
        }

        @Test
        @DisplayName("RichiestaScheda: getSesso delega a DatiFisici")
        void testGetSesso() {
            assertEquals("M", richiesta.getSesso());
        }

        @Test
        @DisplayName("RichiestaScheda: getEta delega a DatiFisici")
        void testGetEta() {
            assertEquals(25, richiesta.getEta());
        }

        @Test
        @DisplayName("RichiestaScheda: getPeso delega a DatiFisici")
        void testGetPeso() {
            assertEquals(75.5, richiesta.getPeso());
        }

        @Test
        @DisplayName("RichiestaScheda: getObiettivo → corretto")
        void testGetObiettivo() {
            assertEquals("Dimagrimento", richiesta.getObiettivo());
        }

        @Test
        @DisplayName("RichiestaScheda: setObiettivo → aggiornato")
        void testSetObiettivo() {
            richiesta.setObiettivo("Massa");
            assertEquals("Massa", richiesta.getObiettivo());
        }

        @Test
        @DisplayName("RichiestaScheda: getFrequenzaSettimanale → corretto")
        void testGetFrequenza() {
            assertEquals(3, richiesta.getFrequenzaSettimanale());
        }

        @Test
        @DisplayName("RichiestaScheda: setFrequenzaSettimanale → aggiornato")
        void testSetFrequenza() {
            richiesta.setFrequenzaSettimanale(5);
            assertEquals(5, richiesta.getFrequenzaSettimanale());
        }

        @Test
        @DisplayName("RichiestaScheda: getNote → corretto")
        void testGetNote() {
            assertEquals("Nessuna nota", richiesta.getNote());
        }

        @Test
        @DisplayName("RichiestaScheda: getClienteEmail → corretto")
        void testGetClienteEmail() {
            assertEquals("cli@test.it", richiesta.getClienteEmail());
        }

        @Test
        @DisplayName("RichiestaScheda: getIdPersonalTrainer → corretto")
        void testGetIdPersonalTrainer() {
            assertEquals("pt@test.it", richiesta.getIdPersonalTrainer());
        }

        @Test
        @DisplayName("RichiestaScheda: toString contiene email e obiettivo")
        void testToString() {
            String s = richiesta.toString();
            assertTrue(s.contains("cli@test.it"));
            assertTrue(s.contains("Dimagrimento"));
        }
    }

    // ════════════════════════════════════════════════════
    //  LoginBean
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("LoginBean")
    class LoginBeanTest {

        @Test
        @DisplayName("setEmail normalizza in lowercase e trim")
        void testSetEmailNormalizza() {
            LoginBean bean = new LoginBean();
            bean.setEmail("  MARIO@TEST.IT  ");
            assertEquals("mario@test.it", bean.getEmail());
        }

        @Test
        @DisplayName("setEmail null → getEmail restituisce null")
        void testSetEmailNull() {
            LoginBean bean = new LoginBean();
            bean.setEmail(null);
            assertNull(bean.getEmail());
        }

        @Test
        @DisplayName("isValid: email e password valorizzate → true")
        void testIsValidTrue() {
            LoginBean bean = new LoginBean();
            bean.setEmail("mario@test.it");
            bean.setPassword("pass123");
            assertTrue(bean.isValid());
        }

        @Test
        @DisplayName("isValid: email vuota → false")
        void testIsValidEmailVuota() {
            LoginBean bean = new LoginBean();
            bean.setEmail("");
            bean.setPassword("pass123");
            assertFalse(bean.isValid());
        }

        @Test
        @DisplayName("isValid: password null → false")
        void testIsValidPasswordNull() {
            LoginBean bean = new LoginBean();
            bean.setEmail("mario@test.it");
            bean.setPassword(null);
            assertFalse(bean.isValid());
        }
    }

    // ════════════════════════════════════════════════════
    //  PersonalTrainerBean
    // ════════════════════════════════════════════════════

    @Nested
    @DisplayName("PersonalTrainerBean")
    class PersonalTrainerBeanTest {

        @Test
        @DisplayName("Costruttore vuoto → getter restituiscono null")
        void testCostruttoreVuoto() {
            PersonalTrainerBean bean = new PersonalTrainerBean();
            assertNull(bean.getId());
            assertNull(bean.getNome());
            assertNull(bean.getCognome());
            assertNull(bean.getEmail());
        }

        @Test
        @DisplayName("Costruttore completo → getter restituiscono valori corretti")
        void testCostruttoreCompleto() {
            PersonalTrainerBean bean = new PersonalTrainerBean("PT-001", "Mario", "Rossi", "mario@pt.it");
            assertEquals("PT-001", bean.getId());
            assertEquals("Mario", bean.getNome());
            assertEquals("Rossi", bean.getCognome());
            assertEquals("mario@pt.it", bean.getEmail());
        }

        @Test
        @DisplayName("Setter → valori aggiornati correttamente")
        void testSetter() {
            PersonalTrainerBean bean = new PersonalTrainerBean();
            bean.setId("PT-002");
            bean.setNome("Luca");
            bean.setCognome("Verdi");
            bean.setEmail("luca@pt.it");
            assertEquals("PT-002", bean.getId());
            assertEquals("Luca", bean.getNome());
            assertEquals("Verdi", bean.getCognome());
            assertEquals("luca@pt.it", bean.getEmail());
        }

        @Test
        @DisplayName("toString → contiene nome, cognome e id")
        void testToString() {
            PersonalTrainerBean bean = new PersonalTrainerBean("PT-001", "Mario", "Rossi", "mario@pt.it");
            String s = bean.toString();
            assertTrue(s.contains("Mario"));
            assertTrue(s.contains("Rossi"));
            assertTrue(s.contains("PT-001"));
        }
    }
}

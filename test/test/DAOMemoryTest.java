package test;

import model.dao.*;
import model.entity.*;
import model.exception.DAOException;
import model.exception.UserNotFoundException;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per le implementazioni DAO In-Memory e per DAOFactory.
 * Ogni test è indipendente dall'ordine di esecuzione grazie al @BeforeEach.
 */

// ════════════════════════════════════════════════════
//  DAOFactory
// ════════════════════════════════════════════════════

class DAOMemoryTest {
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
        @DisplayName("DAOFactory non è istanziabile")
        void testNonInstanziabile() throws Exception {
            java.lang.reflect.Constructor<DAOFactory> c =
                    DAOFactory.class.getDeclaredConstructor();
            c.setAccessible(true);
            assertThrows(java.lang.reflect.InvocationTargetException.class, c::newInstance);
        }
    }

    @Nested
    @DisplayName("NotificaDAOMemory")
    class NotificaDAOMemoryTest {

        private NotificaDAOMemory dao;

        @BeforeEach
        void setUp() throws Exception {
            java.lang.reflect.Field field = NotificaDAOMemory.class.getDeclaredField("storage");
            field.setAccessible(true);
            ((java.util.Map<?, ?>) field.get(null)).clear();
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
            List<String> result = dao.caricaECancellaNotifiche("utente@test.it");
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("utente senza notifiche → lista vuota")
        void testNessunNotifica(){
            List<String> result = dao.caricaECancellaNotifiche("nessuno@test.it");
            assertTrue(result.isEmpty());
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
            dao = new UtenteDAOMemory(); // pre-carica mario@test.it e coach@test.it
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
            Utente trovato = dao.trovaUtentePerEmail("nuovo@test.it");
            assertEquals("nuovo@test.it", trovato.getEmail());
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
            dao.aggiornaStato("b@test.it", StatoAssociazione.ASSOCIATO); // escluso

            List<String> richieste = dao.getRichiestePerPT("pt@test.it");
            assertEquals(1, richieste.size());
            assertEquals("a@test.it", richieste.get(0));
        }

        @Test
        @DisplayName("getRichiestePerPT: PT senza richieste → lista vuota")
        void testGetRichiestePerPT_Vuota() {
            List<String> richieste = dao.getRichiestePerPT("nessunpt@test.it");
            assertTrue(richieste.isEmpty());
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
            List<RichiestaScheda> result = dao.prendiRichiestePerPT(null);
            assertNotNull(result);
            assertTrue(result.isEmpty());
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
            RichiestaScheda r = creaRichiesta("cli@test.it", "pt@test.it", "Forza");
            dao.salvaRichiesta(r);

            RichiestaScheda fantasma = creaRichiesta("cli@test.it", "pt@test.it", "ObiettivoDiverso");
            assertDoesNotThrow(() -> dao.cancellaRichiesta(fantasma));
            assertEquals(1, dao.prendiTutteLeRichieste().size());
        }

        private RichiestaScheda creaRichiesta(String emailCliente, String emailPT, String obiettivo) {
            DatiFisici df = new DatiFisici("M", 25, 75);
            return new RichiestaScheda(df, obiettivo, 3, "Note", emailCliente, emailPT);
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
            PersonalTrainer pt = dao.getPTByEmail("marco@gym.it");
            assertNotNull(pt);
            assertEquals("Marco", pt.getNome());
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
        @DisplayName("getPTByName: nome inesistente → lista vuota")
        void testGetPTByNomeInesistente() {
            assertTrue(dao.getPTByName("Fantasma").isEmpty());
        }

        @Test
        @DisplayName("getAllPT → restituisce tutti i PT caricati")
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
            String idReale = lista.get(0).getId();

            assertNotNull(dao.getPTById(idReale));
        }

        @Test
        @DisplayName("getPTById: id inesistente → null")
        void testGetPTByIdNonEsistente() {
            assertNull(dao.getPTById("PT-INESISTENTE"));
        }
    }
}

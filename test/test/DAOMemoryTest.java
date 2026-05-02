package model.dao;

import model.entity.*;
import model.exception.DAOException;
import model.exception.UserNotFoundException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per le implementazioni DAO In-Memory.
 * Copre: UtenteDAOMemory, AssociazioneDAOMemory, RichiestaDAOMemory, PersonalTrainerDAOMemory
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOMemoryTest {

    // ════════════════════════════════════════════════════
    //  UtenteDAOMemory
    // ════════════════════════════════════════════════════

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("UtenteDAOMemory")
    class UtenteDAOMemoryTest {

        private UtenteDAOMemory dao;

        @BeforeEach
        void setUp() throws Exception {
            // Puliamo la lista statica e ricarichiamo i dati di default
            java.lang.reflect.Field field = UtenteDAOMemory.class.getDeclaredField("utenti");
            field.setAccessible(true);
            ((List<?>) field.get(null)).clear();
            dao = new UtenteDAOMemory(); // richiama il costruttore che pre-carica mario e coach
        }

        @Test
        @Order(1)
        @DisplayName("trovaUtente: credenziali corrette → utente restituito")
        void testTrovaUtenteOk() throws UserNotFoundException {
            Utente u = dao.trovaUtente("mario@test.it", "pass123");
            assertNotNull(u);
            assertEquals("mario@test.it", u.getEmail());
        }

        @Test
        @Order(2)
        @DisplayName("trovaUtente: password errata → UserNotFoundException")
        void testTrovaUtentePasswordErrata() {
            assertThrows(UserNotFoundException.class,
                    () -> dao.trovaUtente("mario@test.it", "sbagliata"));
        }

        @Test
        @Order(3)
        @DisplayName("trovaUtente: email inesistente → UserNotFoundException")
        void testTrovaUtenteEmailInesistente() {
            assertThrows(UserNotFoundException.class,
                    () -> dao.trovaUtente("nessuno@test.it", "pass123"));
        }

        @Test
        @Order(4)
        @DisplayName("trovaUtentePerEmail: email esistente → utente restituito")
        void testTrovaPerEmailOk() throws UserNotFoundException {
            Utente u = dao.trovaUtentePerEmail("coach@test.it");
            assertNotNull(u);
            assertInstanceOf(PersonalTrainer.class, u);
        }

        @Test
        @Order(5)
        @DisplayName("trovaUtentePerEmail: email inesistente → UserNotFoundException")
        void testTrovaPerEmailNonEsistente() {
            assertThrows(UserNotFoundException.class,
                    () -> dao.trovaUtentePerEmail("ghost@test.it"));
        }

        @Test
        @Order(6)
        @DisplayName("salvaNuovoUtente: utente valido → trovabile dopo salvataggio")
        void testSalvaNuovoUtenteOk() throws DAOException, UserNotFoundException {
            Utente nuovo = new Cliente("Nuovo", "Utente", "nuovo@test.it", "pwd");
            dao.salvaNuovoUtente(nuovo);
            Utente trovato = dao.trovaUtentePerEmail("nuovo@test.it");
            assertEquals("nuovo@test.it", trovato.getEmail());
        }

        @Test
        @Order(7)
        @DisplayName("salvaNuovoUtente: utente null → DAOException")
        void testSalvaNuovoUtenteNull() {
            assertThrows(DAOException.class, () -> dao.salvaNuovoUtente(null));
        }
    }

    // ════════════════════════════════════════════════════
    //  AssociazioneDAOMemory
    // ════════════════════════════════════════════════════

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
        @Order(1)
        @DisplayName("getStato: cliente senza richiesta → NESSUNA")
        void testGetStatoDefault() {
            assertEquals(StatoAssociazione.NESSUNA, dao.getStato("sconosciuto@test.it"));
        }

        @Test
        @Order(2)
        @DisplayName("salvaRichiesta → stato diventa PENDING")
        void testSalvaRichiesta() {
            dao.salvaRichiesta("cli@test.it", "pt@test.it");
            assertEquals(StatoAssociazione.PENDING, dao.getStato("cli@test.it"));
        }

        @Test
        @Order(3)
        @DisplayName("salvaRichiesta → getEmailPTAssociato restituisce email PT corretta")
        void testGetEmailPTAssociato() {
            dao.salvaRichiesta("cli@test.it", "pt@test.it");
            assertEquals("pt@test.it", dao.getEmailPTAssociato("cli@test.it"));
        }

        @Test
        @Order(4)
        @DisplayName("aggiornaStato ASSOCIATO → stato aggiornato")
        void testAggiornaStatoAssociato() {
            dao.salvaRichiesta("cli@test.it", "pt@test.it");
            dao.aggiornaStato("cli@test.it", StatoAssociazione.ASSOCIATO);
            assertEquals(StatoAssociazione.ASSOCIATO, dao.getStato("cli@test.it"));
        }

        @Test
        @Order(5)
        @DisplayName("aggiornaStato NESSUNA → stato aggiornato")
        void testAggiornaStatoNessuna() {
            dao.salvaRichiesta("cli@test.it", "pt@test.it");
            dao.aggiornaStato("cli@test.it", StatoAssociazione.NESSUNA);
            assertEquals(StatoAssociazione.NESSUNA, dao.getStato("cli@test.it"));
        }

        @Test
        @Order(6)
        @DisplayName("getRichiestePerPT: restituisce solo i PENDING per quel PT")
        void testGetRichiestePerPT() {
            dao.salvaRichiesta("a@test.it", "pt@test.it");
            dao.salvaRichiesta("b@test.it", "pt@test.it");
            dao.salvaRichiesta("c@test.it", "altropt@test.it");
            dao.aggiornaStato("b@test.it", StatoAssociazione.ASSOCIATO); // non deve comparire

            List<String> richieste = dao.getRichiestePerPT("pt@test.it");
            assertEquals(1, richieste.size());
            assertEquals("a@test.it", richieste.get(0));
        }

        @Test
        @Order(7)
        @DisplayName("getRichiestePerPT: PT senza richieste → lista vuota")
        void testGetRichiestePerPT_Vuota() {
            List<String> richieste = dao.getRichiestePerPT("nessunpt@test.it");
            assertTrue(richieste.isEmpty());
        }

        private void pulisciMappa(String fieldName) throws Exception {
            java.lang.reflect.Field f = AssociazioneDAOMemory.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            ((java.util.Map<?, ?>) f.get(null)).clear();
        }
    }

    // ════════════════════════════════════════════════════
    //  RichiestaDAOMemory
    // ════════════════════════════════════════════════════

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("RichiestaDAOMemory")
    class RichiestaDAOMemoryTest {

        private RichiestaDAOMemory dao;

        @BeforeEach
        void setUp() throws Exception {
            java.lang.reflect.Field field = RichiestaDAOMemory.class.getDeclaredField("storage");
            field.setAccessible(true);
            ((List<?>) field.get(null)).clear();
            dao = new RichiestaDAOMemory();
        }

        @Test
        @Order(1)
        @DisplayName("salvaRichiesta: richiesta valida → trovata in prendiTutte")
        void testSalvaEPrendiTutte() throws DAOException {
            RichiestaScheda r = creaRichiesta("cli@test.it", "pt@test.it", "Massa");
            dao.salvaRichiesta(r);

            List<RichiestaScheda> tutte = dao.prendiTutteLeRichieste();
            assertEquals(1, tutte.size());
        }

        @Test
        @Order(2)
        @DisplayName("salvaRichiesta: richiesta null → DAOException")
        void testSalvaNull() {
            assertThrows(DAOException.class, () -> dao.salvaRichiesta(null));
        }

        @Test
        @Order(3)
        @DisplayName("prendiRichiestePerPT: filtra correttamente per PT")
        void testPrendiRichiestePerPT() throws DAOException {
            dao.salvaRichiesta(creaRichiesta("a@test.it", "pt1@test.it", "Forza"));
            dao.salvaRichiesta(creaRichiesta("b@test.it", "pt1@test.it", "Cardio"));
            dao.salvaRichiesta(creaRichiesta("c@test.it", "pt2@test.it", "Massa"));

            List<RichiestaScheda> perPT1 = dao.prendiRichiestePerPT("pt1@test.it");
            assertEquals(2, perPT1.size());
        }

        @Test
        @Order(4)
        @DisplayName("prendiRichiestePerPT: id null → lista vuota")
        void testPrendiRichiestePerPT_Null() {
            List<RichiestaScheda> result = dao.prendiRichiestePerPT(null);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @Order(5)
        @DisplayName("cancellaRichiesta: richiesta esistente → rimossa")
        void testCancellaRichiesta() throws DAOException {
            RichiestaScheda r = creaRichiesta("cli@test.it", "pt@test.it", "Dimagrimento");
            dao.salvaRichiesta(r);
            dao.cancellaRichiesta(r);

            assertTrue(dao.prendiTutteLeRichieste().isEmpty());
        }

        @Test
        @Order(6)
        @DisplayName("cancellaRichiesta: richiesta null → DAOException")
        void testCancellaNull() {
            assertThrows(DAOException.class, () -> dao.cancellaRichiesta(null));
        }

        @Test
        @Order(7)
        @DisplayName("cancellaRichiesta: richiesta non esistente → lista invariata (warning)")
        void testCancellaRichiestaNonEsistente() throws DAOException {
            RichiestaScheda r = creaRichiesta("cli@test.it", "pt@test.it", "Forza");
            dao.salvaRichiesta(r);

            RichiestaScheda fantasma = creaRichiesta("cli@test.it", "pt@test.it", "ObiettivoDiverso");
            assertDoesNotThrow(() -> dao.cancellaRichiesta(fantasma));
            assertEquals(1, dao.prendiTutteLeRichieste().size()); // quella originale è ancora lì
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
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("PersonalTrainerDAOMemory")
    class PersonalTrainerDAOMemoryTest {

        private PersonalTrainerDAOMemory dao;

        @BeforeEach
        void setUp() throws Exception {
            java.lang.reflect.Field field = PersonalTrainerDAOMemory.class.getDeclaredField("listaPT");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PersonalTrainer> lista = (List<PersonalTrainer>) field.get(null);
            lista.clear();
            lista.add(new PersonalTrainer("Marco", "Neri", "marco@gym.it", "pwd"));
            lista.add(new PersonalTrainer("Sara", "Blu", "sara@gym.it", "pwd"));
            dao = new PersonalTrainerDAOMemory();
        }

        @Test
        @Order(1)
        @DisplayName("getPTByEmail: email esistente → PT trovato")
        void testGetPTByEmailOk() {
            PersonalTrainer pt = dao.getPTByEmail("marco@gym.it");
            assertNotNull(pt);
            assertEquals("Marco", pt.getNome());
        }

        @Test
        @Order(2)
        @DisplayName("getPTByEmail: case insensitive")
        void testGetPTByEmailCaseInsensitive() {
            PersonalTrainer pt = dao.getPTByEmail("MARCO@GYM.IT");
            assertNotNull(pt);
        }

        @Test
        @Order(3)
        @DisplayName("getPTByEmail: email inesistente → null")
        void testGetPTByEmailNull() {
            assertNull(dao.getPTByEmail("nessuno@gym.it"));
        }

        @Test
        @Order(4)
        @DisplayName("getPTByName: nome esistente → lista con 1 elemento")
        void testGetPTByNome() {
            List<PersonalTrainer> risultati = dao.getPTByName("Marco");
            assertEquals(1, risultati.size());
        }

        @Test
        @Order(5)
        @DisplayName("getPTByName: cognome esistente → lista con 1 elemento")
        void testGetPTByCognome() {
            List<PersonalTrainer> risultati = dao.getPTByName("Blu");
            assertEquals(1, risultati.size());
        }

        @Test
        @Order(6)
        @DisplayName("getPTByName: nome inesistente → lista vuota")
        void testGetPTByNomeInesistente() {
            List<PersonalTrainer> risultati = dao.getPTByName("Fantasma");
            assertTrue(risultati.isEmpty());
        }

        @Test
        @Order(7)
        @DisplayName("getAllPT → restituisce tutti i PT")
        void testGetAllPT() {
            List<PersonalTrainer> tutti = dao.getAllPT();
            assertEquals(2, tutti.size());
        }

        @Test
        @Order(8)
        @DisplayName("getPTById: id esistente → PT trovato")
        void testGetPTById() throws Exception {
            java.lang.reflect.Field field = PersonalTrainerDAOMemory.class.getDeclaredField("listaPT");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PersonalTrainer> lista = (List<PersonalTrainer>) field.get(null);
            String idReale = lista.get(0).getId();

            PersonalTrainer pt = dao.getPTById(idReale);
            assertNotNull(pt);
        }

        @Test
        @Order(9)
        @DisplayName("getPTById: id inesistente → null")
        void testGetPTByIdNonEsistente() {
            assertNull(dao.getPTById("PT-INESISTENTE"));
        }
    }
}

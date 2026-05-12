package test;

import bean.RichiestaSchedaBean;
import controller.RichiediSchedaController;
import model.Sessione;
import model.dao.*;
import model.entity.*;
import model.exception.DAOException;
import model.exception.InvalidFormException;
import model.exception.TrainerNotAssociatedException;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RichiediSchedaTest {

    @Test
    @DisplayName("Suite richiedi scheda caricata correttamente")
    void testSuiteCaricata() {
        assertTrue(true);
    }

    //  RichiediSchedaController

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
        void setUp() throws Exception {
            controller = new RichiediSchedaController();
            // Pulisce lo storage del RichiestaDAOMemory
            Field field = RichiestaDAOMemory.class.getDeclaredField("storage");
            field.setAccessible(true);
            ((List<?>) field.get(null)).clear();
        }

        //  Verifica stato associazione

        @Test
        @DisplayName("Cliente con stato NESSUNA → TrainerNotAssociatedException")
        void testVerificaAssociazione_StatoNessuna() {
            impostaSessione(StatoAssociazione.NESSUNA, StatoRichiesta.NESSUNA);
            assertThrows(TrainerNotAssociatedException.class,
                    () -> controller.verificaAssociazionePT());
        }

        @Test
        @DisplayName("Cliente con stato PENDING → TrainerNotAssociatedException")
        void testVerificaAssociazione_StatoPending() {
            impostaSessione(StatoAssociazione.PENDING, StatoRichiesta.NESSUNA);
            assertThrows(TrainerNotAssociatedException.class,
                    () -> controller.verificaAssociazionePT());
        }

        @Test
        @DisplayName("Cliente con stato ASSOCIATO → nessuna eccezione")
        void testVerificaAssociazione_StatoAssociato() {
            impostaSessione(StatoAssociazione.ASSOCIATO, StatoRichiesta.NESSUNA);
            assertDoesNotThrow(() -> controller.verificaAssociazionePT());
        }

        //  Validazione peso

        @Test
        @DisplayName("Peso = 0 → InvalidFormException")
        void testPesoZero() {
            impostaSessione(StatoAssociazione.ASSOCIATO, StatoRichiesta.NESSUNA);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setPeso(0);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        @Test
        @DisplayName("Peso negativo → InvalidFormException")
        void testPesoNegativo() {
            impostaSessione(StatoAssociazione.ASSOCIATO, StatoRichiesta.NESSUNA);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setPeso(-5);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        @Test
        @DisplayName("Peso > 200 → InvalidFormException")
        void testPesoEccessivo() {
            impostaSessione(StatoAssociazione.ASSOCIATO, StatoRichiesta.NESSUNA);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setPeso(201);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        //  Validazione età

        @Test
        @DisplayName("Età < 10 → InvalidFormException")
        void testEtaMinore() {
            impostaSessione(StatoAssociazione.ASSOCIATO, StatoRichiesta.NESSUNA);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setEta(9);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        @Test
        @DisplayName("Età > 100 → InvalidFormException")
        void testEtaMaggiore() {
            impostaSessione(StatoAssociazione.ASSOCIATO, StatoRichiesta.NESSUNA);
            RichiestaSchedaBean bean = creaBeanValido();
            bean.setEta(101);
            assertThrows(InvalidFormException.class, () -> controller.elaboraRichiesta(bean));
        }

        //  Blocco richiesta già attiva

        @Test
        @DisplayName("Richiesta già in PENDING → InvalidFormException")
        void testRichiestaGiaPending() throws DAOException {
            impostaSessione(StatoAssociazione.ASSOCIATO, StatoRichiesta.NESSUNA);
            // Prima richiesta: salva correttamente
            assertDoesNotThrow(() -> controller.elaboraRichiesta(creaBeanValido()));
            // Seconda richiesta: deve essere bloccata
            assertThrows(InvalidFormException.class,
                    () -> controller.elaboraRichiesta(creaBeanValido()));
        }

        //  Happy path

        @Test
        @DisplayName("Dati validi → elaboraRichiesta senza eccezioni")
        void testElaboraRichiesta_DatiValidi() {
            impostaSessione(StatoAssociazione.ASSOCIATO, StatoRichiesta.NESSUNA);
            assertDoesNotThrow(() -> controller.elaboraRichiesta(creaBeanValido()));
        }

        @Test
        @DisplayName("Dati validi → stato sessione aggiornato a PENDING")
        void testElaboraRichiesta_AggiornaSessione() throws InvalidFormException, DAOException {
            impostaSessione(StatoAssociazione.ASSOCIATO, StatoRichiesta.NESSUNA);
            controller.elaboraRichiesta(creaBeanValido());
            Cliente c = (Cliente) Sessione.getInstance().getUtente();
            assertEquals(StatoRichiesta.PENDING, c.getStatoRichiesta());
        }

        private void impostaSessione(StatoAssociazione statoAssoc,
                                     StatoRichiesta statoRichiesta) {
            Cliente c = new Cliente("Test", "Atleta", CLIENTE_EMAIL, "pwd");
            c.setStatoAssociazione(statoAssoc);
            c.setStatoRichiesta(statoRichiesta);
            if (statoAssoc != StatoAssociazione.NESSUNA) {
                c.setIdPersonalTrainer(PT_EMAIL);
            }
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

    //  RichiestaDAOMemory

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
        @DisplayName("esisteRichiestaAttiva: cliente con richiesta PENDING → true")
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

        @Test
        @DisplayName("aggiornaStato: PENDING → IN_LAVORAZIONE")
        void testAggiornaStato() throws DAOException {
            dao.salvaRichiesta(creaRichiesta("cli@test.it", "pt@test.it", "Forza"));
            dao.aggiornaStato("cli@test.it", StatoRichiesta.IN_LAVORAZIONE);
            RichiestaScheda r = dao.prendiTutteLeRichieste().get(0);
            assertEquals(StatoRichiesta.IN_LAVORAZIONE, r.getStato());
        }

        @Test
        @DisplayName("prendiRichiestePerPTEStato: filtra per stato PENDING")
        void testPrendiPerPTEStato() throws DAOException {
            dao.salvaRichiesta(creaRichiesta("a@test.it", "pt@test.it", "Forza"));
            dao.salvaRichiesta(creaRichiesta("b@test.it", "pt@test.it", "Massa"));
            dao.aggiornaStato("b@test.it", StatoRichiesta.IN_LAVORAZIONE);
            List<RichiestaScheda> pending =
                    dao.prendiRichiestePerPTEStato("pt@test.it", StatoRichiesta.PENDING);
            assertEquals(1, pending.size());
            assertEquals("a@test.it", pending.get(0).getClienteEmail());
        }

        private RichiestaScheda creaRichiesta(String emailCliente,
                                               String emailPT,
                                               String obiettivo) {
            DatiFisici df = new DatiFisici("M", 25, 75);
            return new RichiestaScheda(df, obiettivo, 3, "Note", emailCliente, emailPT);
        }
    }
}

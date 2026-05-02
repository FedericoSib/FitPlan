package controller.graphic;

import bean.AssociazioneBean;
import model.dao.AssociazioneDAOMemory;
import model.dao.DAOFactory;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per GestisciRichiestePTController.
 *
 * Casi coperti:
 *  - getRichiesteSospese: PT senza richieste → lista vuota
 *  - getRichiesteSospese: PT con richieste PENDING → lista valorizzata
 *  - accettaAssociazione → stato aggiornato a ASSOCIATO
 *  - rifiutaAssociazione → stato aggiornato a NESSUNA
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GestisciRichiestePTControllerTest {

    private GestisciRichiestePTController controller;

    private static final String PT_EMAIL      = "pt.gestore@test.it";
    private static final String CLIENTE1_EMAIL = "cliente1@test.it";
    private static final String CLIENTE2_EMAIL = "cliente2@test.it";

    @BeforeAll
    static void setupDAO() {
        DAOFactory.setMode(1);
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new GestisciRichiestePTController();
        // Puliamo le mappe statiche del DAO tra un test e l'altro
        pulisciAssociazioneDAO();
    }

    // ─────────────────────────────────────────────
    //  GET RICHIESTE SOSPESE
    // ─────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("PT senza richieste → lista vuota")
    void testGetRichiesteSospese_ListaVuota() throws DAOException {
        List<AssociazioneBean> richieste = controller.getRichiesteSospese(PT_EMAIL);
        assertNotNull(richieste);
        assertTrue(richieste.isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("PT con 2 richieste PENDING → lista con 2 elementi")
    void testGetRichiesteSospese_ConRichieste() throws DAOException {
        // Salviamo direttamente nel DAO memory
        AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
        dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);
        dao.salvaRichiesta(CLIENTE2_EMAIL, PT_EMAIL);

        List<AssociazioneBean> richieste = controller.getRichiesteSospese(PT_EMAIL);

        assertEquals(2, richieste.size());
        // Verifica che ogni bean abbia i campi valorizzati correttamente
        richieste.forEach(b -> {
            assertEquals(PT_EMAIL, b.getEmailPT());
            assertEquals(StatoAssociazione.PENDING.name(), b.getStato());
            assertNotNull(b.getEmailCliente());
        });
    }

    @Test
    @Order(3)
    @DisplayName("Richiesta di altro PT non viene restituita")
    void testGetRichiesteSospese_FiltroPerPT() throws DAOException {
        AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
        dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);
        dao.salvaRichiesta(CLIENTE2_EMAIL, "altroPT@test.it"); // va ignorata

        List<AssociazioneBean> richieste = controller.getRichiesteSospese(PT_EMAIL);

        assertEquals(1, richieste.size());
        assertEquals(CLIENTE1_EMAIL, richieste.get(0).getEmailCliente());
    }

    // ─────────────────────────────────────────────
    //  ACCETTA ASSOCIAZIONE
    // ─────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("accettaAssociazione → stato diventa ASSOCIATO nel DAO")
    void testAccettaAssociazione() throws DAOException {
        // Setup: salviamo una richiesta pending
        AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
        dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);

        // Costruiamo il bean come farebbe la Boundary
        AssociazioneBean bean = new AssociazioneBean();
        bean.setEmailCliente(CLIENTE1_EMAIL);
        bean.setEmailPT(PT_EMAIL);

        controller.accettaAssociazione(bean);

        // Verifica: il DAO deve aver aggiornato lo stato
        assertEquals(StatoAssociazione.ASSOCIATO, dao.getStato(CLIENTE1_EMAIL));
    }

    // ─────────────────────────────────────────────
    //  RIFIUTA ASSOCIAZIONE
    // ─────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("rifiutaAssociazione → stato diventa NESSUNA nel DAO")
    void testRifiutaAssociazione() throws DAOException {
        AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
        dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);

        AssociazioneBean bean = new AssociazioneBean();
        bean.setEmailCliente(CLIENTE1_EMAIL);
        bean.setEmailPT(PT_EMAIL);

        controller.rifiutaAssociazione(bean);

        assertEquals(StatoAssociazione.NESSUNA, dao.getStato(CLIENTE1_EMAIL));
    }

    @Test
    @Order(6)
    @DisplayName("Dopo rifiuto, la richiesta non appare più in getRichiesteSospese")
    void testRifiutaAssociazione_ScompareRichieste() throws DAOException {
        AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
        dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);

        AssociazioneBean bean = new AssociazioneBean();
        bean.setEmailCliente(CLIENTE1_EMAIL);
        bean.setEmailPT(PT_EMAIL);
        controller.rifiutaAssociazione(bean);

        // Dopo il rifiuto lo stato è NESSUNA → non deve più comparire tra le PENDING
        List<AssociazioneBean> richieste = controller.getRichiesteSospese(PT_EMAIL);
        assertTrue(richieste.isEmpty());
    }

    // ─────────────────────────────────────────────
    //  HELPER: pulizia mappe statiche del DAO Memory
    // ─────────────────────────────────────────────

    private void pulisciAssociazioneDAO() throws Exception {
        java.lang.reflect.Field richiesteField =
                AssociazioneDAOMemory.class.getDeclaredField("richiestePT");
        richiesteField.setAccessible(true);
        ((java.util.Map<?, ?>) richiesteField.get(null)).clear();

        java.lang.reflect.Field statiField =
                AssociazioneDAOMemory.class.getDeclaredField("stati");
        statiField.setAccessible(true);
        ((java.util.Map<?, ?>) statiField.get(null)).clear();
    }
}

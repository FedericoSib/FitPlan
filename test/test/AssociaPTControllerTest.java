package controller.graphic;

import bean.AssociazioneBean;
import bean.PersonalTrainerBean;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import model.exception.TrainerNotFoundException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per AssociaPTController.
 *
 * NOTA: PersonalTrainerDAOMemory parte con lista vuota → aggiungiamo PT
 * direttamente tramite il DAO prima dei test che richiedono ricerca.
 *
 * Casi coperti:
 *  - Ricerca per ID (prefisso PT-)
 *  - Ricerca per Email (regex email)
 *  - Ricerca per Nome
 *  - Nessun risultato → TrainerNotFoundException
 *  - Invio richiesta associazione → stato PENDING in sessione
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AssociaPTControllerTest {

    private AssociaPTController controller;

    // PT di test pre-caricato
    private static final String PT_EMAIL  = "trainer@gym.it";
    private static final String PT_ID     = "PT-001";
    private static final String PT_NOME   = "Giorgio";
    private static final String PT_COGN   = "Ferro";
    private static final String CLIENTE_EMAIL = "cliente@test.it";

    @BeforeAll
    static void setupDAO() {
        DAOFactory.setMode(1);
        // Aggiunge un PT nella lista statica del DAO memory tramite il DAO stesso
        // Usiamo PersonalTrainerDAOMemory indirettamente: salviamo via reflection
        // oppure semplicemente pre-carichiamo con un metodo pubblico del DAO.
        // Siccome il DAO non ha "salva", creiamo il PT e lo aggiungiamo via UtenteDAO
        // (che in realtà non è nel PersonalTrainerDAO), quindi usiamo UtenteDAOMemory
        // per aggiungere un PT che poi PersonalTrainerDAOMemory troverà dalla sua lista.
        //
        // La soluzione corretta con la tua architettura è testare la ricerca
        // rispetto alla lista pre-esistente del PersonalTrainerDAOMemory.
        // Poiché quella lista è statica e inizia vuota, i test di ricerca
        // copriranno il percorso "lista vuota → TrainerNotFoundException".
        // Per il test di ricerca positiva occorre esporre un metodo di seed
        // oppure usare la lista statica via reflection (vedi sotto).
    }

    @BeforeEach
    void setUp() {
        controller = new AssociaPTController();

        // Prepara sessione con un Cliente
        Cliente c = new Cliente("Test", "Cliente", CLIENTE_EMAIL, "pwd");
        c.setStatoAssociazione(StatoAssociazione.NESSUNA);
        Sessione.getInstance().setUtente(c);
    }

    // ─────────────────────────────────────────────
    //  RICERCA — lista vuota (stato iniziale del DAO Memory)
    // ─────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Ricerca per ID inesistente → TrainerNotFoundException")
    void testCercaPerIdNonEsistente() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("PT-999"));
    }

    @Test
    @Order(2)
    @DisplayName("Ricerca per Email inesistente → TrainerNotFoundException")
    void testCercaPerEmailNonEsistente() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("nessuno@gym.it"));
    }

    @Test
    @Order(3)
    @DisplayName("Ricerca per Nome inesistente → TrainerNotFoundException")
    void testCercaPerNomeNonEsistente() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("NomeInesistente"));
    }

    // ─────────────────────────────────────────────
    //  DISCRIMINAZIONE DEL TIPO DI RICERCA (branch coverage)
    // ─────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Stringa con prefisso 'PT-' minuscolo → branch ID (case insensitive)")
    void testCercaConPrefissoPtMinuscolo() {
        // Anche "pt-001" deve entrare nel branch ID
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("pt-001"));
    }

    @Test
    @Order(5)
    @DisplayName("Stringa con formato email → entra nel branch email")
    void testCercaBranchEmail() {
        // Deve entrare nel branch email (anche se non trova nulla)
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("qualcuno@dominio.com"));
    }

    @Test
    @Order(6)
    @DisplayName("Stringa senza @ e senza 'PT-' → entra nel branch nome")
    void testCercaBranchNome() {
        assertThrows(TrainerNotFoundException.class,
                () -> controller.cercaTrainer("SoloNome"));
    }

    // ─────────────────────────────────────────────
    //  RICERCA POSITIVA — aggiungiamo PT con reflection
    // ─────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("Ricerca per Email esistente → lista con 1 bean")
    void testCercaPerEmailEsistente() throws Exception {
        // Aggiungiamo un PT alla lista statica del PersonalTrainerDAOMemory via reflection
        PersonalTrainer pt = new PersonalTrainer(PT_NOME, PT_COGN, PT_EMAIL, "pwd");

        java.lang.reflect.Field field = model.dao.PersonalTrainerDAOMemory.class
                .getDeclaredField("listaPT");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<PersonalTrainer> lista =
                (java.util.List<PersonalTrainer>) field.get(null);
        lista.clear();
        lista.add(pt);

        List<PersonalTrainerBean> risultati = controller.cercaTrainer(PT_EMAIL);

        assertEquals(1, risultati.size());
        assertEquals(PT_EMAIL, risultati.get(0).getEmail());
    }

    @Test
    @Order(8)
    @DisplayName("Ricerca per Nome esistente → lista non vuota")
    void testCercaPerNomeEsistente() throws Exception {
        // Lista già popolata dal test precedente (stessa JVM, campo statico)
        List<PersonalTrainerBean> risultati = controller.cercaTrainer(PT_NOME);
        assertFalse(risultati.isEmpty());
        assertEquals(PT_NOME, risultati.get(0).getNome());
    }

    @Test
    @Order(9)
    @DisplayName("Ricerca per ID esistente → lista con 1 bean")
    void testCercaPerIdEsistente() throws Exception {
        // Il campo id viene assegnato automaticamente nel costruttore di PersonalTrainer
        // Recuperiamo l'id reale dall'entità nella lista
        java.lang.reflect.Field field = model.dao.PersonalTrainerDAOMemory.class
                .getDeclaredField("listaPT");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<PersonalTrainer> lista =
                (java.util.List<PersonalTrainer>) field.get(null);

        String idReale = lista.get(0).getId(); // es. "PT-0001"
        List<PersonalTrainerBean> risultati = controller.cercaTrainer(idReale);
        assertFalse(risultati.isEmpty());
    }

    // ─────────────────────────────────────────────
    //  INVIO RICHIESTA ASSOCIAZIONE
    // ─────────────────────────────────────────────

    @Test
    @Order(10)
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
}

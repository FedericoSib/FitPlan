package test;

import bean.AssociazioneBean;
import bean.PersonalTrainerBean;
import controller.AssociaPTController;
import controller.GestisciRichiestePTController;
import model.Sessione;
import model.dao.AssociazioneDAOMemory;
import model.dao.DAOFactory;
import model.dao.PersonalTrainerDAOMemory;
import model.entity.Cliente;
import model.entity.PersonalTrainer;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import model.exception.TrainerNotFoundException;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AssociazionePTTest {

    @Test
    @DisplayName("Suite associazione PT caricata correttamente")
    void testSuiteCaricata() {
        assertTrue(true);
    }
    //  AssociaPTController
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
            assertThrows(TrainerNotFoundException.class,
                    () -> controller.cercaTrainer("PT-999"));
        }

        @Test
        @DisplayName("Ricerca per Email inesistente → TrainerNotFoundException")
        void testCercaPerEmailNonEsistente() {
            assertThrows(TrainerNotFoundException.class,
                    () -> controller.cercaTrainer("nessuno@gym.it"));
        }

        @Test
        @DisplayName("Ricerca per Nome inesistente → TrainerNotFoundException")
        void testCercaPerNomeNonEsistente() {
            assertThrows(TrainerNotFoundException.class,
                    () -> controller.cercaTrainer("NomeInesistente"));
        }

        @Test
        @DisplayName("Prefisso 'pt-' minuscolo → branch ID (toUpperCase)")
        void testCercaConPrefissoPtMinuscolo() {
            assertThrows(TrainerNotFoundException.class,
                    () -> controller.cercaTrainer("pt-001"));
        }

        @Test
        @DisplayName("Stringa con formato email → branch email")
        void testCercaBranchEmail() {
            assertThrows(TrainerNotFoundException.class,
                    () -> controller.cercaTrainer("qualcuno@dominio.com"));
        }

        @Test
        @DisplayName("Stringa senza '@' e senza 'PT-' → branch nome")
        void testCercaBranchNome() {
            assertThrows(TrainerNotFoundException.class,
                    () -> controller.cercaTrainer("SoloNome"));
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
        @DisplayName("Ricerca per prefisso parziale → tutti i PT con quel prefisso")
        void testCercaPerPrefissoParziale() throws Exception {
            aggiungiPTInLista(new PersonalTrainer("Giulia", PT_COGN, "giulia@gym.it", "pwd"));
            aggiungiPTInLista(new PersonalTrainer("Giulio", "Bianchi", "giulio@gym.it", "pwd"));
            List<PersonalTrainerBean> risultati = controller.cercaTrainer("giu");
            assertEquals(2, risultati.size());
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
    //  GestisciRichiestePTController
    @Nested
    @DisplayName("GestisciRichiestePTController")
    class GestisciRichiestePTControllerTest {

        private GestisciRichiestePTController controller;

        private static final String PT_EMAIL       = "pt.gestore@test.it";
        private static final String CLIENTE1_EMAIL = "cliente1@test.it";
        private static final String CLIENTE2_EMAIL = "cliente2@test.it";

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
            controller.accettaAssociazione(creaBean(CLIENTE1_EMAIL, PT_EMAIL));
            assertEquals(StatoAssociazione.ASSOCIATO, dao.getStato(CLIENTE1_EMAIL));
        }

        @Test
        @DisplayName("rifiutaAssociazione → stato diventa NESSUNA")
        void testRifiutaAssociazione() throws DAOException {
            AssociazioneDAOMemory dao = new AssociazioneDAOMemory();
            dao.salvaRichiesta(CLIENTE1_EMAIL, PT_EMAIL);
            controller.rifiutaAssociazione(creaBean(CLIENTE1_EMAIL, PT_EMAIL));
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
            ((Map<?, ?>) f.get(null)).clear();
        }
    }
}

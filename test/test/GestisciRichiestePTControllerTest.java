package test;
import bean.AssociazioneBean;
import controller.graphic.GestisciRichiestePTController;
import model.dao.DAOFactory;
import model.entity.StatoAssociazione;
import model.exception.DAOException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per GestisciRichiestePTController.
 * Usa DAOFactory in modalità DEMO (in-memory).
 *
 * Casi coperti:
 *  - getRichiesteSospese: PT senza richieste       → lista vuota
 *  - getRichiesteSospese: PT con richieste PENDING → bean corretti
 *  - getRichiesteSospese: richieste già accettate non appaiono
 *  - accettaAssociazione: stato diventa ASSOCIATO nel DAO
 *  - rifiutaAssociazione: stato diventa NESSUNA nel DAO
 *  - Flusso completo: richiesta → accetta → non più nelle sospese
 *  - Flusso completo: richiesta → rifiuta → non più nelle sospese
 */
class GestisciRichiestePTControllerTest {

    private GestisciRichiestePTController controller;

    @BeforeEach
    void setUp() {
        DAOFactory.setMode(1);
        controller = new GestisciRichiestePTController();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // getRichiesteSospese
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PT senza richieste → lista vuota")
    void getRichieste_nessuna() throws DAOException {
        List<AssociazioneBean> risultato = controller.getRichiesteSospese("pt_vuoto@fitplan.it");
        assertNotNull(risultato);
        assertTrue(risultato.isEmpty());
    }

    @Test
    @DisplayName("PT con 2 richieste PENDING → restituisce 2 bean con dati corretti")
    void getRichieste_duePending() throws DAOException {
        // Salviamo 2 richieste direttamente nel DAO
        DAOFactory.getAssociazioneDAO().salvaRichiesta("c1@test.it", "pt@fitplan.it");
        DAOFactory.getAssociazioneDAO().salvaRichiesta("c2@test.it", "pt@fitplan.it");

        List<AssociazioneBean> risultato = controller.getRichiesteSospese("pt@fitplan.it");

        assertEquals(2, risultato.size());
        // Tutti con stato PENDING e emailPT corretta
        risultato.forEach(b -> {
            assertEquals("pt@fitplan.it", b.getEmailPT());
            assertEquals(StatoAssociazione.PENDING.name(), b.getStato());
        });
    }

    @Test
    @DisplayName("Richiesta già accettata non appare tra le sospese")
    void getRichieste_nonMostraAccettate() throws DAOException {
        DAOFactory.getAssociazioneDAO().salvaRichiesta("c1@test.it", "pt@fitplan.it");
        // Accettiamo subito la richiesta
        DAOFactory.getAssociazioneDAO().aggiornaStato("c1@test.it", StatoAssociazione.ASSOCIATO);

        List<AssociazioneBean> risultato = controller.getRichiesteSospese("pt@fitplan.it");
        assertTrue(risultato.isEmpty());
    }

    @Test
    @DisplayName("Richieste di PT diversi non si mescolano")
    void getRichieste_isolamentoPT() throws DAOException {
        DAOFactory.getAssociazioneDAO().salvaRichiesta("c1@test.it", "pt1@fitplan.it");
        DAOFactory.getAssociazioneDAO().salvaRichiesta("c2@test.it", "pt2@fitplan.it");

        List<AssociazioneBean> risultatoPT1 = controller.getRichiesteSospese("pt1@fitplan.it");
        assertEquals(1, risultatoPT1.size());
        assertEquals("c1@test.it", risultatoPT1.get(0).getEmailCliente());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // accettaAssociazione
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Accetta → stato nel DAO diventa ASSOCIATO")
    void accetta_statoDiventaAssociato() throws DAOException {
        DAOFactory.getAssociazioneDAO().salvaRichiesta("c1@test.it", "pt@fitplan.it");

        AssociazioneBean bean = buildBean("c1@test.it", "pt@fitplan.it");
        controller.accettaAssociazione(bean);

        StatoAssociazione stato = DAOFactory.getAssociazioneDAO().getStato("c1@test.it");
        assertEquals(StatoAssociazione.ASSOCIATO, stato);
    }

    @Test
    @DisplayName("Flusso completo: richiesta → accetta → scompare dalle sospese")
    void accetta_nonApparePiuNelleSospese() throws DAOException {
        DAOFactory.getAssociazioneDAO().salvaRichiesta("c1@test.it", "pt@fitplan.it");

        AssociazioneBean bean = buildBean("c1@test.it", "pt@fitplan.it");
        controller.accettaAssociazione(bean);

        List<AssociazioneBean> sospese = controller.getRichiesteSospese("pt@fitplan.it");
        assertTrue(sospese.isEmpty());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // rifiutaAssociazione
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Rifiuta → stato nel DAO diventa NESSUNA")
    void rifiuta_statoDiventaNessuna() throws DAOException {
        DAOFactory.getAssociazioneDAO().salvaRichiesta("c1@test.it", "pt@fitplan.it");

        AssociazioneBean bean = buildBean("c1@test.it", "pt@fitplan.it");
        controller.rifiutaAssociazione(bean);

        StatoAssociazione stato = DAOFactory.getAssociazioneDAO().getStato("c1@test.it");
        assertEquals(StatoAssociazione.NESSUNA, stato);
    }

    @Test
    @DisplayName("Flusso completo: richiesta → rifiuta → scompare dalle sospese")
    void rifiuta_nonApparePiuNelleSospese() throws DAOException {
        DAOFactory.getAssociazioneDAO().salvaRichiesta("c1@test.it", "pt@fitplan.it");

        AssociazioneBean bean = buildBean("c1@test.it", "pt@fitplan.it");
        controller.rifiutaAssociazione(bean);

        List<AssociazioneBean> sospese = controller.getRichiesteSospese("pt@fitplan.it");
        assertTrue(sospese.isEmpty());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────────────────

    private AssociazioneBean buildBean(String emailCliente, String emailPT) {
        AssociazioneBean b = new AssociazioneBean();
        b.setEmailCliente(emailCliente);
        b.setEmailPT(emailPT);
        return b;
    }
}

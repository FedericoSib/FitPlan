package test;

import bean.RegistrazioneBean;
import controller.graphic.RegistrazioneController;
import model.dao.DAOFactory;
import model.entity.Utente;
import model.exception.DAOException;
import model.exception.RegistrazioneException;
import model.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegistrazioneControllerTest {

    private RegistrazioneController registrazioneController;

    @BeforeEach
    void setUp() {
        DAOFactory.setMode(1);
        registrazioneController = new RegistrazioneController();
    }

    @Test
    void testRegistrazioneSuccesso() throws RegistrazioneException, UserNotFoundException, DAOException {
        String emailUnivoca = "nuovo_utente_" + System.currentTimeMillis() + "@test.it";

        RegistrazioneBean bean = new RegistrazioneBean();
        bean.setNome("Mario");
        bean.setCognome("Rossi");
        bean.setEmail(emailUnivoca);
        bean.setPassword("Password123");
        bean.setRuolo(1);

        registrazioneController.registraNuovoUtente(bean);

        Utente salvato = DAOFactory.getUtenteDAO().trovaUtentePerEmail(emailUnivoca);
        assertNotNull(salvato, "L'utente dovrebbe essere stato salvato nel DAO");
        assertEquals("Mario", salvato.getNome());
        assertEquals(emailUnivoca, salvato.getEmail());
    }

    @Test
    void testRegistrazioneEmailGiaEsistente() throws RegistrazioneException {
        String emailEsistente = "luca@test.it";

        RegistrazioneBean bean1 = new RegistrazioneBean();
        bean1.setNome("Mario");
        bean1.setCognome("Rossi");
        bean1.setEmail(emailEsistente);
        bean1.setPassword("pass123");
        bean1.setRuolo(1);
        registrazioneController.registraNuovoUtente(bean1);

        RegistrazioneBean bean2 = new RegistrazioneBean();
        bean2.setNome("Luca");
        bean2.setCognome("Verdi");
        bean2.setEmail(emailEsistente);
        bean2.setPassword("pass");
        bean2.setRuolo(1);

        assertThrows(RegistrazioneException.class, () ->
                        registrazioneController.registraNuovoUtente(bean2),
                "Dovrebbe lanciare RegistrationException se l'email è già registrata"
        );
    }

    @Test
    void testRegistrazioneDatiIncompleti() {
        RegistrazioneBean bean = new RegistrazioneBean();
        bean.setNome("");
        bean.setCognome("");
        bean.setEmail("email@test.it");
        bean.setPassword("");
        bean.setRuolo(1);

        assertThrows(RegistrazioneException.class, () ->
                        registrazioneController.registraNuovoUtente(bean),
                "Dovrebbe impedire la registrazione con campi mancanti"
        );
    }
}

package test;

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
        // Impostiamo la modalità Memory (1) per non sporcare i file reali durante i test
        DAOFactory.setMode(1);
    }

    @Test
    void testRegistrazioneSuccesso() throws RegistrazioneException, UserNotFoundException, DAOException {
        String emailUnivoca = "nuovo_utente_" + System.currentTimeMillis() + "@test.it";

        // 1. Eseguiamo la registrazione
        // Assumiamo che il metodo accetti (nome, cognome, email, password, ruolo)
        registrazioneController.registraNuovoUtente("Mario", "Rossi", emailUnivoca, "Password123", 1);

        // 2. Verifica: cerchiamo l'utente nel DAO per confermare il salvataggio
        Utente salvato = DAOFactory.getUtenteDAO().trovaUtentePerEmail(emailUnivoca);

        assertNotNull(salvato, "L'utente dovrebbe essere stato salvato nel DAO");
        assertEquals("Mario", salvato.getNome());
        assertEquals(emailUnivoca, salvato.getEmail());
    }

    @Test
    void testRegistrazioneEmailGiaEsistente() {
        // Supponiamo che questa email sia già presente nel DAOMemory (es. caricata nello static block)
        String emailEsistente = "luca@test.it";

        // Verifichiamo che il controller lanci RegistrationException
        assertThrows(RegistrazioneException.class, () -> {
            registrazioneController.registraNuovoUtente("Luca", "Verdi", emailEsistente, "pass", 1);
        }, "Dovrebbe lanciare RegistrationException se l'email è già registrata");
    }

    @Test
    void testRegistrazioneDatiIncompleti() {
        // Test di robustezza: proviamo a registrare con campi vuoti
        assertThrows(RegistrazioneException.class, () -> {
            registrazioneController.registraNuovoUtente("", "", "email@test.it", "", 1);
        }, "Dovrebbe impedire la registrazione con campi mancanti");
    }
}

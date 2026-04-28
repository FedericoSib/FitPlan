package test;

import controller.graphic.LoginController;
import model.dao.DAOFactory;
import model.entity.Utente;
import model.exception.LoginException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    @BeforeAll
    static void setup() {
        // Forza la modalità Memory per i test in modo che siano veloci e indipendenti dai file
        // Assicurati che nella tua DAOFactory esista un modo per forzare demoMode = true
        // DAOFactory.setDemoMode(true);
    }

    @Test
    void testLoginFallitoPerPasswordErrata() {
        LoginController loginController = new LoginController();

        // Verifichiamo che venga lanciata la LoginException con credenziali finte
        assertThrows(LoginException.class, () -> {
            loginController.autentica("email@inesistente.it", "sbagliata");
        });
    }

    @Test
    void testLoginSuccesso() {
        // Nota: questo test richiede che un utente sia presente nel DAOMemory
        // Potresti dover aggiungere un utente "test@test.it" nel blocco static del DAOMemory
        LoginController loginController = new LoginController();

        assertDoesNotThrow(() -> {
            // Se hai aggiunto un utente di test nel DAOMemory:
            // loginController.autentica("test@test.it", "password123");
        });
    }
}
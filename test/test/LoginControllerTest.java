package test;

import controller.graphic.LoginController;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.Utente;
import model.exception.LoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
        DAOFactory.setMode(1); // Forza Memory per i test
    }

    @Test
    void testLoginSuccesso() throws LoginException {
        // Supponendo che il PT PeppeZ sia nel tuo DAOMemory
        loginController.autentica("PeppeZ@gmail.com", "Peppez");

        Utente loggato = Sessione.getInstance().getUtente();
        assertNotNull(loggato);
        assertEquals("PeppeZ@gmail.com", loggato.getEmail());
    }

    @Test
    void testLoginCredenzialiErrate() {
        assertThrows(LoginException.class, () -> {
            loginController.autentica("PeppeZ@gmail.com", "password_sbagliata");
        }, "Dovrebbe lanciare LoginException per password errata");
    }
}
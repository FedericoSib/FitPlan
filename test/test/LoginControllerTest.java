package test;

import controller.graphic.LoginController;
import model.Sessione;
import model.dao.DAOFactory;
import model.entity.*;
import model.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setUp() throws DAOException {
        DAOFactory.setMode(1);
        loginController = new LoginController();

        // Aggiungi l'utente che userai nel test
        PersonalTrainer peppe = new PersonalTrainer("Peppe", "Z", "PeppeZ@gmail.com", "Peppez");
        DAOFactory.getUtenteDAO().salvaNuovoUtente(peppe);
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
package test;

import bean.LoginBean;
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

        PersonalTrainer peppe = new PersonalTrainer("Peppe", "Z", "PeppeZ@gmail.com", "Peppez");
        DAOFactory.getUtenteDAO().salvaNuovoUtente(peppe);
    }

    @Test
    void testLoginSuccesso() throws LoginException {
        LoginBean bean = new LoginBean();
        bean.setEmail("PeppeZ@gmail.com");
        bean.setPassword("Peppez");

        loginController.autentica(bean);

        Utente loggato = Sessione.getInstance().getUtente();
        assertNotNull(loggato);
        assertEquals("PeppeZ@gmail.com", loggato.getEmail());
    }

    @Test
    void testLoginCredenzialiErrate() {
        LoginBean bean = new LoginBean();
        bean.setEmail("PeppeZ@gmail.com");
        bean.setPassword("password_sbagliata");

        assertThrows(LoginException.class, () ->
                        loginController.autentica(bean),
                "Dovrebbe lanciare LoginException per password errata"
        );
    }
}
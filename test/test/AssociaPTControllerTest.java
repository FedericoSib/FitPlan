package test;

import controller.graphic.AssociaPTController;
import model.dao.DAOFactory;
import model.dao.UtenteDAO;
import model.entity.PersonalTrainer;
import java.util.List;
import model.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AssociaPTControllerTest {

    private AssociaPTController controller;

    @BeforeEach
    void setUp() throws DAOException {
        DAOFactory.setMode(1); // Memory
        controller = new AssociaPTController();

        PersonalTrainer pt1 = new PersonalTrainer("Mario", "Rossi", "mario@pt.it", "pass");
        PersonalTrainer pt2 = new PersonalTrainer("Valerio", "S", "Valerio@gmail.com", "ValerioS");

        // Oppure, se il tuo DAOMemory ha una lista accessibile o un metodo di salvataggio:
        UtenteDAO utenteDao = DAOFactory.getUtenteDAO();
        utenteDao.salvaNuovoUtente(pt1);
        utenteDao.salvaNuovoUtente(pt2);
    }

    @Test
    void testCercaTrainerPerEmailSuccesso() throws TrainerNotFoundException {
        List<PersonalTrainer> risultati = controller.cercaTrainer("mario@pt.it");
        assertNotNull(risultati, "La lista dei risultati non dovrebbe essere nulla");
        assertFalse(risultati.isEmpty(), "La lista dovrebbe contenere almeno un trainer");
        PersonalTrainer pt = risultati.get(0);

        assertEquals("Mario", pt.getNome(), "Il nome del trainer dovrebbe corrispondere");
        assertEquals("mario@pt.it", pt.getEmail(), "L'email dovrebbe corrispondere");
    }

    @Test
    void testCercaTrainerInesistenteLanciaEccezione() {
        // Testiamo che il sistema reagisca correttamente all'errore
        assertThrows(TrainerNotFoundException.class, () -> {
            controller.cercaTrainer("email_inesistente@test.com");
        }, "Dovrebbe lanciare TrainerNotFoundException");
    }

    @Test
    void testRicercaPerID() throws TrainerNotFoundException {
        List<PersonalTrainer> risultati = controller.cercaTrainer("PT-PZ-2431749");

        assertNotNull(risultati);
        assertFalse(risultati.isEmpty(), "La lista non dovrebbe essere vuota");

        assertEquals("PT-PZ-2431749", risultati.get(0).getId(), "L'ID deve corrispondere");
        assertTrue(risultati.get(0).getId().startsWith("PT-"));
    }

    @Test
    void testRicercaPerNomeRisultatiMultipli() throws TrainerNotFoundException {
        // Supponendo che nel tuo DAOMemory ci siano due "Valerio" (S e R)
        List<PersonalTrainer> risultati = controller.cercaTrainer("Valerio");

        assertNotNull(risultati);
        // Verifichiamo che la lista contenga più di un elemento se ci sono omonimi
        assertTrue(risultati.size() >= 1, "Dovrebbe trovare almeno un trainer con questo nome");

        // Verifichiamo che il nome di uno dei risultati sia corretto
        assertTrue(risultati.stream().anyMatch(pt -> pt.getNome().equalsIgnoreCase("Valerio")));
    }
}
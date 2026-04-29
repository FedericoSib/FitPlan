package test;

import bean.PersonalTrainerBean;
import controller.graphic.AssociaPTController;
import model.dao.*;
import model.entity.PersonalTrainer;
import java.util.List;
import model.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AssociaPTControllerTest {

    private AssociaPTController controller;

    @BeforeEach
    void setUp() {
        DAOFactory.setMode(1);
        controller = new AssociaPTController();

        PersonalTrainerDAO dao = DAOFactory.getPersonalTrainerDAO();
        dao.getAllPT().clear();
        dao.getAllPT().add(new PersonalTrainer("Mario", "Rossi", "mario@pt.it", "pass"));
        dao.getAllPT().add(new PersonalTrainer("Valerio", "R", "ValerioR@gmail.com", "pass"));
        dao.getAllPT().add(new PersonalTrainer("Valerio", "Sibilano", "valerio.sib@test.it", "pass"));
    }

    @Test
    void testCercaTrainerPerEmailSuccesso() throws TrainerNotFoundException {
        List<PersonalTrainerBean> risultati = controller.cercaTrainer("mario@pt.it");
        assertNotNull(risultati, "La lista dei risultati non dovrebbe essere nulla");
        assertFalse(risultati.isEmpty(), "La lista dovrebbe contenere almeno un trainer");

        PersonalTrainerBean pt = risultati.get(0);
        assertEquals("Mario", pt.getNome(), "Il nome del trainer dovrebbe corrispondere");
        assertEquals("mario@pt.it", pt.getEmail(), "L'email dovrebbe corrispondere");
    }

    @Test
    void testCercaTrainerInesistenteLanciaEccezione() {
        assertThrows(TrainerNotFoundException.class, () ->
                        controller.cercaTrainer("email_inesistente@test.com"),
                "Dovrebbe lanciare TrainerNotFoundException"
        );
    }

    @Test
    void testRicercaPerIDDinamico() throws Exception {
        PersonalTrainer pt = new PersonalTrainer("Valerio", "S", "valerio@test.it", "pass");
        DAOFactory.getUtenteDAO().salvaNuovoUtente(pt);
        String idGenerato = pt.getId();

        List<PersonalTrainerBean> risultati = controller.cercaTrainer(idGenerato);

        assertFalse(risultati.isEmpty());
        assertEquals(idGenerato, risultati.get(0).getId());
    }

    @Test
    void testRicercaPerNomeRisultatiMultipli() throws TrainerNotFoundException {
        List<PersonalTrainerBean> risultati = controller.cercaTrainer("Valerio");

        assertNotNull(risultati);
        assertTrue(risultati.size() >= 1, "Dovrebbe trovare almeno un trainer con questo nome");
        assertTrue(risultati.stream().anyMatch(pt -> pt.getNome().equalsIgnoreCase("Valerio")));
    }
}
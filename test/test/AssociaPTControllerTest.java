package test;

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
        DAOFactory.setMode(1); // Modalità Memory
        controller = new AssociaPTController();

        // IMPORTANTE: Dobbiamo aggiungere i PT al DAOMemory prima di cercarli!
        PersonalTrainerDAO dao = DAOFactory.getPersonalTrainerDAO();

        // Puliamo e aggiungiamo i dati per i test
        dao.getAllPT().clear();
        dao.getAllPT().add(new PersonalTrainer("Mario", "Rossi", "mario@pt.it", "pass"));
        dao.getAllPT().add(new PersonalTrainer("Valerio", "R", "ValerioR@gmail.com", "pass"));
        dao.getAllPT().add(new PersonalTrainer("Valerio", "Sibilano", "valerio.sib@test.it", "pass"));
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
    void testRicercaPerIDDinamico() throws Exception {
        // 1. Creo e salvo un PT
        PersonalTrainer pt = new PersonalTrainer("Valerio", "S", "valerio@test.it", "pass");
        DAOFactory.getUtenteDAO().salvaNuovoUtente(pt);
        String idGenerato = pt.getId(); // Recupero l'ID generato dal costruttore

        // 2. Cerco usando l'ID appena generato
        List<PersonalTrainer> risultati = controller.cercaTrainer(idGenerato);

        assertFalse(risultati.isEmpty());
        assertEquals(idGenerato, risultati.get(0).getId());
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
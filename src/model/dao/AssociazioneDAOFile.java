package model.dao;

import model.entity.StatoAssociazione;
import model.exception.DAOException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AssociazioneDAOFile implements AssociazioneDAO {
    private static final String FILE_NAME = "richieste_associazione.csv";

    @Override
    public void salvaRichiesta(String emailCliente, String emailPT) throws DAOException {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
            out.println(emailCliente + ";" + emailPT + ";" + StatoAssociazione.PENDING.name());
        } catch (IOException _) {
            throw new DAOException("Errore nel salvataggio della richiesta su file");
        }
    }

    @Override
    public StatoAssociazione getStato(String emailCliente) throws DAOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(emailCliente)) {
                    return StatoAssociazione.valueOf(parts[2]);
                }
            }
        } catch (FileNotFoundException _) {
            return StatoAssociazione.NESSUNA;
        } catch (IOException _) {
            throw new DAOException("Errore nella lettura dello stato associazione");
        }
        return StatoAssociazione.NESSUNA;
    }

    @Override
    public void aggiornaStato(String emailCliente, StatoAssociazione nuovoStato) throws DAOException {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(emailCliente)) {
                    lines.add(parts[0] + ";" + parts[1] + ";" + nuovoStato.name());
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException _) {
            throw new DAOException("Errore durante l'aggiornamento dello stato");
        }

        if (found) {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME)))) {
                for (String l : lines) out.println(l);
            } catch (IOException _) {
                throw new DAOException("Errore nella scrittura del file aggiornato");
            }
        }
    }

    @Override
    public String getEmailPTAssociato(String emailCliente) throws DAOException {
        // Logica simile a getStato per recuperare la seconda colonna
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(emailCliente)) return parts[1];
            }
        } catch (IOException _) { return null; }
        return null;
    }
}
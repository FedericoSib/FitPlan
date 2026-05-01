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

    @Override
    public List<String> getRichiestePerPT(String emailPT) throws DAOException {
        List<String> clientiTrovati = new ArrayList<>();
        File file = new File(FILE_NAME);

        // Se il file non esiste ancora, restituiamo semplicemente una lista vuota
        if (!file.exists()) {
            return clientiTrovati;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Formato CSV: emailCliente;emailPT;stato
                String[] parts = line.split(";");

                if (parts.length >= 3) {
                    String clienteInRiga = parts[0];
                    String ptInRiga = parts[1];
                    String statoInRiga = parts[2];

                    // Filtriamo: deve essere il PT loggato e lo stato deve essere PENDING
                    if (ptInRiga.equals(emailPT) && StatoAssociazione.PENDING.name().equals(statoInRiga)) {
                        clientiTrovati.add(clienteInRiga);
                    }
                }
            }
        } catch (IOException e) {
            throw new DAOException("Errore durante la lettura delle richieste per il PT: " + e.getMessage());
        }

        return clientiTrovati;
    }
}
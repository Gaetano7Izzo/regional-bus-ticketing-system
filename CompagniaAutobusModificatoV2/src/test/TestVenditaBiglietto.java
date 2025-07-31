package test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import java.awt.print.PrinterException;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import control.GestioneCompagnia;
import entity.EntityBiglietto;
import entity.EntityImpiegato;
import entity.EntityCorsa;
import database.BigliettoDAO;
import database.CorsaDAO;
import database.ImpiegatoDAO;
import exception.AutenticazioneException;
import exception.CorsaNonTrovataException;
import exception.VenditaBigliettiException;
import util.PrinterManager;
import database.DBManager;

/**
 * Classe di test per verificare i casi di vendita biglietti
 */
public class TestVenditaBiglietto {
    private GestioneCompagnia gestioneCompagnia;
    private ImpiegatoDAO impiegatoDAO;
    private CorsaDAO corsaDAO;
    private BigliettoDAO bigliettoDAO;
    private int idCorsaTest;

    @Before
    public void setUp() throws Exception {
        gestioneCompagnia = GestioneCompagnia.getInstance();
        impiegatoDAO = new ImpiegatoDAO();
        corsaDAO = new CorsaDAO();
        bigliettoDAO = new BigliettoDAO();
        
        // Usiamo la corsa Napoli-Torre del Greco del 1 settembre 2025 (ID 9)
        // Questa corsa è futura e dovrebbe essere vuota
        idCorsaTest = 9;
        System.out.println("\n=== Inizializzazione test vendita biglietti ===");
        System.out.println("Corsa di test: ID " + idCorsaTest + " (Napoli-Torre del Greco)");
        
        // Verifica che la corsa esista e sia vuota
        EntityCorsa corsa = corsaDAO.readCorsa(idCorsaTest);
        assertNotNull("La corsa di test dovrebbe esistere", corsa);
        System.out.println("✅ Corsa trovata nel database");
        System.out.println("Data corsa: " + corsa.getData());
        System.out.println("Orario: " + corsa.getOrario());
        System.out.println("Prezzo: " + corsa.getPrezzo() + "€");
        
        // Verifica che non ci siano biglietti venduti
        List<EntityBiglietto> bigliettiEsistenti = bigliettoDAO.getBigliettiByCorsa(idCorsaTest);
        assertEquals("La corsa dovrebbe essere vuota", 0, bigliettiEsistenti.size());
        System.out.println("✅ Corsa verificata vuota");
    }

    @Test
    public void testVenditaRiuscitaSenzaStampa() throws VenditaBigliettiException, CorsaNonTrovataException, AutenticazioneException {
        System.out.println("\n--- Test: Vendita biglietto senza stampa ---");
        
        // Autenticazione impiegato ID 5
        System.out.println("Tentativo di autenticazione impiegato ID: 5");
        gestioneCompagnia.setIdImpiegato(5);
        EntityImpiegato impiegato = gestioneCompagnia.autenticazione(5);
        assertNotNull("L'impiegato dovrebbe essere autenticato", impiegato);
        System.out.println("✅ Autenticazione impiegato riuscita");
        
        // Verifica disponibilità posti
        int postiDisponibili = gestioneCompagnia.verificaDisponibilita(idCorsaTest);
        System.out.println("Posti disponibili: " + postiDisponibili);
        assertTrue("Dovrebbero esserci posti disponibili", postiDisponibili > 0);
        
        // Vendita biglietto (1 posto)
        System.out.println("\nTentativo di vendita biglietto (posto 1)");
        List<Integer> postiScelti = new ArrayList<>();
        postiScelti.add(1);
        String risultato = gestioneCompagnia.vendiBiglietto(idCorsaTest, postiScelti);
        
        // Verifica vendita
        assertNotNull("Il risultato della vendita non dovrebbe essere null", risultato);
        assertTrue("La vendita dovrebbe essere riuscita", risultato.contains("Biglietti venduti con successo"));
        System.out.println("✅ Biglietto venduto con successo");
        System.out.println("→ Richiesta di stampa: non richiesta");
        
        // Verifica nel database
        List<EntityBiglietto> biglietti = bigliettoDAO.getBigliettiByCorsa(idCorsaTest);
        assertEquals("Dovrebbe esserci un solo biglietto", 1, biglietti.size());
        EntityBiglietto biglietto = biglietti.get(0);
        assertEquals("L'ID dell'impiegato dovrebbe essere 5", 5, biglietto.getIdImpiegato());
        assertEquals("Il numero del posto dovrebbe essere 1", Integer.valueOf(1), biglietto.getNumPosto());
        System.out.println("✅ Verifica database: biglietto registrato correttamente");
        System.out.println("Codice QR: " + biglietto.getCodiceQR());
        
        // Pulizia
        System.out.println("\nPulizia test...");
        boolean eliminato = bigliettoDAO.deleteBiglietto(biglietto.getCodiceQR());
        assertTrue("Il biglietto dovrebbe essere stato eliminato", eliminato);
        System.out.println("✅ Biglietto eliminato dal database");
        
        // Verifica finale
        biglietti = bigliettoDAO.getBigliettiByCorsa(idCorsaTest);
        assertEquals("La corsa dovrebbe essere di nuovo vuota", 0, biglietti.size());
        System.out.println("✅ Verifica finale: corsa vuota");
    }

    @Test
    public void testVenditaRiuscitaConStampa() throws VenditaBigliettiException, CorsaNonTrovataException, AutenticazioneException, PrinterException {
        System.out.println("\n--- Test: Vendita biglietti con stampa ---");
        
        // Autenticazione impiegato ID 2
        System.out.println("Tentativo di autenticazione impiegato ID: 2");
        gestioneCompagnia.setIdImpiegato(2);
        EntityImpiegato impiegato = gestioneCompagnia.autenticazione(2);
        assertNotNull("L'impiegato dovrebbe essere autenticato", impiegato);
        System.out.println("✅ Autenticazione impiegato riuscita");
        
        // Verifica disponibilità posti
        int postiDisponibili = gestioneCompagnia.verificaDisponibilita(idCorsaTest);
        System.out.println("Posti disponibili: " + postiDisponibili);
        assertTrue("Dovrebbero esserci almeno 2 posti disponibili", postiDisponibili >= 2);
        
        // Vendita biglietti (2 posti)
        System.out.println("\nTentativo di vendita biglietti (posti 1 e 2)");
        List<Integer> postiScelti = new ArrayList<>();
        postiScelti.add(1);
        postiScelti.add(2);
        String risultato = gestioneCompagnia.vendiBiglietto(idCorsaTest, postiScelti);
        
        // Verifica vendita
        assertNotNull("Il risultato della vendita non dovrebbe essere null", risultato);
        assertTrue("La vendita dovrebbe essere riuscita", risultato.contains("Biglietti venduti con successo"));
        System.out.println("✅ Biglietti venduti con successo");
        
        // Verifica nel database
        List<EntityBiglietto> biglietti = bigliettoDAO.getBigliettiByCorsa(idCorsaTest);
        assertEquals("Dovrebbero esserci due biglietti", 2, biglietti.size());
        System.out.println("✅ Verifica database: " + biglietti.size() + " biglietti registrati correttamente");
        
        // Verifica stampa per ogni biglietto
        System.out.println("\nTentativo di stampa biglietti...");
        for (EntityBiglietto biglietto : biglietti) {
            assertEquals("L'ID dell'impiegato dovrebbe essere 2", 2, biglietto.getIdImpiegato());
            assertTrue("Il numero del posto dovrebbe essere 1 o 2", 
                biglietto.getNumPosto() == 1 || biglietto.getNumPosto() == 2);
            
            // Simula la stampa del biglietto
            System.out.println("\nStampa biglietto posto " + biglietto.getNumPosto() + ":");
            System.out.println("Codice QR: " + biglietto.getCodiceQR());
            boolean stampaRiuscita = PrinterManager.simulaStampaBiglietto(biglietto.getCodiceQR(), "Stampante Test");
            if (stampaRiuscita) {
                System.out.println("✅ Richiesta di stampa accettata");
            } else {
                System.out.println("❌ Richiesta di stampa rifiutata");
            }
            assertTrue("La simulazione della stampa dovrebbe avere successo", stampaRiuscita);
            
            // Pulizia del biglietto
            System.out.println("Eliminazione biglietto dal database...");
            boolean eliminato = bigliettoDAO.deleteBiglietto(biglietto.getCodiceQR());
            assertTrue("Il biglietto dovrebbe essere stato eliminato", eliminato);
            System.out.println("✅ Biglietto eliminato");
        }
        
        // Verifica finale
        biglietti = bigliettoDAO.getBigliettiByCorsa(idCorsaTest);
        assertEquals("La corsa dovrebbe essere di nuovo vuota", 0, biglietti.size());
        System.out.println("✅ Verifica finale: corsa vuota");
    }
} 
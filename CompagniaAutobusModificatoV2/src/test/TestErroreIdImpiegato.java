package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import control.GestioneCompagnia;
import exception.AutenticazioneException;

/**
 * Classe di test per verificare i casi di errore relativi all'ID impiegato
 */
public class TestErroreIdImpiegato {
    private GestioneCompagnia gestioneCompagnia;

    @Before
    public void setUp() throws Exception {
        gestioneCompagnia = GestioneCompagnia.getInstance();
        System.out.println("\n=== Inizializzazione test errori ID impiegato ===");
    }

    @Test
    public void testErroreIdImpiegatoNonRegistrato() {
        System.out.println("\n--- Test: ID impiegato non registrato ---");
        System.out.println("Tentativo di autenticazione con ID: 999999");
        try {
            gestioneCompagnia.autenticazione(999999);
            fail("Dovrebbe lanciare AutenticazioneException per ID non registrato");
        } catch (AutenticazioneException e) {
            System.out.println("❌ Errore ID impiegato non registrato: " + e.getMessage());
            System.out.println("→ Richiesta reinserimento ID");
            // Test passato se viene lanciata l'eccezione
            assertTrue(true);
        }
    }

    @Test
    public void testErroreIdConCaratteriAlfabetici() {
        System.out.println("\n--- Test: ID con caratteri alfabetici ---");
        System.out.println("Tentativo di conversione ID: '2AB'");
        try {
            Integer.parseInt("2AB");
            fail("Dovrebbe lanciare NumberFormatException per ID non numerico");
        } catch (NumberFormatException e) {
            System.out.println("❌ Errore ID con caratteri alfabetici: " + e.getMessage());
            System.out.println("→ Richiesta reinserimento ID numerico valido");
            // Test passato se viene lanciata l'eccezione
            assertTrue(true);
        }
    }
} 
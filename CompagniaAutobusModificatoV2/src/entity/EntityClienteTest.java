package entity;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class EntityClienteTest {
    private EntityCliente cliente;

    @Before
    public void setUp() {
        cliente = new EntityCliente();
    }

    // TC-AB-01: Dati validi
    @Test
    public void testValidData() {
        // Email
        cliente.setEmail("mario@gmail.com");
        assertNotNull(cliente.getEmail());
        
        // Numero di telefono
        cliente.setNumTelefono(3331234567L);
        assertEquals(3331234567L, cliente.getNumTelefono());
    }

    // TC-AB-02: Email senza @
    @Test
    public void testInvalidEmailWithoutAt() {
        try {
            cliente.setEmail("mariogmail.com");
            fail("Dovrebbe aver lanciato IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("L'indirizzo email non è valido: deve contenere il carattere '@'", e.getMessage());
        }
    }

    // TC-AB-03: Numero di telefono con più di 10 cifre
    @Test
    public void testInvalidPhoneNumberTooLong() {
        try {
            cliente.setNumTelefono(3331234566559L);
            fail("Dovrebbe aver lanciato IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Il numero di telefono non è valido: deve contenere esattamente 10 cifre", e.getMessage());
        }
    }

    // TC-AB-04: Numero di telefono con lettere
    @Test
    public void testInvalidPhoneNumberWithLetters() {
        try {
            cliente.setNumTelefono(333123456789L); // 12 cifre
            fail("Dovrebbe aver lanciato IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Il numero di telefono non è valido: deve contenere esattamente 10 cifre", e.getMessage());
        }
    }
}

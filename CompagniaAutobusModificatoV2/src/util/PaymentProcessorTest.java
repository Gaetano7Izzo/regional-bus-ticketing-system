package util;

import static org.junit.Assert.*;
import org.junit.Test;
import entity.MetodoPagamento;

public class PaymentProcessorTest {

    @Test
    public void testNumeroCartaNon16Cifre() {
        MetodoPagamento mp = new MetodoPagamento("CARTA");
        mp.setNumeroCarta("12345678123456"); // 14 cifre
        mp.setScadenza("12/25");
        mp.setCvv("123");
        try {
            PaymentProcessor.processaPagamentoCarta(mp, 10.0);
            fail("Doveva lanciare IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Il numero della carta deve essere di 16 cifre.", e.getMessage());
        }
    }

    @Test
    public void testScadenzaFormatoErrato() {
        MetodoPagamento mp = new MetodoPagamento("CARTA");
        mp.setNumeroCarta("1234567812345678");
        mp.setScadenza("2025-06"); // Formato errato
        mp.setCvv("123");
        try {
            PaymentProcessor.processaPagamentoCarta(mp, 10.0);
            fail("Doveva lanciare IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("La scadenza deve essere nel formato MM/AA.", e.getMessage());
        }
    }

    @Test
    public void testCVVNon3Cifre() {
        MetodoPagamento mp = new MetodoPagamento("CARTA");
        mp.setNumeroCarta("1234567812345678");
        mp.setScadenza("12/25");
        mp.setCvv("12"); // Solo 2 cifre
        try {
            PaymentProcessor.processaPagamentoCarta(mp, 10.0);
            fail("Doveva lanciare IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Il CVV deve essere di 3 cifre.", e.getMessage());
        }
    }
}

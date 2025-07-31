package util;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValidazioneUtilsTest {

    @Test
    public void testNumeriUgualiNessunaEccezione() {
        List<Integer> posti = Arrays.asList(1, 2, 3);
        // Non deve lanciare eccezione
        try {
            ValidazioneUtils.verificaPosti(3, posti);
        } catch (IllegalArgumentException e) {
            fail("Non dovrebbe lanciare eccezione se i numeri coincidono");
        }
    }

    @Test
    public void testNumeriDiversiEccezione() {
        List<Integer> posti = Arrays.asList(1, 2);
        try {
            ValidazioneUtils.verificaPosti(3, posti);
            fail("Doveva lanciare IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Il numero di posti selezionati non corrisponde a quello richiesto.", e.getMessage());
        }
    }

    @Test
    public void testEntrambiZeroNessunaEccezione() {
        List<Integer> posti = Collections.emptyList();
        // Non deve lanciare eccezione
        try {
            ValidazioneUtils.verificaPosti(0, posti);
        } catch (IllegalArgumentException e) {
            fail("Non dovrebbe lanciare eccezione se entrambi sono zero");
        }
    }
}
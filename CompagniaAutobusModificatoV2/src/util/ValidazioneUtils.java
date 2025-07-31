package util;

import java.util.List;

public class ValidazioneUtils {
    /**
     * Verifica che il numero di posti desiderati corrisponda al numero di posti selezionati.
     * @param postiDesiderati numero di posti richiesti dall'utente
     * @param postiSelezionati lista dei posti selezionati
     * @throws IllegalArgumentException se i numeri non coincidono
     */
    public static void verificaPosti(int postiDesiderati, List<Integer> postiSelezionati) {
        if (postiDesiderati != (postiSelezionati != null ? postiSelezionati.size() : 0)) {
            throw new IllegalArgumentException("Il numero di posti selezionati non corrisponde a quello richiesto.");
        }
    }
}

package exception;

/**
 * Eccezione lanciata quando si verifica un errore durante la vendita di biglietti.
 * Ad esempio, quando non ci sono posti disponibili per una corsa.
 */
public class VenditaBigliettiException extends Exception {
    
    /**
     * Costruttore con messaggio di errore
     * 
     * @param message messaggio di errore
     */
    public VenditaBigliettiException(String message) {
        super(message);
    }
    
    /**
     * Costruttore con messaggio di errore e causa
     * 
     * @param message messaggio di errore
     * @param cause causa dell'eccezione
     */
    public VenditaBigliettiException(String message, Throwable cause) {
        super(message, cause);
    }
}

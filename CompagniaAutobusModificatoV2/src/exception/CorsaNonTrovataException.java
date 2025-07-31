package exception;

/**
 * Eccezione lanciata quando si verifica un errore durante la ricerca di una corsa.
 * Ad esempio, quando una corsa con un determinato codice non viene trovata.
 */
public class CorsaNonTrovataException extends Exception {
    
    /**
     * Costruttore con messaggio di errore
     * 
     * @param message messaggio di errore
     */
    public CorsaNonTrovataException(String message) {
        super(message);
    }
    
    /**
     * Costruttore con messaggio di errore e causa
     * 
     * @param message messaggio di errore
     * @param cause causa dell'eccezione
     */
    public CorsaNonTrovataException(String message, Throwable cause) {
        super(message, cause);
    }
}

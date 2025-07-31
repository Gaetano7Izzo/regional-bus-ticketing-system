package exception;

/**
 * Eccezione lanciata quando si verifica un errore di autenticazione.
 * Ad esempio, quando un impiegato tenta di accedere con un ID non valido.
 */
public class AutenticazioneException extends Exception {
    
    /**
     * Costruttore con messaggio di errore
     * 
     * @param message messaggio di errore
     */
    public AutenticazioneException(String message) {
        super(message);
    }
    
    /**
     * Costruttore con messaggio di errore e causa
     * 
     * @param message messaggio di errore
     * @param cause causa dell'eccezione
     */
    public AutenticazioneException(String message, Throwable cause) {
        super(message, cause);
    }
}

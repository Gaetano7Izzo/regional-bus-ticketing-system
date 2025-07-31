package entity;

/**
 * Classe che rappresenta l'entità Cliente
 */
public class EntityCliente {
    private long numTelefono;
    private String email;
    
    /**
     * Costruttore vuoto
     */
    public EntityCliente() {
    }
    
    /**
     * Costruttore completo
     * 
     * @param numTelefono Numero di telefono del cliente
     * @param email Email del cliente
     */
    public EntityCliente(long numTelefono, String email) {
        this.numTelefono = numTelefono;
        this.email = email;
    }
    
    /**
     * Restituisce il numero di telefono del cliente
     * 
     * @return Numero di telefono
     */
    public long getNumTelefono() {
        return numTelefono;
    }
    
    /**
     * Imposta il numero di telefono del cliente
     * 
     * @param numTelefono Nuovo numero di telefono
     */
    public void setNumTelefono(long numTelefono) {
        String numStr = Long.toString(numTelefono);
        if (numStr.length() != 10) {
            throw new IllegalArgumentException("Il numero di telefono non è valido: deve contenere esattamente 10 cifre");
        }
        this.numTelefono = numTelefono;
    }
    /**
     * Restituisce l'email del cliente
     * 
     * @return Email del cliente
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Imposta l'email del cliente
     * 
     * @param email Nuova email
     */
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("L'indirizzo email non è valido: deve contenere il carattere '@'");
        }
        this.email = email;
    }
}

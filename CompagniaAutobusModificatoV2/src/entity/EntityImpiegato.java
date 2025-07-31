package entity;

/**
 * Classe che rappresenta l'entità Impiegato
 */
public class EntityImpiegato {
    private int id;
    
    /**
     * Costruttore vuoto
     */
    public EntityImpiegato() {
    }
    
    /**
     * Costruttore con ID
     * 
     * @param id ID dell'impiegato
     */
    public EntityImpiegato(int id) {
        this.id = id;
    }
    
    /**
     * Restituisce l'ID dell'impiegato
     * 
     * @return ID dell'impiegato
     */
    public int getId() {
        return id;
    }
    
    /**
     * Imposta l'ID dell'impiegato
     * 
     * @param id Nuovo ID dell'impiegato
     */
    public void setId(int id) {
        this.id = id;
    }
}

package entity;

/**
 * Classe che rappresenta l'entità Autobus
 */
public class EntityAutobus {
    private int id;
    private int capienza;
    private String trattaAssegnata;
    
    /**
     * Costruttore vuoto
     */
    public EntityAutobus() {
    }
    
    /**
     * Costruttore completo
     * 
     * @param id ID dell'autobus
     * @param capienza Capienza dell'autobus
     * @param trattaAssegnata Tratta assegnata all'autobus
     */
    public EntityAutobus(int id, int capienza, String trattaAssegnata) {
        this.id = id;
        this.capienza = capienza;
        this.trattaAssegnata = trattaAssegnata;
    }
    
    /**
     * Restituisce l'ID dell'autobus
     * 
     * @return ID dell'autobus
     */
    public int getId() {
        return id;
    }
    
    /**
     * Imposta l'ID dell'autobus
     * 
     * @param id Nuovo ID dell'autobus
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Restituisce la capienza dell'autobus
     * 
     * @return Capienza dell'autobus
     */
    public int getCapienza() {
        return capienza;
    }
    
    /**
     * Imposta la capienza dell'autobus
     * 
     * @param capienza Nuova capienza dell'autobus
     */
    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }
    
    /**
     * Restituisce la tratta assegnata all'autobus
     * 
     * @return Tratta assegnata
     */
    public String getTrattaAssegnata() {
        return trattaAssegnata;
    }
    
    /**
     * Imposta la tratta assegnata all'autobus
     * 
     * @param trattaAssegnata Nuova tratta assegnata
     */
    public void setTrattaAssegnata(String trattaAssegnata) {
        this.trattaAssegnata = trattaAssegnata;
    }
}

package entity;

import java.util.Date;

/**
 * Classe che rappresenta l'entità Corsa
 */
public class EntityCorsa {
    private int id;
    private Date orario;
    private Date data;
    private String cittaPartenza;
    private String cittaArrivo;
    private double prezzo;
    private int numBigliettiVenduti;
    private int idAutobus;
    private int postiDisponibili; // Nuovo campo per memorizzare i posti disponibili
    
    /**
     * Costruttore vuoto
     */
    public EntityCorsa() {
    }
    
    /**
     * Costruttore completo
     * 
     * @param id ID della corsa
     * @param orario Orario della corsa
     * @param data Data della corsa
     * @param cittaPartenza Città di partenza
     * @param cittaArrivo Città di arrivo
     * @param prezzo Prezzo del biglietto
     * @param numBigliettiVenduti Numero di biglietti venduti
     * @param idAutobus ID dell'autobus assegnato
     */
    public EntityCorsa(int id, Date orario, Date data, String cittaPartenza, String cittaArrivo, 
                      double prezzo, int numBigliettiVenduti, int idAutobus) {
        this.id = id;
        this.orario = orario;
        this.data = data;
        this.cittaPartenza = cittaPartenza;
        this.cittaArrivo = cittaArrivo;
        this.prezzo = prezzo;
        this.numBigliettiVenduti = numBigliettiVenduti;
        this.idAutobus = idAutobus;
    }
    
    /**
     * Restituisce l'ID della corsa
     * 
     * @return ID della corsa
     */
    public int getId() {
        return id;
    }
    
    /**
     * Imposta l'ID della corsa
     * 
     * @param id Nuovo ID della corsa
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Restituisce l'orario della corsa
     * 
     * @return Orario della corsa
     */
    public Date getOrario() {
        return orario;
    }
    
    /**
     * Imposta l'orario della corsa
     * 
     * @param orario Nuovo orario della corsa
     */
    public void setOrario(Date orario) {
        this.orario = orario;
    }
    
    /**
     * Restituisce la data della corsa
     * 
     * @return Data della corsa
     */
    public Date getData() {
        return data;
    }
    
    /**
     * Imposta la data della corsa
     * 
     * @param data Nuova data della corsa
     */
    public void setData(Date data) {
        this.data = data;
    }
    
    /**
     * Restituisce la città di partenza
     * 
     * @return Città di partenza
     */
    public String getCittaPartenza() {
        return cittaPartenza;
    }
    
    /**
     * Imposta la città di partenza
     * 
     * @param cittaPartenza Nuova città di partenza
     */
    public void setCittaPartenza(String cittaPartenza) {
        this.cittaPartenza = cittaPartenza;
    }
    
    /**
     * Restituisce la città di arrivo
     * 
     * @return Città di arrivo
     */
    public String getCittaArrivo() {
        return cittaArrivo;
    }
    
    /**
     * Imposta la città di arrivo
     * 
     * @param cittaArrivo Nuova città di arrivo
     */
    public void setCittaArrivo(String cittaArrivo) {
        this.cittaArrivo = cittaArrivo;
    }
    
    /**
     * Restituisce il prezzo del biglietto
     * 
     * @return Prezzo del biglietto
     */
    public double getPrezzo() {
        return prezzo;
    }
    
    /**
     * Imposta il prezzo del biglietto
     * 
     * @param prezzo Nuovo prezzo del biglietto
     */
    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }
    
    /**
     * Restituisce il numero di biglietti venduti
     * 
     * @return Numero di biglietti venduti
     */
    public int getNumBigliettiVenduti() {
        return numBigliettiVenduti;
    }
    
    /**
     * Imposta il numero di biglietti venduti
     * 
     * @param numBigliettiVenduti Nuovo numero di biglietti venduti
     */
    public void setNumBigliettiVenduti(int numBigliettiVenduti) {
        this.numBigliettiVenduti = numBigliettiVenduti;
    }
    
    /**
     * Restituisce l'ID dell'autobus
     * 
     * @return ID dell'autobus
     */
    public int getIdAutobus() {
        return idAutobus;
    }
    
    /**
     * Imposta l'ID dell'autobus
     * 
     * @param idAutobus Nuovo ID dell'autobus
     */
    public void setIdAutobus(int idAutobus) {
        this.idAutobus = idAutobus;
    }
    
    /**
     * Restituisce il numero di posti disponibili
     * 
     * @return Numero di posti disponibili
     */
    public int getPostiDisponibili() {
        return postiDisponibili;
    }
    
    /**
     * Imposta il numero di posti disponibili
     * 
     * @param postiDisponibili Nuovo numero di posti disponibili
     */
    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }
    
    /**
     * Restituisce l'autobus associato alla corsa
     * 
     * @return ID dell'autobus
     */
    public int getAutobus() {
        return idAutobus;
    }
    
    /**
     * Restituisce una rappresentazione testuale della corsa
     * 
     * @return Stringa che rappresenta la corsa
     */
    @Override
    public String toString() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
        
        return String.format("ID: %d - %s → %s - %s %s - Prezzo: %.2f€ - Posti disponibili: %d", 
                id, cittaPartenza, cittaArrivo, 
                dateFormat.format(data), timeFormat.format(getOrario()), 
                prezzo, postiDisponibili);
    }
}

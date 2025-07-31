package entity;

import java.util.Date;

/**
 * Classe che rappresenta l'entità Biglietto
 */
public class EntityBiglietto {
    private int id;
    private Date orario;
    private Date data;
    private String codiceQR;
    private Integer numPosto;
    private int idCorsa;
    private int idImpiegato;
    private long telefono;
    private String email;
    private double prezzo;
    private String cittaPartenza;
    private String cittaArrivo;
    
    /**
     * Costruttore vuoto
     */
    public EntityBiglietto() {
    }
    
    /**
     * Costruttore completo
     * 
     * @param id ID del biglietto
     * @param orario Orario di emissione
     * @param data Data di emissione
     * @param codiceQR Codice QR del biglietto
     * @param numPosto Numero del posto
     * @param idCorsa ID della corsa associata
     */
    public EntityBiglietto(int id, Date orario, Date data, String codiceQR, Integer numPosto, int idCorsa) {
        this.id = id;
        this.orario = orario;
        this.data = data;
        this.codiceQR = codiceQR;
        this.numPosto = numPosto;
        this.idCorsa = idCorsa;
    }
    
    /**
     * Restituisce l'ID del biglietto
     * 
     * @return ID del biglietto
     */
    public int getId() {
        return id;
    }
    
    /**
     * Imposta l'ID del biglietto
     * 
     * @param id Nuovo ID del biglietto
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Restituisce l'orario di emissione
     * 
     * @return Orario di emissione
     */
    public Date getOrario() {
        return orario;
    }
    
    /**
     * Imposta l'orario di emissione
     * 
     * @param orario Nuovo orario di emissione
     */
    public void setOrario(Date orario) {
        this.orario = orario;
    }
    
    /**
     * Restituisce la data di emissione
     * 
     * @return Data di emissione
     */
    public Date getData() {
        return data;
    }
    
    /**
     * Imposta la data di emissione
     * 
     * @param data Nuova data di emissione
     */
    public void setData(Date data) {
        this.data = data;
    }
    
    /**
     * Restituisce il codice QR
     * 
     * @return Codice QR
     */
    public String getCodiceQR() {
        return codiceQR;
    }
    
    /**
     * Imposta il codice QR
     * 
     * @param codiceQR Nuovo codice QR
     */
    public void setCodiceQR(String codiceQR) {
        this.codiceQR = codiceQR;
    }
    
    /**
     * Restituisce il numero del posto
     * 
     * @return Numero del posto
     */
    public Integer getNumPosto() {
        return numPosto;
    }
    
    /**
     * Imposta il numero del posto
     * 
     * @param numPosto Nuovo numero del posto
     */
    public void setNumPosto(Integer numPosto) {
        this.numPosto = numPosto;
    }
    
    /**
     * Restituisce l'ID della corsa
     * 
     * @return ID della corsa
     */
    public int getIdCorsa() {
        return idCorsa;
    }
    
    /**
     * Imposta l'ID della corsa
     * 
     * @param idCorsa Nuovo ID della corsa
     */
    public void setIdCorsa(int idCorsa) {
        this.idCorsa = idCorsa;
    }
    
    /**
     * Restituisce l'ID dell'impiegato
     * 
     * @return ID dell'impiegato
     */
    public int getIdImpiegato() {
        return idImpiegato;
    }
    
    /**
     * Imposta l'ID dell'impiegato
     * 
     * @param idImpiegato Nuovo ID dell'impiegato
     */
    public void setIdImpiegato(int idImpiegato) {
        this.idImpiegato = idImpiegato;
    }

    /**
     * Restituisce il numero di telefono del cliente
     * 
     * @return Numero di telefono
     */
    public long getTelefono() {
        return telefono;
    }

    /**
     * Imposta il numero di telefono del cliente
     * 
     * @param telefono Nuovo numero di telefono
     */
    public void setTelefono(long telefono) {
        this.telefono = telefono;
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
        this.email = email;
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
     * Restituisce la città di partenza della corsa
     *
     * @return Città di partenza
     */
    public String getCittaPartenza() {
        return cittaPartenza;
    }

    /**
     * Imposta la città di partenza della corsa
     *
     * @param cittaPartenza Nuova città di partenza
     */
    public void setCittaPartenza(String cittaPartenza) {
        this.cittaPartenza = cittaPartenza;
    }

    /**
     * Restituisce la città di arrivo della corsa
     *
     * @return Città di arrivo
     */
    public String getCittaArrivo() {
        return cittaArrivo;
    }

    /**
     * Imposta la città di arrivo della corsa
     *
     * @param cittaArrivo Nuova città di arrivo
     */
    public void setCittaArrivo(String cittaArrivo) {
        this.cittaArrivo = cittaArrivo;
    }
}

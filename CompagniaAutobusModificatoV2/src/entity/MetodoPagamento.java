package entity;

public class MetodoPagamento {
    private String tipo; // "PAYPAL" o "CARTA"
    private String numeroCarta; // Per carta di credito
    private String scadenza; // Per carta di credito
    private String cvv; // Per carta di credito
    private String emailPaypal; // Per PayPal
    
    public MetodoPagamento(String tipo) {
        this.tipo = tipo;
    }
    
    // Getters e Setters
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getNumeroCarta() {
        return numeroCarta;
    }
    
    public void setNumeroCarta(String numeroCarta) {
        this.numeroCarta = numeroCarta;
    }
    
    public String getScadenza() {
        return scadenza;
    }
    
    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }
    
    public String getCvv() {
        return cvv;
    }
    
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    
    public String getEmailPaypal() {
        return emailPaypal;
    }
    
    public void setEmailPaypal(String emailPaypal) {
        this.emailPaypal = emailPaypal;
    }
} 
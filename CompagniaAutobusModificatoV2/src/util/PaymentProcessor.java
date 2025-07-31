package util;

import entity.MetodoPagamento;
import java.util.Calendar;

/**
 * Classe di utilità per la gestione dei pagamenti
 */
public class PaymentProcessor {
    /**
     * Processa un pagamento utilizzando il metodo di pagamento specificato
     * 
     * @param metodoPagamento il metodo di pagamento da utilizzare
     * @param importo l'importo da pagare
     * @return true se il pagamento è andato a buon fine, false altrimenti
     */
    public static boolean processPayment(MetodoPagamento metodoPagamento, double importo) {
        try {
            if (metodoPagamento.getTipo().equals("CARTA")) {
                return processaPagamentoCarta(metodoPagamento, importo);
            } else if (metodoPagamento.getTipo().equals("PAYPAL")) {
                return processaPagamentoPayPal(metodoPagamento, importo);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Errore durante il processamento del pagamento: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Valida la scadenza della carta di credito
     * 
     * @param scadenza la scadenza nel formato MM/YY
     * @return true se la scadenza è valida, false altrimenti
     */
    public static boolean validaScadenza(String scadenza) {
        try {
            // Verifica il formato MM/YY
            if (!scadenza.matches("\\d{2}/\\d{2}")) {
                return false;
            }

            // Estrai mese e anno
            String[] parti = scadenza.split("/");
            int mese = Integer.parseInt(parti[0]);
            int anno = Integer.parseInt(parti[1]);

            // Verifica che il mese sia valido (1-12)
            if (mese < 1 || mese > 12) {
                return false;
            }

            // Ottieni la data corrente
            Calendar cal = Calendar.getInstance();
            int annoCorrente = cal.get(Calendar.YEAR) % 100; // Prendi solo le ultime 2 cifre
            int meseCorrente = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH è 0-based

            // Verifica che la scadenza sia nel futuro
            if (anno < annoCorrente || (anno == annoCorrente && mese < meseCorrente)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Processa un pagamento con carta di credito
     * 
     * @param metodoPagamento il metodo di pagamento con carta
     * @param importo l'importo da pagare
     * @return true se il pagamento è andato a buon fine, false altrimenti
     */
    public static boolean processaPagamentoCarta(MetodoPagamento metodoPagamento, double importo) {
        // Validazione della carta
        if (!validaNumeroCarta(metodoPagamento.getNumeroCarta())) {
            throw new IllegalArgumentException("Il numero della carta deve essere di 16 cifre.");
        }
        if (!validaScadenza(metodoPagamento.getScadenza())) {
            throw new IllegalArgumentException("La scadenza deve essere nel formato MM/AA.");
        }
        if (!metodoPagamento.getCvv().matches("\\d{3}")) {
            throw new IllegalArgumentException("Il CVV deve essere di 3 cifre.");
        }
        // Implementazione del pagamento con carta
        return true;
    }

    /**
     * Valida il numero della carta di credito
     * @param numeroCarta il numero della carta
     * @return true se il numero è valido (16 cifre)
     */
    public static boolean validaNumeroCarta(String numeroCarta) {
        return numeroCarta != null && numeroCarta.matches("\\d{16}");
    }
    
    /**
     * Processa un pagamento con PayPal
     * 
     * @param metodoPagamento il metodo di pagamento con PayPal
     * @param importo l'importo da pagare
     * @return true se il pagamento è andato a buon fine, false altrimenti
     */
    private static boolean processaPagamentoPayPal(MetodoPagamento metodoPagamento, double importo) {
        // Implementazione del pagamento con PayPal
        return true;
    }
} 
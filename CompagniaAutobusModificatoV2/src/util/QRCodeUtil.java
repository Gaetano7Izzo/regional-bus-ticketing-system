package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Classe di utilità per la generazione e gestione dei codici QR.
 */
public class QRCodeUtil {
    
    private static final String QR_CODE_PATH = "temp/qrcodes/";
    
    /**
     * Genera un codice QR univoco
     * 
     * @return il codice QR generato
     */
    public String generaCodiceQR() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Genera l'immagine del QR code e la salva su file
     * 
     * @param codiceQR il codice QR da convertire in immagine
     * @return il percorso del file immagine generato
     */
    public String generaImmagineQRCode(String codiceQR) {
        try {
            // Crea la directory se non esiste
            Path directory = Paths.get(QR_CODE_PATH);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            // Genera il QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(codiceQR, BarcodeFormat.QR_CODE, 200, 200);
            
            // Salva l'immagine
            String filePath = QR_CODE_PATH + codiceQR + ".png";
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get(filePath));
            
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Verifica la validità di un codice QR
     * 
     * @param codiceQR il codice QR da verificare
     * @return true se il codice è valido, false altrimenti
     */
    public boolean verificaCodiceQR(String codiceQR) {
        // Verifica che il codice QR inizi con "QR" e abbia una lunghezza di 12 caratteri
        return codiceQR != null && codiceQR.startsWith("QR") && codiceQR.length() == 12;
    }
}

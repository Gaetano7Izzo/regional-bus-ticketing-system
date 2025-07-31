package util;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import entity.EntityCliente;
import entity.EntityCorsa;

/**
 * Utility class per la generazione di PDF dei biglietti
 */
public class PDFGenerator {
    
    private static final String PDF_PATH = "temp/biglietti/";
    private static final float MARGIN = 20; // Margini ridotti per il biglietto
    
    /**
     * Genera un PDF del biglietto
     * 
     * @param corsa la corsa del biglietto
     * @param cliente il cliente che ha acquistato il biglietto
     * @param codiceQR il codice del biglietto
     * @param numPosto il numero del posto assegnato
     * @return il percorso del file PDF generato
     */
    public static String generaBigliettoPDF(EntityCorsa corsa, EntityCliente cliente, String codiceQR, int numPosto) {
        if (corsa == null) {
            System.err.println("Errore: corsa non può essere null");
            return null;
        }
        if (codiceQR == null || codiceQR.trim().isEmpty()) {
            System.err.println("Errore: codiceQR non può essere null o vuoto");
            return null;
        }

        try {
            // Crea la directory se non esiste
            Path directory = Paths.get(PDF_PATH);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            // Crea il documento PDF con formato A5
            Document document = new Document(PageSize.A5, MARGIN, MARGIN, MARGIN, MARGIN);
            String pdfPath = PDF_PATH + codiceQR + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();
            
            // Aggiungi il titolo
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Paragraph title = new Paragraph("Biglietto Autobus", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);
            
            // Aggiungi i dettagli del viaggio
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
            document.add(new Paragraph("Dettagli Viaggio:", normalFont));
            document.add(new Paragraph("Data: " + corsa.getData(), normalFont));
            document.add(new Paragraph("Orario: " + corsa.getOrario(), normalFont));
            document.add(new Paragraph("Partenza: " + corsa.getCittaPartenza(), normalFont));
            document.add(new Paragraph("Arrivo: " + corsa.getCittaArrivo(), normalFont));
            document.add(new Paragraph("Prezzo: €" + corsa.getPrezzo(), normalFont));
            document.add(new Paragraph("Posto: " + numPosto, normalFont));
            document.add(new Paragraph("\n"));
            
            // Aggiungi i dettagli del cliente solo se disponibili
            if (cliente != null) {
                document.add(new Paragraph("Dettagli Cliente:", normalFont));
                document.add(new Paragraph("Email: " + cliente.getEmail(), normalFont));
                document.add(new Paragraph("Telefono: " + cliente.getNumTelefono(), normalFont));
                document.add(new Paragraph("\n"));
            }
            
            // Aggiungi il codice del biglietto
            document.add(new Paragraph("Codice Biglietto: " + codiceQR, normalFont));
            
            document.close();
            return pdfPath;
            
        } catch (Exception e) {
            System.err.println("Errore durante la generazione del PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

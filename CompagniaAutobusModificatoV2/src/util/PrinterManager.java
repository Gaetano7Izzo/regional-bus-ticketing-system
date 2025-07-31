package util;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.print.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DatabaseConnection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import entity.EntityBiglietto;
import control.GestioneCompagnia;
import entity.EntityCorsa;
import entity.EntityCliente;

public class PrinterManager {
    
    private static final String DEBUG_PREFIX = "[DEBUG] ";
    private static final String ERROR_PREFIX = "[ERROR] ";
    private static final String SUCCESS_PREFIX = "[SUCCESS] ";
    
    /**
     * Recupera i dettagli del biglietto dal database
     */
    private static String[] getDettagliBiglietto(String codiceQR) throws SQLException {
        debug("Recupero dettagli biglietto per codice QR: " + codiceQR);
        String[] dettagli = new String[6]; // [cliente, partenza, arrivo, data, ora, posto]
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            debug("Connessione al database stabilita");
            String query = "SELECT b.email, co.cittaPartenza, co.cittaArrivo, " +
                          "co.data, co.orario, b.numposto " +
                          "FROM Biglietto b " +
                          "JOIN Corsa co ON b.idCorsa = co.id " +
                          "WHERE b.codiceQR = ?";
            
            debug("Query SQL: " + query);
            debug("Parametro codiceQR: " + codiceQR);
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, codiceQR);
                debug("Esecuzione query per recuperare dettagli biglietto");
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    dettagli[0] = rs.getString("email"); // Usiamo l'email come identificativo del cliente
                    dettagli[1] = rs.getString("cittaPartenza");
                    dettagli[2] = rs.getString("cittaArrivo");
                    dettagli[3] = rs.getString("data");
                    dettagli[4] = rs.getString("orario");
                    dettagli[5] = rs.getString("numposto");
                    debug("Dettagli biglietto recuperati con successo:");
                    debug("- Email cliente: " + dettagli[0]);
                    debug("- Partenza: " + dettagli[1]);
                    debug("- Arrivo: " + dettagli[2]);
                    debug("- Data: " + dettagli[3]);
                    debug("- Ora: " + dettagli[4]);
                    debug("- Posto: " + dettagli[5]);
                } else {
                    error("Nessun biglietto trovato con codice QR: " + codiceQR);
                    throw new SQLException("Biglietto non trovato");
                }
            }
        } catch (SQLException e) {
            error("Errore nel recupero dei dettagli del biglietto: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
        return dettagli;
    }
    
    /**
     * Converte un file PDF in un'immagine PNG
     */
    private static byte[] convertPDFToPNG(String pdfPath) throws IOException {
        debug("=== INIZIO CONVERSIONE PDF -> PNG ===");
        debug("Percorso file PDF: " + pdfPath);
        
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            error("File PDF non trovato");
            throw new IOException("File PDF non trovato: " + pdfPath);
        }
        debug("Dimensione file PDF: " + pdfFile.length() + " bytes");
        
        try {
            // Estrai il codice QR dal nome del file
            String codiceQR = pdfFile.getName().replace(".pdf", "");
            debug("Codice QR estratto: " + codiceQR);
            
            String[] dettagli;
            try {
                dettagli = getDettagliBiglietto(codiceQR);
            } catch (SQLException e) {
                error("Errore nel recupero dei dettagli del biglietto: " + e.getMessage());
                throw new IOException("Impossibile recuperare i dettagli del biglietto", e);
            }
            
            // Crea un'immagine temporanea (A5: 420x595 pixels a 72 DPI)
            BufferedImage image = new BufferedImage(420, 595, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // Imposta lo sfondo bianco
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            
            // Imposta il font per il titolo
            Font titleFont = new Font("Arial", Font.BOLD, 24);
            g2d.setFont(titleFont);
            g2d.setColor(Color.BLACK);
            
            // Disegna il titolo
            String title = "BIGLIETTO AUTOBUS";
            FontMetrics titleMetrics = g2d.getFontMetrics();
            int titleWidth = titleMetrics.stringWidth(title);
            g2d.drawString(title, (image.getWidth() - titleWidth) / 2, 50);
            
            // Imposta il font per i dettagli
            Font detailFont = new Font("Arial", Font.PLAIN, 14);
            g2d.setFont(detailFont);
            
            // Disegna i dettagli del biglietto
            int y = 100;
            g2d.drawString("Cliente: " + dettagli[0], 30, y);
            y += 30;
            g2d.drawString("Partenza: " + dettagli[1], 30, y);
            y += 30;
            g2d.drawString("Arrivo: " + dettagli[2], 30, y);
            y += 30;
            g2d.drawString("Data: " + dettagli[3], 30, y);
            y += 30;
            g2d.drawString("Ora: " + dettagli[4], 30, y);
            y += 30;
            g2d.drawString("Posto: " + dettagli[5], 30, y);
            y += 30;
            
            // Carica e disegna il QR code
            File qrFile = new File("temp/qrcodes/" + codiceQR + ".png");
            debug("Tentativo di caricamento QR code da: " + qrFile.getAbsolutePath());
            if (qrFile.exists()) {
                BufferedImage qrImage = ImageIO.read(qrFile);
                if (qrImage != null) {
                    debug("QR code caricato con successo");
                    // Ridimensiona il QR code a 150x150 pixels
                    BufferedImage resizedQR = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
                    Graphics2D qrG2d = resizedQR.createGraphics();
                    qrG2d.drawImage(qrImage, 0, 0, 150, 150, null);
                    qrG2d.dispose();
                    
                    // Disegna il QR code centrato
                    g2d.drawImage(resizedQR, (image.getWidth() - 150) / 2, y, null);
                    debug("QR code disegnato sull'immagine");
                } else {
                    error("QR code non valido");
                }
            } else {
                error("File QR code non trovato");
            }
            
            // Aggiungi il codice QR come testo
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString("Codice: " + codiceQR, 30, image.getHeight() - 20);
            
            g2d.dispose();
            
            // Converti in PNG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] imageData = baos.toByteArray();
            
            debug("Dimensione immagine PNG: " + imageData.length + " bytes");
            success("Conversione PDF -> PNG completata con successo");
            
            return imageData;
            
        } catch (Exception e) {
            error("Errore durante la conversione: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Errore nella conversione PDF -> PNG: " + e.getMessage());
        }
    }
    
    /**
     * Stampa direttamente il biglietto come immagine (PNG) con dati e QR code
     */
    public static boolean stampaBigliettoConQR(String codiceQR, String nomeStampante) throws PrinterException {
        try {
            System.out.println("[DEBUG] Inizio processo di stampa biglietto");
            System.out.println("[DEBUG] Codice QR: " + codiceQR);
            System.out.println("[DEBUG] Nome stampante: " + (nomeStampante != null ? nomeStampante : "default"));

            // Ottieni il biglietto dal database
            System.out.println("[DEBUG] Tentativo di recupero biglietto dal database...");
            EntityBiglietto biglietto = GestioneCompagnia.getInstance().getBigliettoByCodiceQR(codiceQR);
            if (biglietto == null) {
                System.out.println("[ERROR] Biglietto non trovato nel database");
                throw new PrinterException("Biglietto non trovato");
            }
            System.out.println("[DEBUG] Biglietto recuperato con successo");

            // Ottieni la corsa
            System.out.println("[DEBUG] Tentativo di recupero corsa...");
            EntityCorsa corsa = GestioneCompagnia.getInstance().getCorsaById(biglietto.getIdCorsa());
            if (corsa == null) {
                System.out.println("[ERROR] Corsa non trovata nel database");
                throw new PrinterException("Corsa non trovata");
            }
            System.out.println("[DEBUG] Corsa recuperata con successo");

            // Genera il PDF solo con le informazioni essenziali
            System.out.println("[DEBUG] Tentativo di generazione PDF...");
            String pdfPath = PDFGenerator.generaBigliettoPDF(corsa, null, codiceQR, biglietto.getNumPosto());
            if (pdfPath == null) {
                System.out.println("[ERROR] Generazione PDF fallita");
                throw new PrinterException("Errore nella generazione del PDF");
            }
            System.out.println("[DEBUG] PDF generato con successo: " + pdfPath);

            // Converti PDF in PNG
            System.out.println("[DEBUG] Conversione PDF in PNG...");
            byte[] imageData = convertPDFToPNG(pdfPath);
            if (imageData == null) {
                System.out.println("[ERROR] Conversione PDF in PNG fallita");
                throw new PrinterException("Errore nella conversione del PDF in PNG");
            }
            System.out.println("[DEBUG] Conversione PDF in PNG completata");

            // Stampa l'immagine PNG
            System.out.println("[DEBUG] Tentativo di stampa PNG...");
            boolean result = stampaImmagine(imageData, nomeStampante);
            System.out.println("[DEBUG] Risultato stampa: " + (result ? "successo" : "fallimento"));
            return result;

        } catch (Exception e) {
            System.out.println("[ERROR] Errore durante la stampa del biglietto: " + e.getMessage());
            e.printStackTrace();
            throw new PrinterException("Errore durante la stampa del biglietto: " + e.getMessage());
        }
    }

    private static boolean stampaImmagine(byte[] imageData, String nomeStampante) throws PrinterException {
        try {
            System.out.println("[DEBUG] Inizio processo di stampa immagine");
            System.out.println("[DEBUG] Dimensione immagine: " + imageData.length + " bytes");
            
            // Ottieni la stampante
            PrintService printService = null;
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
            
            System.out.println("[DEBUG] Stampanti disponibili:");
            for (PrintService service : printServices) {
                System.out.println("- " + service.getName());
                if (service.getName().equals(nomeStampante)) {
                    printService = service;
                    System.out.println("[DEBUG] Stampante target trovata: " + service.getName());
                }
            }
            
            if (printService == null) {
                System.out.println("[DEBUG] Stampante specificata non trovata, uso stampante predefinita");
                printService = PrintServiceLookup.lookupDefaultPrintService();
            }
            
            if (printService == null) {
                throw new PrinterException("Nessuna stampante disponibile");
            }
            
            System.out.println("[DEBUG] Stampante selezionata: " + printService.getName());
            
            // Verifica le capacità della stampante
            DocFlavor[] flavors = printService.getSupportedDocFlavors();
            System.out.println("[DEBUG] Formati supportati dalla stampante:");
            for (DocFlavor flavor : flavors) {
                System.out.println("- " + flavor);
            }
            
            // Crea il lavoro di stampa
            DocPrintJob job = printService.createPrintJob();
            
            // Imposta gli attributi di stampa
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(MediaSizeName.ISO_A5);
            attributes.add(OrientationRequested.PORTRAIT);
            attributes.add(PrintQuality.HIGH);
            
            // Imposta i margini minimi
            attributes.add(new MediaPrintableArea(0, 0, 148, 210, MediaPrintableArea.MM));
            
            // Crea il documento da stampare
            Doc doc = new SimpleDoc(imageData, DocFlavor.BYTE_ARRAY.PNG, null);
            
            // Aggiungi listener per il monitoraggio
            job.addPrintJobListener(new PrintJobAdapter() {
                @Override
                public void printJobCompleted(PrintJobEvent pje) {
                    System.out.println("[DEBUG] Stampa completata con successo");
                }
                @Override
                public void printJobFailed(PrintJobEvent pje) {
                    System.out.println("[ERROR] Stampa fallita");
                }
                @Override
                public void printJobCanceled(PrintJobEvent pje) {
                    System.out.println("[ERROR] Stampa annullata");
                }
                @Override
                public void printJobNoMoreEvents(PrintJobEvent pje) {
                    System.out.println("[DEBUG] Nessun altro evento di stampa");
                }
                @Override
                public void printJobRequiresAttention(PrintJobEvent pje) {
                    System.out.println("[WARNING] Stampa richiede attenzione");
                }
            });
            
            // Esegui la stampa
            System.out.println("[DEBUG] Invio comando di stampa...");
            job.print(doc, attributes);
            System.out.println("[DEBUG] Comando di stampa inviato");
            
            return true;
            
        } catch (Exception e) {
            System.out.println("[ERROR] Errore durante la stampa dell'immagine: " + e.getMessage());
            e.printStackTrace();
            throw new PrinterException("Errore durante la stampa dell'immagine: " + e.getMessage());
        }
    }

    /**
     * Genera un'immagine BufferedImage del biglietto con dati e QR code
     */
    public static BufferedImage generaImmagineBiglietto(String codiceQR) throws IOException {
        try {
            String[] dettagli = getDettagliBiglietto(codiceQR);
            // Crea un'immagine temporanea (A5: 420x595 pixels a 72 DPI)
            BufferedImage image = new BufferedImage(420, 595, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            Font titleFont = new Font("Arial", Font.BOLD, 24);
            g2d.setFont(titleFont);
            g2d.setColor(Color.BLACK);
            String title = "BIGLIETTO AUTOBUS";
            FontMetrics titleMetrics = g2d.getFontMetrics();
            int titleWidth = titleMetrics.stringWidth(title);
            g2d.drawString(title, (image.getWidth() - titleWidth) / 2, 50);
            Font detailFont = new Font("Arial", Font.PLAIN, 14);
            g2d.setFont(detailFont);
            int y = 100;
            g2d.drawString("Cliente: " + dettagli[0], 30, y); y += 30;
            g2d.drawString("Partenza: " + dettagli[1], 30, y); y += 30;
            g2d.drawString("Arrivo: " + dettagli[2], 30, y); y += 30;
            g2d.drawString("Data: " + dettagli[3], 30, y); y += 30;
            g2d.drawString("Ora: " + dettagli[4], 30, y); y += 30;
            g2d.drawString("Posto: " + dettagli[5], 30, y); y += 30;
            // Carica e disegna il QR code
            File qrFile = new File("temp/qrcodes/" + codiceQR + ".png");
            if (qrFile.exists()) {
                BufferedImage qrImage = ImageIO.read(qrFile);
                if (qrImage != null) {
                    BufferedImage resizedQR = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
                    Graphics2D qrG2d = resizedQR.createGraphics();
                    qrG2d.drawImage(qrImage, 0, 0, 150, 150, null);
                    qrG2d.dispose();
                    g2d.drawImage(resizedQR, (image.getWidth() - 150) / 2, y, null);
                }
            }
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString("Codice: " + codiceQR, 30, image.getHeight() - 20);
            g2d.dispose();
            return image;
        } catch (Exception e) {
            error("Errore nella generazione dell'immagine del biglietto: " + e.getMessage());
            throw new IOException("Errore nella generazione dell'immagine del biglietto", e);
        }
    }
    
    /**
     * Lista tutte le stampanti disponibili nel sistema
     */
    public static String[] getStampantiDisponibili() {
        debug("\n=== STAMPANTI DISPONIBILI ===");
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        String[] printerNames = new String[printServices.length];
        
        for (int i = 0; i < printServices.length; i++) {
            printerNames[i] = printServices[i].getName();
            debug("- " + printerNames[i]);
        }
        
        return printerNames;
    }
    
    /**
     * Simula la stampa di un biglietto
     * 
     * @param codiceQR codice QR del biglietto da stampare
     * @param nomeStampante nome della stampante da utilizzare
     * @return true se la simulazione della stampa ha successo, false altrimenti
     * @throws PrinterException se si verifica un errore durante la simulazione della stampa
     */
    public static boolean simulaStampaBiglietto(String codiceQR, String nomeStampante) throws PrinterException {
        try {
            // Simuliamo un ritardo di stampa
            Thread.sleep(1000);
            
            // Verifichiamo che il codice QR sia valido
            if (codiceQR == null || codiceQR.trim().isEmpty()) {
                throw new PrinterException("Codice QR non valido");
            }
            
            // Verifichiamo che la stampante sia valida
            if (nomeStampante == null || nomeStampante.trim().isEmpty()) {
                throw new PrinterException("Nome stampante non valido");
            }
            
            // Simuliamo una stampa riuscita
            System.out.println("Simulazione stampa biglietto con codice QR: " + codiceQR + " su stampante: " + nomeStampante);
            return true;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PrinterException("Simulazione stampa interrotta: " + e.getMessage());
        }
    }
    
    private static void debug(String message) {
        System.out.println(DEBUG_PREFIX + message);
    }
    
    private static void error(String message) {
        System.err.println(ERROR_PREFIX + message);
    }
    
    private static void success(String message) {
        System.out.println(SUCCESS_PREFIX + message);
    }
} 
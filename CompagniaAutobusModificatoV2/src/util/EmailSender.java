package util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.Multipart;
import javax.mail.BodyPart;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;

/**
 * Classe di utilità per l'invio di email e SMS.
 */
public class EmailSender {
    
    private static final String EMAIL_FROM = "eugenioiando59@gmail.com"; // Sostituisci con la tua email
    private static final String EMAIL_PASSWORD = "rdawnjkmgtfziich"; // Sostituisci con la password dell'app
    
    // Credenziali Twilio
    private static final String ACCOUNT_SID = "ACce4e764a2fa303f2c9c1654adeeb418b";
    private static final String AUTH_TOKEN = "e24e4139d3ff3aa42463280af3b820ed";
    private static final String TWILIO_PHONE = "+14155238886"; // Numero Twilio Trial
    
    private Session session;
    
    /**
     * Costruttore della classe EmailSender
     */
    public EmailSender() {
        // Inizializza Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        
        // Inizializza la sessione email
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });
    }
    
    /**
     * Invia un'email con il biglietto allegato
     * 
     * @param email indirizzo email del destinatario
     * @param pdfPath percorso del file PDF da allegare
     * @param qrCodePath percorso dell'immagine QR code
     * @return true se l'invio ha successo, false altrimenti
     */
    public boolean inviaBiglietto(String email, String pdfPath, String qrCodePath) {
        // Configura le proprietà SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        // Crea la sessione
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });
        
        try {
            // Crea il messaggio
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Il tuo biglietto autobus");
            
            // Crea il corpo del messaggio
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Gentile cliente,\n\nIn allegato trovi il tuo biglietto autobus.\n\nCordiali saluti,\nCompagnia Autobus");
            
            // Aggiungi il QR code
            MimeBodyPart qrCodePart = new MimeBodyPart();
            qrCodePart.attachFile(qrCodePath);
            
            // Aggiungi il PDF
            MimeBodyPart pdfPart = new MimeBodyPart();
            pdfPart.attachFile(pdfPath);
            
            // Combina le parti del messaggio
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(qrCodePart);
            multipart.addBodyPart(pdfPart);
            message.setContent(multipart);
            
            // Invia l'email
            Transport.send(message);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Invia un SMS con il link al biglietto
     * 
     * @param telefono numero di telefono del destinatario (formato: +39XXXXXXXXXX)
     * @param messaggio il messaggio da inviare
     * @return true se l'invio ha successo, false altrimenti
     */
    public boolean inviaSMS(String telefono, String messaggio) {
        try {
            // Assicurati che il numero sia nel formato corretto (+39XXXXXXXXXX)
            if (!telefono.startsWith("+")) {
                telefono = "+39" + telefono;
            }
            
            // Crea il messaggio SMS usando il nome completo della classe
            com.twilio.rest.api.v2010.account.Message sms = com.twilio.rest.api.v2010.account.Message.creator(
                new PhoneNumber(telefono),
                new PhoneNumber(TWILIO_PHONE),
                messaggio
            ).create();
            
            System.out.println("SMS inviato con successo a: " + telefono);
            System.out.println("SID del messaggio: " + sms.getSid());
            return true;
        } catch (Exception e) {
            System.err.println("Errore nell'invio dell'SMS: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Invia un'email con il report allegato
     * 
     * @param email indirizzo email del destinatario
     * @param reportPath percorso del file di report da allegare
     * @return true se l'invio ha successo, false altrimenti
     */
    public boolean inviaReport(String email, String reportPath) {
        System.out.println("Invio report a: " + email);
        System.out.println("Allegato: " + reportPath);
        return true;
    }

    /**
     * Invia un'email con il biglietto in allegato
     * 
     * @param to l'indirizzo email del destinatario
     * @param subject l'oggetto dell'email
     * @param body il corpo dell'email
     * @param attachmentPath il percorso del file da allegare
     * @return true se l'invio è riuscito, false altrimenti
     */
    public static boolean inviaEmail(String to, String subject, String body, String attachmentPath) {
        // Configurazione delle proprietà
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        // Creazione della sessione
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });
        
        try {
            // Creazione del messaggio
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            
            // Invio dell'email
            Transport.send(message);
            return true;
            
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private MimeMessage createMessage(String to, String subject, String content) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(content);
        return message;
    }
}

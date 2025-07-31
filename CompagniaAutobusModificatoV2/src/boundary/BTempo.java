package boundary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import database.DBManager;
import entity.EntityBiglietto;

/**
 * Classe boundary che gestisce la generazione e l'invio di report settimanali.
 * Integra le funzionalità precedentemente presenti nelle classi control SchedulerReport e ReportSettimanale.
 */

public class BTempo {
    private static final long serialVersionUID = 1L;
    
    private DBManager dbManager;
    private ScheduledExecutorService scheduler;
    private static final String DEFAULT_EMAIL_DIRETTORE = "troaproject@gmail.com";
    
    // Configurazione per l'invio email
    private static final String EMAIL_MITTENTE = "eugenio.iando59@gmail.com";
    private static final String EMAIL_PASSWORD = "rdawnjkmgtfziich";
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final String EMAIL_PORT = "587";
    
    /**
     * Costruttore della classe BTempo
     */
    public BTempo() {
        this.dbManager = DBManager.getInstance();
        
        // Avvia automaticamente lo scheduler per l'invio settimanale
        avviaSchedulerReport(DEFAULT_EMAIL_DIRETTORE);
    }
    
    /**
     * Avvia lo scheduler per l'invio automatico del report
     */
    public void avviaSchedulerReport(String emailDestinatario) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        
        scheduler = Executors.newScheduledThreadPool(1);

        // *** INIZIO TEST INVIO IMMEDIATO ***
        // Programma un invio di test dopo 30 secondi
        
        scheduler.schedule(() -> {
            try {
                System.out.println("Test invio report: preparazione invio...");
                String report = generaReportSettimanale();
                inviaReportEmail(report, emailDestinatario);
                System.out.println("Test invio report: invio completato con successo!");
            } catch (Exception e) {
                System.err.println("Test invio report: errore durante l'invio - " + e.getMessage());
            }
        }, 30, TimeUnit.SECONDS);
        System.out.println("Test invio report: programmato per tra 30 secondi");
        // *** FINE TEST INVIO IMMEDIATO ***
        

        // Calcola il ritardo iniziale fino al prossimo lunedì alle 8:00
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonday = now.withHour(8).withMinute(0).withSecond(0);
        
        // Se oggi è lunedì e sono già le 8:00, programma per il prossimo lunedì
        if (now.getDayOfWeek().getValue() == 1 && now.getHour() >= 8) {
            nextMonday = nextMonday.plusWeeks(1);
        } else {
            // Altrimenti, trova il prossimo lunedì
            while (nextMonday.getDayOfWeek().getValue() != 1) {
                nextMonday = nextMonday.plusDays(1);
            }
        }
        
        // Se il prossimo lunedì è già passato, programma per il lunedì successivo
        if (now.isAfter(nextMonday)) {
            nextMonday = nextMonday.plusWeeks(1);
        }
        
        // Calcola il ritardo iniziale in millisecondi
        long initialDelay = java.time.Duration.between(now, nextMonday).toMillis();
        
        // Programma l'invio ogni lunedì alle 8:00
        scheduler.scheduleAtFixedRate(() -> {
            try {
                String report = generaReportSettimanale();
                inviaReportEmail(report, emailDestinatario);
                System.out.println("Report settimanale inviato automaticamente al direttore: " + emailDestinatario);
            } catch (Exception e) {
                System.err.println("Errore durante l'invio automatico del report: " + e.getMessage());
            }
        }, initialDelay, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS);
        
        System.out.println("Scheduler avviato");
    }
    
    /**
     * Genera il report settimanale dei biglietti venduti
     */
    public String generaReportSettimanale() throws SQLException {
        // Calcola la data di inizio e fine della settimana precedente
        LocalDate oggi = LocalDate.now();
        LocalDate inizioSettimana = oggi.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        LocalDate fineSettimana = inizioSettimana.plusDays(6);
        
        // Ottieni i biglietti venduti nella settimana
        List<EntityBiglietto> biglietti = getBigliettiSettimana(inizioSettimana, fineSettimana);
        
        // Genera il report
        return generaReport(biglietti, inizioSettimana, fineSettimana);
    }
    
    /**
     * Ottiene i biglietti venduti in un determinato periodo
     * 
     * @param inizio data di inizio del periodo
     * @param fine data di fine del periodo
     * @return lista dei biglietti venduti nel periodo
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    private List<EntityBiglietto> getBigliettiSettimana(LocalDate inizio, LocalDate fine) throws SQLException {
        List<EntityBiglietto> biglietti = new ArrayList<>();
        String query = "SELECT b.*, c.cittaPartenza, c.cittaArrivo " +
                      "FROM Biglietto b " +
                      "JOIN Corsa c ON b.idCorsa = c.id " +
                      "LEFT JOIN Impiegato i ON b.idImpiegato = i.id " +
                      "WHERE b.data >= ? AND b.data < ? " +
                      "ORDER BY b.data, b.orario";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Utilizza java.sql.Date per le colonne DATE nel database
            java.sql.Date sqlInizio = java.sql.Date.valueOf(inizio);
            java.sql.Date sqlFineEsclusa = java.sql.Date.valueOf(fine.plusDays(1));

            System.out.println("DEBUG getBigliettiSettimana: Querying for dates FROM (inclusive) " + sqlInizio + " TO (exclusive) " + sqlFineEsclusa);

            stmt.setDate(1, sqlInizio); // Data di inizio (inclusa)
            stmt.setDate(2, sqlFineEsclusa); // Data di fine + 1 giorno (esclusa)
            
            ResultSet rs = stmt.executeQuery();

            // Aggiungi stampe di debug per ogni biglietto recuperato
            System.out.println("DEBUG getBigliettiSettimana: Retrieved tickets:");
            while (rs.next()) {
                EntityBiglietto biglietto = new EntityBiglietto();
                biglietto.setId(rs.getInt("id"));
                biglietto.setOrario(rs.getTimestamp("orario"));
                biglietto.setData(rs.getDate("data"));
                biglietto.setCodiceQR(rs.getString("codiceQR"));
                biglietto.setNumPosto(rs.getInt("numposto"));
                biglietto.setIdCorsa(rs.getInt("idCorsa"));
                Object idImpiegatoObj = rs.getObject("idImpiegato");
                if (idImpiegatoObj != null) {
                    biglietto.setIdImpiegato(rs.getInt("idImpiegato"));
                } else {
                    biglietto.setIdImpiegato(0);
                }
                biglietto.setCittaPartenza(rs.getString("cittaPartenza"));
                biglietto.setCittaArrivo(rs.getString("cittaArrivo"));
                biglietti.add(biglietto);
                System.out.println("DEBUG getBigliettiSettimana: - Biglietto ID: " + biglietto.getId() + 
                                 ", Data: " + biglietto.getData() + 
                                 ", Tratta: " + biglietto.getCittaPartenza() + "-" + biglietto.getCittaArrivo() +
                                 ", Impiegato: " + biglietto.getIdImpiegato());
            }
            System.out.println("DEBUG getBigliettiSettimana: Total tickets retrieved: " + biglietti.size());
        }
        
        return biglietti;
    }
    
    /**
     * Genera il report in formato testuale
     * 
     * @param biglietti lista dei biglietti da includere nel report
     * @param inizio data di inizio del periodo
     * @param fine data di fine del periodo
     * @return il report generato in formato testuale
     */
    private String generaReport(List<EntityBiglietto> biglietti, LocalDate inizio, LocalDate fine) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        report.append("REPORT SETTIMANALE BIGLIETTI VENDUTI\n");
        report.append("Periodo: ").append(inizio.format(formatter)).append(" - ").append(fine.format(formatter)).append("\n\n");
        
        // Raggruppa i biglietti per data
        Map<LocalDate, List<EntityBiglietto>> bigliettiPerGiorno = new HashMap<>();
        for (EntityBiglietto b : biglietti) {
            // Converte java.util.Date in java.time.LocalDate
            LocalDate data = ((java.sql.Date)b.getData()).toLocalDate();
            bigliettiPerGiorno.computeIfAbsent(data, k -> new ArrayList<>()).add(b);
        }
        
        // Genera il report per ogni giorno
        for (LocalDate data : bigliettiPerGiorno.keySet()) {
            report.append("Data: ").append(data.format(formatter)).append("\n");
            report.append("Numero biglietti venduti: ").append(bigliettiPerGiorno.get(data).size()).append("\n");
            report.append("Dettaglio tratte:\n");
            
            // Raggruppa per tratta
            Map<String, Integer> tratte = new HashMap<>();
            for (EntityBiglietto b : bigliettiPerGiorno.get(data)) {
                String tratta = b.getCittaPartenza() + " - " + b.getCittaArrivo();
                tratte.merge(tratta, 1, Integer::sum);
            }
            
            for (Map.Entry<String, Integer> tratta : tratte.entrySet()) {
                report.append("  - ").append(tratta.getKey()).append(": ").append(tratta.getValue()).append(" biglietti\n");
            }
            report.append("\n");
        }
        
        // Se non ci sono biglietti, aggiungi un messaggio
        if (biglietti.isEmpty()) {
            report.append("Nessun biglietto venduto nel periodo specificato.\n");
        }
        
        return report.toString();
    }
    
    /**
     * Invia il report via email utilizzando JavaMail
     * 
     * @param report il contenuto del report da inviare
     * @param emailDestinatario l'indirizzo email del destinatario
     */
    private void inviaReportEmail(String report, String emailDestinatario) {
        System.out.println("Invio report settimanale a " + emailDestinatario);
        
        // Configurazione delle proprietà per l'invio email
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", EMAIL_HOST);
        props.put("mail.smtp.port", EMAIL_PORT);
        
        // Creazione della sessione con autenticazione
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_MITTENTE, EMAIL_PASSWORD);
            }
        });
        
        try {
            // Creazione del messaggio
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_MITTENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestinatario));
            message.setSubject("Report Settimanale Biglietti Venduti");
            
            // Crea il corpo del messaggio con formattazione HTML
            String htmlContent = "<html><body><pre style='font-family: monospace;'>" + 
                               report.replace("\n", "<br>") + 
                               "</pre></body></html>";
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Invio del messaggio
            Transport.send(message);
            
            System.out.println("Email inviata con successo a " + emailDestinatario);
            
        } catch (MessagingException e) {
            System.err.println("Errore durante l'invio dell'email: " + e.getMessage());
            e.printStackTrace();
            
            // In caso di errore, mostra un messaggio di log ma non bloccare l'esecuzione
            System.out.println("Simulazione invio email (fallback):");
            System.out.println("A: " + emailDestinatario);
            System.out.println("Oggetto: Report Settimanale Biglietti Venduti");
            System.out.println("Contenuto:");
            System.out.println(report);
        }
    }
    
    /**
     * Avvia l'interfaccia grafica
     */
    public void avviaInterfacciaGrafica() {
        // Implementa la logica per avviare l'interfaccia grafica
    }
}

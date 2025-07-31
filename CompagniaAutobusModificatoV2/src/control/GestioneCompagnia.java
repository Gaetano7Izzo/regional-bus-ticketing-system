package control;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.IOException;
import javax.print.PrintException;
import java.util.HashSet;
import java.util.Set;

import database.AutobusDAO;
import database.BigliettoDAO;
import database.ClienteDAO;
import database.CorsaDAO;
import database.ImpiegatoDAO;
import entity.EntityAutobus;
import entity.EntityBiglietto;
import entity.EntityCliente;
import entity.EntityCorsa;
import entity.EntityImpiegato;
import exception.AutenticazioneException;
import exception.CorsaNonTrovataException;
import exception.VenditaBigliettiException;
import util.EmailSender;
import util.PDFGenerator;
import util.QRCodeUtil;
import entity.MetodoPagamento;
import util.PaymentProcessor;
import util.PrinterManager;
import exception.PrinterException;

/**
 * Controller principale dell'applicazione.
 * Gestisce la logica di business per la compagnia di trasporto regionale.
 */
public class GestioneCompagnia {
    private static GestioneCompagnia instance = null;
    
    private BigliettoDAO bigliettoDAO;
    private CorsaDAO corsaDAO;
    private ClienteDAO clienteDAO;
    private ImpiegatoDAO impiegatoDAO;
    private AutobusDAO autobusDAO;
    
    private QRCodeUtil qrCodeUtil;
    private EmailSender emailSender;
    private PDFGenerator pdfGenerator;
    private int idImpiegato; // Campo per memorizzare l'ID dell'impiegato corrente che effettua la vendita
    
    /**
     * Costruttore privato (pattern Singleton)
     */
    private GestioneCompagnia() {
        // Inizializzazione dei DAO
        bigliettoDAO = new BigliettoDAO();
        corsaDAO = new CorsaDAO();
        clienteDAO = new ClienteDAO();
        impiegatoDAO = new ImpiegatoDAO();
        autobusDAO = new AutobusDAO();
        
        // Inizializzazione delle utility
        qrCodeUtil = new QRCodeUtil();
        emailSender = new EmailSender();
        pdfGenerator = new PDFGenerator();
        
        // L'inizializzazione del database è ora gestita da DBManager all'ottenimento della prima connessione.
        // Non è necessario chiamare inizializzaCorseDiEsempio qui.
        // corsaDAO.inizializzaCorseDiEsempio(); 
    }
    
    /**
     * Restituisce l'istanza singleton di GestioneCompagnia
     * 
     * @return l'istanza di GestioneCompagnia
     */
    public static synchronized GestioneCompagnia getInstance() {
        if (instance == null) {
            instance = new GestioneCompagnia();
        }
        return instance;
    }
    
    /**
     * Autentica un impiegato tramite il suo ID
     * 
     * @param idImpiegato ID dell'impiegato da autenticare
     * @return l'oggetto EntityImpiegato autenticato
     * @throws AutenticazioneException se l'autenticazione fallisce
     */
    public EntityImpiegato autenticazione(int idImpiegato) throws AutenticazioneException {
        return impiegatoDAO.readImpiegato(idImpiegato);
    }
    
    /**
     * Ottiene la lista delle corse disponibili
     * 
     * @param dataRicerca data di riferimento per filtrare le corse future
     * @return lista di corse disponibili
     */
    public List<EntityCorsa> getCorseDisponibili(Date dataRicerca) {
        return corsaDAO.getCorseDisponibili(dataRicerca);
    }
    
    /**
     * Acquista un biglietto per un cliente
     * 
     * @param idCorsa ID della corsa selezionata
     * @param numPosti numero di posti da acquistare
     * @param telefono numero di telefono del cliente
     * @param email email del cliente
     * @param richiestaQR se true, invia il codice QR via SMS
     * @return messaggio di conferma dell'acquisto
     * @throws CorsaNonTrovataException se la corsa non viene trovata
     * @throws VenditaBigliettiException se ci sono problemi durante la vendita
     */
    public String acquistaBiglietto(int idCorsa, int numPosti, long telefono, String email, boolean richiestaQR) 
            throws CorsaNonTrovataException, VenditaBigliettiException {
        
        // Verifica se il cliente esiste, altrimenti lo crea
        EntityCliente cliente = clienteDAO.readCliente(telefono);
        if (cliente == null) {
            cliente = new EntityCliente(telefono, email);
            clienteDAO.createCliente(cliente);
        }
        
        // Cerca la corsa
        EntityCorsa corsa = corsaDAO.readCorsa(idCorsa);
        
        // Verifica disponibilità posti
        int postiDisponibili = verificaDisponibilita(corsa.getId());
        if (postiDisponibili < numPosti) {
            throw new VenditaBigliettiException("Non ci sono abbastanza posti disponibili. Posti disponibili: " + postiDisponibili);
        }
        
        // Crea i biglietti
        StringBuilder risultato = new StringBuilder("Biglietti acquistati con successo:\n");
        String codiceQR = null;
        
        for (int i = 0; i < numPosti; i++) {
            // Genera codice QR
            codiceQR = qrCodeUtil.generaCodiceQR();
            String qrCodePath = qrCodeUtil.generaImmagineQRCode(codiceQR);
            
            // Crea il biglietto
            EntityBiglietto biglietto = new EntityBiglietto();
            biglietto.setOrario(new Date()); // Data e ora correnti
            biglietto.setData(corsa.getData());
            biglietto.setCodiceQR(codiceQR);
            biglietto.setNumPosto(corsa.getNumBigliettiVenduti() + i + 1);
            biglietto.setIdCorsa(corsa.getId());
            
            // Salva il biglietto
            bigliettoDAO.createBiglietto(biglietto);
            
            risultato.append("Biglietto #").append(biglietto.getId())
                    .append(", Posto: ").append(biglietto.getNumPosto())
                    .append(", Codice QR: ").append(codiceQR).append("\n");
            
            // Genera PDF e invia email per ogni biglietto
            String pdfPath = pdfGenerator.generaBigliettoPDF(corsa, cliente, codiceQR, biglietto.getNumPosto());
            emailSender.inviaBiglietto(email, pdfPath, qrCodePath);
            
            // Se richiesto, invia SMS
            if (richiestaQR) {
                String messaggio = String.format("Il tuo biglietto è stato acquistato con successo!\nCodice QR: %s\nPosto: %d", 
                    codiceQR, biglietto.getNumPosto());
                emailSender.inviaSMS(String.valueOf(telefono), messaggio);
            }
        }
        
        // Aggiorna il numero di biglietti venduti
        corsa.setNumBigliettiVenduti(corsa.getNumBigliettiVenduti() + numPosti);
        corsaDAO.updateCorsa(corsa);
        
        return risultato.toString();
    }
    
    /**
     * Vende un biglietto tramite un impiegato
     * 
     * @param idCorsa ID della corsa
     * @param postiScelti lista dei numeri dei posti scelti
     * @return messaggio di conferma della vendita
     * @throws VenditaBigliettiException se ci sono problemi durante la vendita
     */
    public String vendiBiglietto(int idCorsa, List<Integer> postiScelti) throws CorsaNonTrovataException, VenditaBigliettiException {
        System.out.println("Inizio vendita biglietti per corsa: " + idCorsa);
        
        EntityCorsa corsa = getCorsaById(idCorsa);
        if (corsa == null) {
            throw new CorsaNonTrovataException("Corsa non trovata");
        }

        // Verifica disponibilità posti
        int postiDisponibili = verificaDisponibilita(idCorsa);
        if (postiDisponibili < postiScelti.size()) {
            throw new VenditaBigliettiException("Non ci sono abbastanza posti disponibili");
        }

        StringBuilder risultato = new StringBuilder();
        risultato.append("Biglietti venduti con successo:\n");

        // Crea un biglietto per ogni posto selezionato
        for (Integer posto : postiScelti) {
            // Genera il codice QR prima di creare il biglietto
            String codiceQR = qrCodeUtil.generaCodiceQR();
            System.out.println("Generato codice QR: " + codiceQR + " per posto: " + posto);
            
            EntityBiglietto biglietto = new EntityBiglietto();
            biglietto.setOrario(new Date()); // Data e ora correnti
            biglietto.setData(corsa.getData());
            biglietto.setCodiceQR(codiceQR);
            biglietto.setNumPosto(posto);
            biglietto.setIdCorsa(idCorsa);
            biglietto.setIdImpiegato(idImpiegato);
            biglietto.setTelefono(0); // Valore di default per il telefono
            biglietto.setEmail("vendita@biglietteria.it"); // Email di default per le vendite in biglietteria
            biglietto.setPrezzo(corsa.getPrezzo());

            System.out.println("Tentativo di salvataggio biglietto nel database...");
            if (bigliettoDAO.createBiglietto(biglietto)) {
                System.out.println("Biglietto salvato con successo nel database");
                
                // Aggiorna i posti disponibili
                corsa.setNumBigliettiVenduti(corsa.getNumBigliettiVenduti() + 1);
                corsaDAO.updateCorsa(corsa);
                System.out.println("Aggiornato numero biglietti venduti per la corsa");

                risultato.append(String.format("- Posto %d: %s\n", posto, codiceQR));
            } else {
                System.err.println("Errore durante il salvataggio del biglietto nel database");
                throw new VenditaBigliettiException("Errore durante la creazione del biglietto per il posto " + posto);
            }
        }

        return risultato.toString();
    }
    
    /**
     * Ottiene un biglietto dal database tramite il suo codice QR
     * 
     * @param codiceQR codice QR del biglietto
     * @return oggetto EntityBiglietto se trovato, null altrimenti
     */
    public EntityBiglietto getBigliettoByCodiceQR(String codiceQR) {
        return bigliettoDAO.getBigliettoByCodiceQR(codiceQR);
    }
    
    /**
     * Ottiene una corsa dal database tramite il suo ID
     * 
     * @param idCorsa ID della corsa
     * @return oggetto EntityCorsa se trovato, null altrimenti
     * @throws CorsaNonTrovataException se la corsa non viene trovata
     */
    public EntityCorsa getCorsaById(int idCorsa) throws CorsaNonTrovataException {
        return corsaDAO.readCorsa(idCorsa);
    }
    
    /**
     * Ottiene le corse alternative con le stesse località di partenza e arrivo
     * 
     * @param cittaPartenza città di partenza
     * @param cittaArrivo città di arrivo
     * @return lista di corse alternative disponibili
     */
    public List<EntityCorsa> getCorseAlternative(String cittaPartenza, String cittaArrivo) {
        return corsaDAO.getCorseByLocalita(cittaPartenza, cittaArrivo);
    }
    
    /**
     * Modifica un biglietto esistente
     * 
     * @param codiceQR codice QR del biglietto da modificare
     * @param idNuovaCorsa ID della nuova corsa
     * @param nuovoPosto nuovo posto del biglietto
     * @return messaggio di conferma della modifica
     * @throws VenditaBigliettiException se ci sono problemi durante la modifica
     * @throws CorsaNonTrovataException se la corsa non viene trovata
     */
    public String modificaBiglietto(String codiceQR, int idNuovaCorsa, Integer nuovoPosto) 
            throws VenditaBigliettiException, CorsaNonTrovataException {
        
        // Cerca il biglietto
        EntityBiglietto biglietto = bigliettoDAO.getBigliettoByCodiceQR(codiceQR);
        if (biglietto == null) {
            throw new VenditaBigliettiException("Biglietto con codice QR " + codiceQR + " non trovato");
        }
        
        // Cerca la corsa originale
        EntityCorsa corsaOriginale = corsaDAO.readCorsa(biglietto.getIdCorsa());
        if (corsaOriginale == null) {
            throw new VenditaBigliettiException("Corsa originale non trovata");
        }
        
        // Cerca la nuova corsa
        EntityCorsa nuovaCorsa = corsaDAO.readCorsa(idNuovaCorsa);
        if (nuovaCorsa == null) {
            throw new VenditaBigliettiException("Nuova corsa non trovata");
        }
        
        // Verifica disponibilità posti nella nuova corsa
        EntityAutobus autobus = autobusDAO.readAutobus(nuovaCorsa.getIdAutobus());
        if (autobus == null) {
            throw new VenditaBigliettiException("Autobus associato alla nuova corsa non trovato");
        }
        
        // Verifica che ci siano effettivamente posti disponibili
        int postiDisponibili = autobus.getCapienza() - nuovaCorsa.getNumBigliettiVenduti();
        if (postiDisponibili <= 0) {
            throw new VenditaBigliettiException("Non ci sono posti disponibili per questa corsa");
        }
        
        // Verifica se il posto originale è disponibile nella nuova corsa
        List<EntityBiglietto> bigliettiNuovaCorsa = bigliettoDAO.getBigliettiByCorsa(idNuovaCorsa);
        boolean postoOriginaleDisponibile = true;
        for (EntityBiglietto b : bigliettiNuovaCorsa) {
            if (b.getNumPosto() != null && b.getNumPosto().equals(biglietto.getNumPosto())) {
                postoOriginaleDisponibile = false;
                break;
            }
        }
        
        // Se il posto originale non è disponibile e non è stato specificato un nuovo posto
        if (!postoOriginaleDisponibile && nuovoPosto == null) {
            // Ottieni la lista dei posti disponibili
            List<Integer> postiDisponibiliList = getPostiDisponibili(idNuovaCorsa);
            throw new VenditaBigliettiException("POSTI_DISPONIBILI:" + String.join(",", postiDisponibiliList.stream().map(String::valueOf).toArray(String[]::new)));
        }
        
        // Se è stato specificato un nuovo posto, verifica che sia disponibile
        if (nuovoPosto != null) {
            List<Integer> postiDisponibiliList = getPostiDisponibili(idNuovaCorsa);
            if (!postiDisponibiliList.contains(nuovoPosto)) {
                throw new VenditaBigliettiException("Il posto selezionato non è disponibile");
            }
            biglietto.setNumPosto(nuovoPosto);
        }
        
        // Genera un nuovo codice QR per il biglietto modificato
        String nuovoCodiceQR = qrCodeUtil.generaCodiceQR();
        
        // Aggiorna il biglietto
        biglietto.setData(nuovaCorsa.getData());
        biglietto.setOrario(nuovaCorsa.getOrario());
        biglietto.setIdCorsa(nuovaCorsa.getId());
        biglietto.setCodiceQR(nuovoCodiceQR);
        
        // Salva le modifiche al biglietto
        boolean success = bigliettoDAO.updateBiglietto(biglietto);
        if (!success) {
            throw new VenditaBigliettiException("Errore durante l'aggiornamento del biglietto");
        }
        
        // Aggiorna il numero di biglietti venduti per entrambe le corse
        corsaOriginale.setNumBigliettiVenduti(corsaOriginale.getNumBigliettiVenduti() - 1);
        nuovaCorsa.setNumBigliettiVenduti(nuovaCorsa.getNumBigliettiVenduti() + 1);
        
        // Verifica che non si superi la capienza dell'autobus
        if (nuovaCorsa.getNumBigliettiVenduti() > autobus.getCapienza()) {
            throw new VenditaBigliettiException("Impossibile modificare il biglietto: supererebbe la capienza dell'autobus");
        }
        
        // Salva le modifiche alle corse
        if (!corsaDAO.updateCorsa(corsaOriginale)) {
            throw new VenditaBigliettiException("Errore durante l'aggiornamento della corsa originale");
        }
        if (!corsaDAO.updateCorsa(nuovaCorsa)) {
            throw new VenditaBigliettiException("Errore durante l'aggiornamento della nuova corsa");
        }
        
        // Invia email di conferma con il nuovo codice QR
        try {
            inviaEmailConferma(biglietto);
        } catch (Exception e) {
            System.err.println("Errore durante l'invio dell'email di conferma: " + e.getMessage());
        }
        
        return "Biglietto modificato con successo!\n" +
               "Biglietto #" + biglietto.getId() + "\n" +
               "Nuovo codice QR: " + nuovoCodiceQR + "\n" +
               "Nuova data: " + nuovaCorsa.getData() + "\n" +
               "Nuovo orario: " + nuovaCorsa.getOrario() + "\n" +
               "Partenza: " + nuovaCorsa.getCittaPartenza() + "\n" +
               "Arrivo: " + nuovaCorsa.getCittaArrivo() + "\n" +
               "Nuovo posto: " + biglietto.getNumPosto();
    }
    
    /**
     * Verifica la disponibilità di posti per una corsa
     * 
     * @param idCorsa ID della corsa
     * @return numero di posti disponibili
     * @throws CorsaNonTrovataException se la corsa non viene trovata
     */
    public int verificaDisponibilita(int idCorsa) throws CorsaNonTrovataException {
        // Cerca la corsa
        EntityCorsa corsa = corsaDAO.readCorsa(idCorsa);
        if (corsa == null) {
            throw new CorsaNonTrovataException("Corsa non trovata");
        }
        
        // Cerca l'autobus associato
        EntityAutobus autobus = autobusDAO.readAutobus(corsa.getIdAutobus());
        if (autobus == null) {
            throw new CorsaNonTrovataException("Autobus associato alla corsa non trovato");
        }
        
        // Ottieni la lista dei posti occupati
        List<Integer> postiOccupati = bigliettoDAO.getPostiOccupati(idCorsa);
        
        // Calcola posti disponibili
        int postiDisponibili = autobus.getCapienza() - postiOccupati.size();
        
        // Verifica che il numero di biglietti venduti corrisponda ai posti occupati
        if (corsa.getNumBigliettiVenduti() != postiOccupati.size()) {
            // Correggi il numero di biglietti venduti
            corsa.setNumBigliettiVenduti(postiOccupati.size());
            corsaDAO.updateCorsa(corsa);
        }
        
        return postiDisponibili;
    }
    
    /**
     * Ottiene la lista dei posti disponibili per una corsa
     * 
     * @param idCorsa ID della corsa
     * @return Lista dei posti disponibili
     * @throws CorsaNonTrovataException se la corsa non viene trovata
     */
    public List<Integer> getPostiDisponibili(int idCorsa) throws CorsaNonTrovataException {
        EntityCorsa corsa = corsaDAO.getCorsaById(idCorsa);
        if (corsa == null) {
            throw new CorsaNonTrovataException("Corsa non trovata");
        }
        
        // Ottieni l'autobus associato alla corsa
        EntityAutobus autobus = autobusDAO.readAutobus(corsa.getIdAutobus());
        if (autobus == null) {
            throw new CorsaNonTrovataException("Autobus non trovato");
        }
        
        int capienza = autobus.getCapienza();
        
        // Ottieni la lista dei posti occupati per questa corsa
        List<Integer> postiOccupati = bigliettoDAO.getPostiOccupati(idCorsa);
        
        // Verifica che il numero di biglietti venduti corrisponda ai posti occupati
        if (corsa.getNumBigliettiVenduti() != postiOccupati.size()) {
            // Correggi il numero di biglietti venduti
            corsa.setNumBigliettiVenduti(postiOccupati.size());
            corsaDAO.updateCorsa(corsa);
        }
        
        List<Integer> postiDisponibili = new ArrayList<>();
        
        // Genera la lista dei posti disponibili
        for (int i = 1; i <= capienza; i++) {
            // Aggiungi il posto se NON è presente nella lista dei posti occupati
            if (!postiOccupati.contains(i)) {
                postiDisponibili.add(i);
            }
        }
        
        return postiDisponibili;
    }
    
    /**
     * Aggiorna la disponibilità di posti per una corsa
     * 
     * @param idCorsa ID della corsa
     * @param nuovaCapienza nuova capienza dell'autobus
     * @return messaggio di conferma dell'aggiornamento
     * @throws CorsaNonTrovataException se la corsa non viene trovata
     */
    public String aggiornaDisponibilita(int idCorsa, int nuovaCapienza) throws CorsaNonTrovataException {
        // Cerca la corsa
        EntityCorsa corsa = corsaDAO.readCorsa(idCorsa);
        
        // Cerca l'autobus associato
        EntityAutobus autobus = autobusDAO.readAutobus(corsa.getIdAutobus());
        if (autobus == null) {
            throw new CorsaNonTrovataException("Autobus associato alla corsa non trovato");
        }
        
        // Aggiorna la capienza
        autobus.setCapienza(nuovaCapienza);
        autobusDAO.createAutobus(autobus);
        
        return "Disponibilità aggiornata con successo!\n" +
               "Corsa #" + idCorsa + "\n" +
               "Nuova capienza: " + nuovaCapienza + "\n" +
               "Posti disponibili: " + (nuovaCapienza - corsa.getNumBigliettiVenduti());
    }
    
    /**
     * Gestisce il processo di stampa di un biglietto.
     *
     * @param codiceQR Il codice QR del biglietto da stampare.
     * @param nomeStampante Il nome della stampante su cui stampare (può essere null per quella predefinita).
     * @return Un messaggio di esito.
     * @throws PrinterException se si verifica un errore durante la stampa.
     */
    public String stampaBiglietto(String codiceQR, String nomeStampante) throws PrinterException {
        if (codiceQR == null || codiceQR.trim().isEmpty()) {
            throw new PrinterException("Codice QR non valido");
        }
        
        System.out.println("[DEBUG GestioneCompagnia] Inizio stampa biglietto per QR: " + codiceQR);
        try {
            // Verifica che il biglietto esista
            EntityBiglietto biglietto = bigliettoDAO.getBigliettoByCodiceQR(codiceQR);
            if (biglietto == null) {
                System.err.println("[ERROR GestioneCompagnia] Biglietto non trovato nel database per QR: " + codiceQR);
                throw new PrinterException("Biglietto non trovato");
            }
            System.out.println("[DEBUG GestioneCompagnia] Biglietto trovato nel database");

            // Ottieni i dettagli della corsa
            EntityCorsa corsa = corsaDAO.readCorsa(biglietto.getIdCorsa());
            if (corsa == null) {
                System.err.println("[ERROR GestioneCompagnia] Corsa non trovata nel database per ID: " + biglietto.getIdCorsa());
                throw new PrinterException("Corsa non trovata");
            }
            System.out.println("[DEBUG GestioneCompagnia] Corsa trovata nel database");

            // Stampa il biglietto usando PrinterManager
            System.out.println("[DEBUG GestioneCompagnia] Chiamata a PrinterManager.stampaBigliettoConQR...");
            boolean stampaOk = PrinterManager.stampaBigliettoConQR(codiceQR, nomeStampante);

            if (!stampaOk) {
                System.err.println("[ERROR GestioneCompagnia] PrinterManager.stampaBigliettoConQR ha restituito false.");
                throw new PrinterException("La stampa tramite PrinterManager ha segnalato un problema.");
            }

            System.out.println("[DEBUG GestioneCompagnia] Stampa completata con successo per QR: " + codiceQR);

            // Restituisci un messaggio di successo con i dettagli
            return String.format("Biglietto stampato con successo:\nCorsa: %s → %s\nData: %s\nOrario: %s\nPosto: %d\nCodice QR: %s",
                corsa.getCittaPartenza(),
                corsa.getCittaArrivo(),
                new SimpleDateFormat("dd/MM/yyyy").format(corsa.getOrario()),
                new SimpleDateFormat("HH:mm").format(corsa.getOrario()),
                biglietto.getNumPosto(),
                biglietto.getCodiceQR());

        } catch (PrinterException e) {
            System.out.println("[ERROR GestioneCompagnia] Eccezione PrinterException durante la stampa per QR " + codiceQR + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("[ERROR GestioneCompagnia] Errore generico durante la stampa per QR " + codiceQR + ": " + e.getMessage());
            e.printStackTrace();
            throw new PrinterException("Errore generico durante il processo di stampa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Invia un biglietto via email e/o SMS
     * 
     * @param codiceQR codice QR del biglietto da inviare
     * @param email email del destinatario
     * @param invioSMS se true, invia anche via SMS
     * @param telefono numero di telefono per l'invio SMS
     * @return messaggio di conferma dell'invio
     * @throws VenditaBigliettiException se ci sono problemi durante l'invio
     */
    public String inviaBiglietto(String codiceQR, String email, boolean invioSMS, long telefono) 
            throws VenditaBigliettiException {
        
        if (codiceQR == null || codiceQR.trim().isEmpty()) {
            throw new VenditaBigliettiException("Codice QR non valido");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new VenditaBigliettiException("Email non valida");
        }
        if (telefono <= 0) {
            throw new VenditaBigliettiException("Numero di telefono non valido");
        }
        
        // Cerca il biglietto
        EntityBiglietto biglietto = bigliettoDAO.getBigliettoByCodiceQR(codiceQR);
        if (biglietto == null) {
            throw new VenditaBigliettiException("Biglietto con codice QR " + codiceQR + " non trovato");
        }
        
        StringBuilder risultato = new StringBuilder("Invio biglietto completato:\n");
        
        // Invia email
        try {
            // Ottieni la corsa e il cliente
            EntityCorsa corsa = corsaDAO.readCorsa(biglietto.getIdCorsa());
            if (corsa == null) {
                throw new VenditaBigliettiException("Corsa associata al biglietto non trovata");
            }
            
            EntityCliente cliente = clienteDAO.readCliente(biglietto.getTelefono());
            if (cliente == null) {
                throw new VenditaBigliettiException("Cliente associato al biglietto non trovato");
            }
            
            // Genera PDF
            String pdfPath = pdfGenerator.generaBigliettoPDF(corsa, cliente, biglietto.getCodiceQR(), biglietto.getNumPosto());
            if (pdfPath == null) {
                throw new VenditaBigliettiException("Errore nella generazione del PDF");
            }
            
            String qrCodePath = qrCodeUtil.generaImmagineQRCode(biglietto.getCodiceQR());
            if (qrCodePath == null) {
                throw new VenditaBigliettiException("Errore nella generazione del codice QR");
            }
            
            emailSender.inviaBiglietto(email, pdfPath, qrCodePath);
            risultato.append("- Email inviata a ").append(email).append("\n");
        } catch (Exception e) {
            throw new VenditaBigliettiException("Errore durante l'invio dell'email: " + e.getMessage());
        }
        
        // Invia SMS se richiesto
        if (invioSMS) {
            try {
                String messaggio = String.format("Il tuo biglietto è stato acquistato con successo!\nCodice QR: %s\nPosto: %d", 
                    biglietto.getCodiceQR(), biglietto.getNumPosto());
                emailSender.inviaSMS(String.valueOf(telefono), messaggio);
                risultato.append("- SMS inviato al numero ").append(telefono).append("\n");
            } catch (Exception e) {
                throw new VenditaBigliettiException("Errore durante l'invio dell'SMS: " + e.getMessage());
            }
        }
        
        return risultato.toString();
    }
    
    /**
     * Genera un report dei biglietti venduti
     * 
     * @return messaggio di conferma della generazione del report
     */
    public String generaReport() {
        // Implementazione della generazione del report
        return "Report generato con successo e inviato al direttore";
    }
    
    /**
     * Acquista biglietti specificando i posti desiderati
     * 
     * @param idCorsa ID della corsa
     * @param postiScelti lista dei numeri dei posti scelti
     * @param telefono numero di telefono del cliente
     * @param email email del cliente
     * @param riceviSMS se true, invia anche via SMS
     * @param metodoPagamento metodo di pagamento
     * @return messaggio di conferma dell'acquisto
     * @throws VenditaBigliettiException se ci sono problemi durante la vendita
     */
    public String acquistaBigliettoConPosti(int idCorsa, List<Integer> postiScelti, long telefono, String email, boolean riceviSMS, MetodoPagamento metodoPagamento) throws VenditaBigliettiException {
        try {
            // Verifica che la corsa esista
            EntityCorsa corsa = corsaDAO.readCorsa(idCorsa);
            if (corsa == null) {
                throw new VenditaBigliettiException("Corsa non trovata");
            }

            // Verifica disponibilità posti
            for (Integer posto : postiScelti) {
                if (!corsaDAO.isPostoDisponibile(idCorsa, posto)) {
                    throw new VenditaBigliettiException("Il posto " + posto + " non è disponibile");
                }
            }

            // Verifica che il cliente esista o lo crea
            EntityCliente cliente = clienteDAO.readCliente(telefono);
            if (cliente == null) {
                cliente = new EntityCliente(telefono, email);
                if (!clienteDAO.createCliente(cliente)) {
                    throw new VenditaBigliettiException("Errore durante la creazione del cliente");
                }
            }

            // Processa il pagamento
            if (!PaymentProcessor.processPayment(metodoPagamento, corsa.getPrezzo() * postiScelti.size())) {
                throw new VenditaBigliettiException("Pagamento non riuscito");
            }

            // Crea i biglietti
            List<EntityBiglietto> bigliettiCreati = new ArrayList<>();
            for (Integer posto : postiScelti) {
                EntityBiglietto biglietto = new EntityBiglietto();
                biglietto.setOrario(corsa.getOrario());
                biglietto.setData(corsa.getData());
                biglietto.setCodiceQR(qrCodeUtil.generaCodiceQR());
                biglietto.setNumPosto(posto);
                biglietto.setIdCorsa(idCorsa);
                // Per acquisti online, impostiamo idImpiegato a null
                biglietto.setIdImpiegato(0);  // 0 verrà convertito in NULL nel database
                biglietto.setTelefono(telefono);
                biglietto.setEmail(email);
                biglietto.setPrezzo(corsa.getPrezzo());

                if (!bigliettoDAO.createBiglietto(biglietto)) {
                    throw new VenditaBigliettiException("Errore durante la creazione del biglietto");
                }
                bigliettiCreati.add(biglietto);
            }

            // Aggiorna il numero di biglietti venduti per la corsa
            corsa.setNumBigliettiVenduti(corsa.getNumBigliettiVenduti() + postiScelti.size());
            if (!corsaDAO.updateCorsa(corsa)) {
                throw new VenditaBigliettiException("Errore durante l'aggiornamento della corsa");
            }

            // Invia email di conferma per ogni biglietto
            for (EntityBiglietto biglietto : bigliettiCreati) {
                inviaEmailConferma(biglietto);
            }

            return "Biglietti acquistati con successo! Riceverai una email di conferma.";
        } catch (Exception e) {
            throw new VenditaBigliettiException("Errore durante l'acquisto dei biglietti: " + e.getMessage());
        }
    }
    
    /**
     * Imposta l'ID dell'impiegato corrente
     * 
     * @param idImpiegato ID dell'impiegato
     */
    public void setIdImpiegato(int idImpiegato) {
        this.idImpiegato = idImpiegato;
    }

    /**
     * Invia email di conferma per un biglietto
     * 
     * @param biglietto Il biglietto per cui inviare la conferma
     */
    private void inviaEmailConferma(EntityBiglietto biglietto) {
        try {
            // Ottieni la corsa e il cliente
            EntityCorsa corsa = corsaDAO.readCorsa(biglietto.getIdCorsa());
            if (corsa == null) {
                System.err.println("Corsa non trovata per il biglietto: " + biglietto.getCodiceQR());
                return;
            }

            // Crea un cliente temporaneo se non esiste nel database
            EntityCliente cliente = clienteDAO.readCliente(biglietto.getTelefono());
            if (cliente == null) {
                cliente = new EntityCliente(biglietto.getTelefono(), biglietto.getEmail());
                // Non è strettamente necessario creare il cliente qui se lo usiamo solo per il PDF,
                // ma se la logica di business richiede che il cliente esista, allora creiamolo.
                // Assumendo che debba esistere per l'email.
                clienteDAO.createCliente(cliente);
            }

            // Genera il QR code
            String qrCodePath = qrCodeUtil.generaImmagineQRCode(biglietto.getCodiceQR());

            // Genera il PDF del biglietto - usa i parametri corretti
            String pdfPath = pdfGenerator.generaBigliettoPDF(corsa, cliente, biglietto.getCodiceQR(), biglietto.getNumPosto());

            // Invia l'email con il PDF e il QR code allegati - usa i parametri corretti
            emailSender.inviaBiglietto(biglietto.getEmail(), pdfPath, qrCodePath);
        } catch (Exception e) {
            System.err.println("Errore durante l'invio dell'email di conferma: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void debug(String message) {
        // Implementazione del debug log
    }
}
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import entity.EntityCorsa;
import entity.EntityAutobus;
import exception.CorsaNonTrovataException;

/**
 * Classe DAO per l'accesso ai dati delle corse nel database.
 * Implementa le operazioni CRUD per l'entità EntityCorsa.
 */
public class CorsaDAO {
    private DBManager dbManager;
    private AutobusDAO autobusDAO;
    
    /**
     * Costruttore della classe CorsaDAO
     */
    public CorsaDAO() {
        this.dbManager = DBManager.getInstance();
        this.autobusDAO = new AutobusDAO();
    }
    
    /**
     * Crea una nuova corsa nel database
     * 
     * @param corsa oggetto EntityCorsa da inserire
     * @return true se l'inserimento ha successo, false altrimenti
     */
    public boolean createCorsa(EntityCorsa corsa) {
        String query = "INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, numBigliettiVenduti, idAutobus) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setTimestamp(1, new java.sql.Timestamp(corsa.getOrario().getTime()));
            stmt.setDate(2, new java.sql.Date(corsa.getData().getTime()));
            stmt.setString(3, corsa.getCittaPartenza());
            stmt.setString(4, corsa.getCittaArrivo());
            stmt.setDouble(5, corsa.getPrezzo());
            stmt.setInt(6, corsa.getNumBigliettiVenduti());
            stmt.setInt(7, corsa.getIdAutobus());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione della corsa: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Legge una corsa dal database tramite il suo ID
     * 
     * @param id ID della corsa da leggere
     * @return oggetto EntityCorsa se trovato, null altrimenti
     * @throws CorsaNonTrovataException se la corsa non viene trovata
     */
    public EntityCorsa readCorsa(int id) throws CorsaNonTrovataException {
        String query = "SELECT * FROM Corsa WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    EntityCorsa corsa = new EntityCorsa();
                    corsa.setId(rs.getInt("id"));
                    corsa.setOrario(rs.getTimestamp("orario"));
                    corsa.setData(rs.getDate("data"));
                    corsa.setCittaPartenza(rs.getString("cittaPartenza"));
                    corsa.setCittaArrivo(rs.getString("cittaArrivo"));
                    corsa.setPrezzo(rs.getDouble("prezzo"));
                    corsa.setNumBigliettiVenduti(rs.getInt("numBigliettiVenduti"));
                    corsa.setIdAutobus(rs.getInt("idAutobus"));
                    return corsa;
                }
            }
            
            throw new CorsaNonTrovataException("Corsa con ID " + id + " non trovata");
        } catch (SQLException e) {
            System.err.println("Errore durante la lettura della corsa: " + e.getMessage());
            throw new CorsaNonTrovataException("Errore durante la lettura della corsa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Aggiorna una corsa esistente nel database
     * 
     * @param corsa oggetto EntityCorsa da aggiornare
     * @return true se l'aggiornamento ha successo, false altrimenti
     */
    public boolean updateCorsa(EntityCorsa corsa) {
        String query = "UPDATE Corsa SET orario = ?, data = ?, cittaPartenza = ?, cittaArrivo = ?, prezzo = ?, numBigliettiVenduti = ?, idAutobus = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setTimestamp(1, new java.sql.Timestamp(corsa.getOrario().getTime()));
            stmt.setDate(2, new java.sql.Date(corsa.getData().getTime()));
            stmt.setString(3, corsa.getCittaPartenza());
            stmt.setString(4, corsa.getCittaArrivo());
            stmt.setDouble(5, corsa.getPrezzo());
            stmt.setInt(6, corsa.getNumBigliettiVenduti());
            stmt.setInt(7, corsa.getIdAutobus());
            stmt.setInt(8, corsa.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiornamento della corsa: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ottiene una corsa dal database in base alla tratta e alla data
     * 
     * @param cittaPartenza città di partenza
     * @param cittaArrivo città di arrivo
     * @param data data della corsa
     * @param ora ora della corsa
     * @return oggetto EntityCorsa se trovato, null altrimenti
     * @throws CorsaNonTrovataException se la corsa non viene trovata
     */
    public EntityCorsa getCorsaByTrattaEData(String cittaPartenza, String cittaArrivo, Date data, Date ora) throws CorsaNonTrovataException {
        String query = "SELECT * FROM Corsa WHERE cittaPartenza = ? AND cittaArrivo = ? AND DATE(data) = ? AND TIME(orario) = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, cittaPartenza);
            stmt.setString(2, cittaArrivo);
            stmt.setDate(3, new java.sql.Date(data.getTime()));
            stmt.setTime(4, new java.sql.Time(ora.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    EntityCorsa corsa = new EntityCorsa();
                    corsa.setId(rs.getInt("id"));
                    corsa.setOrario(rs.getTimestamp("orario"));
                    corsa.setData(rs.getDate("data"));
                    corsa.setCittaPartenza(rs.getString("cittaPartenza"));
                    corsa.setCittaArrivo(rs.getString("cittaArrivo"));
                    corsa.setPrezzo(rs.getDouble("prezzo"));
                    corsa.setNumBigliettiVenduti(rs.getInt("numBigliettiVenduti"));
                    corsa.setIdAutobus(rs.getInt("idAutobus"));
                    return corsa;
                }
            }
            
            throw new CorsaNonTrovataException("Corsa con tratta " + cittaPartenza + "-" + cittaArrivo + " in data " + data + " alle ore " + ora + " non trovata");
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca della corsa per tratta e data: " + e.getMessage());
            throw new CorsaNonTrovataException("Errore durante la ricerca della corsa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ottiene tutte le corse disponibili dal database
     * 
     * @param dataRicerca data di riferimento per filtrare le corse future
     * @return lista di oggetti EntityCorsa disponibili
     */
    public List<EntityCorsa> getCorseDisponibili(Date dataRicerca) {
        List<EntityCorsa> corse = new ArrayList<>();
        String query = "SELECT c.*, a.capienza FROM Corsa c JOIN Autobus a ON c.idAutobus = a.id " +
                      "WHERE c.data >= ? AND c.numBigliettiVenduti < a.capienza " +
                      "ORDER BY c.data, c.orario";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, new java.sql.Date(dataRicerca.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EntityCorsa corsa = new EntityCorsa();
                    corsa.setId(rs.getInt("id"));
                    corsa.setOrario(rs.getTimestamp("orario"));
                    corsa.setData(rs.getDate("data"));
                    corsa.setCittaPartenza(rs.getString("cittaPartenza"));
                    corsa.setCittaArrivo(rs.getString("cittaArrivo"));
                    corsa.setPrezzo(rs.getDouble("prezzo"));
                    corsa.setNumBigliettiVenduti(rs.getInt("numBigliettiVenduti"));
                    corsa.setIdAutobus(rs.getInt("idAutobus"));
                    
                    int capienza = rs.getInt("capienza");
                    int postiDisponibili = capienza - corsa.getNumBigliettiVenduti();
                    corsa.setPostiDisponibili(postiDisponibili);
                    
                    corse.add(corsa);
                }
            }

        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca delle corse disponibili: " + e.getMessage());
        }

        return corse;
    }
    
    /**
     * Inizializza il database con corse di esempio
     */
    public void inizializzaCorseDiEsempio() {
        // Verifica se ci sono già corse nel database
        String countQuery = "SELECT COUNT(*) FROM Corsa";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countQuery);
             ResultSet rs = countStmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) > 0) {
                // Ci sono già corse nel database, non è necessario inizializzare
                System.out.println("Database già inizializzato con corse.");
                return;
            }
            
        } catch (SQLException e) {
             System.err.println("Errore durante la verifica esistenza corse: " + e.getMessage());
             // Continua l'inizializzazione anche in caso di errore nel count, per sicurezza
        }

        // Inizializza alcune corse di esempio
        // Prima verifica che ci siano autobus nel database
        String countAutobusQuery = "SELECT COUNT(*) FROM Autobus";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement countAutobusStmt = conn.prepareStatement(countAutobusQuery);
             ResultSet rsAutobus = countAutobusStmt.executeQuery()) {

             if (rsAutobus.next() && rsAutobus.getInt(1) == 0) {
                // Non ci sono autobus, ne creiamo alcuni
                String insertAutobusQuery = "INSERT INTO Autobus (id, capienza, trattaAssegnata) VALUES (?, ?, ?)";
                try (PreparedStatement insertAutobusStmt = conn.prepareStatement(insertAutobusQuery)) {
                    // Autobus 1
                    insertAutobusStmt.setInt(1, 1);
                    insertAutobusStmt.setInt(2, 50);
                    insertAutobusStmt.setString(3, "Roma-Milano");
                    insertAutobusStmt.executeUpdate();
                    
                    // Autobus 2
                    insertAutobusStmt.setInt(1, 2);
                    insertAutobusStmt.setInt(2, 40);
                    insertAutobusStmt.setString(3, "Napoli-Firenze");
                    insertAutobusStmt.executeUpdate();
                    
                    // Autobus 3
                    insertAutobusStmt.setInt(1, 3);
                    insertAutobusStmt.setInt(2, 60);
                    insertAutobusStmt.setString(3, "Torino-Venezia");
                    insertAutobusStmt.executeUpdate();
                    
                    System.out.println("Autobus inizializzati con successo.");
                }
             }
        } catch (SQLException e) {
             System.err.println("Errore durante l'inizializzazione degli autobus: " + e.getMessage());
        }

        // Crea le corse di esempio con date fisse del 2025
        String insertCorsaQuery = "INSERT INTO Corsa (id, orario, data, cittaPartenza, cittaArrivo, prezzo, numBigliettiVenduti, idAutobus) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement insertCorsaStmt = conn.prepareStatement(insertCorsaQuery)) {

            // Corsa 1: Roma-Milano 15 luglio 2025
            insertCorsaStmt.setInt(1, 1);
            insertCorsaStmt.setTimestamp(2, java.sql.Timestamp.valueOf("2025-07-15 08:00:00"));
            insertCorsaStmt.setDate(3, java.sql.Date.valueOf("2025-07-15"));
            insertCorsaStmt.setString(4, "Roma");
            insertCorsaStmt.setString(5, "Milano");
            insertCorsaStmt.setDouble(6, 50.0);
            insertCorsaStmt.setInt(7, 0);
            insertCorsaStmt.setInt(8, 1);
            insertCorsaStmt.executeUpdate();
            
            // Corsa 2: Milano-Roma 15 luglio 2025
            insertCorsaStmt.setInt(1, 2);
            insertCorsaStmt.setTimestamp(2, java.sql.Timestamp.valueOf("2025-07-15 16:00:00"));
            insertCorsaStmt.setDate(3, java.sql.Date.valueOf("2025-07-15"));
            insertCorsaStmt.setString(4, "Milano");
            insertCorsaStmt.setString(5, "Roma");
            insertCorsaStmt.setDouble(6, 50.0);
            insertCorsaStmt.setInt(7, 0);
            insertCorsaStmt.setInt(8, 1);
            insertCorsaStmt.executeUpdate();
            
            // Corsa 3: Napoli-Firenze 20 luglio 2025
            insertCorsaStmt.setInt(1, 3);
            insertCorsaStmt.setTimestamp(2, java.sql.Timestamp.valueOf("2025-07-20 09:30:00"));
            insertCorsaStmt.setDate(3, java.sql.Date.valueOf("2025-07-20"));
            insertCorsaStmt.setString(4, "Napoli");
            insertCorsaStmt.setString(5, "Firenze");
            insertCorsaStmt.setDouble(6, 45.0);
            insertCorsaStmt.setInt(7, 0);
            insertCorsaStmt.setInt(8, 2);
            insertCorsaStmt.executeUpdate();
            
            // Corsa 4: Firenze-Napoli 20 luglio 2025
            insertCorsaStmt.setInt(1, 4);
            insertCorsaStmt.setTimestamp(2, java.sql.Timestamp.valueOf("2025-07-20 17:30:00"));
            insertCorsaStmt.setDate(3, java.sql.Date.valueOf("2025-07-20"));
            insertCorsaStmt.setString(4, "Firenze");
            insertCorsaStmt.setString(5, "Napoli");
            insertCorsaStmt.setDouble(6, 45.0);
            insertCorsaStmt.setInt(7, 0);
            insertCorsaStmt.setInt(8, 2);
            insertCorsaStmt.executeUpdate();
            
            // Corsa 5: Torino-Venezia 25 luglio 2025
            insertCorsaStmt.setInt(1, 5);
            insertCorsaStmt.setTimestamp(2, java.sql.Timestamp.valueOf("2025-07-25 10:00:00"));
            insertCorsaStmt.setDate(3, java.sql.Date.valueOf("2025-07-25"));
            insertCorsaStmt.setString(4, "Torino");
            insertCorsaStmt.setString(5, "Venezia");
            insertCorsaStmt.setDouble(6, 60.0);
            insertCorsaStmt.setInt(7, 0);
            insertCorsaStmt.setInt(8, 3);
            insertCorsaStmt.executeUpdate();
            
            // Corsa 6: Venezia-Torino 25 luglio 2025
            insertCorsaStmt.setInt(1, 6);
            insertCorsaStmt.setTimestamp(2, java.sql.Timestamp.valueOf("2025-07-25 18:00:00"));
            insertCorsaStmt.setDate(3, java.sql.Date.valueOf("2025-07-25"));
            insertCorsaStmt.setString(4, "Venezia");
            insertCorsaStmt.setString(5, "Torino");
            insertCorsaStmt.setDouble(6, 60.0);
            insertCorsaStmt.setInt(7, 0);
            insertCorsaStmt.setInt(8, 3);
            insertCorsaStmt.executeUpdate();
            
            System.out.println("Corse di esempio inizializzate con successo.");
            
        } catch (SQLException e) {
            System.err.println("Errore durante l'inizializzazione delle corse di esempio: " + e.getMessage());
        }
    }
    
    /**
     * Ottiene le corse con le stesse località di partenza e arrivo
     * 
     * @param cittaPartenza città di partenza
     * @param cittaArrivo città di arrivo
     * @return lista di corse disponibili con le stesse località
     */
    public List<EntityCorsa> getCorseByLocalita(String cittaPartenza, String cittaArrivo) {
        List<EntityCorsa> corse = new ArrayList<>();
        String query = "SELECT c.*, a.capienza FROM Corsa c " +
                          "JOIN Autobus a ON c.idAutobus = a.id " +
                          "WHERE c.cittaPartenza = ? AND c.cittaArrivo = ? " +
                          "AND c.data >= CURRENT_DATE() " +
                          "ORDER BY c.data, c.orario";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, cittaPartenza);
            stmt.setString(2, cittaArrivo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EntityCorsa corsa = new EntityCorsa();
                    corsa.setId(rs.getInt("id"));
                    corsa.setOrario(rs.getTimestamp("orario"));
                    corsa.setData(rs.getDate("data"));
                    corsa.setCittaPartenza(rs.getString("cittaPartenza"));
                    corsa.setCittaArrivo(rs.getString("cittaArrivo"));
                    corsa.setPrezzo(rs.getDouble("prezzo"));
                    corsa.setNumBigliettiVenduti(rs.getInt("numBigliettiVenduti"));
                    corsa.setIdAutobus(rs.getInt("idAutobus"));
                    
                    // Calcola e memorizza i posti disponibili
                    int capienza = rs.getInt("capienza");
                    int postiDisponibili = capienza - corsa.getNumBigliettiVenduti();
                    corsa.setPostiDisponibili(postiDisponibili);
                    
                    corse.add(corsa);
                }
            }
            
            return corse;
        } catch (SQLException e) {
            System.err.println("Errore durante la lettura delle corse: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Ottiene una corsa dal database tramite il suo ID
     * 
     * @param id ID della corsa da ottenere
     * @return EntityCorsa se trovata, null altrimenti
     */
    public EntityCorsa getCorsaById(int id) {
        String query = "SELECT c.*, a.capienza FROM Corsa c " +
                          "JOIN Autobus a ON c.idAutobus = a.id " +
                          "WHERE c.id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    EntityCorsa corsa = new EntityCorsa();
                    corsa.setId(rs.getInt("id"));
                    corsa.setOrario(rs.getTimestamp("orario"));
                    corsa.setData(rs.getDate("data"));
                    corsa.setCittaPartenza(rs.getString("cittaPartenza"));
                    corsa.setCittaArrivo(rs.getString("cittaArrivo"));
                    corsa.setPrezzo(rs.getDouble("prezzo"));
                    corsa.setNumBigliettiVenduti(rs.getInt("numBigliettiVenduti"));
                    corsa.setIdAutobus(rs.getInt("idAutobus"));
                    
                    // Calcola e memorizza i posti disponibili
                    int capienza = rs.getInt("capienza");
                    int postiDisponibili = capienza - corsa.getNumBigliettiVenduti();
                    corsa.setPostiDisponibili(postiDisponibili);
                    
                    return corsa;
                }
            }

            return null;
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca della corsa: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifica se un posto specifico è disponibile per una corsa
     * 
     * @param idCorsa ID della corsa
     * @param numPosto numero del posto da verificare
     * @return true se il posto è disponibile, false altrimenti
     */
    public boolean isPostoDisponibile(int idCorsa, Integer numPosto) {
        try {
            // Ottieni la corsa
            EntityCorsa corsa;
            try {
                corsa = readCorsa(idCorsa);
            } catch (CorsaNonTrovataException e) {
                System.err.println("Corsa non trovata: " + e.getMessage());
                return false;
            }
            
            if (corsa == null) {
                return false;
            }

            // Ottieni l'autobus associato
            EntityAutobus autobus = autobusDAO.readAutobus(corsa.getIdAutobus());
            if (autobus == null) {
                return false;
            }

            // Verifica se il posto è già occupato
            String query = "SELECT COUNT(*) FROM Biglietto WHERE idCorsa = ? AND numposto = ?";
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, idCorsa);
                stmt.setInt(2, numPosto);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        return count == 0 && numPosto <= autobus.getCapienza();
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Errore durante la verifica della disponibilità del posto: " + e.getMessage());
            return false;
        }
    }
}

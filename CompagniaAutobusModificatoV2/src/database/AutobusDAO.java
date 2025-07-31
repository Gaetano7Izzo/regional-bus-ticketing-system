package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import entity.EntityAutobus;
import entity.EntityCorsa;
import exception.CorsaNonTrovataException;

/**
 * Classe DAO per l'accesso ai dati degli autobus nel database.
 * Implementa le operazioni CRUD per l'entità EntityAutobus.
 */
public class AutobusDAO {
    private DBManager dbManager;
    
    /**
     * Costruttore della classe AutobusDAO
     */
    public AutobusDAO() {
        this.dbManager = DBManager.getInstance();
    }
    
    /**
     * Crea un nuovo autobus nel database.
     * 
     * @param autobus l'oggetto EntityAutobus da inserire.
     * @return true se l'inserimento è avvenuto con successo, false altrimenti.
     */
    public boolean createAutobus(EntityAutobus autobus) {
        String query = "INSERT INTO Autobus (capienza, trattaAssegnata) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, autobus.getCapienza());
            stmt.setString(2, autobus.getTrattaAssegnata());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione dell'autobus: " + e.getMessage());
            return false;
        }
    }

    /**
     * Legge un autobus dal database tramite il suo ID.
     * 
     * @param id l'ID dell'autobus da leggere.
     * @return l'oggetto EntityAutobus se trovato, altrimenti null.
     */
    public EntityAutobus readAutobus(int id) {
        String query = "SELECT * FROM Autobus WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    EntityAutobus autobus = new EntityAutobus();
                    autobus.setId(rs.getInt("id"));
                    autobus.setCapienza(rs.getInt("capienza"));
                    autobus.setTrattaAssegnata(rs.getString("trattaAssegnata"));
                    return autobus;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la lettura dell'autobus: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Aggiorna un autobus esistente nel database.
     * 
     * @param autobus l'oggetto EntityAutobus da aggiornare.
     * @return true se l'aggiornamento è avvenuto con successo, false altrimenti.
     */
    public boolean updateAutobus(EntityAutobus autobus) {
        String query = "UPDATE Autobus SET capienza = ?, trattaAssegnata = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, autobus.getCapienza());
            stmt.setString(2, autobus.getTrattaAssegnata());
            stmt.setInt(3, autobus.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiornamento dell'autobus: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un autobus dal database tramite il suo ID.
     * Nota: Assicurarsi che non ci siano corse associate prima di eliminare un autobus.
     * 
     * @param id l'ID dell'autobus da eliminare.
     * @return true se l'eliminazione è avvenuta con successo, false altrimenti.
     */
    public boolean deleteAutobus(int id) {
        String query = "DELETE FROM Autobus WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione dell'autobus: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ottiene tutti gli autobus presenti nel database.
     * 
     * @return una lista di oggetti EntityAutobus.
     */
    public List<EntityAutobus> getAllAutobus() {
        List<EntityAutobus> autobusList = new ArrayList<>();
        String query = "SELECT * FROM Autobus";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                EntityAutobus autobus = new EntityAutobus();
                autobus.setId(rs.getInt("id"));
                autobus.setCapienza(rs.getInt("capienza"));
                autobus.setTrattaAssegnata(rs.getString("trattaAssegnata"));
                autobusList.add(autobus);
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero di tutti gli autobus: " + e.getMessage());
        }
        return autobusList;
    }
    
    /**
     * Inizializza il database con autobus di esempio se non esistono già
     */
    public void inizializzaAutobusDiEsempio() {
        // Verifica se ci sono già autobus nel database
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbManager.getConnection();
            String query = "SELECT COUNT(*) FROM Autobus";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Ci sono già autobus nel database, non è necessario inizializzare
                System.out.println("Database già inizializzato con autobus.");
                return;
            }
            
            // Crea gli autobus di esempio
            EntityAutobus autobus1 = new EntityAutobus();
            autobus1.setId(1);
            autobus1.setCapienza(50);
            autobus1.setTrattaAssegnata("Roma-Milano");
            createAutobus(autobus1);
            
            EntityAutobus autobus2 = new EntityAutobus();
            autobus2.setId(2);
            autobus2.setCapienza(40);
            autobus2.setTrattaAssegnata("Napoli-Firenze");
            createAutobus(autobus2);
            
            EntityAutobus autobus3 = new EntityAutobus();
            autobus3.setId(3);
            autobus3.setCapienza(60);
            autobus3.setTrattaAssegnata("Torino-Venezia");
            createAutobus(autobus3);
            
            System.out.println("Autobus di esempio inizializzati con successo.");
            
        } catch (SQLException e) {
            System.err.println("Errore durante l'inizializzazione degli autobus di esempio: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Errore nella chiusura delle risorse: " + e.getMessage());
            }
        }
    }
}

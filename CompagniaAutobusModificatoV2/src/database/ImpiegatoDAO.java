package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.EntityImpiegato;
import exception.AutenticazioneException;

/**
 * Classe DAO per l'accesso ai dati degli impiegati nel database.
 * Implementa le operazioni CRUD per l'entità EntityImpiegato.
 */
public class ImpiegatoDAO {
    private DBManager dbManager;
    
    /**
     * Costruttore della classe ImpiegatoDAO
     */
    public ImpiegatoDAO() {
        this.dbManager = DBManager.getInstance();
    }
    
    /**
     * Crea un nuovo impiegato nel database
     * 
     * @param impiegato oggetto EntityImpiegato da inserire
     * @return true se l'inserimento ha successo, false altrimenti
     */
    public boolean createImpiegato(EntityImpiegato impiegato) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbManager.getConnection();
            String query = "INSERT INTO Impiegato (idImpiegato) VALUES (?)";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, impiegato.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione dell'impiegato: " + e.getMessage());
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Errore nella chiusura dello statement: " + e.getMessage());
            }
        }
    }
    
    /**
     * Legge un impiegato dal database tramite il suo ID
     * 
     * @param idImpiegato ID dell'impiegato da leggere
     * @return oggetto EntityImpiegato se trovato
     * @throws AutenticazioneException se l'impiegato non viene trovato
     */
    public EntityImpiegato readImpiegato(int idImpiegato) throws AutenticazioneException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbManager.getConnection();
            String query = "SELECT * FROM Impiegato WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, idImpiegato);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                EntityImpiegato impiegato = new EntityImpiegato();
                impiegato.setId(rs.getInt("id"));
                return impiegato;
            }
            
            throw new AutenticazioneException("Impiegato con ID " + idImpiegato + " non trovato");
        } catch (SQLException e) {
            System.err.println("Errore durante la lettura dell'impiegato: " + e.getMessage());
            throw new AutenticazioneException("Errore durante l'autenticazione: " + e.getMessage(), e);
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

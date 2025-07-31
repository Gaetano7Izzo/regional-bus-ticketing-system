package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entity.EntityCliente;

/**
 * Classe DAO per l'accesso ai dati dei clienti nel database.
 * Implementa le operazioni CRUD per l'entità EntityCliente.
 */
public class ClienteDAO {
    private DBManager dbManager;
    
    /**
     * Costruttore della classe ClienteDAO
     */
    public ClienteDAO() {
        this.dbManager = DBManager.getInstance();
    }
    
    /**
     * Crea un nuovo cliente nel database
     * 
     * @param cliente oggetto EntityCliente da inserire
     * @return true se l'inserimento ha successo, false altrimenti
     */
    public boolean createCliente(EntityCliente cliente) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbManager.getConnection();
            String query = "INSERT INTO Cliente (telefono, email) VALUES (?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setLong(1, cliente.getNumTelefono());
            stmt.setString(2, cliente.getEmail());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione del cliente: " + e.getMessage());
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
     * Legge un cliente dal database tramite il suo numero di telefono
     * 
     * @param telefono numero di telefono del cliente da leggere
     * @return oggetto EntityCliente se trovato, null altrimenti
     */
    public EntityCliente readCliente(long telefono) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbManager.getConnection();
            String query = "SELECT * FROM Cliente WHERE telefono = ?";
            stmt = conn.prepareStatement(query);
            stmt.setLong(1, telefono);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                EntityCliente cliente = new EntityCliente();
                cliente.setNumTelefono(rs.getLong("telefono"));
                cliente.setEmail(rs.getString("email"));
                return cliente;
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Errore durante la lettura del cliente: " + e.getMessage());
            return null;
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

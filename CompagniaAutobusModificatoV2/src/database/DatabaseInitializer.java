package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe per l'inizializzazione del database
 */
public class DatabaseInitializer {
    private DBManager dbManager;
    
    /**
     * Costruttore della classe DatabaseInitializer
     */
    public DatabaseInitializer() {
        this.dbManager = DBManager.getInstance();
    }
    
    /**
     * Crea le tabelle nel database se non esistono
     */
    private void createTables() {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Tabella Corsa
            stmt.execute("CREATE TABLE IF NOT EXISTS Corsa (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "cittaPartenza VARCHAR(100), " +
                    "cittaArrivo VARCHAR(100), " +
                    "orario TIMESTAMP, " +
                    "data DATE, " +
                    "prezzo DOUBLE, " +
                    "postiDisponibili INT)");
            
            // Tabella Biglietto
            stmt.execute("CREATE TABLE IF NOT EXISTS Biglietto (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "data DATE, " +
                    "orario TIMESTAMP, " +
                    "codiceQR VARCHAR(255), " +
                    "numposto INT, " +
                    "idCorsa INT, " +
                    "idImpiegato INT NULL, " +
                    "telefono BIGINT, " +
                    "email VARCHAR(255), " +
                    "prezzo DOUBLE, " +
                    "FOREIGN KEY (idCorsa) REFERENCES Corsa(id), " +
                    "FOREIGN KEY (idImpiegato) REFERENCES Impiegato(idImpiegato))");
            
            // Tabella Impiegato
            stmt.execute("CREATE TABLE IF NOT EXISTS Impiegato (" +
                    "idImpiegato INT PRIMARY KEY)");
            
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione delle tabelle: " + e.getMessage());
        }
    }
} 
package database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe di utilità per la gestione delle connessioni al database.
 * Utilizza il pattern Singleton attraverso DBManager.
 */
public class DatabaseConnection {
    
    /**
     * Ottiene una connessione al database utilizzando il DBManager.
     * 
     * @return la connessione al database
     * @throws SQLException se si verifica un errore durante la connessione
     */
    public static Connection getConnection() throws SQLException {
        return DBManager.getInstance().getConnection();
    }
    
    /**
     * Chiude la connessione al database.
     */
    public static void closeConnection() {
        DBManager.getInstance().closeConnection();
    }
} 
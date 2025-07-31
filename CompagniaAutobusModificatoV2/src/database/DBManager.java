package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe che gestisce la connessione al database.
 * Implementa il pattern Singleton per garantire una singola istanza di connessione.
 */
public class DBManager {
    private static DBManager instance = null;
    private Connection connection = null;
    private String dbType = "h2"; // Default a H2
    
    // Parametri di connessione per MySQL
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/trasporto_regionale";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "";
    
    // Parametri di connessione per H2
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:./trasporto_regionale_db";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    
    /**
     * Costruttore privato (pattern Singleton)
     */
    private DBManager() {
        // Il costruttore è privato per implementare il pattern Singleton
    }
    
    /**
     * Restituisce l'istanza singleton di DBManager
     * 
     * @return l'istanza di DBManager
     */
    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }
    
    /**
     * Imposta il tipo di database da utilizzare
     * 
     * @param dbType il tipo di database ("mysql" o "h2")
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
        // Chiudi la connessione esistente se presente
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
            }
        }
    }
    
    /**
     * Ottiene una connessione al database
     * 
     * @return la connessione al database
     * @throws SQLException se si verifica un errore durante la connessione
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                if ("mysql".equalsIgnoreCase(dbType)) {
                    // Connessione a MySQL
                    Class.forName(MYSQL_DRIVER);
                    connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                } else {
                    // Connessione a H2 (default)
                    try {
                        System.out.println("Tentativo di caricamento del driver H2...");
                        Class.forName(H2_DRIVER);
                        System.out.println("Driver H2 caricato con successo");
                    } catch (ClassNotFoundException e) {
                        System.err.println("Driver H2 non trovato nel classpath. Verificare che il file h2-1.4.200.jar sia nella cartella lib");
                        throw new SQLException("Driver del database non trovato", e);
                    }
                    
                    try {
                        System.out.println("Tentativo di connessione al database H2...");
                        connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
                        System.out.println("Connessione al database H2 stabilita con successo");
                        
                        // Verifica se le tabelle esistono e inizializza il database se necessario
                        if (!tableExists("CORSA")) {
                            System.out.println("Tabelle non trovate. Inizializzazione del database H2...");
                            initializeH2Database();
                        }
                    } catch (SQLException e) {
                        System.err.println("Errore durante la connessione al database H2: " + e.getMessage());
                        throw e;
                    }
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Driver del database non trovato: " + e.getMessage());
                throw new SQLException("Driver del database non trovato", e);
            }
        }
        return connection;
    }
    
    /**
     * Verifica se una tabella esiste nel database
     * 
     * @param tableName nome della tabella da verificare
     * @return true se la tabella esiste, false altrimenti
     */
    private boolean tableExists(String tableName) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getTables(null, null, tableName, new String[] {"TABLE"});
            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (SQLException e) {
            System.err.println("Errore durante la verifica dell'esistenza della tabella: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inizializza il database H2 con le tabelle e i dati di esempio
     */
    private void initializeH2Database() {
        try {
            // Leggi lo script SQL dal file
            String sqlScript = readSqlScript();
            if (sqlScript != null && !sqlScript.isEmpty()) {
                System.out.println("Esecuzione dello script di inizializzazione del database...");
                executeSqlScript(sqlScript);
                System.out.println("Database H2 inizializzato con successo!");
            } else {
                System.err.println("Script SQL vuoto o non trovato.");
            }
        } catch (Exception e) {
            System.err.println("Errore durante l'inizializzazione del database H2: " + e.getMessage());
        }
    }
    
    /**
     * Legge lo script SQL dal file database_init.sql
     * 
     * @return il contenuto dello script SQL
     */
    private String readSqlScript() {
        StringBuilder script = new StringBuilder();
        
        // Prova prima a leggere il file dalla directory del progetto
        try {
            BufferedReader reader = new BufferedReader(new FileReader("database_init.sql"));
            String line;
            while ((line = reader.readLine()) != null) {
                script.append(line).append("\n");
            }
            reader.close();
            return script.toString();
        } catch (IOException e) {
            System.out.println("File database_init.sql non trovato nella directory principale. Tentativo di lettura dalle risorse...");
        }
        
        // Se il file non è stato trovato, prova a leggerlo dalle risorse
        try {
            // Script SQL incorporato nel codice come fallback
            script = new StringBuilder();
            script.append("-- Creazione delle tabelle per il sistema di gestione della compagnia di trasporto regionale\n");
            
            // Tabella Autobus
            script.append("CREATE TABLE IF NOT EXISTS Autobus (\n");
            script.append("    idAutobus INT PRIMARY KEY,\n");
            script.append("    capienza INT NOT NULL,\n");
            script.append("    trattaAssegnata VARCHAR(100) NOT NULL\n");
            script.append(");\n");
            
            // Tabella Corsa
            script.append("CREATE TABLE IF NOT EXISTS Corsa (\n");
            script.append("    idCorsa INT PRIMARY KEY,\n");
            script.append("    orario TIMESTAMP NOT NULL,\n");
            script.append("    data DATE NOT NULL,\n");
            script.append("    citta_partenza VARCHAR(50) NOT NULL,\n");
            script.append("    citta_arrivo VARCHAR(50) NOT NULL,\n");
            script.append("    prezzo INT NOT NULL,\n");
            script.append("    numBigliettiVenduti INT NOT NULL DEFAULT 0,\n");
            script.append("    idAutobus INT NOT NULL,\n");
            script.append("    FOREIGN KEY (idAutobus) REFERENCES Autobus(idAutobus)\n");
            script.append(");\n");
            
            // Tabella Cliente
            script.append("CREATE TABLE IF NOT EXISTS Cliente (\n");
            script.append("    telefono INT PRIMARY KEY,\n");
            script.append("    email VARCHAR(100) NOT NULL\n");
            script.append(");\n");
            
            // Tabella Biglietto
            script.append("CREATE TABLE IF NOT EXISTS Biglietto (\n");
            script.append("    idBiglietto INT PRIMARY KEY AUTO_INCREMENT,\n");
            script.append("    orario TIMESTAMP NOT NULL,\n");
            script.append("    data DATE NOT NULL,\n");
            script.append("    codiceQR VARCHAR(100) NOT NULL,\n");
            script.append("    numPosto INT NOT NULL,\n");
            script.append("    idCorsa INT NOT NULL,\n");
            script.append("    FOREIGN KEY (idCorsa) REFERENCES Corsa(idCorsa)\n");
            script.append(");\n");
            
            // Tabella Impiegato
            script.append("CREATE TABLE IF NOT EXISTS Impiegato (\n");
            script.append("    idImpiegato INT PRIMARY KEY\n");
            script.append(");\n");
            
            // Inserimento dati di esempio
            
            // Autobus
            script.append("INSERT INTO Autobus (idAutobus, capienza, trattaAssegnata) VALUES (1, 50, 'Roma-Milano');\n");
            script.append("INSERT INTO Autobus (idAutobus, capienza, trattaAssegnata) VALUES (2, 40, 'Napoli-Firenze');\n");
            script.append("INSERT INTO Autobus (idAutobus, capienza, trattaAssegnata) VALUES (3, 60, 'Torino-Venezia');\n");
            
            // Corse (con date relative alla data corrente)
            script.append("INSERT INTO Corsa (idCorsa, orario, data, citta_partenza, citta_arrivo, prezzo, numBigliettiVenduti, idAutobus) VALUES (1, DATEADD('HOUR', 8, CURRENT_DATE()), CURRENT_DATE(), 'Roma', 'Milano', 50, 0, 1);\n");
            script.append("INSERT INTO Corsa (idCorsa, orario, data, citta_partenza, citta_arrivo, prezzo, numBigliettiVenduti, idAutobus) VALUES (2, DATEADD('HOUR', 16, CURRENT_DATE()), CURRENT_DATE(), 'Milano', 'Roma', 50, 0, 1);\n");
            script.append("INSERT INTO Corsa (idCorsa, orario, data, citta_partenza, citta_arrivo, prezzo, numBigliettiVenduti, idAutobus) VALUES (3, DATEADD('HOUR', 9, DATEADD('DAY', 1, CURRENT_DATE())), DATEADD('DAY', 1, CURRENT_DATE()), 'Napoli', 'Firenze', 45, 0, 2);\n");
            script.append("INSERT INTO Corsa (idCorsa, orario, data, citta_partenza, citta_arrivo, prezzo, numBigliettiVenduti, idAutobus) VALUES (4, DATEADD('HOUR', 17, DATEADD('DAY', 1, CURRENT_DATE())), DATEADD('DAY', 1, CURRENT_DATE()), 'Firenze', 'Napoli', 45, 0, 2);\n");
            script.append("INSERT INTO Corsa (idCorsa, orario, data, citta_partenza, citta_arrivo, prezzo, numBigliettiVenduti, idAutobus) VALUES (5, DATEADD('HOUR', 10, DATEADD('DAY', 2, CURRENT_DATE())), DATEADD('DAY', 2, CURRENT_DATE()), 'Torino', 'Venezia', 60, 0, 3);\n");
            script.append("INSERT INTO Corsa (idCorsa, orario, data, citta_partenza, citta_arrivo, prezzo, numBigliettiVenduti, idAutobus) VALUES (6, DATEADD('HOUR', 18, DATEADD('DAY', 2, CURRENT_DATE())), DATEADD('DAY', 2, CURRENT_DATE()), 'Venezia', 'Torino', 60, 0, 3);\n");
            
            // Impiegati
            script.append("INSERT INTO Impiegato (idImpiegato) VALUES (1);\n");
            script.append("INSERT INTO Impiegato (idImpiegato) VALUES (2);\n");
            script.append("INSERT INTO Impiegato (idImpiegato) VALUES (3);\n");
            
            return script.toString();
        } catch (Exception e) {
            System.err.println("Errore durante la lettura dello script SQL incorporato: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Esegue uno script SQL
     * 
     * @param sqlScript lo script SQL da eseguire
     */
    private void executeSqlScript(String sqlScript) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            
            // Dividi lo script in singole istruzioni SQL
            String[] sqlStatements = sqlScript.split(";");
            
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        System.err.println("Errore nell'esecuzione dell'istruzione SQL: " + sql);
                        System.err.println("Messaggio di errore: " + e.getMessage());
                        // Continua con le altre istruzioni
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'esecuzione dello script SQL: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("Errore durante la chiusura dello statement: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Chiude la connessione al database
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
            }
        }
    }
    
    /**
     * Esegue l'inizializzazione del database
     * 
     * @param sqlScript lo script SQL da eseguire
     * @return true se l'inizializzazione ha successo, false altrimenti
     */
    public boolean initializeDatabase(String sqlScript) {
        try {
            Connection conn = getConnection();
            java.sql.Statement stmt = conn.createStatement();
            
            // Dividi lo script in singole istruzioni SQL
            String[] sqlStatements = sqlScript.split(";");
            
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Errore durante l'inizializzazione del database: " + e.getMessage());
            return false;
        }
    }
}

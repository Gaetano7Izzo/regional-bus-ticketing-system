package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.EntityBiglietto;
import exception.VenditaBigliettiException;

/**
 * Classe DAO per l'accesso ai dati dei biglietti nel database.
 * Implementa le operazioni CRUD per l'entità EntityBiglietto.
 */
public class BigliettoDAO {
    private DBManager dbManager;
    
    /**
     * Costruttore della classe BigliettoDAO
     */
    public BigliettoDAO() {
        this.dbManager = DBManager.getInstance();
    }
    
    /**
     * Crea un nuovo biglietto nel database
     * 
     * @param biglietto oggetto EntityBiglietto da inserire
     * @return true se l'inserimento ha successo, false altrimenti
     * @throws VenditaBigliettiException se ci sono problemi durante la creazione
     */
    public boolean createBiglietto(EntityBiglietto biglietto) throws VenditaBigliettiException {
        String sql = "INSERT INTO Biglietto (orario, data, codiceQR, numPosto, idCorsa, idImpiegato, telefono, email, prezzo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            System.out.println("Preparazione statement per inserimento biglietto...");
            pstmt.setTimestamp(1, new java.sql.Timestamp(biglietto.getOrario().getTime()));
            pstmt.setDate(2, new java.sql.Date(biglietto.getData().getTime()));
            pstmt.setString(3, biglietto.getCodiceQR());
            pstmt.setInt(4, biglietto.getNumPosto());
            pstmt.setInt(5, biglietto.getIdCorsa());
            if (biglietto.getIdImpiegato() == 0) {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(6, biglietto.getIdImpiegato());
            }
            pstmt.setLong(7, biglietto.getTelefono());
            pstmt.setString(8, biglietto.getEmail());
            pstmt.setDouble(9, biglietto.getPrezzo());
            
            System.out.println("Esecuzione inserimento biglietto nel database...");
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Biglietto inserito con successo, recupero ID generato...");
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        biglietto.setId(generatedKeys.getInt(1));
                        System.out.println("ID biglietto generato: " + biglietto.getId());
                    }
                }
                return true;
            }
            System.err.println("Nessuna riga inserita nel database");
            return false;
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione del biglietto: " + e.getMessage());
            throw new VenditaBigliettiException("Errore durante la creazione del biglietto: " + e.getMessage());
        }
    }
    
    /**
     * Aggiorna un biglietto esistente nel database
     * 
     * @param biglietto oggetto EntityBiglietto da aggiornare
     * @return true se l'aggiornamento ha successo, false altrimenti
     */
    public boolean updateBiglietto(EntityBiglietto biglietto) {
        String query = "UPDATE Biglietto SET orario = ?, data = ?, codiceQR = ?, numposto = ?, idCorsa = ?, idImpiegato = ?, telefono = ?, email = ?, prezzo = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setTimestamp(1, new java.sql.Timestamp(biglietto.getOrario().getTime()));
            stmt.setDate(2, new java.sql.Date(biglietto.getData().getTime()));
            stmt.setString(3, biglietto.getCodiceQR());
            if (biglietto.getNumPosto() != null) {
                 stmt.setInt(4, biglietto.getNumPosto());
            } else {
                 stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setInt(5, biglietto.getIdCorsa());
            if (biglietto.getIdImpiegato() != 0) {
                 stmt.setInt(6, biglietto.getIdImpiegato());
            } else {
                 stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.setLong(7, biglietto.getTelefono());
            stmt.setString(8, biglietto.getEmail());
            stmt.setDouble(9, biglietto.getPrezzo());
            stmt.setInt(10, biglietto.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiornamento del biglietto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ottiene un biglietto dal database tramite il suo codice QR
     * 
     * @param codiceQR codice QR del biglietto
     * @return oggetto EntityBiglietto se trovato, null altrimenti
     */
    public EntityBiglietto getBigliettoByCodiceQR(String codiceQR) {
        String query = "SELECT * FROM Biglietto WHERE codiceQR = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, codiceQR);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    EntityBiglietto biglietto = new EntityBiglietto();
                    biglietto.setId(rs.getInt("id"));
                    biglietto.setOrario(rs.getTimestamp("orario"));
                    biglietto.setData(rs.getDate("data"));
                    biglietto.setCodiceQR(rs.getString("codiceQR"));
                    Object numPostoObj = rs.getObject("numposto");
                    if (numPostoObj != null) {
                         biglietto.setNumPosto(rs.getInt("numposto"));
                    } else {
                         biglietto.setNumPosto(null);
                    }
                    biglietto.setIdCorsa(rs.getInt("idCorsa"));
                    Object idImpiegatoObj = rs.getObject("idImpiegato");
                    if (idImpiegatoObj != null) {
                         biglietto.setIdImpiegato(rs.getInt("idImpiegato"));
                    } else {
                         biglietto.setIdImpiegato(0);
                    }
                    biglietto.setTelefono(rs.getLong("telefono"));
                    biglietto.setEmail(rs.getString("email"));
                    biglietto.setPrezzo(rs.getDouble("prezzo"));
                    return biglietto;
                }
            }
            
            return null;
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca del biglietto per codice QR: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Ottiene tutti i biglietti per una specifica corsa
     * 
     * @param idCorsa ID della corsa
     * @return lista di oggetti EntityBiglietto
     */
    public List<EntityBiglietto> getBigliettiByCorsa(int idCorsa) {
        List<EntityBiglietto> biglietti = new ArrayList<>();
        String query = "SELECT * FROM Biglietto WHERE idCorsa = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idCorsa);
            
            try (ResultSet rs = stmt.executeQuery()) {
                 while (rs.next()) {
                    EntityBiglietto biglietto = new EntityBiglietto();
                    biglietto.setId(rs.getInt("id"));
                    biglietto.setOrario(rs.getTimestamp("orario"));
                    biglietto.setData(rs.getDate("data"));
                    biglietto.setCodiceQR(rs.getString("codiceQR"));
                    Object numPostoObj = rs.getObject("numposto");
                    if (numPostoObj != null) {
                         biglietto.setNumPosto(rs.getInt("numposto"));
                    } else {
                         biglietto.setNumPosto(null);
                    }
                    biglietto.setIdCorsa(rs.getInt("idCorsa"));
                    Object idImpiegatoObj = rs.getObject("idImpiegato");
                    if (idImpiegatoObj != null) {
                         biglietto.setIdImpiegato(rs.getInt("idImpiegato"));
                    } else {
                         biglietto.setIdImpiegato(0);
                    }
                    biglietto.setTelefono(rs.getLong("telefono"));
                    biglietto.setEmail(rs.getString("email"));
                    biglietto.setPrezzo(rs.getDouble("prezzo"));
                    biglietti.add(biglietto);
                }
            }
            
            return biglietti;
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca dei biglietti per corsa: " + e.getMessage());
            return biglietti;
        }
    }
    
    /**
     * Ottiene la lista dei posti già occupati per una corsa
     * 
     * @param idCorsa ID della corsa
     * @return lista dei numeri dei posti occupati
     */
    public List<Integer> getPostiOccupati(int idCorsa) {
        List<Integer> postiOccupati = new ArrayList<>();
        String query = "SELECT numposto FROM Biglietto WHERE idCorsa = ? AND numposto IS NOT NULL";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idCorsa);
            
            try (ResultSet rs = stmt.executeQuery()) {
                 while (rs.next()) {
                    postiOccupati.add(rs.getInt("numposto"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca dei posti occupati: " + e.getMessage());
        }
        
        return postiOccupati;
    }
    
    /**
     * Elimina un biglietto dal database tramite il suo codice QR
     * 
     * @param codiceQR codice QR del biglietto da eliminare
     * @return true se l'eliminazione ha successo, false altrimenti
     */
    public boolean deleteBiglietto(String codiceQR) {
        String query = "DELETE FROM Biglietto WHERE codiceQR = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, codiceQR);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione del biglietto: " + e.getMessage());
            return false;
        }
    }
}

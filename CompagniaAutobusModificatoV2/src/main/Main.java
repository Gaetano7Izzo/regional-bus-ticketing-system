package main;
import javax.swing.*;
import boundary.BMainUI;
import control.GestioneCompagnia;
import database.DBManager;
import util.ThemeManager;
import com.formdev.flatlaf.FlatDarkLaf;

/**
 * Classe principale dell'applicazione
 */
public class Main {
    
    /**
     * Metodo main che avvia l'applicazione
     * 
     * @param args Argomenti da linea di comando
     */
    public static void main(String[] args) {
        try {
            // Imposta il Look and Feel di FlatLaf
            UIManager.setLookAndFeel(new FlatDarkLaf());
            
            // Inizializza il tema personalizzato
            ThemeManager.initializeTheme();
            
            // Inizializza il controller
            GestioneCompagnia.getInstance();
            
            // Avvia l'interfaccia grafica
            SwingUtilities.invokeLater(() -> {
                BMainUI mainUI = new BMainUI();
                mainUI.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    // Palette colori personalizzata
    public static final Color PRIMARY_YELLOW = new Color(255, 196, 0);    // Giallo autobus
    public static final Color SECONDARY_YELLOW = new Color(255, 213, 79); // Giallo più chiaro
    public static final Color DARK_BACKGROUND = new Color(33, 33, 33);    // Sfondo scuro
    public static final Color LIGHT_TEXT = new Color(255, 255, 255);      // Testo chiaro
    public static final Color ACCENT_GRAY = new Color(66, 66, 66);        // Grigio per elementi secondari

    public static void initializeTheme() {
        try {
            // Personalizza i colori del tema
            UIManager.put("Panel.background", DARK_BACKGROUND);
            UIManager.put("Panel.foreground", LIGHT_TEXT);
            
            // Stile per i pulsanti
            UIManager.put("Button.background", PRIMARY_YELLOW);
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Button.arc", 10);
            UIManager.put("Button.focusedBackground", SECONDARY_YELLOW);
            UIManager.put("Button.hoverBackground", SECONDARY_YELLOW);
            UIManager.put("Button.pressedBackground", SECONDARY_YELLOW.darker());
            
            // Stile per i campi di testo
            UIManager.put("TextField.background", ACCENT_GRAY);
            UIManager.put("TextField.foreground", LIGHT_TEXT);
            UIManager.put("TextField.caretForeground", PRIMARY_YELLOW);
            UIManager.put("TextField.arc", 8);
            UIManager.put("TextField.selectionBackground", PRIMARY_YELLOW);
            UIManager.put("TextField.selectionForeground", Color.BLACK);
            
            // Stile per le tabelle
            UIManager.put("Table.background", DARK_BACKGROUND);
            UIManager.put("Table.foreground", LIGHT_TEXT);
            UIManager.put("TableHeader.background", ACCENT_GRAY);
            UIManager.put("TableHeader.foreground", PRIMARY_YELLOW);
            UIManager.put("Table.selectionBackground", PRIMARY_YELLOW);
            UIManager.put("Table.selectionForeground", Color.BLACK);
            
            // Stile per i menu
            UIManager.put("MenuBar.background", DARK_BACKGROUND);
            UIManager.put("MenuBar.foreground", LIGHT_TEXT);
            UIManager.put("Menu.background", DARK_BACKGROUND);
            UIManager.put("Menu.foreground", LIGHT_TEXT);
            UIManager.put("MenuItem.background", DARK_BACKGROUND);
            UIManager.put("MenuItem.foreground", LIGHT_TEXT);
            UIManager.put("MenuItem.selectionBackground", PRIMARY_YELLOW);
            UIManager.put("MenuItem.selectionForeground", Color.BLACK);
            
            // Stile per le finestre
            UIManager.put("Frame.background", DARK_BACKGROUND);
            UIManager.put("Frame.foreground", LIGHT_TEXT);
            UIManager.put("Dialog.background", DARK_BACKGROUND);
            UIManager.put("Dialog.foreground", LIGHT_TEXT);
            
            // Stile per le etichette
            UIManager.put("Label.foreground", LIGHT_TEXT);
            
            // Stile per i bordi
            UIManager.put("Border.color", ACCENT_GRAY);
            
            // Stile per gli scroll pane
            UIManager.put("ScrollPane.background", DARK_BACKGROUND);
            UIManager.put("ScrollPane.foreground", LIGHT_TEXT);
            UIManager.put("ScrollBar.background", ACCENT_GRAY);
            UIManager.put("ScrollBar.foreground", PRIMARY_YELLOW);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo per applicare stili personalizzati ai componenti
    public static void applyCustomStyle(JComponent component) {
        if (component instanceof JButton) {
            JButton button = (JButton) component;
            button.setBackground(PRIMARY_YELLOW);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
        } else if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            textField.setBackground(ACCENT_GRAY);
            textField.setForeground(LIGHT_TEXT);
            textField.setCaretColor(PRIMARY_YELLOW);
        }
    }
} 
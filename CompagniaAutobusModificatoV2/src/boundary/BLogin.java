package boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Interfaccia di login per l'impiegato
 */
public class BLogin extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private JTextField idField;
    private JButton loginButton;
    private JButton backButton;
    
    /**
     * Costruttore della classe BLogin
     */
    public BLogin() {
        initComponents();
    }
    
    /**
     * Inizializza i componenti dell'interfaccia
     */
    private void initComponents() {
        // Impostazioni base della finestra
        setTitle("Login Impiegato");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // Pannello principale con layout BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titolo in alto
        JLabel titleLabel = new JLabel("Autenticazione Impiegato", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Pannello centrale con form di login
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Etichetta ID
        JLabel idLabel = new JLabel("ID Impiegato:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(idLabel, gbc);
        
        // Campo di testo per l'ID
        idField = new JTextField(15);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(idField, gbc);
        
        // Pulsante di login
        loginButton = new JButton("Accedi");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticaImpiegato();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(loginButton, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Pannello inferiore con pulsante indietro
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButton = new JButton("Torna al Menu Principale");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tornalMenuPrincipale();
            }
        });
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Aggiunta del pannello principale alla finestra
        add(mainPanel);
        
        // Imposta il pulsante di login come default
        getRootPane().setDefaultButton(loginButton);
    }
    
    /**
     * Autentica l'impiegato e apre il menu impiegato se l'autenticazione ha successo
     */
    private void autenticaImpiegato() {
        String idText = idField.getText().trim();
        
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Inserisci l'ID impiegato", 
                "Errore", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int idImpiegato = Integer.parseInt(idText);
            
            // Verifica l'ID nel database tramite ImpiegatoDAO
            database.ImpiegatoDAO impiegatoDAO = new database.ImpiegatoDAO();
            try {
                // Tenta di recuperare l'impiegato dal database
                entity.EntityImpiegato impiegato = impiegatoDAO.readImpiegato(idImpiegato);
                
                // Se arriviamo qui, l'impiegato esiste nel database
                dispose(); // Chiude la finestra corrente
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        BImpiegatoUI impiegatoUI = new BImpiegatoUI(idImpiegato);
                        impiegatoUI.setVisible(true);
                    }
                });
            } catch (exception.AutenticazioneException ex) {
                // L'impiegato non è stato trovato nel database
                JOptionPane.showMessageDialog(this, 
                    "ID impiegato non valido o non presente nel sistema", 
                    "Errore di autenticazione", 
                    JOptionPane.ERROR_MESSAGE);
                // Resetta il campo ID per permettere un nuovo tentativo
                idField.setText("");
                idField.requestFocus();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "L'ID impiegato deve essere un numero", 
                "Errore di formato", 
                JOptionPane.ERROR_MESSAGE);
            // Resetta il campo ID per permettere un nuovo tentativo
            idField.setText("");
            idField.requestFocus();
        }
    }
    
    /**
     * Torna al menu principale
     */
    private void tornalMenuPrincipale() {
        dispose(); // Chiude la finestra corrente
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BMainUI mainUI = new BMainUI();
                mainUI.setVisible(true);
            }
        });
    }
}

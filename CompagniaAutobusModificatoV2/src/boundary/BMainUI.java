package boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import control.GestioneCompagnia;
import exception.AutenticazioneException;

/**
 * Interfaccia utente principale (menu iniziale)
 */
public class BMainUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private JButton clienteButton;
    private JButton impiegatoButton;

    /**
     * Costruttore della classe BMainUI
     */
    public BMainUI() {
        initComponents();
    }

    /**
     * Ridimensiona un'icona alle dimensioni specificate
     */
    private ImageIcon resizeIcon(String path, int width, int height) {
        try {
            File imageFile = new File("src/resources/icons/" + path);
            BufferedImage originalImage = ImageIO.read(imageFile);
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inizializza i componenti dell'interfaccia
     */
    private void initComponents() {
        // Impostazioni base della finestra
        setTitle("Compagnia Autobus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Pannello principale
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Pannello per i pulsanti
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        // Pulsante Area Passeggero
        clienteButton = new JButton();
        clienteButton.setIcon(resizeIcon("passenger_icon.png", 200, 200));
        clienteButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        clienteButton.setHorizontalTextPosition(SwingConstants.CENTER);
        clienteButton.setText("Passeggero");
        clienteButton.setFont(new Font("Arial", Font.BOLD, 16));
        clienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraAreaPasseggero();
            }
        });

        // Pulsante Area Impiegato
        impiegatoButton = new JButton();
        impiegatoButton.setIcon(resizeIcon("employee_icon.png", 200, 200));
        impiegatoButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        impiegatoButton.setHorizontalTextPosition(SwingConstants.CENTER);
        impiegatoButton.setText("Impiegato");
        impiegatoButton.setFont(new Font("Arial", Font.BOLD, 16));
        impiegatoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraAreaImpiegato();
            }
        });

        // Aggiunta dei pulsanti al pannello
        buttonPanel.add(clienteButton);
        buttonPanel.add(impiegatoButton);
        mainPanel.add(buttonPanel);

        // Aggiunta del pannello principale alla finestra
        add(mainPanel);
    }

    /**
     * Mostra l'interfaccia per l'area passeggero.
     */
    private void mostraAreaPasseggero() {
        BClienteUI clienteUI = new BClienteUI();
        clienteUI.setVisible(true);
        this.dispose();
    }

    /**
     * Mostra l'interfaccia per l'area impiegato.
     */
    private void mostraAreaImpiegato() {
        String idImpiegatoStr = JOptionPane.showInputDialog(this, "Inserisci l'ID Impiegato:", "Login Impiegato", JOptionPane.QUESTION_MESSAGE);
        if (idImpiegatoStr != null && !idImpiegatoStr.trim().isEmpty()) {
            try {
                int idImpiegato = Integer.parseInt(idImpiegatoStr.trim());
                
                // Verifica l'esistenza dell'impiegato nel database
                try {
                    GestioneCompagnia.getInstance().autenticazione(idImpiegato);
                    
                    // Se arriviamo qui, l'impiegato esiste nel database
                    BImpiegatoUI impiegatoUI = new BImpiegatoUI(idImpiegato);
                    impiegatoUI.setVisible(true);
                    
                    // Avvia lo scheduler del report in background
                    BTempo reportScheduler = new BTempo();
                    
                    this.dispose();
                } catch (AutenticazioneException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "ID Impiegato non valido o non presente nel sistema", 
                        "Errore di autenticazione", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "L'ID Impiegato deve essere un numero", 
                    "Errore di formato", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Metodo main (già presente in src.Main, questo è solo per riferimento se volessi testare BMainUI singolarmente)
     */
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(new Runnable() {
    //         @Override
    //         public void run() {
    //             new BMainUI().setVisible(true);
    //         }
    //     });
    // }
}

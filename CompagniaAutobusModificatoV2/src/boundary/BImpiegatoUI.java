package boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;
import javax.print.PrintException;
import exception.PrinterException;

import control.GestioneCompagnia;
import entity.EntityCorsa;
import exception.CorsaNonTrovataException;
import exception.VenditaBigliettiException;
import util.PrinterManager;
import util.ThemeManager;

/**
 * Interfaccia utente per l'impiegato
 */
public class BImpiegatoUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private JButton vendiBigliettoButton;
    private JButton logoutButton;
    private JComboBox<String> stampanteComboBox;
    private int idImpiegato;
    private GestioneCompagnia controller;
    private List<EntityCorsa> corseDisponibili;
    
    /**
     * Costruttore della classe BImpiegatoUI
     * 
     * @param idImpiegato ID dell'impiegato autenticato
     */
    public BImpiegatoUI(int idImpiegato) {
        this.idImpiegato = idImpiegato;
        this.controller = GestioneCompagnia.getInstance();
        this.controller.setIdImpiegato(idImpiegato);
        this.corseDisponibili = new ArrayList<>();
        ThemeManager.initializeTheme();
        initComponents();
    }
    
    /**
     * Inizializza i componenti dell'interfaccia
     */
    private void initComponents() {
        // Impostazioni base della finestra
        setTitle("Area Impiegato");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        // Pannello principale con layout BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        // Titolo in alto con ID impiegato
        JLabel titleLabel = new JLabel("Menu Impiegato - ID: " + idImpiegato, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(ThemeManager.PRIMARY_YELLOW);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Pannello centrale con i pulsanti
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Pulsante Vendi Biglietto
        vendiBigliettoButton = createMenuButton("Vendi Biglietto");
        vendiBigliettoButton.setPreferredSize(new Dimension(200, 60));
        vendiBigliettoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraFormVenditaBiglietto();
            }
        });
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(vendiBigliettoButton, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Pannello inferiore con pulsante logout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        // Pannello informazioni stampante
        JPanel printerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        printerInfoPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        printerInfoPanel.setBorder(BorderFactory.createTitledBorder("Stato Stampante"));
        
        try {
            String[] stampanti = PrinterManager.getStampantiDisponibili();
            if (stampanti.length > 0) {
                stampanteComboBox = new JComboBox<>(stampanti);
                stampanteComboBox.setPreferredSize(new Dimension(200, 25));
                JLabel printerLabel = new JLabel("Seleziona stampante: ");
                printerLabel.setForeground(ThemeManager.LIGHT_TEXT);
                printerInfoPanel.add(printerLabel);
                printerInfoPanel.add(stampanteComboBox);
            } else {
                JLabel printerLabel = new JLabel("Nessuna stampante disponibile");
                printerLabel.setForeground(new Color(255, 0, 0)); // Rosso
                printerInfoPanel.add(printerLabel);
            }
        } catch (Exception e) {
            JLabel printerLabel = new JLabel("Errore nel rilevamento della stampante");
            printerLabel.setForeground(new Color(255, 0, 0)); // Rosso
            printerInfoPanel.add(printerLabel);
        }
        
        bottomPanel.add(printerInfoPanel, BorderLayout.CENTER);
        
        // Pannello pulsante logout
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        logoutButton = createMenuButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        logoutPanel.add(logoutButton);
        bottomPanel.add(logoutPanel, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Aggiunta del pannello principale alla finestra
        add(mainPanel);
    }
    
    /**
     * Mostra il form per la vendita di un biglietto con selezione visuale delle corse disponibili
     */
    private void mostraFormVenditaBiglietto() {
        try {
            // Crea il form per la vendita del biglietto
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Pannello per la selezione della data
            JPanel dataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            dataPanel.setBorder(BorderFactory.createTitledBorder("Selezione Data"));
            
            JSpinner dataSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor editor = new JSpinner.DateEditor(dataSpinner, "dd/MM/yyyy");
            dataSpinner.setEditor(editor);
            dataPanel.add(new JLabel("Data di partenza:"));
            dataPanel.add(dataSpinner);
            
            panel.add(dataPanel);
            panel.add(Box.createVerticalStrut(10));

            // Pannello per i dati del cliente
            JPanel datiClientePanel = new JPanel(new GridLayout(1, 2, 5, 5));
            datiClientePanel.setBorder(BorderFactory.createTitledBorder("Dati Cliente"));
            
            // Campo per il numero di posti
            JTextField numPostiField = new JTextField("1");
            datiClientePanel.add(new JLabel("Numero di posti:"));
            datiClientePanel.add(numPostiField);

            panel.add(datiClientePanel);
            panel.add(Box.createVerticalStrut(10));

            // Pannello per la selezione della corsa
            JPanel corsaPanel = new JPanel(new BorderLayout(5, 5));
            corsaPanel.setBorder(BorderFactory.createTitledBorder("Selezione Corsa"));

            // Lista delle corse disponibili
            JComboBox<String> corseCombo = new JComboBox<>();
            corsaPanel.add(corseCombo, BorderLayout.CENTER);

            // Pannello per i posti disponibili
            JPanel postiPanel = new JPanel();
            postiPanel.setLayout(new BoxLayout(postiPanel, BoxLayout.Y_AXIS));
            postiPanel.setBorder(BorderFactory.createTitledBorder("Posti Disponibili"));
            
            // Pannello interno per la griglia dei posti
            JPanel grigliaPostiPanel = new JPanel(new GridLayout(0, 4, 5, 5));
            grigliaPostiPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            // Scroll pane per i posti
            JScrollPane scrollPane = new JScrollPane(grigliaPostiPanel);
            scrollPane.setPreferredSize(new Dimension(300, 150));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            postiPanel.add(scrollPane);
            List<JCheckBox> postiCheckBoxes = new ArrayList<>();
            List<Integer> postiDisponibili = new ArrayList<>();

            // Aggiorna le corse disponibili quando viene selezionata una data
            dataSpinner.addChangeListener(e -> {
                Date dataSelezionata = (Date) dataSpinner.getValue();
                corseDisponibili.clear();
                corseDisponibili.addAll(controller.getCorseDisponibili(dataSelezionata));
                
                corseCombo.removeAllItems();
                if (corseDisponibili.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Non ci sono corse disponibili per la data selezionata", "Informazione", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                for (EntityCorsa corsa : corseDisponibili) {
                    corseCombo.addItem(corsa.toString());
                }
                
                // Trigger iniziale per popolare i posti
                if (corseCombo.getItemCount() > 0) {
                    corseCombo.setSelectedIndex(0);
                }
            });

            // Aggiorna i posti disponibili quando viene selezionata una corsa
            corseCombo.addActionListener(e -> {
                grigliaPostiPanel.removeAll();
                postiCheckBoxes.clear();
                postiDisponibili.clear();
                
                int indexCorsa = corseCombo.getSelectedIndex();
                if (indexCorsa >= 0 && indexCorsa < corseDisponibili.size()) {
                    EntityCorsa corsaSelezionata = corseDisponibili.get(indexCorsa);
                    try {
                        // Ottieni i posti disponibili
                        List<Integer> postiLiberi = controller.getPostiDisponibili(corsaSelezionata.getId());
                        
                        if (postiLiberi.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Non ci sono posti disponibili per questa corsa", 
                                "Informazione", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        
                        // Aggiungi solo i posti effettivamente disponibili
                        for (Integer posto : postiLiberi) {
                            JCheckBox checkBox = new JCheckBox("Posto " + posto);
                            checkBox.setFont(new Font("Arial", Font.PLAIN, 12));
                            postiCheckBoxes.add(checkBox);
                            postiDisponibili.add(posto);
                            grigliaPostiPanel.add(checkBox);
                        }
                        
                        grigliaPostiPanel.revalidate();
                        grigliaPostiPanel.repaint();
                    } catch (CorsaNonTrovataException ex) {
                        JOptionPane.showMessageDialog(this, "Errore nel recupero dei posti disponibili", 
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            panel.add(corsaPanel);
            panel.add(Box.createVerticalStrut(10));
            panel.add(postiPanel);

            // Trigger iniziale per popolare le corse
            dataSpinner.setValue(new Date());

            int result = JOptionPane.showConfirmDialog(this, panel, "Vendi Biglietto", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // Ottieni il numero di posti richiesto
                    int numPostiRichiesti;
                    try {
                        numPostiRichiesti = Integer.parseInt(numPostiField.getText().trim());
                        if (numPostiRichiesti <= 0) {
                            JOptionPane.showMessageDialog(this, "Il numero di posti deve essere maggiore di zero", "Errore", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Inserisci un numero valido di posti", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Ottieni i posti selezionati
                    List<Integer> postiScelti = new ArrayList<>();
                    for (int j = 0; j < postiCheckBoxes.size(); j++) {
                        if (postiCheckBoxes.get(j).isSelected()) {
                            // Estrai il numero del posto dal testo della checkbox
                            String checkboxText = postiCheckBoxes.get(j).getText();
                            int posto = Integer.parseInt(checkboxText.split(" ")[1]);
                            postiScelti.add(posto);
                        }
                    }

                    if (postiScelti.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Seleziona almeno un posto", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Verifica che il numero di posti selezionati corrisponda esattamente al numero richiesto
                    if (postiScelti.size() != numPostiRichiesti) {
                        JOptionPane.showMessageDialog(this, 
                            "Devi selezionare esattamente " + numPostiRichiesti + " posti. Hai selezionato " + postiScelti.size() + " posti.", 
                            "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Ottieni la corsa selezionata
                    int indexCorsaSelezionata = corseCombo.getSelectedIndex();
                    if (indexCorsaSelezionata < 0 || indexCorsaSelezionata >= corseDisponibili.size()) {
                        throw new IllegalArgumentException("Seleziona una corsa valida");
                    }
                    EntityCorsa corsaSelezionata = corseDisponibili.get(indexCorsaSelezionata);

                    // Verifica disponibilità
                    int postiDisponibiliTotali = controller.verificaDisponibilita(corsaSelezionata.getId());
                    if (postiDisponibiliTotali < postiScelti.size()) {
                        throw new IllegalArgumentException("Non ci sono abbastanza posti disponibili");
                    }

                    // Verifica che tutti i posti selezionati siano effettivamente disponibili
                    List<Integer> postiLiberi = controller.getPostiDisponibili(corsaSelezionata.getId());
                    for (Integer posto : postiScelti) {
                        if (!postiLiberi.contains(posto)) {
                            throw new IllegalArgumentException("Il posto " + posto + " non è più disponibile");
                        }
                    }

                    // Processa la vendita
                    String risultato = controller.vendiBiglietto(corsaSelezionata.getId(), postiScelti);
                    
                    // Mostra il risultato
                    JOptionPane.showMessageDialog(this, risultato, "Vendita Completata", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Chiedi se vuole stampare il biglietto
                    int scelta = JOptionPane.showConfirmDialog(this, 
                        "Vuoi stampare il biglietto?", 
                        "Stampa Biglietto", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (scelta == JOptionPane.YES_OPTION) {
                        // Estrai il codice QR dal risultato
                        String[] righe = risultato.split("\n");
                        for (String riga : righe) {
                            if (riga.startsWith("- Posto")) {
                                // Estrai il codice QR dopo i due punti
                                String codiceQR = riga.split(":")[1].trim();
                                System.out.println("Tentativo di stampa biglietto con codice QR: " + codiceQR);
                                
                                try {
                                    // Stampa il biglietto usando la stampante selezionata
                                    String nomeStampante = stampanteComboBox != null ? 
                                        (String)stampanteComboBox.getSelectedItem() : null;
                                    GestioneCompagnia.getInstance().stampaBiglietto(codiceQR, nomeStampante);
                                    JOptionPane.showMessageDialog(this, "Biglietto stampato con successo!");
                                } catch (PrinterException e) {
                                    JOptionPane.showMessageDialog(this, "Errore durante la stampa del biglietto: " + e.getMessage(),
                                        "Errore di stampa", JOptionPane.ERROR_MESSAGE);
                                }
                                break;
                            }
                        }
                    }
                    
                } catch (IllegalArgumentException | CorsaNonTrovataException | VenditaBigliettiException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore: " + e.getMessage(),
                "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Crea un pulsante per il menu con stile uniforme
     * 
     * @param text Testo del pulsante
     * @return JButton configurato
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(ThemeManager.PRIMARY_YELLOW);
        button.setForeground(Color.BLACK);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ThemeManager.applyCustomStyle(button);
        return button;
    }
    
    /**
     * Effettua il logout e torna alla schermata di login
     */
    private void logout() {
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

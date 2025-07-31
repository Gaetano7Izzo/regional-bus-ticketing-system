package boundary;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import control.GestioneCompagnia;
import entity.EntityBiglietto;
import entity.EntityCorsa;
import exception.CorsaNonTrovataException;
import exception.VenditaBigliettiException;
import entity.MetodoPagamento;
import util.ThemeManager;
import util.PaymentProcessor;

/**
 * Interfaccia utente per il cliente/passeggero
 */
public class BClienteUI extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private JButton acquistaBigliettoButton;
    private JButton modificaBigliettoButton;
    private JButton backButton;
    private GestioneCompagnia controller;
    private List<EntityCorsa> corseDisponibili;
    
    /**
     * Costruttore della classe BClienteUI
     */
    public BClienteUI() {
        controller = GestioneCompagnia.getInstance();
        corseDisponibili = new ArrayList<>();
        ThemeManager.initializeTheme();
        initComponents();
    }
    
    /**
     * Inizializza i componenti dell'interfaccia
     */
    private void initComponents() {
        // Impostazioni base della finestra
        setTitle("Area Passeggero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        // Pannello principale con layout BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        // Titolo in alto
        JLabel titleLabel = new JLabel("Menu Passeggero", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(ThemeManager.PRIMARY_YELLOW);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Pannello centrale con i pulsanti
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        centerPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        
        // Pulsante Acquista Biglietto
        acquistaBigliettoButton = createMenuButton("Acquista Biglietto");
        acquistaBigliettoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraFormAcquistoBiglietto();
            }
        });
        
        // Pulsante Modifica Biglietto
        modificaBigliettoButton = createMenuButton("Modifica Biglietto");
        modificaBigliettoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraFormModificaBiglietto();
            }
        });
        
        // Aggiunta dei pulsanti al pannello centrale
        centerPanel.add(acquistaBigliettoButton);
        centerPanel.add(modificaBigliettoButton);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Pannello inferiore con pulsante indietro
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(ThemeManager.DARK_BACKGROUND);
        backButton = createMenuButton("Torna al Menu Principale");
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
    }
    
    /**
     * Mostra il form per l'acquisto di un biglietto con selezione visuale delle corse disponibili
     */
    private void mostraFormAcquistoBiglietto() {
        try {
            // Crea il form per l'acquisto del biglietto
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

            // Pannello per i dati personali
            JPanel datiPersonaliPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            datiPersonaliPanel.setBorder(BorderFactory.createTitledBorder("Dati Personali"));
            
            // Campo per il telefono
            JTextField telefonoField = new JTextField();
            datiPersonaliPanel.add(new JLabel("Telefono:"));
            datiPersonaliPanel.add(telefonoField);

            // Campo per l'email
            JTextField emailField = new JTextField();
            datiPersonaliPanel.add(new JLabel("Email:"));
            datiPersonaliPanel.add(emailField);

            // Campo per il numero di posti
            JTextField numPostiField = new JTextField("1");
            datiPersonaliPanel.add(new JLabel("Numero di posti:"));
            datiPersonaliPanel.add(numPostiField);

            panel.add(datiPersonaliPanel);
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

            int result = JOptionPane.showConfirmDialog(this, panel, "Acquista Biglietto", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // Validazione dei campi
                    if (telefonoField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
                        throw new IllegalArgumentException("Tutti i campi sono obbligatori");
                    }
                    
                    // Validazione dell'email (deve contenere @)
                    String email = emailField.getText().trim();
                    if (!email.contains("@")) {
                        throw new IllegalArgumentException("L'indirizzo email non è valido: deve contenere il carattere '@'");
                    }
                    
                    // Validazione del numero di telefono (deve essere di 10 cifre)
                    String telefonoStr = telefonoField.getText().trim();
                    if (telefonoStr.length() != 10) {
                        throw new IllegalArgumentException("Il numero di telefono non è valido: deve contenere esattamente 10 cifre");
                    }
                    
                    long telefono = Long.parseLong(telefonoStr);
                    
                    // Ottieni il numero di posti richiesto
                    int numPostiRichiesti;
                    try {
                        numPostiRichiesti = Integer.parseInt(numPostiField.getText().trim());
                        if (numPostiRichiesti <= 0) {
                            throw new IllegalArgumentException("Il numero di posti deve essere maggiore di zero");
                        }
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("Inserisci un numero valido di posti");
                    }
                    
                    // Verifica che sia stato selezionato il numero esatto di posti richiesto
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
                        throw new IllegalArgumentException("Seleziona almeno un posto");
                    }
                    
                    // Verifica che il numero di posti selezionati corrisponda esattamente al numero richiesto
                    if (postiScelti.size() != numPostiRichiesti) {
                        throw new IllegalArgumentException("Devi selezionare esattamente " + numPostiRichiesti + 
                            " posti. Hai selezionato " + postiScelti.size() + " posti.");
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

                    // Mostra il form per il pagamento
                    JPanel paymentPanel = new JPanel(new GridLayout(0, 2, 5, 5));
                    paymentPanel.setBorder(BorderFactory.createTitledBorder("Dati di Pagamento"));
                    
                    // Selezione metodo di pagamento
                    String[] metodiPagamento = {"CARTA", "PAYPAL"};
                    JComboBox<String> metodoPagamentoCombo = new JComboBox<>(metodiPagamento);
                    paymentPanel.add(new JLabel("Metodo di pagamento:"));
                    paymentPanel.add(metodoPagamentoCombo);
                    
                    // Campi per carta di credito
                    JTextField numeroCartaField = new JTextField();
                    JTextField scadenzaField = new JTextField();
                    JTextField cvvField = new JTextField();
                    
                    // Campi per PayPal
                    JTextField emailPaypalField = new JTextField();
                    
                    // Pannello per i campi carta
                    JPanel cartaPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                    cartaPanel.add(new JLabel("Numero carta:"));
                    cartaPanel.add(numeroCartaField);
                    cartaPanel.add(new JLabel("Scadenza (MM/AA):"));
                    cartaPanel.add(scadenzaField);
                    cartaPanel.add(new JLabel("CVV:"));
                    cartaPanel.add(cvvField);
                    
                    // Pannello per i campi PayPal
                    JPanel paypalPanel = new JPanel(new GridLayout(1, 2, 5, 5));
                    paypalPanel.add(new JLabel("Email PayPal:"));
                    paypalPanel.add(emailPaypalField);
                    
                    // CardLayout per alternare i pannelli
                    JPanel paymentDetailsPanel = new JPanel(new CardLayout());
                    paymentDetailsPanel.add(cartaPanel, "CARTA");
                    paymentDetailsPanel.add(paypalPanel, "PAYPAL");
                    
                    // Listener per cambiare il pannello in base al metodo di pagamento
                    metodoPagamentoCombo.addActionListener(e -> {
                        CardLayout cl = (CardLayout) paymentDetailsPanel.getLayout();
                        cl.show(paymentDetailsPanel, (String) metodoPagamentoCombo.getSelectedItem());
                    });
                    
                    paymentPanel.add(paymentDetailsPanel);
                    
                    // Mostra il dialog per il pagamento
                    int paymentResult = JOptionPane.showConfirmDialog(this, paymentPanel, 
                        "Inserisci i dati di pagamento", JOptionPane.OK_CANCEL_OPTION);
                    
                    if (paymentResult == JOptionPane.OK_OPTION) {
                        // Crea l'oggetto MetodoPagamento
                        MetodoPagamento metodoPagamento = new MetodoPagamento((String) metodoPagamentoCombo.getSelectedItem());
                        
                        if (metodoPagamento.getTipo().equals("CARTA")) {
                            metodoPagamento.setNumeroCarta(numeroCartaField.getText().trim());
                            metodoPagamento.setScadenza(scadenzaField.getText().trim());
                            metodoPagamento.setCvv(cvvField.getText().trim());
                            
                            // Validazione base dei dati carta
                            if (metodoPagamento.getNumeroCarta().length() != 16) {
                                throw new IllegalArgumentException("Numero carta non valido");
                            }
                            if (!PaymentProcessor.validaScadenza(metodoPagamento.getScadenza())) {
                                throw new IllegalArgumentException("Data di scadenza della carta non Valida");
                            }
                            if (!metodoPagamento.getCvv().matches("\\d{3}")) {
                                throw new IllegalArgumentException("CVV deve essere di 3 cifre");
                            }
                        } else {
                            metodoPagamento.setEmailPaypal(emailPaypalField.getText().trim());
                            if (!metodoPagamento.getEmailPaypal().contains("@")) {
                                throw new IllegalArgumentException("Email PayPal non valida");
                            }
                        }
                        
                        // Procedi con l'acquisto
                        String risultato = controller.acquistaBigliettoConPosti(
                            corsaSelezionata.getId(), 
                            postiScelti, 
                            telefono, 
                            email, 
                            false,
                            metodoPagamento
                        );
                        JOptionPane.showMessageDialog(this, risultato, "Successo", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore durante l'acquisto del biglietto: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mostra il form per la modifica di un biglietto
     */
    private void mostraFormModificaBiglietto() {
        // Creazione del pannello per il form
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Campi del form
        JTextField codiceQRField = new JTextField();
        
        // Aggiunta dei campi al pannello
        panel.add(new JLabel("Codice QR:"));
        panel.add(codiceQRField);
        
        // Mostra il dialog per inserire il codice QR
        int result = JOptionPane.showConfirmDialog(this, panel, "Modifica Biglietto", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        // Se l'utente ha premuto OK, procedi con la modifica
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validazione del codice QR
                String codiceQR = codiceQRField.getText().trim();
                if (codiceQR.isEmpty()) {
                    throw new IllegalArgumentException("Il codice QR è obbligatorio");
                }
                
                // Cerca il biglietto
                EntityBiglietto biglietto = controller.getBigliettoByCodiceQR(codiceQR);
                if (biglietto == null) {
                    throw new IllegalArgumentException("Biglietto non trovato");
                }
                
                // Cerca la corsa originale
                EntityCorsa corsaOriginale = controller.getCorsaById(biglietto.getIdCorsa());
                if (corsaOriginale == null) {
                    throw new IllegalArgumentException("Corsa originale non trovata");
                }
                
                // Ottieni le corse alternative con le stesse località
                List<EntityCorsa> corseAlternative = controller.getCorseAlternative(
                    corsaOriginale.getCittaPartenza(), 
                    corsaOriginale.getCittaArrivo()
                );
                
                if (corseAlternative.isEmpty()) {
                    throw new IllegalArgumentException("Non ci sono corse alternative disponibili");
                }
                
                // Crea il pannello per la selezione della nuova corsa
                JPanel corsaPanel = new JPanel(new GridLayout(0, 2, 10, 10));
                corsaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                // Crea il modello per la JComboBox
                DefaultComboBoxModel<EntityCorsa> corsaModel = new DefaultComboBoxModel<>();
                for (EntityCorsa corsa : corseAlternative) {
                    corsaModel.addElement(corsa);
                }
                
                // Crea la JComboBox per la selezione della corsa
                JComboBox<EntityCorsa> corsaComboBox = new JComboBox<>(corsaModel);
                corsaComboBox.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof EntityCorsa) {
                            EntityCorsa corsa = (EntityCorsa) value;
                            setText(String.format("%s - %s | %s %s | Prezzo: €%.2f | Posti disponibili: %d",
                                corsa.getCittaPartenza(),
                                corsa.getCittaArrivo(),
                                new SimpleDateFormat("dd/MM/yyyy").format(corsa.getData()),
                                new SimpleDateFormat("HH:mm").format(corsa.getOrario()),
                                corsa.getPrezzo(),
                                corsa.getPostiDisponibili()
                            ));
                        }
                        return this;
                    }
                });
                
                corsaPanel.add(new JLabel("Seleziona nuova corsa:"));
                corsaPanel.add(corsaComboBox);
                
                // Mostra il dialog per selezionare la nuova corsa
                int corsaResult = JOptionPane.showConfirmDialog(this, corsaPanel, "Seleziona Nuova Corsa", 
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
                if (corsaResult == JOptionPane.OK_OPTION) {
                    // Ottieni la corsa selezionata
                    EntityCorsa nuovaCorsa = (EntityCorsa) corsaComboBox.getSelectedItem();
                    if (nuovaCorsa == null) {
                        throw new IllegalArgumentException("Seleziona una corsa");
                    }
                    
                    // Verifica disponibilità posti
                    try {
                        int postiDisponibili = controller.verificaDisponibilita(nuovaCorsa.getId());
                        if (postiDisponibili <= 0) {
                            throw new IllegalArgumentException("Non ci sono posti disponibili per questa corsa");
                        }
                        
                        // Prima chiamata per ottenere i posti disponibili
                        try {
                            controller.modificaBiglietto(codiceQR, nuovaCorsa.getId(), null);
                        } catch (VenditaBigliettiException e) {
                            if (e.getMessage().startsWith("POSTI_DISPONIBILI:")) {
                                // Estrai la lista dei posti disponibili
                                String[] postiDisponibiliArray = e.getMessage().substring("POSTI_DISPONIBILI:".length()).split(",");
                                List<Integer> postiDisponibiliList = new ArrayList<>();
                                for (String posto : postiDisponibiliArray) {
                                    postiDisponibiliList.add(Integer.parseInt(posto));
                                }
                                
                                // Crea il pannello per la selezione del posto
                                JPanel postoPanel = new JPanel(new GridLayout(0, 1, 10, 10));
                                postoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                                
                                // Crea il modello per la JComboBox
                                DefaultComboBoxModel<Integer> postoModel = new DefaultComboBoxModel<>();
                                for (Integer posto : postiDisponibiliList) {
                                    postoModel.addElement(posto);
                                }
                                
                                // Crea la JComboBox per la selezione del posto
                                JComboBox<Integer> postoComboBox = new JComboBox<>(postoModel);
                                postoPanel.add(new JLabel("Seleziona un posto disponibile:"));
                                postoPanel.add(postoComboBox);
                                
                                // Mostra il dialog per la selezione del posto
                                int postoResult = JOptionPane.showConfirmDialog(
                                    this,
                                    postoPanel,
                                    "Selezione Posto",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.PLAIN_MESSAGE
                                );
                                
                                if (postoResult == JOptionPane.OK_OPTION) {
                                    // Ottieni il posto selezionato
                                    Integer postoSelezionato = (Integer) postoComboBox.getSelectedItem();
                                    if (postoSelezionato == null) {
                                        throw new IllegalArgumentException("Seleziona un posto");
                                    }
                                    
                                    // Chiamata al controller per la modifica del biglietto con il posto selezionato
                                    String risultato = controller.modificaBiglietto(codiceQR, nuovaCorsa.getId(), postoSelezionato);
                                    
                                    // Mostra il risultato
                                    JOptionPane.showMessageDialog(this, risultato, "Modifica Completata", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else {
                                throw e;
                            }
                        }
                    } catch (CorsaNonTrovataException ex) {
                        JOptionPane.showMessageDialog(this, "Corsa non trovata: " + ex.getMessage(), 
                                "Errore", JOptionPane.ERROR_MESSAGE);
                    } catch (VenditaBigliettiException ex) {
                        JOptionPane.showMessageDialog(this, "Errore durante la modifica: " + ex.getMessage(), 
                                "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
                
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), 
                        "Errore", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore imprevisto: " + ex.getMessage(), 
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
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
        button.setPreferredSize(new Dimension(200, 60));
        ThemeManager.applyCustomStyle(button);
        return button;
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

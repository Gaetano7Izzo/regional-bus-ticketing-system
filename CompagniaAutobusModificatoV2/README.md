# Progetto Compagnia Autobus

## Descrizione
Questo progetto implementa un sistema di gestione per una compagnia di trasporto su autobus regionale. Il sistema permette la gestione delle corse, la vendita e modifica dei biglietti, l'autenticazione degli impiegati e la generazione di report.

## Architettura
Il progetto è strutturato secondo il pattern architetturale BCED (Boundary-Control-Entity-Database):

- **Boundary**: Interfacce utente sia grafiche (GUI) che testuali (CLI)
- **Control**: Logica di business centralizzata
- **Entity**: Modelli di dati
- **Database**: Accesso ai dati tramite pattern DAO
- **Exception**: Gestione degli errori
- **Util**: Classi di utilità

## Funzionalità principali
- Autenticazione degli impiegati
- Acquisto e modifica dei biglietti da parte dei clienti
- Vendita di biglietti da parte degli impiegati
- Visualizzazione e selezione delle corse disponibili
- Verifica e aggiornamento automatico della disponibilità dei posti
- Generazione e invio di biglietti elettronici con codice QR
- Generazione automatica di report settimanali

## Requisiti di sistema
- Java 11 o superiore
- MySQL Server o H2 Database (configurabile all'avvio)
- Librerie necessarie (incluse nella cartella `lib`):
  - mysql-connector-j-8.0.32.jar
  - h2-1.4.200.jar

## Configurazione del database
Il sistema supporta due tipi di database:

### MySQL
1. Installare MySQL Server
2. Creare un database chiamato `trasporto_regionale`
3. Eseguire lo script `database_init.sql` per creare le tabelle e inserire i dati di esempio

### H2 Database
Non è necessaria alcuna configurazione aggiuntiva. Il database verrà creato automaticamente nella directory del progetto.

## Modalità di esecuzione
Il sistema può essere eseguito in due modalità:

### Interfaccia grafica (GUI)
1. Eseguire la classe `Main` per avviare l'interfaccia grafica
2. Selezionare il tipo di database (MySQL o H2) quando richiesto
3. Scegliere il ruolo (Passeggero o Impiegato)
4. Per accedere come impiegato, utilizzare uno degli ID presenti nel database (es. 1, 2, 3)

### Interfaccia a riga di comando (CLI)
1. Eseguire la classe `MainMenu` per avviare l'interfaccia a riga di comando
2. Seguire le istruzioni visualizzate per navigare nei menu e utilizzare le funzionalità

## Importazione in Eclipse
1. File > Import > General > Existing Projects into Workspace
2. Selezionare la directory del progetto o il file zip
3. Assicurarsi che il progetto sia selezionato e fare clic su Finish

## Struttura del progetto
```
compagnia_autobus/
├── src/
│   ├── boundary/       # Interfacce utente
│   ├── control/        # Logica di business
│   ├── entity/         # Modelli di dati
│   ├── database/       # Accesso ai dati
│   ├── exception/      # Gestione degli errori
│   ├── util/           # Classi di utilità
│   └── Main.java       # Punto di ingresso dell'applicazione
├── lib/                # Librerie esterne
└── database_init.sql   # Script di inizializzazione del database
```

## Guida all'uso

### Per i passeggeri
- **Acquisto biglietto**: Completamente funzionante! Ora mostra un elenco di corse disponibili tra cui scegliere, con informazioni dettagliate su:
  - Tratta (città di partenza e arrivo)
  - Data e ora
  - Prezzo
  - Posti disponibili
  
  L'utente deve solo selezionare la corsa desiderata e specificare:
  - Numero di posti
  - Telefono
  - Email
  - Opzione per ricevere il codice QR via SMS
  
- **Modifica biglietto**: Completamente funzionante! Permette di modificare data e ora di un biglietto esistente compilando un form con:
  - Codice QR del biglietto
  - Nuova data (formato: gg/mm/aaaa)
  - Nuovo orario (formato: hh:mm)

### Per gli impiegati
- **Autenticazione**: Accesso al sistema tramite ID impiegato
- **Vendita biglietto**: Completamente funzionante! Ora mostra un elenco di corse disponibili tra cui scegliere, con informazioni dettagliate. L'impiegato deve solo selezionare la corsa desiderata e specificare:
  - Telefono del cliente
  - Email del cliente
- **Verifica disponibilità**: Controllo automatico dei posti disponibili durante la selezione della corsa
- **Aggiorna disponibilità**: La disponibilità viene aggiornata automaticamente dopo ogni acquisto o vendita
- **Stampa biglietto**: Stampa di un biglietto esistente

## Gestione della disponibilità
Il sistema ora gestisce automaticamente la disponibilità dei posti:
- All'avvio, il database viene inizializzato con corse e autobus di esempio
- Ogni corsa mostra il numero di posti disponibili
- Dopo ogni acquisto o vendita, la disponibilità viene aggiornata automaticamente
- Le corse senza posti disponibili non vengono mostrate nella selezione
- Il sistema impedisce di acquistare più posti di quelli disponibili

## Gestione degli errori
Il sistema gestisce correttamente tutti i possibili errori durante le operazioni:
- Formato dati non valido
- Corsa non trovata
- Posti non disponibili
- Biglietto non trovato
- Errori di connessione al database

## Note per gli sviluppatori
- Il controller principale è implementato nella classe `GestioneCompagnia` che segue il pattern Singleton
- L'accesso ai dati è gestito tramite classi DAO specifiche per ogni entità
- La gestione delle eccezioni è centralizzata con classi specifiche per ogni tipo di errore
- Le utility includono classi per la generazione di codici QR, PDF e l'invio di email
- Tutte le interfacce utente sono completamente funzionanti e collegate alla logica di business

## Aggiornamenti recenti
- Implementata la selezione visuale delle corse disponibili per acquisto e vendita biglietti
- Aggiunta l'inizializzazione automatica del database con corse e autobus di esempio
- Implementato l'aggiornamento automatico della disponibilità dopo ogni operazione
- Migliorata l'interfaccia utente con informazioni dettagliate sulle corse
- Aggiunta la verifica della disponibilità durante la selezione della corsa

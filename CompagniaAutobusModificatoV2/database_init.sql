-- Creazione delle tabelle per il sistema di gestione della compagnia di trasporto regionale

-- Tabella Autobus
CREATE TABLE IF NOT EXISTS Autobus (
    id INT AUTO_INCREMENT PRIMARY KEY,
    capienza INT NOT NULL,
    trattaAssegnata VARCHAR(100) NOT NULL
);

-- Tabella Cliente
CREATE TABLE IF NOT EXISTS Cliente (
    telefono BIGINT PRIMARY KEY,
    email VARCHAR(100) NOT NULL
);

-- Tabella Impiegato
CREATE TABLE IF NOT EXISTS Impiegato (
    id INT PRIMARY KEY
);

-- Tabella Corsa
CREATE TABLE IF NOT EXISTS Corsa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orario TIMESTAMP NOT NULL,
    data DATE NOT NULL,
    cittaPartenza VARCHAR(50) NOT NULL,
    cittaArrivo VARCHAR(50) NOT NULL,
    prezzo DOUBLE NOT NULL,
    numBigliettiVenduti INT NOT NULL DEFAULT 0,
    idAutobus INT NOT NULL,
    FOREIGN KEY (idAutobus) REFERENCES Autobus(id)
);

-- Tabella Biglietto
CREATE TABLE IF NOT EXISTS Biglietto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orario TIMESTAMP NOT NULL,
    data DATE NOT NULL,
    codiceQR VARCHAR(100) NOT NULL UNIQUE,
    numposto INT,
    idCorsa INT NOT NULL,
    idImpiegato INT,
    telefono BIGINT,
    email VARCHAR(100),
    prezzo DOUBLE,
    FOREIGN KEY (idCorsa) REFERENCES Corsa(id),
    FOREIGN KEY (idImpiegato) REFERENCES Impiegato(id)
);

-- Inserimento dati di esempio

-- Autobus
INSERT INTO Autobus (capienza, trattaAssegnata) VALUES (50, 'Napoli-Salerno');
INSERT INTO Autobus (capienza, trattaAssegnata) VALUES (40, 'Napoli-Caserta');
INSERT INTO Autobus (capienza, trattaAssegnata) VALUES (60, 'Napoli-Avellino');

-- Impiegati
INSERT INTO Impiegato (id) VALUES (1);
INSERT INTO Impiegato (id) VALUES (2);
INSERT INTO Impiegato (id) VALUES (3);
INSERT INTO Impiegato (id) VALUES (4);
INSERT INTO Impiegato (id) VALUES (5);

-- Corse di esempio
INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-07-15 08:00:00', '2025-07-15', 'Napoli', 'Salerno', 12.50, 1);

INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-07-15 16:00:00', '2025-07-15', 'Salerno', 'Napoli', 12.50, 1);

INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-07-16 08:00:00', '2025-07-16', 'Napoli', 'Salerno', 12.50, 1);

INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-07-16 16:00:00', '2025-07-16', 'Salerno', 'Napoli', 12.50, 1);

INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-07-20 09:30:00', '2025-07-20', 'Napoli', 'Caserta', 8.50, 2);

INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-07-20 17:30:00', '2025-07-20', 'Caserta', 'Napoli', 8.50, 2);

INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-07-25 10:00:00', '2025-07-25', 'Napoli', 'Avellino', 10.50, 3);

INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-07-25 18:00:00', '2025-07-25', 'Avellino', 'Napoli', 10.50, 3);

-- Corse aggiuntive
INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-09-01 09:30:00', '2025-09-01', 'Napoli', 'Torre del Greco', 5.50, 1);

INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-09-10 10:00:00', '2025-09-10', 'Napoli', 'Volla', 4.50, 2);

INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, idAutobus)
VALUES ('2025-09-20 11:30:00', '2025-09-20', 'Napoli', 'Giugliano', 6.50, 3);

-- Corse della settimana precedente (7 giorni fa)
INSERT INTO Corsa (orario, data, cittaPartenza, cittaArrivo, prezzo, numBigliettiVenduti, idAutobus)
VALUES 
(DATEADD('DAY', -7, CURRENT_TIMESTAMP), DATEADD('DAY', -7, CURRENT_DATE), 'Napoli', 'Salerno', 12.50, 3, 1),
(DATEADD('DAY', -6, CURRENT_TIMESTAMP), DATEADD('DAY', -6, CURRENT_DATE), 'Napoli', 'Caserta', 8.00, 2, 2),
(DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_DATE), 'Napoli', 'Avellino', 10.00, 4, 3),
(DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_DATE), 'Salerno', 'Napoli', 12.50, 2, 1),
(DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_DATE), 'Caserta', 'Napoli', 8.00, 3, 2),
(DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_DATE), 'Avellino', 'Napoli', 10.00, 1, 3),
(DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_DATE), 'Napoli', 'Salerno', 12.50, 2, 1);

-- Biglietti venduti per le corse della settimana precedente
INSERT INTO Biglietto (orario, data, codiceQR, numposto, idCorsa, idImpiegato, telefono, email, prezzo)
VALUES 
-- Biglietti per Napoli-Salerno (7 giorni fa)
(DATEADD('DAY', -7, CURRENT_TIMESTAMP), DATEADD('DAY', -7, CURRENT_DATE), 'QR001', 1, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -7, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 1, 3331234567, 'cliente1@email.com', 12.50),
(DATEADD('DAY', -7, CURRENT_TIMESTAMP), DATEADD('DAY', -7, CURRENT_DATE), 'QR002', 2, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -7, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 1, 3331234568, 'cliente2@email.com', 12.50),
(DATEADD('DAY', -7, CURRENT_TIMESTAMP), DATEADD('DAY', -7, CURRENT_DATE), 'QR003', 3, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -7, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 2, 3331234569, 'cliente3@email.com', 12.50),

-- Biglietti per Napoli-Caserta (6 giorni fa)
(DATEADD('DAY', -6, CURRENT_TIMESTAMP), DATEADD('DAY', -6, CURRENT_DATE), 'QR004', 1, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -6, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 2, 3331234570, 'cliente4@email.com', 8.00),
(DATEADD('DAY', -6, CURRENT_TIMESTAMP), DATEADD('DAY', -6, CURRENT_DATE), 'QR005', 2, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -6, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 3, 3331234571, 'cliente5@email.com', 8.00),

-- Biglietti per Napoli-Avellino (5 giorni fa)
(DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_DATE), 'QR006', 1, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -5, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 3, 3331234572, 'cliente6@email.com', 10.00),
(DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_DATE), 'QR007', 2, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -5, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 4, 3331234573, 'cliente7@email.com', 10.00),
(DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_DATE), 'QR008', 3, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -5, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 4, 3331234574, 'cliente8@email.com', 10.00),
(DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_DATE), 'QR009', 4, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -5, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 5, 3331234575, 'cliente9@email.com', 10.00),

-- Biglietti per Salerno-Napoli (4 giorni fa)
(DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_DATE), 'QR010', 1, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -4, CURRENT_DATE) AND cittaPartenza = 'Salerno'), 1, 3331234576, 'cliente10@email.com', 12.50),
(DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_DATE), 'QR011', 2, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -4, CURRENT_DATE) AND cittaPartenza = 'Salerno'), 2, 3331234577, 'cliente11@email.com', 12.50),

-- Biglietti per Caserta-Napoli (3 giorni fa)
(DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_DATE), 'QR012', 1, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -3, CURRENT_DATE) AND cittaPartenza = 'Caserta'), 3, 3331234578, 'cliente12@email.com', 8.00),
(DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_DATE), 'QR013', 2, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -3, CURRENT_DATE) AND cittaPartenza = 'Caserta'), 4, 3331234579, 'cliente13@email.com', 8.00),
(DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_DATE), 'QR014', 3, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -3, CURRENT_DATE) AND cittaPartenza = 'Caserta'), 5, 3331234580, 'cliente14@email.com', 8.00),

-- Biglietto per Avellino-Napoli (2 giorni fa)
(DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_DATE), 'QR015', 1, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -2, CURRENT_DATE) AND cittaPartenza = 'Avellino'), 1, 3331234581, 'cliente15@email.com', 10.00),

-- Biglietti per Napoli-Salerno (1 giorno fa)
(DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_DATE), 'QR016', 1, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -1, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 2, 3331234582, 'cliente16@email.com', 12.50),
(DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_DATE), 'QR017', 2, (SELECT id FROM Corsa WHERE data = DATEADD('DAY', -1, CURRENT_DATE) AND cittaPartenza = 'Napoli'), 3, 3331234583, 'cliente17@email.com', 12.50);
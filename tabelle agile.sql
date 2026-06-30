USE progetto_agile_pianificatore_attivita;
DROP TABLE IF EXISTS attivita;

CREATE TABLE attivita (
    id_attivita INT AUTO_INCREMENT PRIMARY KEY,
    titolo VARCHAR(255) NOT NULL,            -- Il titolo è obbligatorio [cite: 7]
    descrizione TEXT,                        -- Mappato dai dati del progetto
    data_scadenza DATETIME,                  -- Mappato dai dati del progetto
    data_completamento DATETIME,             -- Mappato dai dati del progetto
    completata TINYINT(1) DEFAULT 0,         -- 0 = Incompleta, 1 = Completata (essenziale per la Dashboard)
    id_utente INT NOT NULL,                  -- FK verso utente (1:n)
    id_categoria INT,                        -- FK verso categoria (1:n)
    id_priorita INT NOT NULL,                -- FK verso priorità (1:n)
    
    -- Vincoli di Integrità Referenziale (Chiavi Esterne)
    FOREIGN KEY (id_utente) REFERENCES utente(id_utente) ON DELETE CASCADE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE SET NULL,
    FOREIGN KEY (id_priorita) REFERENCES priorita(id_priorita)
);                                                                                                          
-- 1. TABELLA UTENTE(per il login)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               cnxfv d dj 
CREATE TABLE IF NOT EXISTS utente (
    id_utente INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL -- Fondamentale per il login davanti al prof!
);

-- 2. TABELLA CATEGORIA(Gestione categorie personalizzabili dell'utente)
CREATE TABLE IF NOT EXISTS categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL
);

-- 3. TABELLA PRIORITÀ BASSA, MEDIA, ALTA
CREATE TABLE IF NOT EXISTS priorita (
    id_priorita INT AUTO_INCREMENT PRIMARY KEY,
    livello VARCHAR(20) NOT NULL UNIQUE
);

-- Popoliamo subito la tabella priorità con i valori richiesti
INSERT IGNORE INTO priorita (id_priorita, livello) VALUES 
(1, 'BASSA'),
(2, 'MEDIA'),
(3, 'ALTA');

-- 4. TABELLA ATTIVITÀ (Aggiornata con Titolo obbligatorio e flag completata)
CREATE TABLE IF NOT EXISTS attivita (
    id_attivita INT AUTO_INCREMENT PRIMARY KEY,
    titolo VARCHAR(255) NOT NULL, -- Richiesto dai criteri di accettazione
    descrizione TEXT,
    data_scadenza DATETIME,
    data_completamento DATETIME,
    completata TINYINT(1) DEFAULT 0, -- 0 = Incompleta, 1 = Completata (per la Dashboard)
    id_utente INT NOT NULL,
    id_categoria INT,
    id_priorita INT NOT NULL,
    FOREIGN KEY (id_utente) REFERENCES utente(id_utente) ON DELETE CASCADE,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria) ON DELETE SET NULL,
    FOREIGN KEY (id_priorita) REFERENCES priorita(id_priorita)
);
                                                                                                 
-- 5.TABELLA NOTIFICA
CREATE TABLE IF NOT EXISTS notifica (
    id_notifica INT AUTO_INCREMENT PRIMARY KEY,
    messaggio VARCHAR(255) NOT NULL,
    stato VARCHAR(20) DEFAULT 'DA_INVIARE', -- Es. DA_INVIARE, INVIATO
    data_invio DATETIME,
    id_attivita INT NOT NULL,
    FOREIGN KEY (id_attivita) REFERENCES attivita(id_attivita) ON DELETE CASCADE
);
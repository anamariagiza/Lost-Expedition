package PaooGame.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.nio.file.Paths;
import java.io.File;

/**
 * @class DatabaseManager
 * @brief Gestioneaza toate interactiunile cu baza de date SQLite a jocului.
 * Responsabilitati: stabilirea conexiunii, crearea tabelelor 'game_save' si
 * 'game_settings', si furnizarea de metode pentru salvarea si incarcarea
 * progresului jocului si a setarilor.
 */
public class DatabaseManager {

    /** Numele fisierului bazei de date.*/
    private static final String DB_FILE_NAME = "game_data.db";
    private Connection connection = null;
    private String databasePath;

    /**
     * @brief Constructorul clasei.
     * Initializeaza calea, se conecteaza si creeaza tabelele.
     */
    public DatabaseManager() {
        initializeDatabasePath();
        connect();
        createTables();
    }

    /**
     * @brief Stabileste o conexiune la baza de date SQLite.
     * @return Obiectul Connection, sau null in caz de eroare.
     */
    public Connection connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + databasePath;
            connection = DriverManager.getConnection(url);
            //System.out.println("DEBUG Database: Conectat la baza de date SQLite.");
        } catch (SQLException e) {
            System.err.println("Eroare SQL la conectare la baza de date: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Eroare: Driverul JDBC SQLite nu a fost gasit. Asigurati-va ca JAR-ul este adaugat corect la proiect.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * @brief Inchide conexiunea la baza de date, daca este activa.
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                //System.out.println("DEBUG Database: Conexiunea la baza de date a fost inchisa.");
            }
        } catch (SQLException e) {
            System.err.println("Eroare SQL la deconectare de la baza de date: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @fn public void saveGameData(int levelIndex, float playerX, float playerY, int playerHealth, boolean hasKey, boolean[] hasDoorKeys, String puzzlesSolvedString)
     * @brief Salveaza progresul curent al jocului in baza de date.
     * Include acum starea ambelor chei si numarul de puzzle-uri rezolvate.
     * @param levelIndex Indexul nivelului curent.
     * @param playerX Coordonata X a jucatorului.
     * @param playerY Coordonata Y a jucatorului.
     * @param playerHealth Viata curenta a jucatorului.
     * @param hasKey Starea cheii de nivel.
     * @param hasDoorKeys Tabloul cu starea cheilor de usa.
     * @param puzzlesSolvedString String cu ID-urile puzzle-urilor rezolvate, separate prin virgula.
     */
    public void saveGameData(int levelIndex, float playerX, float playerY, int playerHealth, boolean hasKey, boolean[] hasDoorKeys, String puzzlesSolvedString) {
        StringBuilder keysStr = new StringBuilder();
        for (int i = 0; i < hasDoorKeys.length; i++) {
            keysStr.append(hasDoorKeys[i]);
            if (i < hasDoorKeys.length - 1) {
                keysStr.append(",");
            }
        }

        String sql = "INSERT OR REPLACE INTO game_save(id, level_index, player_x, player_y, player_health, has_key, has_door_keys, puzzles_solved_str) VALUES(1, ?, ?, ?, ?, ?, ?, ?);";
        Connection conn = connect();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, levelIndex);
            pstmt.setFloat(2, playerX);
            pstmt.setFloat(3, playerY);
            pstmt.setInt(4, playerHealth);
            pstmt.setBoolean(5, hasKey);
            pstmt.setString(6, keysStr.toString());
            pstmt.setString(7, puzzlesSolvedString);
            pstmt.executeUpdate();
            //System.out.println("DEBUG Database: Progresul jocului salvat.");
        } catch (SQLException e) {
            System.err.println("Eroare SQL la salvarea progresului jocului: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Incarca progresul jocului din baza de date.
     * @return Un obiect SaveGameData cu datele incarcate, sau null daca nu exista o salvare.
     */
    public SaveGameData loadGameData() {
        String sql = "SELECT level_index, player_x, player_y, player_health, has_key, has_door_keys, puzzles_solved_str FROM game_save WHERE id = 1;";
        Connection conn = connect();
        if (conn == null) return null;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int levelIndex = rs.getInt("level_index");
                float playerX = rs.getFloat("player_x");
                float playerY = rs.getFloat("player_y");
                int playerHealth = rs.getInt("player_health");
                boolean hasKey = rs.getBoolean("has_key");
                String hasDoorKeysStr = rs.getString("has_door_keys");
                boolean[] hasDoorKeys = new boolean[7];
                if (hasDoorKeysStr != null && !hasDoorKeysStr.isEmpty()) {
                    String[] keys = hasDoorKeysStr.split(",");
                    for (int i = 0; i < keys.length; i++) {
                        hasDoorKeys[i] = Boolean.parseBoolean(keys[i]);
                    }
                }
                String puzzlesSolvedString = rs.getString("puzzles_solved_str");
                //System.out.println("DEBUG Database: Progresul jocului incarcat.");
                return new SaveGameData(levelIndex, playerX, playerY, playerHealth, hasKey, hasDoorKeys, puzzlesSolvedString);
            }
        } catch (SQLException e) {
            System.err.println("Eroare SQL la incarcarea progresului jocului: " + e.getMessage());
            e.printStackTrace();
        }
        //System.out.println("DEBUG Database: Nu a fost gasit niciun progres salvat.");
        return null;
    }

    /**
     * @brief Verifica daca exista o salvare in baza de date.
     * @return True daca exista o inregistrare in 'game_save', false altfel.
     */
    public boolean hasGameSave() {
        String sql = "SELECT COUNT(id) FROM game_save WHERE id = 1;";
        Connection conn = connect();
        if (conn == null) return false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Eroare SQL la verificarea existentei salvarii: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @class SaveGameData
     * @brief DTO (Data Transfer Object) pentru a stoca datele incarcate dintr-o salvare.
     */
    public static class SaveGameData {
        public int levelIndex;
        public float playerX;
        public float playerY;
        public int playerHealth;
        public boolean hasKey;
        public boolean[] hasDoorKeys;
        public String puzzlesSolvedString;

        public SaveGameData(int levelIndex, float playerX, float playerY, int playerHealth, boolean hasKey, boolean[] hasDoorKeys, String puzzlesSolvedString) {
            this.levelIndex = levelIndex;
            this.playerX = playerX;
            this.playerY = playerY;
            this.playerHealth = playerHealth;
            this.hasKey = hasKey;
            this.hasDoorKeys = hasDoorKeys;
            this.puzzlesSolvedString = puzzlesSolvedString;
        }
    }

    /**
     * @brief Salveaza setarile jocului in baza de date.
     */
    public void saveSettingsData(boolean soundEnabled, boolean musicEnabled, int volume) {
        String sql = "INSERT OR REPLACE INTO game_settings(id, sound_enabled, music_enabled, volume) VALUES(1, ?, ?, ?);";
        Connection conn = connect();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, soundEnabled);
            pstmt.setBoolean(2, musicEnabled);
            pstmt.setInt(3, volume);
            pstmt.executeUpdate();
            //System.out.println("DEBUG Database: Setarile jocului salvate.");
        } catch (SQLException e) {
            System.err.println("Eroare SQL la salvarea setarilor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Incarca setarile jocului din baza de date.
     * @return Un obiect SettingsData cu setarile incarcate, sau setari implicite.
     */
    public SettingsData loadSettingsData() {
        String sql = "SELECT sound_enabled, music_enabled, volume FROM game_settings WHERE id = 1;";
        Connection conn = connect();
        if (conn == null) return new SettingsData(true, true, 100);
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                boolean soundEnabled = rs.getBoolean("sound_enabled");
                boolean musicEnabled = rs.getBoolean("music_enabled");
                int volume = rs.getInt("volume");
                //System.out.println("DEBUG Database: Setarile jocului incarcate.");
                return new SettingsData(soundEnabled, musicEnabled, volume);
            }
        } catch (SQLException e) {
            System.err.println("Eroare SQL la incarcarea setarilor: " + e.getMessage());
            e.printStackTrace();
        }
        //System.out.println("DEBUG Database: Nu a fost gasit nicio inregistrare de setari. Se folosesc setari implicite.");
        return new SettingsData(true, true, 100);
    }

    /**
     * @class SettingsData
     * @brief DTO (Data Transfer Object) pentru a stoca setarile incarcate.
     */
    public static class SettingsData {
        public boolean soundEnabled;
        public boolean musicEnabled;
        public int volume;

        public SettingsData(boolean soundEnabled, boolean musicEnabled, int volume) {
            this.soundEnabled = soundEnabled;
            this.musicEnabled = musicEnabled;
            this.volume = volume;
        }
    }

    /**
     * @brief Determina calea absoluta pentru fisierul bazei de date in directorul 'home' al utilizatorului.
     */
    private void initializeDatabasePath() {
        try {
            String userHome = System.getProperty("user.home");
            File gameDataFolder = Paths.get(userHome, ".lostexpedition_db").toFile();
            if (!gameDataFolder.exists()) {
                if (!gameDataFolder.mkdirs()) {
                    System.err.println("Eroare: Nu s-a putut crea directorul bazei de date: " + gameDataFolder.getAbsolutePath());
                }
            }
            this.databasePath = Paths.get(gameDataFolder.getAbsolutePath(), DB_FILE_NAME).toString();
            //System.out.println("DEBUG Database: Calea bazei de date: " + this.databasePath);
        } catch (Exception e) {
            System.err.println("Eroare la initializarea caii bazei de date: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @brief Creeaza tabelele 'game_save' si 'game_settings' in baza de date, daca nu exista deja.
     */
    private void createTables() {
        Connection conn = connect();
        if (conn == null) {
            System.err.println("Eroare: Nu se poate crea tabele, conexiunea la baza de date este nula.");
            return;
        }

        Statement statement = null;
        try {
            statement = conn.createStatement();
            String createSaveTableSQL = "CREATE TABLE IF NOT EXISTS game_save (" +
                    "id INTEGER PRIMARY KEY," +
                    "level_index INTEGER NOT NULL," +
                    "player_x REAL NOT NULL," +
                    "player_y REAL NOT NULL," +
                    "player_health INTEGER NOT NULL," +
                    "has_key BOOLEAN NOT NULL DEFAULT FALSE," +
                    "has_door_keys TEXT NOT NULL DEFAULT ''," +
                    "puzzles_solved_str TEXT NOT NULL DEFAULT ''" +
                    ");";
            statement.execute(createSaveTableSQL);
            //System.out.println("DEBUG Database: Tabela 'game_save' verificata/creata.");

            String createSettingsTableSQL = "CREATE TABLE IF NOT EXISTS game_settings (" +
                    "id INTEGER PRIMARY KEY," +
                    "sound_enabled BOOLEAN NOT NULL," +
                    "music_enabled BOOLEAN NOT NULL," +
                    "volume INTEGER NOT NULL" +
                    ");";
            statement.execute(createSettingsTableSQL);
            //System.out.println("DEBUG Database: Tabela 'game_settings' verificata/creata.");

            String insertDefaultSettingsSQL = "INSERT OR IGNORE INTO game_settings (id, sound_enabled, music_enabled, volume) VALUES (1, TRUE, TRUE, 100);";
            statement.execute(insertDefaultSettingsSQL);
            //System.out.println("DEBUG Database: Setari implicite verificate/inserate in 'game_settings'.");


        } catch (SQLException e) {
            System.err.println("Eroare SQL la crearea tabelelor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
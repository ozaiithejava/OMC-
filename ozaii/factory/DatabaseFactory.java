package ozaii.factory;

import ozaii.apis.base.FactoryApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseFactory {
    private static final Logger logger = Logger.getLogger(DatabaseFactory.class.getName());
    private Connection connection;
    private final SettingsFactory settingsFactory;
    private final FactoryApi api = new FactoryApi();

    public DatabaseFactory(String settingsFilePath) {
        // Ayarları yükle
        settingsFactory = new SettingsFactory(settingsFilePath);
        // Veritabanı bağlantısını başlat
        connectToDatabase();
        validateConnection();
    }

    // Bağlantının doğruluğunu kontrol etme
    private void validateConnection() {
        if (!isConnected()) {
            logger.log(Level.SEVERE, "Veritabanı bağlantısı başarısız. Sunucu kapanıyor...");
            api.getInstance().stop();
        } else {
            logger.info("Veritabanına bağlantı başarılı.");
        }
    }

    // Veritabanı bağlantısını elde etme
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                logger.info("Bağlantı kapalı. Yeniden bağlantı kuruluyor...");
                connectToDatabase();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Bağlantı kontrolü sırasında hata oluştu: ", e);
        }
        return connection;
    }

    // Veritabanı bağlantısını kurma
    private void connectToDatabase() {
        String dbHost = settingsFactory.get("db.host");
        String dbPort = settingsFactory.get("db.port");
        String dbUsername = settingsFactory.get("db.username");
        String dbPassword = settingsFactory.get("db.password");
        String dbDatabase = settingsFactory.get("db.database");

        if (dbHost == null || dbPort == null || dbUsername == null || dbPassword == null || dbDatabase == null) {
            logger.log(Level.SEVERE, "Veritabanı bağlantı bilgileri eksik!");
            return;
        }

        String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbDatabase + "?autoReconnect=true&useSSL=false";

        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            logger.info("Veritabanına başarıyla bağlanıldı.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Veritabanına bağlanırken hata oluştu: ", e);
        }
    }

    // Asenkron bağlantı kurma
    public CompletableFuture<Connection> connectToDatabaseAsync() {
        return CompletableFuture.supplyAsync(() -> {
            connectToDatabase();
            return connection;
        });
    }

    // Bağlantıyı kapatma
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Veritabanı bağlantısı kapatıldı.");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Bağlantı kapatılırken hata oluştu: ", e);
            }
        }
    }

    // Bağlantının durumunu kontrol etme
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Bağlantı durumu kontrol edilirken hata oluştu: ", e);
            return false;
        }
    }

    // SQL sorgusu çalıştırma
    public void executeQuery(String query) {
        if (!isConnected()) {
            logger.log(Level.WARNING, "Bağlantı mevcut değil, yeniden bağlanılıyor...");
            connectToDatabase();
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
            logger.info("SQL sorgusu başarıyla çalıştırıldı.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL sorgusu çalıştırılırken hata oluştu: ", e);
        }
    }

    // Ayarları almak için yardımcı metotlar
    public String getDatabaseHost() {
        return settingsFactory.get("db.host");
    }

    public String getDatabasePort() {
        return settingsFactory.get("db.port");
    }

    public String getDatabaseUsername() {
        return settingsFactory.get("db.username");
    }

    public String getDatabasePassword() {
        return settingsFactory.get("db.password");
    }

    public String getDatabaseName() {
        return settingsFactory.get("db.database");
    }
}

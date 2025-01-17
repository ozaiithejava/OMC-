package ozaii.factory;

import ozaii.apis.base.FactoryApi;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseFactory {
    private static final Logger logger = Logger.getLogger(DatabaseFactory.class.getName());
    private HikariDataSource dataSource;
    private final SettingsFactory settingsFactory;
    private final FactoryApi api = new FactoryApi();

    public DatabaseFactory(String settingsFilePath) {
        settingsFactory = new SettingsFactory(settingsFilePath);
        initializeHikari();
    }

    private void initializeHikari() {
        String dbHost = settingsFactory.get("db.host");
        String dbPort = settingsFactory.get("db.port");
        String dbUsername = settingsFactory.get("db.username");
        String dbPassword = settingsFactory.get("db.password");
        String dbDatabase = settingsFactory.get("db.database");

        if (dbHost == null || dbPort == null || dbUsername == null || dbPassword == null || dbDatabase == null) {
            logger.log(Level.SEVERE, "Veritabanı bağlantı bilgileri eksik!");
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbDatabase + "?autoReconnect=true&useSSL=false");
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(10);  // Maksimum bağlantı sayısı
        config.setIdleTimeout(30000);   // Bağlantı boşta kalma süresi
        config.setConnectionTimeout(30000); // Bağlantı zaman aşımı süresi
        config.setLeakDetectionThreshold(15000); // Sızıntı tespiti süresi (ms)
        config.setValidationTimeout(5000);       // Bağlantı doğrulama süresi (5 saniye)
        config.setMaxLifetime(1800000);          // Bir bağlantının maksimum ömrü (30 dakika)


        // Performans optimizasyonu
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        dataSource = new HikariDataSource(config);
    }

    public CompletableFuture<Void> validateConnectionAsync() {
        return getConnectionAsync().thenAccept(connection -> {
            try {
                if (connection == null || connection.isClosed()) {
                    logger.log(Level.SEVERE, "Veritabanı bağlantısı başarısız. Sunucu kapanıyor...");
                    api.getInstance().stopAsync();
                } else {
                    logger.info("Veritabanına bağlantı başarılı.");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Bağlantı doğrulama sırasında hata oluştu: ", e);
                api.getInstance().stopAsync();
            }
        });
    }

    public CompletableFuture<Connection> getConnectionAsync() {
        return CompletableFuture.supplyAsync(() -> {
            if (dataSource == null || dataSource.isClosed()) {
                logger.info("Bağlantı kapalı. Yeniden bağlantı kuruluyor...");
                initializeHikari();
            }

            Connection connection = null;
            try {
                connection = dataSource != null ? dataSource.getConnection() : null;
                if (connection == null) {
                    logger.warning("Bağlantı alınamadı.");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Bağlantı alınırken hata oluştu: ", e);
                initializeHikari();
            }

            return connection;
        });
    }



    // Asenkron bağlantı kapama
    public CompletableFuture<Void> closeConnectionAsync() {
        return CompletableFuture.runAsync(() -> {
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
                logger.info("Veritabanı bağlantısı kapatıldı.");
            }
        });
    }

    // Asenkron bağlantı durumu kontrolü
    public CompletableFuture<Boolean> isConnectedAsync() {
        return getConnectionAsync().thenApply(connection -> {
            try {
                return connection != null && !connection.isClosed();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Bağlantı durumu kontrol edilirken hata oluştu: ", e);
                return false;
            }
        });
    }

    public CompletableFuture<Void> executeQueryAsync(String query) {
        return getConnectionAsync().thenAccept(connection -> {
            if (connection != null) {
                // `try-with-resources` kullanarak bağlantıyı güvenli şekilde kapatıyoruz
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(query);
                    logger.info("SQL sorgusu başarıyla çalıştırıldı.");
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQL sorgusu çalıştırılırken hata oluştu: ", e);
                } finally {
                    // Bağlantıyı her zaman serbest bırakıyoruz
                    try {
                        if (connection != null && !connection.isClosed()) {
                            connection.close(); // Bağlantıyı kapat
                        }
                    } catch (SQLException e) {
                        logger.log(Level.SEVERE, "Bağlantı kapatılırken hata oluştu: ", e);
                    }
                }
            } else {
                logger.log(Level.WARNING, "Bağlantı mevcut değil, yeniden bağlanılıyor...");
            }
        });
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

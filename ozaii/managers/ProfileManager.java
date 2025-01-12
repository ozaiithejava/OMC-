package ozaii.managers;

import ozaii.apis.base.FactoryApi;
import ozaii.factory.DatabaseFactory;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileManager {
    FactoryApi api = new FactoryApi();
    private DatabaseFactory databaseFactory;
    private static final Logger logger = Logger.getLogger(ProfileManager.class.getName());
    private String tableName; // Variable to store the table name

    public ProfileManager(String settingsFilePath) {
        // DatabaseFactory sınıfını başlat
        databaseFactory = new DatabaseFactory(settingsFilePath);

        // Retrieve table name dynamically from settings
        this.tableName = api.getSettingsFactory().get("playerProfileTable");  // Assuming settingsFactory.get() exists

        // Profile tablosunun olup olmadığını kontrol et, yoksa oluştur
        checkAndCreateTable();
    }

    // Profile tablosunun var olup olmadığını kontrol et, yoksa oluştur
    private void checkAndCreateTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL UNIQUE, " +
                "account_creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_login_date TIMESTAMP NULL, " +  // NULL değerini kabul etsin
                "last_login_ip VARCHAR(45)" +
                ")";

        // Asynchronous table creation
        CompletableFuture.runAsync(() -> {
            try (Connection connection = databaseFactory.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(createTableQuery)) {
                stmt.executeUpdate();
                logger.info(tableName + " tablosu kontrol edildi ve oluşturuldu (varsa).");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, tableName + " tablosu oluşturulurken hata oluştu: ", e);
            }
        });
    }

    // Profilin var olup olmadığını kontrol et (asynchronously)
    private CompletableFuture<Boolean> profileExistsAsync(String name) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT 1 FROM " + tableName + " WHERE name = ?";

            try (Connection connection = databaseFactory.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Profil kontrolü sırasında hata oluştu: ", e);
            }
            return false;
        });
    }

    // Profil oluşturulmadan önce var olup olmadığını kontrol et
    public CompletableFuture<Void> createProfileIfNotExists(String name) {
        return profileExistsAsync(name).thenCompose(exists -> {
            if (exists) {
                logger.log(Level.WARNING, "Profil zaten mevcut: " + name);
                return CompletableFuture.completedFuture(null);
            }
            return createProfileAsync(name); // Profil yoksa oluştur
        });
    }

    // Oyuncu profilini oluştur (asynchronously)
    public CompletableFuture<Void> createProfileAsync(String name) {
        return CompletableFuture.runAsync(() -> {
            // Profilin var olup olmadığını kontrol et
            profileExistsAsync(name).thenAccept(exists -> {
                if (exists) {
                    // Profil zaten varsa, hata yerine bilgi mesajı yazdır
                    logger.log(Level.INFO, "Profil zaten mevcut: " + name);
                    return;  // Profil zaten var, işlem yapılmaz
                }

                String query = "INSERT INTO " + tableName + " (name) VALUES (?)";

                try (Connection connection = databaseFactory.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, name);
                    stmt.executeUpdate();
                    logger.info("Profil oluşturuldu: " + name);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Profil oluşturulurken hata oluştu: ", e);
                }
            });
        });
    }


    // Oyuncunun profilini güncelle (asynchronously)
    public CompletableFuture<Void> updateProfileAsync(String name, String ipAddress) {
        return CompletableFuture.runAsync(() -> {
            profileExistsAsync(name).thenAccept(exists -> {
                if (!exists) {
                    logger.log(Level.WARNING, "Profil bulunamadı: " + name);
                    return;
                }

                String query = "UPDATE " + tableName + " SET last_login_date = CURRENT_TIMESTAMP, last_login_ip = ? WHERE name = ?";
                try (Connection connection = databaseFactory.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, ipAddress);  // IP adresini güncelliyoruz
                    stmt.setString(2, name);       // Profilin ismini belirtiyoruz
                    stmt.executeUpdate();
                    logger.info("Profil güncellendi: " + name);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Profil güncellenirken hata oluştu: ", e);
                }
            });
        });
    }

    // Profil detaylarını al (asynchronously)
    public CompletableFuture<Profile> getProfileAsync(String name) {
        return profileExistsAsync(name).thenCompose(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Profil bulunamadı: " + name);
                return CompletableFuture.completedFuture(null);
            }

            return CompletableFuture.supplyAsync(() -> {
                String query = "SELECT * FROM " + tableName + " WHERE name = ?";

                try (Connection connection = databaseFactory.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, name);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return new Profile(
                                rs.getString("name"),
                                rs.getTimestamp("account_creation_date"),
                                rs.getTimestamp("last_login_date"),
                                rs.getString("last_login_ip")
                        );
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Profil bilgileri sorgulanırken hata oluştu: ", e);
                }
                return null;
            });
        });
    }

    // Profil sınıfı (Profil bilgileri)
    public static class Profile {
        private String name;
        private Timestamp accountCreationDate;
        private Timestamp lastLoginDate;
        private String lastLoginIp;

        public Profile(String name, Timestamp accountCreationDate, Timestamp lastLoginDate, String lastLoginIp) {
            this.name = name;
            this.accountCreationDate = accountCreationDate;
            this.lastLoginDate = lastLoginDate;
            this.lastLoginIp = lastLoginIp;
        }

        public String getName() {
            return name;
        }

        public Timestamp getAccountCreationDate() {
            return accountCreationDate;
        }

        public Timestamp getLastLoginDate() {
            return lastLoginDate;
        }

        public String getLastLoginIp() {
            return lastLoginIp;
        }

        @Override
        public String toString() {
            return "Profile{" +
                    "name='" + name + '\'' +
                    ", accountCreationDate=" + (accountCreationDate != null ? accountCreationDate.toString() : "null") +
                    ", lastLoginDate=" + (lastLoginDate != null ? lastLoginDate.toString() : "null") +
                    ", lastLoginIp='" + lastLoginIp + '\'' +
                    '}';
        }
    }
}

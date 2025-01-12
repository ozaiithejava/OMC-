package ozaii.managers;

import ozaii.factory.DatabaseFactory;
import ozaii.factory.SettingsFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoinManager {
    private DatabaseFactory databaseFactory;
    private SettingsFactory settingsFactory;
    private String tableName;  // Store the table name here
    private static final Logger logger = Logger.getLogger(CoinManager.class.getName());

    public CoinManager(String settingsFilePath) {
        // Initialize SettingsFactory to read the settings file
        settingsFactory = new SettingsFactory(settingsFilePath);

        // Fetch the table name from SettingsFactory
        this.tableName = settingsFactory.get("coinmanagertable");  // Assuming getTableName() exists in SettingsFactory

        // Initialize DatabaseFactory
        databaseFactory = new DatabaseFactory(settingsFilePath);

        // Check and create table if it does not exist
        checkAndCreateTable().join(); // Run synchronously for initialization
    }

    // Check and create the bank accounts table (if it does not exist)
    private CompletableFuture<Void> checkAndCreateTable() {
        return CompletableFuture.runAsync(() -> {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "coins DOUBLE DEFAULT 0" +
                    ")";

            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(createTableQuery)) {
                stmt.executeUpdate();
                logger.info("Banka hesabı tablosu kontrol edildi ve oluşturuldu (varsa).\n");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Banka hesabı tablosu oluşturulurken hata oluştu: ", e);
            }
        });
    }

    // Create a bank account
    public CompletableFuture<Void> createBankAccount(String name) {
        return accountExists(name).thenAcceptAsync(exists -> {
            if (exists) {
                logger.log(Level.WARNING, "Hesap zaten mevcut: " + name);
                return;
            }

            String query = "INSERT INTO " + tableName + " (name, coins) VALUES (?, 0)";

            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.executeUpdate();
                logger.info("Banka hesabı oluşturuldu: " + name);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Banka hesabı oluşturulurken hata oluştu: ", e);
            }
        });
    }

    // Deposit money into the account
    public CompletableFuture<Void> deposit(String name, double amount) {
        return accountExists(name).thenAcceptAsync(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Hesap bulunamadı: " + name);
                return;
            }

            if (amount <= 0) {
                logger.log(Level.WARNING, "Geçersiz miktar: " + amount);
                return;
            }

            String query = "UPDATE " + tableName + " SET coins = coins + ? WHERE name = ?";

            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                stmt.setDouble(1, amount);
                stmt.setString(2, name);
                stmt.executeUpdate();
                logger.info("Hesaba yatırıldı: " + amount + " " + name);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Para yatırılırken hata oluştu: ", e);
            }
        });
    }

    // Withdraw money from the account
    public CompletableFuture<Void> remove(String name, double amount) {
        return getCoins(name).thenAcceptAsync(currentBalance -> {
            if (currentBalance < amount) {
                logger.log(Level.WARNING, "Yetersiz bakiye: " + name);
                return;
            }

            String query = "UPDATE " + tableName + " SET coins = coins - ? WHERE name = ?";

            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                stmt.setDouble(1, amount);
                stmt.setString(2, name);
                stmt.executeUpdate();
                logger.info("Hesaptan çekildi: " + amount + " " + name);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Para çekilirken hata oluştu: ", e);
            }
        });
    }

    // Get the balance of an account
    public CompletableFuture<Double> getCoins(String name) {
        return accountExists(name).thenApplyAsync(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Hesap bulunamadı: " + name);
                return 0.0;
            }

            String query = "SELECT coins FROM " + tableName + " WHERE name = ?";

            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("coins");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Bakiye sorgulama sırasında hata oluştu: ", e);
            }
            return 0.0;
        });
    }

    // Delete a bank account
    public CompletableFuture<Void> deleteAccount(String name) {
        return accountExists(name).thenAcceptAsync(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Hesap bulunamadı: " + name);
                return;
            }

            String query = "DELETE FROM " + tableName + " WHERE name = ?";

            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.executeUpdate();
                logger.info("Hesap silindi: " + name);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Hesap silinirken hata oluştu: ", e);
            }
        });
    }

    // Transfer money between two accounts
    public CompletableFuture<Void> transferAccount(String sender, String taker, double amount) {
        return CompletableFuture.allOf(accountExists(sender), accountExists(taker))
                .thenRunAsync(() -> {
                    double senderBalance = getCoins(sender).join();  // Wait for balance
                    if (senderBalance < amount) {
                        logger.log(Level.WARNING, "Yetersiz bakiye: " + sender);
                        return;
                    }

                    String updateSenderQuery = "UPDATE " + tableName + " SET coins = coins - ? WHERE name = ?";
                    String updateTakerQuery = "UPDATE " + tableName + " SET coins = coins + ? WHERE name = ?";

                    try (PreparedStatement stmtSender = databaseFactory.getConnection().prepareStatement(updateSenderQuery);
                         PreparedStatement stmtTaker = databaseFactory.getConnection().prepareStatement(updateTakerQuery)) {

                        stmtSender.setDouble(1, amount);
                        stmtSender.setString(2, sender);
                        stmtSender.executeUpdate();

                        stmtTaker.setDouble(1, amount);
                        stmtTaker.setString(2, taker);
                        stmtTaker.executeUpdate();

                        logger.info("Transfer başarılı: " + amount + " " + sender + " -> " + taker);
                    } catch (SQLException e) {
                        logger.log(Level.SEVERE, "Transfer işlemi sırasında hata oluştu: ", e);
                    }
                });
    }

    // Check if an account exists
    private CompletableFuture<Boolean> accountExists(String name) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT 1 FROM " + tableName + " WHERE name = ?";
            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Hesap kontrolü sırasında hata oluştu: ", e);
            }
            return false;
        });
    }

    // Reset coins to 0 for a player
    public CompletableFuture<Void> resetCoins(String name) {
        return accountExists(name).thenAcceptAsync(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Hesap bulunamadı: " + name);
                return;
            }

            String query = "UPDATE " + tableName + " SET coins = 0 WHERE name = ?";

            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.executeUpdate();
                logger.info("Hesap sıfırlandı: " + name);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Hesap sıfırlanırken hata oluştu: ", e);
            }
        });
    }
}

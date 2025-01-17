package ozaii.managers;

import ozaii.factory.DatabaseFactory;
import ozaii.factory.SettingsFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoinManager {
    private final DatabaseFactory databaseFactory;
    private final SettingsFactory settingsFactory;
    private final String tableName;
    private static final Logger logger = Logger.getLogger(CoinManager.class.getName());

    public CoinManager(String settingsFilePath) {
        settingsFactory = new SettingsFactory(settingsFilePath);
        this.tableName = settingsFactory.get("coinmanagertable");
        databaseFactory = new DatabaseFactory(settingsFilePath);

        checkAndCreateTable()
                .thenRun(() -> logger.info("Tablo kontrol edildi ve oluşturuldu."))
                .exceptionally(e -> {
                    logger.log(Level.SEVERE, "Tablo oluşturulurken hata oluştu: ", e);
                    return null;
                });
    }

    private CompletableFuture<Void> checkAndCreateTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "coins DOUBLE DEFAULT 0" +
                ")";
        return executeUpdate(createTableQuery, stmt -> {}, "Tablo oluşturuldu.");
    }

    public CompletableFuture<Void> createBankAccount(String name) {
        return accountExists(name).thenCompose(exists -> {
            if (exists) {
                logger.log(Level.WARNING, "Hesap zaten mevcut: " + name);
                return CompletableFuture.completedFuture(null);
            }
            String query = "INSERT INTO " + tableName + " (name, coins) VALUES (?, 0)";
            return executeUpdate(query, stmt -> stmt.setString(1, name), "Hesap oluşturuldu: " + name);
        });
    }

    public CompletableFuture<Void> deposit(String name, double amount) {
        if (amount <= 0) {
            logger.log(Level.WARNING, "Geçersiz miktar: " + amount);
            return CompletableFuture.completedFuture(null);
        }
        return accountExists(name).thenCompose(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Hesap bulunamadı: " + name);
                return CompletableFuture.completedFuture(null);
            }
            String query = "UPDATE " + tableName + " SET coins = coins + ? WHERE name = ?";
            return executeUpdate(query, stmt -> {
                stmt.setDouble(1, amount);
                stmt.setString(2, name);
            }, "Hesaba yatırıldı: " + amount + " -> " + name);
        });
    }

    public CompletableFuture<Void> remove(String name, double amount) {
        if (amount <= 0) {
            logger.log(Level.WARNING, "Geçersiz miktar: " + amount);
            return CompletableFuture.completedFuture(null);
        }
        return getCoins(name).thenCompose(balance -> {
            if (balance < amount) {
                logger.log(Level.WARNING, "Yetersiz bakiye: " + name);
                return CompletableFuture.completedFuture(null);
            }
            String query = "UPDATE " + tableName + " SET coins = coins - ? WHERE name = ?";
            return executeUpdate(query, stmt -> {
                stmt.setDouble(1, amount);
                stmt.setString(2, name);
            }, "Hesaptan çekildi: " + amount + " -> " + name);
        });
    }

    public CompletableFuture<Double> getCoins(String name) {
        String query = "SELECT coins FROM " + tableName + " WHERE name = ?";
        return executeQuery(query, stmt -> stmt.setString(1, name), rs -> rs.next() ? rs.getDouble("coins") : 0.0);
    }

    public CompletableFuture<Void> deleteAccount(String name) {
        return accountExists(name).thenCompose(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Hesap bulunamadı: " + name);
                return CompletableFuture.completedFuture(null);
            }
            String query = "DELETE FROM " + tableName + " WHERE name = ?";
            return executeUpdate(query, stmt -> stmt.setString(1, name), "Hesap silindi: " + name);
        });
    }

    public CompletableFuture<List<String>> topCoin(int limit) {
        String query = "SELECT name, coins FROM " + tableName + " ORDER BY coins DESC LIMIT ?";
        return executeQuery(query, stmt -> stmt.setInt(1, limit), rs -> {
            List<String> topAccounts = new ArrayList<>();
            while (rs.next()) {
                topAccounts.add("Name: " + rs.getString("name") + ", Coins: " + rs.getDouble("coins"));
            }
            return topAccounts;
        });
    }

    private CompletableFuture<Boolean> accountExists(String name) {
        String query = "SELECT 1 FROM " + tableName + " WHERE name = ?";
        return executeQuery(query, stmt -> stmt.setString(1, name), ResultSet::next);
    }

    private CompletableFuture<Void> executeUpdate(String query, ThrowingConsumer<PreparedStatement> parameterSetter, String successMessage) {
        return databaseFactory.getConnectionAsync().thenAcceptAsync(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                parameterSetter.accept(stmt);
                stmt.executeUpdate();
                logger.info(successMessage);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQL hatası: ", e);
            } finally {
                closeConnection(connection);
            }
        });
    }

    private <T> CompletableFuture<T> executeQuery(String query, ThrowingConsumer<PreparedStatement> parameterSetter, ThrowingFunction<ResultSet, T> resultHandler) {
        return databaseFactory.getConnectionAsync().thenApplyAsync(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                parameterSetter.accept(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    return resultHandler.apply(rs);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQL hatası: ", e);
                return null;
            } finally {
                closeConnection(connection);
            }
        });
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Bağlantı kapatılırken hata oluştu: ", e);
        }
    }

    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T t) throws SQLException;
    }

    @FunctionalInterface
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws SQLException;
    }


    /**
     * Resets all players' coin balances to 0 asynchronously.
     */
    public CompletableFuture<Void> resetAllAsync() {
        String query = "UPDATE " + tableName + " SET coins = 0";
        return executeUpdate(query, stmt -> {}, "Tüm oyuncuların coinleri sıfırlandı.");
    }

    /**
     * Resets a player's coin balance to 0 asynchronously.
     * @param name The name of the player to reset.
     */
    public CompletableFuture<Void> resetCoins(String name) {
        String query = "UPDATE " + tableName + " SET coins = 0 WHERE name = ?";
        return executeUpdate(query, stmt -> stmt.setString(1, name), "Oyuncunun coini sıfırlandı: " + name);
    }

    /**
     * Fetches the top coin holders with ranking.
     * @param limit The number of top players to fetch.
     * @return A list of strings representing the top players with their ranks and coins.
     */
    public CompletableFuture<List<String>> topCoinWithRanking(int limit) {
        String query = "SELECT name, coins FROM " + tableName + " ORDER BY coins DESC LIMIT ?";
        return executeQuery(query, stmt -> stmt.setInt(1, limit), rs -> {
            List<String> topAccounts = new ArrayList<>();
            int rank = 1;
            while (rs.next()) {
                topAccounts.add("Rank: " + rank + " - Name: " + rs.getString("name") + ", Coins: " + rs.getDouble("coins"));
                rank++;
            }
            return topAccounts;
        });
    }
    /**
     * Creates a bank account for the player if it does not already exist.
     * @param name The name of the player to create an account for.
     * @return A CompletableFuture that will be completed when the account is created or already exists.
     */
    public CompletableFuture<Void> createAccountIfNotExists(String name) {
        return accountExists(name).thenCompose(exists -> {
            if (exists) {
                // If the account already exists, we log and do nothing.
                logger.log(Level.INFO, "Hesap zaten mevcut: " + name);
                return CompletableFuture.completedFuture(null);
            }
            // If the account doesn't exist, we create it.
            return createBankAccount(name);
        });
    }
    /**
     * Transfers coins from one account to another.
     * @param from The name of the account to transfer coins from.
     * @param to The name of the account to transfer coins to.
     * @param amount The amount to transfer.
     * @return A CompletableFuture that will be completed when the transfer is done or failed.
     */
    public CompletableFuture<Void> transfer(String from, String to, double amount) {
        if (amount <= 0) {
            logger.log(Level.WARNING, "Geçersiz transfer miktarı: " + amount);
            return CompletableFuture.completedFuture(null);
        }

        return accountExists(from).thenCompose(fromExists -> {
            if (!fromExists) {
                logger.log(Level.WARNING, "Kaynak hesap bulunamadı: " + from);
                return CompletableFuture.completedFuture(null);
            }
            return accountExists(to);
        }).thenCompose(toExists -> {
            if (!toExists) {
                logger.log(Level.WARNING, "Hedef hesap bulunamadı: " + to);
                return CompletableFuture.completedFuture(null);
            }
            return getCoins(from);
        }).thenCompose(fromBalance -> {
            if (fromBalance < amount) {
                logger.log(Level.WARNING, "Yetersiz bakiye: " + from);
                return CompletableFuture.completedFuture(null);
            }

            // Proceed with the transfer: remove coins from the "from" account
            return remove(from, amount).thenCompose(v -> {
                // Then deposit coins into the "to" account
                return deposit(to, amount);
            }).thenRun(() -> {
                logger.info("Transfer başarılı: " + amount + " " + from + " -> " + to);
            });
        });
    }


}

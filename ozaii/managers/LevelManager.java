package ozaii.managers;

import ozaii.apis.base.FactoryApi;
import ozaii.factory.DatabaseFactory;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LevelManager {
    private final FactoryApi api;
    private final DatabaseFactory databaseFactory;
    private final String tableName;
    private static final Logger logger = Logger.getLogger(LevelManager.class.getName());

    public LevelManager(String settingsFilePath) {
        this.api = new FactoryApi();
        this.databaseFactory = new DatabaseFactory(settingsFilePath);
        this.tableName = api.getSettingsFactory().get("levelmanagertable");
        initializeTable();
    }

    // Initialize the level table
    private void initializeTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "level INT DEFAULT 1" +
                ")";
        executeUpdate(createTableQuery, "Level table checked and created (if it exists).");
    }

    // Get the player's level asynchronously
    public CompletableFuture<Integer> getLevelAsync(String name) {
        String query = "SELECT level FROM " + tableName + " WHERE name = ?";
        return accountExistsAsync(name).thenCompose(exists -> {
            if (!exists) {
                logger.warning("Account not found: " + name);
                return CompletableFuture.completedFuture(1); // Default level
            }
            return executeQuery(query, stmt -> stmt.setString(1, name), rs -> rs.next() ? rs.getInt("level") : 1);
        });
    }

    // Change the player's level (increase or decrease)
    public CompletableFuture<Void> changeLevelAsync(String name, int delta, boolean increase) {
        return accountExistsAsync(name).thenCompose(exists -> {
            if (!exists) {
                logger.warning("Account not found: " + name);
                return CompletableFuture.completedFuture(null);
            }
            return getLevelAsync(name).thenAccept(currentLevel -> {
                int newLevel = increase ? currentLevel + delta : Math.max(1, currentLevel - delta);
                String query = "UPDATE " + tableName + " SET level = ? WHERE name = ?";
                executeUpdate(query, stmt -> {
                    stmt.setInt(1, newLevel);
                    stmt.setString(2, name);
                }, "Level updated for " + name + ". New level: " + newLevel);
            });
        });
    }

    // Reset the player's level
    public CompletableFuture<Void> resetLevelAsync(String name) {
        return setLevelAsync(name, 1);
    }

    // Set the player's level
    public CompletableFuture<Void> setLevelAsync(String name, int level) {
        if (level <= 0) {
            logger.warning("Invalid level value: " + level);
            return CompletableFuture.completedFuture(null);
        }
        return accountExistsAsync(name).thenCompose(exists -> {
            if (!exists) {
                logger.warning("Account not found: " + name);
                return CompletableFuture.completedFuture(null);
            }
            String query = "UPDATE " + tableName + " SET level = ? WHERE name = ?";
            return executeUpdate(query, stmt -> {
                stmt.setInt(1, level);
                stmt.setString(2, name);
            }, "Level set for " + name + ". New level: " + level);
        });
    }

    // Check if the account exists asynchronously
    private CompletableFuture<Boolean> accountExistsAsync(String name) {
        String query = "SELECT 1 FROM " + tableName + " WHERE name = ?";
        return executeQuery(query, stmt -> stmt.setString(1, name), ResultSet::next);
    }

    // Create a new level account for the player
    public CompletableFuture<Void> createLevelAccountAsync(String name) {
        return accountExistsAsync(name).thenCompose(exists -> {
            if (exists) {
                logger.warning("Account already exists: " + name);
                return CompletableFuture.completedFuture(null);
            }
            String query = "INSERT INTO " + tableName + " (name, level) VALUES (?, 1)";
            return executeUpdate(query, stmt -> stmt.setString(1, name), "Level account created for: " + name);
        });
    }

    // Execute an update query
    private CompletableFuture<Void> executeUpdate(String query, ThrowingConsumer<PreparedStatement> parameterSetter, String successMessage) {
        return databaseFactory.getConnectionAsync().thenCompose(connection -> CompletableFuture.runAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                parameterSetter.accept(stmt);
                stmt.executeUpdate();
                logger.info(successMessage);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error executing update: ", e);
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error closing connection: ", e);
                }
            }
        }));
    }

    private CompletableFuture<Void> executeUpdate(String query, String successMessage) {
        return executeUpdate(query, stmt -> {}, successMessage);
    }

    private <T> CompletableFuture<T> executeQuery(String query, ThrowingConsumer<PreparedStatement> parameterSetter, ThrowingFunction<ResultSet, T> resultHandler) {
        return databaseFactory.getConnectionAsync().thenCompose(connection -> CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                parameterSetter.accept(stmt);
                try (ResultSet rs = stmt.executeQuery()) {
                    return resultHandler.apply(rs);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error executing query: ", e);
                return null;
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error closing connection: ", e);
                }
            }
        }));
    }

    // Functional interfaces for lambda exception handling
    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T t) throws SQLException;
    }

    @FunctionalInterface
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws SQLException;
    }
    // Create a level account for the player if it does not exist
    public CompletableFuture<Void> createLevelAccountIfNotExistsAsync(String name) {
        return accountExistsAsync(name).thenCompose(exists -> {
            if (exists) {
                logger.info("Account already exists: " + name);
                return CompletableFuture.completedFuture(null); // Do nothing if account exists
            }
            // If account doesn't exist, create it with default level
            String query = "INSERT INTO " + tableName + " (name, level) VALUES (?, 1)";
            return executeUpdate(query, stmt -> stmt.setString(1, name), "Level account created for: " + name);
        });
    }
    /**
     * Increases the player's level by a specified amount asynchronously.
     * @param name The name of the player.
     * @param amount The amount to increase the level by.
     * @return A CompletableFuture that will be completed when the operation is done.
     */
    public CompletableFuture<Void> increaseLevelAsync(String name, int amount) {
        if (amount <= 0) {
            logger.warning("Geçersiz artırma miktarı: " + amount);
            return CompletableFuture.completedFuture(null);
        }

        return accountExistsAsync(name).thenCompose(exists -> {
            if (!exists) {
                logger.warning("Hesap bulunamadı: " + name);
                return CompletableFuture.completedFuture(null);
            }
            return getLevelAsync(name);
        }).thenCompose(currentLevel -> {
            int newLevel = currentLevel + amount;
            String query = "UPDATE " + tableName + " SET level = ? WHERE name = ?";
            return executeUpdate(query, stmt -> {
                stmt.setInt(1, newLevel);
                stmt.setString(2, name);
            }, "Seviye artırıldı: " + name + " -> Yeni seviye: " + newLevel);
        });
    }
    /**
     * Decreases the player's level by a specified amount asynchronously.
     * @param name The name of the player.
     * @param amount The amount to decrease the level by.
     * @return A CompletableFuture that will be completed when the operation is done.
     */
    public CompletableFuture<Void> decreaseLevelAsync(String name, int amount) {
        if (amount <= 0) {
            logger.warning("Geçersiz azaltma miktarı: " + amount);
            return CompletableFuture.completedFuture(null);
        }

        return accountExistsAsync(name).thenCompose(exists -> {
            if (!exists) {
                logger.warning("Hesap bulunamadı: " + name);
                return CompletableFuture.completedFuture(null);
            }
            return getLevelAsync(name);
        }).thenCompose(currentLevel -> {
            int newLevel = Math.max(1, currentLevel - amount); // Ensure level doesn't drop below 1
            String query = "UPDATE " + tableName + " SET level = ? WHERE name = ?";
            return executeUpdate(query, stmt -> {
                stmt.setInt(1, newLevel);
                stmt.setString(2, name);
            }, "Seviye azaltıldı: " + name + " -> Yeni seviye: " + newLevel);
        });
    }


}

package ozaii.managers;

import ozaii.apis.base.FactoryApi;
import ozaii.factory.DatabaseFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LevelManager {
    FactoryApi api = new FactoryApi();
    private DatabaseFactory databaseFactory;
    private String tableName;  // To store the table name dynamically
    private static final Logger logger = Logger.getLogger(LevelManager.class.getName());

    public LevelManager(String settingsFilePath) {
        // Initialize DatabaseFactory with the settings file
        databaseFactory = new DatabaseFactory(settingsFilePath);

        // Get the table name from the settings file dynamically
        this.tableName = api.getSettingsFactory().get("levelmanagertable");  // Assuming get() method exists in SettingsFactory

        // Level table check and create
        checkAndCreateTableAsync();
    }

    // Check and create the Level table asynchronously
    private void checkAndCreateTableAsync() {
        CompletableFuture.runAsync(() -> {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "level INT DEFAULT 1" +
                    ")";

            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(createTableQuery)) {
                stmt.executeUpdate();
                logger.info("Level table checked and created (if it exists).");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error while creating level table: ", e);
            }
        });
    }

    // Get the player's level asynchronously
    public CompletableFuture<Integer> getLevelAsync(String name) {
        return accountExistsAsync(name).thenComposeAsync(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Account not found: " + name);
                return CompletableFuture.completedFuture(1); // Default to level 1
            }

            String query = "SELECT level FROM " + tableName + " WHERE name = ?";
            return CompletableFuture.supplyAsync(() -> {
                try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                    stmt.setString(1, name);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("level");
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error while querying level: ", e);
                }
                return 1; // Default to level 1
            });
        });
    }

    // Increase the player's level asynchronously
    public CompletableFuture<Void> increaseLevelAsync(String name, int amount) {
        return accountExistsAsync(name).thenComposeAsync(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Account not found: " + name);
                return CompletableFuture.completedFuture(null);
            }

            if (amount <= 0) {
                logger.log(Level.WARNING, "Invalid level increase amount: " + amount);
                return CompletableFuture.completedFuture(null);
            }

            return getLevelAsync(name).thenAcceptAsync(currentLevel -> {
                int newLevel = currentLevel + amount;
                String query = "UPDATE " + tableName + " SET level = ? WHERE name = ?";

                try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                    stmt.setInt(1, newLevel);
                    stmt.setString(2, name);
                    stmt.executeUpdate();
                    logger.info("Level increased: " + name + " new level: " + newLevel);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error while increasing level: ", e);
                }
            });
        });
    }

    // Decrease the player's level asynchronously
    public CompletableFuture<Void> decreaseLevelAsync(String name, int amount) {
        return accountExistsAsync(name).thenComposeAsync(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Account not found: " + name);
                return CompletableFuture.completedFuture(null);
            }

            if (amount <= 0) {
                logger.log(Level.WARNING, "Invalid level decrease amount: " + amount);
                return CompletableFuture.completedFuture(null);
            }

            return getLevelAsync(name).thenAcceptAsync(currentLevel -> {
                int newLevel = currentLevel - amount;
                if (newLevel < 1) {
                    newLevel = 1; // Level can't go below 1
                }

                String query = "UPDATE " + tableName + " SET level = ? WHERE name = ?";

                try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                    stmt.setInt(1, newLevel);
                    stmt.setString(2, name);
                    stmt.executeUpdate();
                    logger.info("Level decreased: " + name + " new level: " + newLevel);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error while decreasing level: ", e);
                }
            });
        });
    }

    // Reset the player's level asynchronously
    public CompletableFuture<Void> resetLevelAsync(String name) {
        return accountExistsAsync(name).thenComposeAsync(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Account not found: " + name);
                return CompletableFuture.completedFuture(null);
            }

            String query = "UPDATE " + tableName + " SET level = 1 WHERE name = ?";

            return CompletableFuture.runAsync(() -> {
                try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                    stmt.setString(1, name);
                    stmt.executeUpdate();
                    logger.info("Level reset: " + name);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error while resetting level: ", e);
                }
            });
        });
    }

    // Set the player's level asynchronously
    public CompletableFuture<Void> setLevelAsync(String name, int amount) {
        return accountExistsAsync(name).thenComposeAsync(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Account not found: " + name);
                return CompletableFuture.completedFuture(null);
            }

            if (amount <= 0) {
                logger.log(Level.WARNING, "Invalid level amount: " + amount);
                return CompletableFuture.completedFuture(null);
            }

            String query = "UPDATE " + tableName + " SET level = ? WHERE name = ?";

            return CompletableFuture.runAsync(() -> {
                try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                    stmt.setInt(1, amount);
                    stmt.setString(2, name);
                    stmt.executeUpdate();
                    logger.info("Level updated: " + name + " new level: " + amount);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error while updating level: ", e);
                }
            });
        });
    }

    // Check if the account exists asynchronously
    private CompletableFuture<Boolean> accountExistsAsync(String name) {
        String query = "SELECT 1 FROM " + tableName + " WHERE name = ?";
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error while checking account: ", e);
            }
            return false;
        });
    }

    // Create the level account for the player asynchronously
    public CompletableFuture<Void> createLevelAccountAsync(String name) {
        return accountExistsAsync(name).thenComposeAsync(exists -> {
            if (exists) {
                logger.log(Level.WARNING, "Account already exists: " + name);
                return CompletableFuture.completedFuture(null);
            }

            String query = "INSERT INTO " + tableName + " (name, level) VALUES (?, 1)";

            return CompletableFuture.runAsync(() -> {
                try (PreparedStatement stmt = databaseFactory.getConnection().prepareStatement(query)) {
                    stmt.setString(1, name);
                    stmt.executeUpdate();
                    logger.info("Level account created: " + name);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error while creating level account: ", e);
                }
            });
        });
    }
}

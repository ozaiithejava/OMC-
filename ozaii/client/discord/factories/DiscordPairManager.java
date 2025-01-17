package ozaii.client.discord.factories;

import ozaii.apis.base.FactoryApi;
import ozaii.factory.DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscordPairManager {
    private static final Logger logger = Logger.getLogger(DiscordPairManager.class.getName());
    private final DatabaseFactory databaseFactory;
    private final String tableName;
    private static FactoryApi api;

    // Constructor initializes the database factory and retrieves table name from settings
    public DiscordPairManager(DatabaseFactory databaseFactory) {
        this.api = new FactoryApi();
        this.databaseFactory = api.getDatabaseFactory();
        this.tableName = api.getSettingsFactory().get("db.discordPairTable");

        if (this.tableName == null) {
            throw new IllegalStateException("Table name is not specified! Please check the settings file.");
        }

        // Create the pairing table if it doesn't exist
        createPairTableIfNotExists();
    }

    /**
     * Creates the pair table if it doesn't already exist.
     */
    private void createPairTableIfNotExists() {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "minecraft_uuid VARCHAR(36) NOT NULL UNIQUE, " +
                "minecraft_name VARCHAR(255) NOT NULL, " +
                "discord_id VARCHAR(20) NOT NULL UNIQUE" +
                ");";

        // Execute the query asynchronously with CompletableFuture
        executeQueryAsync(query);
    }

    /**
     * Executes a query asynchronously.
     *
     * @param query The SQL query to be executed
     */
    private void executeQueryAsync(String query) {
        databaseFactory.getConnectionAsync().thenAccept(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.executeUpdate();
                logger.info("Query executed successfully: " + query);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error executing query: ", e);
            } finally {
                closeResources(connection, null, null);
            }
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Error obtaining connection: ", ex);
            return null;
        });
    }

    /**
     * Pairs a Minecraft player with a Discord user asynchronously.
     *
     * @param minecraftUuid  The UUID of the Minecraft player.
     * @param minecraftName  The name of the Minecraft player.
     * @param discordId      The Discord user ID.
     * @return CompletableFuture that resolves to true if pairing was successful, otherwise false.
     */
    public CompletableFuture<Boolean> pairPlayerWithDiscord(String minecraftUuid, String minecraftName, String discordId) {
        String query = "INSERT INTO " + tableName + " (minecraft_uuid, minecraft_name, discord_id) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE discord_id = VALUES(discord_id);";

        return databaseFactory.getConnectionAsync().thenApply(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, minecraftUuid);
                stmt.setString(2, minecraftName);
                stmt.setString(3, discordId);
                stmt.executeUpdate();
                logger.info("Minecraft player and Discord user paired.");
                return true;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error occurred during pairing: ", e);
                return false;
            } finally {
                closeResources(connection, null, null);
            }
        });
    }

    /**
     * Retrieves the paired Discord ID for a given Minecraft UUID asynchronously.
     *
     * @param minecraftUuid The UUID of the Minecraft player.
     * @return CompletableFuture that resolves to an Optional containing the Discord ID if found.
     */
    public CompletableFuture<Optional<String>> getDiscordIdByMinecraftUuid(String minecraftUuid) {
        String query = "SELECT discord_id FROM " + tableName + " WHERE minecraft_uuid = ?";

        return databaseFactory.getConnectionAsync().thenApply(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, minecraftUuid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(rs.getString("discord_id"));
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error retrieving Discord ID: ", e);
            } finally {
                closeResources(connection, null, null);
            }
            return Optional.empty();
        });
    }

    /**
     * Retrieves the paired Minecraft UUID for a given Discord ID asynchronously.
     *
     * @param discordId The Discord user ID.
     * @return CompletableFuture that resolves to an Optional containing the Minecraft UUID if found.
     */
    public CompletableFuture<Optional<String>> getMinecraftUuidByDiscordId(String discordId) {
        String query = "SELECT minecraft_uuid FROM " + tableName + " WHERE discord_id = ?";

        return databaseFactory.getConnectionAsync().thenApply(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, discordId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(rs.getString("minecraft_uuid"));
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error retrieving Minecraft UUID: ", e);
            } finally {
                closeResources(connection, null, null);
            }
            return Optional.empty();
        });
    }

    /**
     * Unpairs a player or Discord user by their UUID or Discord ID.
     *
     * @param minecraftUuid The UUID of the Minecraft player or Discord ID.
     * @return CompletableFuture that resolves to true if unpairing was successful, otherwise false.
     */
    public CompletableFuture<Boolean> unpairPlayerOrDiscord(String minecraftUuid) {
        String query = "DELETE FROM " + tableName + " WHERE minecraft_uuid = ? OR discord_id = ?";

        return databaseFactory.getConnectionAsync().thenApply(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, minecraftUuid);
                stmt.setString(2, minecraftUuid);
                int rows = stmt.executeUpdate();
                logger.info(rows + " pair(s) removed.");
                return rows > 0;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error removing pair: ", e);
                return false;
            } finally {
                closeResources(connection, null, null);
            }
        });
    }

    /**
     * Removes the pairing between a Minecraft player and Discord account.
     *
     * @param minecraftUuid The UUID of the Minecraft player or Discord ID.
     * @return CompletableFuture that resolves to true if removal was successful, otherwise false.
     */
    public CompletableFuture<Boolean> removeDiscordPair(String minecraftUuid) {
        String query = "DELETE FROM " + tableName + " WHERE minecraft_uuid = ? OR discord_id = ?";

        return databaseFactory.getConnectionAsync().thenApply(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, minecraftUuid); // This can be either Minecraft UUID or Discord ID
                stmt.setString(2, minecraftUuid); // Reusing the same parameter for both
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    logger.info("Discord and Minecraft pair successfully removed.");
                    return true;
                } else {
                    logger.warning("Pair not found.");
                    return false;
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error removing pair: ", e);
                return false;
            } finally {
                closeResources(connection, null, null);
            }
        });
    }

    /**
     * Closes the provided resources to prevent connection leaks.
     *
     * @param connection The database connection to close.
     * @param stmt The prepared statement to close.
     * @param rs The result set to close.
     */
    private void closeResources(Connection connection, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error closing resources: ", e);
        }
    }
}

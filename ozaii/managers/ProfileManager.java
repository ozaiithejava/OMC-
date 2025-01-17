package ozaii.managers;

import ozaii.apis.base.FactoryApi;
import ozaii.factory.DatabaseFactory;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileManager {

    private final FactoryApi api;
    private final DatabaseFactory databaseFactory;
    private static final Logger logger = Logger.getLogger(ProfileManager.class.getName());
    private final String tableName;

    public ProfileManager( String settingsFilePath) {
        this.api = new FactoryApi();
        this.databaseFactory = api.getDatabaseFactory();
        this.tableName = api.getSettingsFactory().get("playerProfileTable");
        checkAndCreateTable();
    }

    // Check and create the table asynchronously
    private void checkAndCreateTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL UNIQUE, " +
                "account_creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_login_date TIMESTAMP NULL, " +
                "last_login_ip VARCHAR(45)" +
                ")";

        CompletableFuture.runAsync(() ->
                executeUpdate(createTableQuery, stmt -> {}, "Table checked and created.")
        );
    }

    // Check if the profile exists asynchronously
    private CompletableFuture<Boolean> profileExistsAsync(String name) {
        String query = "SELECT 1 FROM " + tableName + " WHERE name = ?";
        return CompletableFuture.supplyAsync(() -> executeQuery(query, stmt -> stmt.setString(1, name), ResultSet::next));
    }

    // Create a profile if it does not exist asynchronously
    public CompletableFuture<Void> createProfileIfNotExists(String name) {
        return profileExistsAsync(name).thenCompose(exists -> {
            if (exists) {
                logger.log(Level.INFO, "Profile already exists: " + name);
                return CompletableFuture.completedFuture(null);
            }
            return createProfileAsync(name);
        });
    }

    // Create a new profile asynchronously
    public CompletableFuture<Void> createProfileAsync(String name) {
        String query = "INSERT INTO " + tableName + " (name) VALUES (?)";
        return CompletableFuture.runAsync(() -> executeUpdate(query, stmt -> stmt.setString(1, name), "Profile created: " + name));
    }

    // Update profile with last login date and IP asynchronously
    public CompletableFuture<Void> updateProfileAsync(String name, String ipAddress) {
        return profileExistsAsync(name).thenCompose(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Profile not found: " + name);
                return CompletableFuture.completedFuture(null);
            }

            String query = "UPDATE " + tableName + " SET last_login_date = CURRENT_TIMESTAMP, last_login_ip = ? WHERE name = ?";
            return CompletableFuture.runAsync(() -> executeUpdate(query, stmt -> {
                stmt.setString(1, ipAddress);
                stmt.setString(2, name);
            }, "Profile updated: " + name));
        });
    }

    // Get profile details asynchronously
    public CompletableFuture<Profile> getProfileAsync(String name) {
        return profileExistsAsync(name).thenCompose(exists -> {
            if (!exists) {
                logger.log(Level.WARNING, "Profile not found: " + name);
                return CompletableFuture.completedFuture(null);
            }

            String query = "SELECT * FROM " + tableName + " WHERE name = ?";
            return CompletableFuture.supplyAsync(() -> executeQuery(query, stmt -> stmt.setString(1, name), rs -> {
                if (rs.next()) {
                    return new Profile(
                            rs.getString("name"),
                            rs.getTimestamp("account_creation_date"),
                            rs.getTimestamp("last_login_date"),
                            rs.getString("last_login_ip")
                    );
                }
                return null;
            }));
        });
    }

    // Profile class to hold player profile details
    public static class Profile {
        private final String name;
        private final Timestamp accountCreationDate;
        private final Timestamp lastLoginDate;
        private final String lastLoginIp;

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
                    ", accountCreationDate=" + accountCreationDate +
                    ", lastLoginDate=" + lastLoginDate +
                    ", lastLoginIp='" + lastLoginIp + '\'' +
                    '}';
        }
    }

    // Helper method to execute updates
    private void executeUpdate(String query, ThrowingConsumer<PreparedStatement> parameterSetter, String successMessage) {
        try (Connection connection = databaseFactory.getConnectionAsync().join();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            parameterSetter.accept(stmt);
            stmt.executeUpdate();
            logger.info(successMessage);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error: ", e);
        }
    }

    // Helper method to execute queries
    private <T> T executeQuery(String query, ThrowingConsumer<PreparedStatement> parameterSetter, ThrowingFunction<ResultSet, T> resultHandler) {
        try (Connection connection = databaseFactory.getConnectionAsync().join();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            parameterSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                return resultHandler.apply(rs);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error: ", e);
        }
        return null;
    }

    // Functional interfaces for lambdas
    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T t) throws SQLException;
    }

    @FunctionalInterface
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws SQLException;
    }
}

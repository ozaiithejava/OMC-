package ozaii.apis.base;

import ozaii.apis.managers.CoinApi;
import ozaii.apis.managers.LevelApi;
import ozaii.factory.DatabaseFactory;
import ozaii.factory.SettingsFactory;
import ozaii.managers.CoinManager;
import ozaii.managers.LevelManager;
import ozaii.managers.ProfileManager;
import ozaii.ozaii;

public class FactoryApi {

    private static final String SERVER_NAME = "OMC";
    private static final String SERVER_VERSION = "1.0.0";
    private static final String OWNER = "ozaiithejava";
    private static final String OWNER_DISCORD = "ozaii1337";

    private static DatabaseFactory databaseFactory;
    private static SettingsFactory settingsFactory;
    private static CoinManager coinManager;
    private static LevelManager levelManager;
    private static ProfileManager profileManager;
    private static LevelApi levelApi;
    private static CoinApi coinApi;

    private static final ozaii instance = new ozaii();

    public FactoryApi() {
        initialize();
    }

    private static synchronized void initialize() {
        if (databaseFactory == null) {
            databaseFactory = instance.getDatabaseFactory();
        }
        if (settingsFactory == null) {
            settingsFactory = instance.getSettingsFactory();
        }
        if (coinManager == null) {
            coinManager = instance.getCoinManager();
        }
        if (levelManager == null) {
            levelManager = instance.getLevelManager();
        }
        if (profileManager == null) {
            profileManager = instance.getProfileManager();
        }
        if (levelApi == null) {
            levelApi = new LevelApi(levelManager);
        }
        if (coinApi == null) {
            coinApi = new CoinApi(coinManager);
        }
    }

    public static ozaii getInstance() {
        return instance;
    }

    public static DatabaseFactory getDatabaseFactory() {
        initialize();
        return databaseFactory;
    }

    public static SettingsFactory getSettingsFactory() {
        initialize();
        return settingsFactory;
    }

    public static CoinManager getCoinManager() {
        initialize();
        return coinManager;
    }

    public static LevelManager getLevelManager() {
        initialize();
        return levelManager;
    }

    public static ProfileManager getProfileManager() {
        initialize();
        return profileManager;
    }

    public static LevelApi getLevelManagerApi() {
        initialize();
        return levelApi;
    }

    public static CoinApi getCoinManagerApi() {
        initialize();
        return coinApi;
    }

    public static String getServerName() {
        return SERVER_NAME;
    }

    public static String getServerVersion() {
        return SERVER_VERSION;
    }

    public static String getOwner() {
        return OWNER;
    }

    public static String getOwnerDiscord() {
        return OWNER_DISCORD;
    }
}

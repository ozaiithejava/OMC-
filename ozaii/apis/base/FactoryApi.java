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

    private static final  String SERVER_NAME = "OMC";
    private static final String SERVER_VERSION = "1.0.0";
    private static final String OWNER = "ozaiithejava";
    private static final String OWNER_DISCORD = "ozaii1337";


    private static DatabaseFactory DATABASE_FACTORY ;
    private static  SettingsFactory SETTINGS_FACTORY ;
    private static CoinManager COIN_MANAGER ;
    private static LevelManager LEVEL_MANAGER ;
    private static ProfileManager PROFILE_MANAGER ;
    private static LevelApi LEVEL_API ;
    private static CoinApi COIN_API ;

    private static final ozaii instance = new ozaii();

    public FactoryApi(){
        DATABASE_FACTORY = instance.getDatabaseFactory();
        SETTINGS_FACTORY = instance.getSettingsFactory();
        COIN_MANAGER = instance.getCoinManager();
        LEVEL_MANAGER = instance.getLevelManager();
        PROFILE_MANAGER = instance.getProfileManager();
        LEVEL_API = new LevelApi(LEVEL_MANAGER);
        COIN_API = new CoinApi(COIN_MANAGER);
    }

    public static ozaii getInstance(){return instance;}
    public static DatabaseFactory getDatabaseFactory(){return DATABASE_FACTORY;}
    public static SettingsFactory getSettingsFactory(){return SETTINGS_FACTORY;}
    public static CoinManager getCoinManager(){return COIN_MANAGER;}
    public static LevelManager getLevelManager(){return LEVEL_MANAGER;}
    public static ProfileManager getProfileManager(){return PROFILE_MANAGER;}
    public static String getServerName(){return SERVER_NAME;}
    public static String getServerVersion(){return SERVER_VERSION;}
    public static LevelApi getLevelManagerApi(){return LEVEL_API;}
    public static CoinApi getCoinManagerApi(){return COIN_API;}
    public static String getOwner(){return OWNER;}
    public static String getOwnerDiscord(){return OWNER_DISCORD;}
}
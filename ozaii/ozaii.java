package ozaii;



import org.apache.logging.log4j.LogManager;
import ozaii.apis.base.FactoryApi;
import ozaii.factory.DatabaseFactory;
import ozaii.factory.LanguageFactory;
import ozaii.factory.LicenseFactory;
import ozaii.factory.SettingsFactory;
import ozaii.managers.CoinManager;
import ozaii.managers.LevelManager;
import ozaii.managers.ProfileManager;
import ozaii.tray.NotificationTray;
import ozaii.tray.NotifyManager;
import ozaii.utils.LisanceUtil;

import java.util.logging.Logger;

public class ozaii {

    private static ozaii INSTANCE;
    // Java Util Logger
    private static final Logger logger = Logger.getLogger(new FactoryApi().getServerName());
    // Log4j2 Logger (Daha detaylı loglama için)
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(ozaii.class);

    // Settings ve Database Factory'ler
    static SettingsFactory settingsFactory = new SettingsFactory("settings.json");
    static DatabaseFactory databaseFactory = new DatabaseFactory("settings.json");
    static LicenseFactory lisanceFactory = new LicenseFactory();
    static LanguageFactory languageFactory = new LanguageFactory("lang.yml");

    static CoinManager coinManager = new CoinManager("settings.json");
    static LevelManager levelManager = new LevelManager("settings.json");
    static ProfileManager profileManager = new ProfileManager("settings.json");



    // Sunucuyu başlatma metodu
    public static void start() {
        LisanceUtil.checkLicense();
        new NotifyManager().setupNotfiy();
    }

    // Sunucuyu durdurma metodu
    public static void stop() {
        databaseFactory.closeConnection();
        System.exit(0);
    }


    public static SettingsFactory getSettingsFactory() {return settingsFactory;}
    public static DatabaseFactory getDatabaseFactory() {return databaseFactory;}
    public static LicenseFactory getLicenseFactory() {return lisanceFactory;}
    public static CoinManager getCoinManager() {return coinManager;}
    public static LevelManager getLevelManager() {return levelManager;}
    public static ProfileManager getProfileManager() {return profileManager;}


} 
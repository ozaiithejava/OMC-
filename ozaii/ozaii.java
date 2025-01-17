package ozaii;

import com.zaxxer.hikari.pool.HikariPool;
import org.apache.logging.log4j.LogManager;
import ozaii.apis.base.FactoryApi;
import ozaii.client.ClientController;
import ozaii.factory.*;
import ozaii.managers.CoinManager;
import ozaii.managers.LevelManager;
import ozaii.managers.ProfileManager;
import ozaii.tray.NotifyManager;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class ozaii {

    private static ozaii INSTANCE;
    // Java Util Logger
    private static final Logger logger = Logger.getLogger(new FactoryApi().getServerName());
    // Log4j2 Logger (Daha detaylı loglama için)
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(ozaii.class);

    // Settings ve Database Factory'ler
    static SettingsFactory settingsFactory = new SettingsFactory("settings.json");
    static DatabaseFactory databaseFactory = new DatabaseFactory("settings.json");;
    static LicenseFactory lisanceFactory = new LicenseFactory();
    static LanguageFactory languageFactory = new LanguageFactory("lang.yml");

    static CoinManager coinManager = new CoinManager("settings.json");
    static LevelManager levelManager = new LevelManager("settings.json");
    static ProfileManager profileManager = new ProfileManager("settings.json");
    static ClientController clientController = new ClientController();

    // Sunucuyu başlatma metodu
    public static CompletableFuture<Void> startAsync() {
        return CompletableFuture.runAsync(() -> {
            try {


                new NotifyManager().setupNotifyAsync();
                 clientController.startDiscordClientAsync();
                 
                //String testUrl = "http://localhost:8080/endpoint";
               // TpsFactory.start(testUrl, 1);
                // new LisanceUtil().checkLicense();

            } catch (HikariPool.PoolInitializationException e) {
                throw e;
            }
        });
    }




    // Sunucuyu durdurma metodu
    public static CompletableFuture<Void> stopAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                databaseFactory.closeConnectionAsync().join();
                System.exit(0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

    // Getters for various managers and factories
    public static SettingsFactory getSettingsFactory() { return settingsFactory; }
    public static DatabaseFactory getDatabaseFactory() { return databaseFactory; }
    public static LicenseFactory getLicenseFactory() { return lisanceFactory; }
    public static CoinManager getCoinManager() { return coinManager; }
    public static LevelManager getLevelManager() { return levelManager; }
    public static ProfileManager getProfileManager() { return profileManager; }
}

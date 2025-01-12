package ozaii.factory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsFactory {

    public static final Logger logger = Logger.getLogger(SettingsFactory.class.getName());
    private File settingsFile;
    private static JsonObject settingsData;

    // SettingsFactory sınıfının yapıcı metodu
    public SettingsFactory(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            logger.log(Level.SEVERE, "Dosya yolu geçersiz: " + filePath);
            return;
        }

        this.settingsFile = new File(filePath);

        // Dosya mevcut değilse varsayılan ayarları oluştur
        if (!settingsFile.exists()) {
            logger.info("settings.json dosyası mevcut değil, varsayılan ayarları oluşturuyor.");
            createDefaultSettings();  // Varsayılan ayarları oluştur
        }

        // Ayarları yükle
        loadSettings();
    }

    // Ayarları yükleme
    public void loadSettings() {
        if (settingsFile == null) {
            logger.log(Level.SEVERE, "settingsFile null! Ayarlar yüklenemedi.");
            return;
        }

        try (FileReader reader = new FileReader(settingsFile)) {
            Gson gson = new Gson();
            settingsData = gson.fromJson(reader, JsonObject.class);

            // settingsData null ise yeni bir boş JsonObject başlat
            if (settingsData == null) {
                logger.warning("settingsData null! Varsayılan ayarlarla başlatılıyor.");
                settingsData = new JsonObject();
            }
        } catch (IOException | JsonParseException e) {
            logger.log(Level.SEVERE, "JSON dosyasından veri okuma hatası: ", e);
            settingsData = new JsonObject();  // Hata durumunda boş bir JsonObject başlatıyoruz
        }
    }

    // Varsayılan ayarları oluşturma
    private void createDefaultSettings() {
        if (settingsFile == null) {
            logger.log(Level.SEVERE, "settingsFile null! Dosya oluşturulamadı.");
            return;
        }

        try {
            // Klasörleri oluştur
            File parentDir = settingsFile.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                logger.log(Level.WARNING, "Klasörler oluşturulamadı: " + parentDir.getAbsolutePath());
            }

            // Dosya yoksa oluştur
            if (!settingsFile.exists()) {
                if (settingsFile.createNewFile()) {
                    logger.info("settings.json dosyası oluşturuldu.");
                } else {
                    logger.log(Level.SEVERE, "settings.json dosyası oluşturulamadı.");
                    return;
                }
            }

            // Ayarları başlat
            settingsData = new JsonObject();

            // Varsayılan ayarları ekle
            settingsData.addProperty("owner", "ozaii"); // Varsayılan bir değer ekle
            settingsData.addProperty("tnt.addSizeY", "1"); // Varsayılan bir değer ekle
            settingsData.addProperty("tnt.addSizeX", "1"); // Varsayılan bir değer ekle

            settingsData.addProperty("db.host", "localhost"); // db host için varsayılan değer
            settingsData.addProperty("db.port", 3306); // db port için varsayılan değer
            settingsData.addProperty("db.username", "root"); // db username için varsayılan değer
            settingsData.addProperty("db.password", ""); // db password için varsayılan değer
            settingsData.addProperty("db.database", "ozaii_db"); // db database için varsayılan değer

            settingsData.addProperty("luck.gold.multiper", "1"); // Gold ore çarpanı
            settingsData.addProperty("luck.diamond.multiper", "1"); // Diamond ore çarpanı
            settingsData.addProperty("luck.emerald.multiper", "1"); // Emerald ore çarpanı
            settingsData.addProperty("luck.lapis.multiper", "1"); // Lapis ore çarpanı
            settingsData.addProperty("luck.quartz.multiper", "1"); // Quartz ore çarpanı

            settingsData.addProperty("lisanceKey", "ozaii1234");

            //tick
            settingsData.addProperty("boom", "true");
            settingsData.addProperty("bookban", "true");
            settingsData.addProperty("redstoneblockban", "true");
            settingsData.addProperty("blockphysics", "true");

            //chat
            settingsData.addProperty("chatEnabled", "true");  // Sohbeti etkinleştirme
            settingsData.addProperty("spamCooldown", "1");  // Sohbeti etkinleştirme

            settingsData.addProperty("stafonlockchat", "true");  // Sohbeti etkinleştirme

            //managers
            settingsData.addProperty("coinmanagertable", "ozaiicoins");  // Sohbeti etkinleştirme
            settingsData.addProperty("levelmanagertable", "ozaiilevel");  // Sohbeti etkinleştirme
            settingsData.addProperty("playerProfileTable", "ozaiiprofiles");  // Sohbeti etkinleştirme

            settingsData.addProperty("joinmessagestatus", "true");  // Katılım mesajlarını etkinleştir
            settingsData.addProperty("joinmessage", "Welcome @p to @server! Your level is @level and your ID is @id.");  // Katılım mesajı şablonu
            settingsData.addProperty("joinmessagercolor", "&e");  // Genel mesaj rengi (örneğin, sarı)
            settingsData.addProperty("playernamecolor", "&6");  // Oyuncu adı için renk (örneğin, altın)
            settingsData.addProperty("levelcolor", "&b");  // Seviye için renk (örneğin, mavi)
            settingsData.addProperty("idcolor", "&7");  // Oyuncu ID'si için renk (örneğin, gri)
            settingsData.addProperty("servercolor", "&a");  // Sunucu adı için renk (örneğin, yeşil)
            settingsData.addProperty("servername", "your_server_name");  // Sunucu adı için renk (örneğin, yeşil)

            // Ayarları dosyaya kaydet
            saveSettings();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "settings.json dosyası oluşturulurken hata oluştu.", e);
        }
    }

    // Ayarları dosyaya kaydetme
    public void saveSettings() {
        if (settingsData == null) {
            logger.log(Level.SEVERE, "settingsData null! Veriler kaydedilemedi.");
            return;
        }

        try (FileWriter writer = new FileWriter(settingsFile)) {
            Gson gson = new Gson();
            gson.toJson(settingsData, writer); // JSON verisini dosyaya yaz
            logger.info("settings.json dosyası başarıyla kaydedildi.");
            logger.info("Sunucu Kapanıyor settings.json dan ayarları güncelleyip veri tabanına bağlanın!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "settings.json dosyasına yazma hatası: ", e);
        }
    }

    // JSON dosyasından veri okuma (key-value şeklinde)
    public String get(String key) {
        if (settingsData == null) {
            logger.log(Level.SEVERE, "settingsData null! Veriler alınamıyor.");
            return null;
        }
        return settingsData.has(key) ? settingsData.get(key).getAsString() : null;
    }

    // JSON dosyasına veri yazma
    public void set(String key, String value) {
        if (settingsData == null) {
            logger.log(Level.SEVERE, "settingsData null! Veriler yazılamıyor.");
            return;
        }
        settingsData.addProperty(key, value); // Anahtar-değer çifti ekleme
        saveSettings(); // Dosyaya kaydet
    }

    // JSON dosyasındaki bir anahtarın var olup olmadığını kontrol etme
    public boolean has(String key) {
        return settingsData != null && settingsData.has(key);
    }

    // Luck çarpanını almak için genel bir metod
    public static int getLuckMultiplier(String oreType) {
        if (settingsData == null || oreType == null || oreType.isEmpty()) {
            logger.log(Level.SEVERE, "settingsData null veya oreType geçersiz.");
            return 1; // Varsayılan çarpan
        }

        String key = "luck." + oreType + ".multiper";
        return settingsData.has(key) ? settingsData.get(key).getAsInt() : 1; // Varsayılan 1 döner
    }

    // Chat ayarlarını kontrol etmek için metot
    public static boolean isChatEnabled() {
        String chatEnabled = settingsData != null ? settingsData.get("chatEnabled").getAsString() : "false";
        return chatEnabled.equals("true");
    }

    // Spam koruma aktifse kontrol etme
    public static boolean isSpamProtectionEnabled() {
        String spamProtection = settingsData != null ? settingsData.get("spamProtectionEnabled").getAsString() : "false";
        return spamProtection.equals("true");
    }

    // Aynı mesaj gönderme zamanını kontrol etme
    public static int getSameMessageCooldown() {
        return settingsData != null && settingsData.has("sameMessageCooldown") ? settingsData.get("sameMessageCooldown").getAsInt() : 3;
    }
}

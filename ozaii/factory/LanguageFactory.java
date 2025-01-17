package ozaii.factory;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LanguageFactory {

    private final String filePath;
    private Map<String, String> languageMap;

    public LanguageFactory(String filePath) {
        this.filePath = filePath;
        this.languageMap = new HashMap<>();

        File file = new File(filePath);
        if (!file.exists()) {
            createDefaultYMLAsync().join(); // Wait for the default file creation
        }

        loadYMLAsync().join(); // Wait for loading the YML
    }

    /**
     * Varsayılan bir YML dosyası oluşturur (Asynchronously).
     */
    private CompletableFuture<Void> createDefaultYMLAsync() {
        return CompletableFuture.runAsync(() -> {
            languageMap.put("welcome", "Welcome to the server!");
            languageMap.put("goodbye", "Goodbye, see you next time!");

            saveYMLAsync().join(); // Save asynchronously after creation
        });
    }

    /**
     * YML dosyasını yükler ve içeriğini languageMap'e aktarır (Asynchronously).
     */
    private CompletableFuture<Void> loadYMLAsync() {
        return CompletableFuture.runAsync(() -> {
            try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
                Yaml yaml = new Yaml(new Constructor(Map.class));
                Map<String, String> loadedMap = (Map<String, String>) yaml.load(inputStream);
                if (loadedMap != null) {
                    languageMap.putAll(loadedMap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * languageMap içeriğini YML dosyasına kaydeder (Asynchronously).
     */
    private CompletableFuture<Void> saveYMLAsync() {
        return CompletableFuture.runAsync(() -> {
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Yaml yaml = new Yaml(options);
            try (Writer writer = Files.newBufferedWriter(Paths.get(filePath))) {
                yaml.dump(languageMap, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Anahtar ile ilgili mesajı döndürür. Eğer anahtar bulunamazsa "Key not found" döner (Asynchronously).
     *
     * @param key Mesaj anahtarı
     * @return Mesaj
     */
    public CompletableFuture<String> getMessageAsync(String key) {
        return CompletableFuture.supplyAsync(() -> languageMap.getOrDefault(key, "Key not found"));
    }

    /**
     * Yeni bir anahtar ve mesaj ekler veya mevcut bir anahtarın mesajını günceller (Asynchronously).
     *
     * @param key Anahtar
     * @param message Mesaj
     */
    public CompletableFuture<Void> addOrUpdateMessageAsync(String key, String message) {
        return CompletableFuture.runAsync(() -> {
            languageMap.put(key, message);
            saveYMLAsync().join(); // Save asynchronously after updating
        });
    }

//    public static void main(String[] args) {
//        // Örnek kullanım
//        LanguageFactory languageFactory = new LanguageFactory("language.yml");
//
//        // Fetching messages asynchronously
//        languageFactory.getMessageAsync("welcome").thenAccept(message -> {
//            System.out.println(message);
//        });
//
//        languageFactory.getMessageAsync("goodbye").thenAccept(message -> {
//            System.out.println(message);
//        });
//
//        // Adding or updating a message asynchronously
//        languageFactory.addOrUpdateMessageAsync("new_message", "This is a new message.").thenRun(() -> {
//            languageFactory.getMessageAsync("new_message").thenAccept(message -> {
//                System.out.println(message);
//            });
//        });
//    }
}

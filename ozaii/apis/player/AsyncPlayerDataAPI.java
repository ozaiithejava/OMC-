package ozaii.apis.player;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AsyncPlayerDataAPI - Asenkron oyuncu verilerini işlemek için API sınıfı.
 */
public class AsyncPlayerDataAPI implements AsyncPlayerData {

    // Oyuncuların özel verilerini saklamak için bir yapı.
    private final Map<String, Map<String, Object>> playerCustomData = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<PlayerInventory> getInventoryAsync(Player player) {
        return CompletableFuture.supplyAsync(player::getInventory);
    }

    @Override
    public CompletableFuture<Location> getLocationAsync(Player player) {
        return CompletableFuture.supplyAsync(player::getLocation);
    }

    @Override
    public CompletableFuture<Object> getCustomDataAsync(Player player, String key) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> data = playerCustomData.get(player.getUniqueId().toString());
            if (data != null) {
                return data.get(key);
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> setCustomDataAsync(Player player, String key, Object value) {
        return CompletableFuture.runAsync(() -> {
            String playerId = player.getUniqueId().toString();
            playerCustomData.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>()).put(key, value);
        });
    }

    /**
     * Oyuncunun özel verilerini temizler. Örneğin, oyuncu sunucudan ayrıldığında çağrılabilir.
     *
     * @param player Oyuncu
     * @return İşlemin tamamlanmasını temsil eden CompletableFuture
     */
    public CompletableFuture<Void> clearCustomDataAsync(Player player) {
        return CompletableFuture.runAsync(() -> playerCustomData.remove(player.getUniqueId().toString()));
    }

    /**
     * Sunucuda çevrimiçi olan tüm oyuncuların konumlarını asenkron olarak getirir.
     *
     * @return Oyuncular ve konumlarının bir haritasını içeren CompletableFuture
     */
    public CompletableFuture<Map<Player, Location>> getAllPlayerLocationsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            Map<Player, Location> locations = new ConcurrentHashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                locations.put(player, player.getLocation());
            }
            return locations;
        });
    }
}

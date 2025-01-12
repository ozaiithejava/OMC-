package ozaii.apis.player;


import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * AsyncPlayerData API - Asenkron oyuncu verilerini işlemek için kullanılan API.
 */
public interface AsyncPlayerData {

    /**
     * Oyuncunun envanterini asenkron olarak getirir.
     *
     * @param player Envanteri alınacak oyuncu
     * @return Oyuncunun envanter verilerini içeren CompletableFuture
     */
    CompletableFuture<PlayerInventory> getInventoryAsync(Player player);

    /**
     * Oyuncunun konumunu asenkron olarak getirir.
     *
     * @param player Konumu alınacak oyuncu
     * @return Oyuncunun mevcut konumunu içeren CompletableFuture
     */
    CompletableFuture<Location> getLocationAsync(Player player);

    /**
     * Oyuncunun belirli bir veri anahtarıyla ilişkili özel verisini asenkron olarak getirir.
     *
     * @param player Verisi alınacak oyuncu
     * @param key Özel veri anahtarı
     * @return Veriyi içeren CompletableFuture
     */
    CompletableFuture<Object> getCustomDataAsync(Player player, String key);

    /**
     * Oyuncunun belirli bir veri anahtarıyla ilişkili özel verisini asenkron olarak ayarlar.
     *
     * @param player Verisi ayarlanacak oyuncu
     * @param key Özel veri anahtarı
     * @param value Ayarlanacak değer
     * @return İşlemin tamamlanmasını temsil eden CompletableFuture
     */
    CompletableFuture<Void> setCustomDataAsync(Player player, String key, Object value);
}

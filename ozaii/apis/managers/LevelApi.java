package ozaii.apis.managers;


import ozaii.managers.LevelManager;

import java.util.concurrent.CompletableFuture;

public class LevelApi {
    private final LevelManager levelManager;

    public LevelApi(LevelManager levelManager) {
        // LevelManager'ı başlat ve ayar dosyasını ilet
        this.levelManager = levelManager;
    }

    /**
     * Oyuncunun seviyesini getirir.
     *
     * @param playerName Oyuncu adı
     * @return Oyuncunun seviyesini içeren bir CompletableFuture
     */
    public CompletableFuture<Integer> getLevelAsync(String playerName) {
        return levelManager.getLevelAsync(playerName);
    }

    /**
     * Oyuncunun seviyesini arttırır.
     *
     * @param playerName Oyuncu adı
     * @param amount Artış miktarı
     * @return İşlem tamamlandığında bir CompletableFuture döner
     */
    public CompletableFuture<Void> increaseLevelAsync(String playerName, int amount) {
        return levelManager.increaseLevelAsync(playerName, amount);
    }

    /**
     * Oyuncunun seviyesini düşürür.
     *
     * @param playerName Oyuncu adı
     * @param amount Azaltma miktarı
     * @return İşlem tamamlandığında bir CompletableFuture döner
     */
    public CompletableFuture<Void> decreaseLevelAsync(String playerName, int amount) {
        return levelManager.decreaseLevelAsync(playerName, amount);
    }

    /**
     * Oyuncunun seviyesini sıfırlar (1'e çeker).
     *
     * @param playerName Oyuncu adı
     * @return İşlem tamamlandığında bir CompletableFuture döner
     */
    public CompletableFuture<Void> resetLevelAsync(String playerName) {
        return levelManager.resetLevelAsync(playerName);
    }

    /**
     * Oyuncunun seviyesini belirli bir değere ayarlar.
     *
     * @param playerName Oyuncu adı
     * @param level Ayarlanacak seviye
     * @return İşlem tamamlandığında bir CompletableFuture döner
     */
    public CompletableFuture<Void> setLevelAsync(String playerName, int level) {
        return levelManager.setLevelAsync(playerName, level);
    }

    /**
     * Oyuncu için yeni bir seviye hesabı oluşturur.
     *
     * @param playerName Oyuncu adı
     * @return İşlem tamamlandığında bir CompletableFuture döner
     */
    public CompletableFuture<Void> createLevelAccountAsync(String playerName) {
        return levelManager.createLevelAccountAsync(playerName);
    }
}

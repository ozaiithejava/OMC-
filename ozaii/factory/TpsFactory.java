package ozaii.factory;

import com.google.gson.Gson;
import ozaii.utils.bukkit.TpsUtils;
import ozaii.utils.web.HttpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TpsFactory {

    private static final Gson gson = new Gson();

    /**
     * Başlatıcı metot. Verilen adresi, belirli bir zaman aralığında TPS verilerini gönderir.
     *
     * @param url      Post isteği yapılacak URL.
     * @param interval Gönderim aralığı (dakika cinsinden).
     */
    public static void start(String url, int interval) {
        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    sendTpsData(url).join(); // TPS verilerini gönder
                    Thread.sleep(interval * 60 * 1000L); // Belirtilen dakika kadar bekle
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Error: " + e.getMessage());
                    break;
                } catch (Exception e) {
                    try {
                        Thread.sleep(interval * 60 * 1000L);
                        start(url, interval);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    System.err.println("An unexpected error occurred: " + e.getMessage());
                    break; // Handle any unexpected exceptions
                }
            }
        });
    }

    /**
     * TPS verilerini toplar ve POST isteği olarak gönderir.
     *
     * @param url Post isteği yapılacak URL.
     * @return CompletableFuture<Void> asenkron işlem.
     */
    private static CompletableFuture<Void> sendTpsData(String url) {
        // Verileri asenkron olarak alıyoruz
        CompletableFuture<double[]> tpsFuture = TpsUtils.getTpsAsync();
        CompletableFuture<Double> cpuFuture = TpsUtils.getCpuUsageAsync();
        CompletableFuture<long[]> ramFuture = TpsUtils.getRamUsageAsync();
        CompletableFuture<Integer> playersFuture = TpsUtils.getPlayerCountAsync();
        CompletableFuture<List<String>> playerNamesFuture = TpsUtils.getOnlinePlayerNamesAsync();
        CompletableFuture<Integer> chunksFuture = TpsUtils.getLoadedChunksAsync();
        CompletableFuture<Integer> threadsFuture = TpsUtils.getThreadCountAsync();
        CompletableFuture<Long> uptimeFuture = TpsUtils.getUptimeAsync();

        // Veriler alındıktan sonra JSON'a dönüştürülüp gönderiliyor
        return CompletableFuture.allOf(tpsFuture, cpuFuture, ramFuture, playersFuture, playerNamesFuture, chunksFuture, threadsFuture, uptimeFuture)
                .thenComposeAsync(ignored -> {
                    // Log before constructing the data
                    Map<String, Object> data = new HashMap<>();
                    data.put("tps", tpsFuture.join());
                    data.put("cpuUsage", cpuFuture.join());
                    data.put("ramUsage", createRamUsageMap(ramFuture.join()));
                    data.put("onlinePlayers", playersFuture.join());
                    data.put("playerNames", playerNamesFuture.join());
                    data.put("loadedChunks", chunksFuture.join());
                    data.put("threadCount", threadsFuture.join());
                    data.put("uptime", uptimeFuture.join());
                    String json = gson.toJson(data); // Verileri JSON formatına çevir
                    return HttpUtil.sendPostRequest(url, json)
                            .thenRun(() -> {
                                System.out.println("Data sent successfully!");
                            })
                            .exceptionally(ex -> {
                                System.err.println("Post request failed: " + ex.getMessage());
                                return null;
                            });
                });
    }

    /**
     * RAM verisini uygun formatta bir haritaya dönüştürür.
     *
     * @param ram RAM verisi (used, max) türünde long[].
     * @return RAM verisini içeren Map.
     */
    private static Map<String, Long> createRamUsageMap(long[] ram) {
        Map<String, Long> ramUsage = new HashMap<>();
        ramUsage.put("used", ram[0]);
        ramUsage.put("max", ram[1]);
        return ramUsage;
    }
}

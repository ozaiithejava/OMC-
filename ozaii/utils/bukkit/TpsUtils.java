package ozaii.utils.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TpsUtils {

    public static CompletableFuture<double[]> getTpsAsync() {
        return CompletableFuture.supplyAsync(() -> Bukkit.spigot().getTPS());
    }

    public static CompletableFuture<String[]> formatTpsAsync(double[] tps) {
        return CompletableFuture.supplyAsync(() -> {
            String[] tpsAvg = new String[tps.length];
            for (int i = 0; i < tps.length; i++) {
                tpsAvg[i] = formatSingleTps(tps[i]);
            }
            return tpsAvg;
        });
    }

    private static String formatSingleTps(double tps) {
        if (tps >= 19.5) {
            return Color.GREEN + String.format("%.2f", tps);
        } else if (tps >= 18.0) {
            return Color.YELLOW + String.format("%.2f", tps);
        } else {
            return Color.RED + String.format("%.2f", tps);
        }
    }

    public static CompletableFuture<Double> getCpuUsageAsync() {
        return CompletableFuture.supplyAsync(() -> {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
                return sunOsBean.getSystemCpuLoad() * 100; // CPU Load as percentage
            }
            return 0.0;
        });
    }

    public static CompletableFuture<long[]> getRamUsageAsync() {
        return CompletableFuture.supplyAsync(() -> {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

            long usedMemory = heapMemoryUsage.getUsed();
            long maxMemory = heapMemoryUsage.getMax();

            return new long[]{usedMemory, maxMemory};
        });
    }

    public static CompletableFuture<Integer> getPlayerCountAsync() {
        return CompletableFuture.supplyAsync(() -> Bukkit.getOnlinePlayers().size());
    }

    public static CompletableFuture<List<String>> getOnlinePlayerNamesAsync() {
        return CompletableFuture.supplyAsync(() -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName) // Her oyuncunun ismini alıyoruz
                .collect(Collectors.toList())); // Listeye dönüştürüyoruz
    }

    public static CompletableFuture<Double> calculateAverageLoadAsync(int playerCount) {
        return CompletableFuture.supplyAsync(() -> {
            double baseLoad = 1.0;
            if (playerCount > 0) {
                return baseLoad + (playerCount * 0.05); // 5% extra load per player
            }
            return baseLoad;
        });
    }

    public static CompletableFuture<Long> getUptimeAsync() {
        return CompletableFuture.supplyAsync(() -> {
            long uptimeMillis = System.currentTimeMillis() - Bukkit.getServer().getStartTime();
            return uptimeMillis / 1000; // Convert to seconds
        });
    }

    public static CompletableFuture<String> formatUptimeAsync(long uptimeInSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            long days = uptimeInSeconds / (24 * 3600);
            long hours = (uptimeInSeconds % (24 * 3600)) / 3600;
            long minutes = (uptimeInSeconds % 3600) / 60;
            long seconds = uptimeInSeconds % 60;

            return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
        });
    }

    public static CompletableFuture<Integer> getThreadCountAsync() {
        return CompletableFuture.supplyAsync(Thread::activeCount);
    }

    public static CompletableFuture<Integer> getLoadedChunksAsync() {
        return CompletableFuture.supplyAsync(() -> Bukkit.getWorlds().stream()
                .mapToInt(world -> world.getLoadedChunks().length)
                .sum());
    }
}

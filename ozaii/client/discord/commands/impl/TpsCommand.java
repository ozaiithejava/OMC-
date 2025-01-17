package ozaii.client.discord.commands.impl;

import org.bukkit.Bukkit;

import ozaii.client.discord.commands.Command;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class TpsCommand implements Command {

    @Override
    public void execute(MessageCreateEvent event, String[] args) {
        // Retrieve the current TPS from the server (1-minute, 5-minute, and 15-minute averages)
        double[] tps = Bukkit.spigot().getTPS();

        // Format the TPS values
        String[] tpsAvg = new String[tps.length];
        for (int i = 0; i < tps.length; i++) {
            tpsAvg[i] = formatTPS(tps[i]);
        }

        // CPU Usage
        double cpuUsage = getCPUUsage();

        // RAM Usage
        long[] ramUsage = getRAMUsage();

        // Player Count and Average Load
        int playerCount = Bukkit.getOnlinePlayers().size();
        double avgLoad = calculateAverageLoad(playerCount);

        // Uptime
        long uptime = getUptime();

        // Thread Count
        int threadCount = getThreadCount();

        // Loaded Chunks
        int chunkCount = Bukkit.getWorlds().get(0).getLoadedChunks().length;

        // Creating the embed message
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Server Status")
                .setColor(Color.ORANGE)
                .addField("TPS (1m, 5m, 15m)", String.join(", ", tpsAvg))
                .addField("CPU Usage", String.format("%.2f%%", cpuUsage))
                .addField("RAM Usage", String.format("%.2f MB / %.2f MB (%.2f%%)", ramUsage[0] / 1024 / 1024.0, ramUsage[1] / 1024 / 1024.0, (ramUsage[0] * 100.0) / ramUsage[1]))
                .addField("Online Players", String.valueOf(playerCount))
                .addField("Average Load (Per Player)", String.format("%.2f", avgLoad))
                .addField("Uptime", formatUptime(uptime))
                .addField("Threads", String.valueOf(threadCount))
                .addField("Loaded Chunks", String.valueOf(chunkCount));

        // Send the embed message to the channel
        event.getChannel().sendMessage(embed);
    }

    @Override
    public String getName() {
        return "tps"; // Command name is "tps"
    }

    // Format the TPS value with color coding
    private String formatTPS(double tps) {
        if (tps >= 19.5) {
            return Color.GREEN + String.format("%.2f", tps); // Green if TPS is good
        } else if (tps >= 18.0) {
            return Color.YELLOW + String.format("%.2f", tps); // Yellow if TPS is slightly lower
        } else {
            return Color.RED + String.format("%.2f", tps); // Red if TPS is too low
        }
    }

    private double getCPUUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            return sunOsBean.getSystemCpuLoad() * 100; // CPU Load as a percentage
        }
        return 0.0; // If CPU usage couldn't be determined
    }

    private long[] getRAMUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

        // Return both the used and the maximum heap memory (in bytes)
        long usedMemory = heapMemoryUsage.getUsed();
        long maxMemory = heapMemoryUsage.getMax();

        return new long[]{usedMemory, maxMemory};
    }

    private double calculateAverageLoad(int playerCount) {
        // Simple estimate: Load increases with player count. This could be refined further.
        double baseLoad = 1.0; // Default "no load"
        if (playerCount > 0) {
            return baseLoad + (playerCount * 0.05); // 5% additional load per player
        }
        return baseLoad; // No players, no load
    }

    private long getUptime() {
        // Getting the uptime in milliseconds and converting it to a more readable format
        long uptimeMillis = System.currentTimeMillis() - Bukkit.getServer().getStartTime();
        return uptimeMillis / 1000; // Convert to seconds
    }

    private int getThreadCount() {
        // Get the current number of threads running in the JVM
        return Thread.activeCount();
    }

    private String formatUptime(long uptimeInSeconds) {
        // Format the uptime into days, hours, minutes, seconds
        long days = uptimeInSeconds / (24 * 3600);
        long hours = (uptimeInSeconds % (24 * 3600)) / 3600;
        long minutes = (uptimeInSeconds % 3600) / 60;
        long seconds = uptimeInSeconds % 60;

        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
    }
}

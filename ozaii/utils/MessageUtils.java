package ozaii.utils;


import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;

public class MessageUtils {

    // Renkli mesaj gönder
    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    // Hata mesajı gönder
    public static void sendErrorMessage(Player player, String message) {
        player.sendMessage(ChatColor.RED + "[ERROR] " + ChatColor.WHITE + message);
    }

    // Başarı mesajı gönder
    public static void sendSuccessMessage(Player player, String message) {
        player.sendMessage(ChatColor.GREEN + "[SUCCESS] " + ChatColor.WHITE + message);
    }

    // Sistem mesajı gönder
    public static void sendSystemMessage(Player player, String message) {
        player.sendMessage(ChatColor.GRAY + "[SYSTEM] " + ChatColor.WHITE + message);
    }
}

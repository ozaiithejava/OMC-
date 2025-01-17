package ozaii.commands.fun;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class ThorCommand extends VanillaCommand {

    public ThorCommand() {
        super("thor");
        this.setPermission("thor");  // Komutun kullanılabilmesi için izin
        this.setDescription("Strikes lightning on players within a 20-block radius.");
        this.setUsage("/thor");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok.");
            return false;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Komutu kullanan oyuncunun bulunduğu konumu al
            Location playerLocation = player.getLocation();

            // Yarı çapı 20 blok olan alandaki tüm oyuncuları bul
            List<String> struckPlayers = new ArrayList<>();  // List to hold players struck by lightning

            for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
                // Sadece komutu kullanan oyuncudan 20 blok uzaklıkta olan oyunculara yıldırım çarptır
                if (nearbyPlayer.getWorld().equals(playerLocation.getWorld()) && nearbyPlayer.getLocation().distance(playerLocation) <= 20 && !nearbyPlayer.equals(player)) {
                    // Her bir oyuncuya yıldırım çarp
                    nearbyPlayer.getWorld().strikeLightning(nearbyPlayer.getLocation());

                    // Yıldırım çarpan oyuncuya mesaj gönder
                    nearbyPlayer.sendMessage("§cYıldırım çarptı! " + player.getName() + " tarafından!");

                    // Add the player to the struck list
                    struckPlayers.add(nearbyPlayer.getName());
                }
            }

            // Display a table-like structure for the struck players
            if (!struckPlayers.isEmpty()) {
                StringBuilder struckMessage = new StringBuilder("§6----------------------------\n" +  // Yellow border
                        "§eYıldırım Çarpan Oyuncular:\n" +  // Header in yellow
                        "§6----------------------------\n");  // Yellow border

                // Loop through the list of struck players and add them to the message
                for (String struckPlayer : struckPlayers) {
                    struckMessage.append("§a" + struckPlayer + "\n");  // Green for each struck player
                }

                struckMessage.append("§6----------------------------");  // Yellow border

                // Send the message to the player who executed the command
                player.sendMessage(struckMessage.toString());
            } else {
                // If no players were struck
                player.sendMessage(ChatColor.YELLOW + "Etrafınızda yıldırım çarpacak oyuncu yok.");
            }

            player.sendMessage("§aYıldırım, etrafınızdaki oyunculara çarptı!");

            return true;  // Komut başarılı bir şekilde işlendi
        } else {
            // Komut yalnızca oyuncular için geçerlidir
            sender.sendMessage("§cBu komut yalnızca oyuncular için geçerlidir.");
            return false;
        }
    }
}

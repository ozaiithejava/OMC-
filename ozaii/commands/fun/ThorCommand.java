package ozaii.commands.fun;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

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
            return false;}

            if (sender instanceof Player) {
            Player player = (Player) sender;

            // Komutu kullanan oyuncunun bulunduğu konumu al
            Location playerLocation = player.getLocation();

            // Yarı çapı 20 blok olan alandaki tüm oyuncuları bul
            for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
                // Sadece komutu kullanan oyuncudan 20 blok uzaklıkta olan oyunculara yıldırım çarptır
                if (nearbyPlayer.getWorld().equals(playerLocation.getWorld()) && nearbyPlayer.getLocation().distance(playerLocation) <= 20 && !nearbyPlayer.equals(player)) {
                    // Her bir oyuncuya yıldırım çarp
                    nearbyPlayer.getWorld().strikeLightning(nearbyPlayer.getLocation());

                    // Yıldırım çarpan oyuncuya mesaj gönder
                    nearbyPlayer.sendMessage("§cYıldırım çarptı! " + player.getName() + " tarafından!");
                }
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

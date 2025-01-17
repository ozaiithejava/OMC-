package ozaii.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

public class SnowToggleCommand extends VanillaCommand {

    private boolean snowEnabled = false; // Kar yağışı durumu

    public SnowToggleCommand() {
        super("kar");
        this.setPermission("snow.toggle.command");
        this.setDescription("Bulunduğunuz alanın biyomunu değiştirir ve kar yağdırır.");
        this.setUsage("/kar on | off");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Bu komut yalnızca oyuncular tarafından kullanılabilir.");
            return false;
        }
        if (!testPermission(sender)) return true;

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Lütfen 'on' veya 'off' argümanını kullanın. Örnek: /kar on");
            return false;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("on")) {
            if (snowEnabled) {
                sender.sendMessage(ChatColor.YELLOW + "Zaten kar yağışı aktif.");
                return true;
            }

            snowEnabled = true;
            sender.sendMessage(ChatColor.GREEN + "Kar yağışı etkinleştiriliyor, lütfen bekleyin...");

            // Yeni bir iş parçacığı (Thread) başlatıyoruz
            new Thread(() -> {
                Location playerLocation = player.getLocation();

                // Çevredeki biyomları değiştir (10 blok yarıçapında)
                for (int x = -10; x <= 10; x++) {
                    for (int z = -10; z <= 10; z++) {
                        Location biomeLocation = playerLocation.clone().add(x, 0, z);
                        player.getWorld().setBiome(biomeLocation.getBlockX(), biomeLocation.getBlockZ(), Biome.EXTREME_HILLS_PLUS_MOUNTAINS);
                    }
                }

                // Ana iş parçacığına dönmeden önce biraz bekleyin
                try {
                    // 2 saniye bekleyin
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Ana iş parçacığında hava durumunu değiştir
                new Thread(() -> {
                    player.getWorld().setStorm(true);
                    player.getWorld().setWeatherDuration(20 * 60 * 5); // 5 dakika boyunca kar yağışı
                    player.sendMessage(ChatColor.GREEN + "Kar yağışı başarıyla etkinleştirildi!");
                }).start();

            }).start();
        } else if (args[0].equalsIgnoreCase("off")) {
            if (!snowEnabled) {
                sender.sendMessage(ChatColor.YELLOW + "Zaten kar yağışı kapalı.");
                return true;
            }

            snowEnabled = false;
            sender.sendMessage(ChatColor.RED + "Kar yağışı durduruluyor...");

            // Hava durumunu kapatmak için başka bir iş parçacığı başlatıyoruz
            new Thread(() -> {
                player.getWorld().setStorm(false);
                player.sendMessage(ChatColor.RED + "Kar yağışı durduruldu.");
            }).start();
        } else {
            sender.sendMessage(ChatColor.RED + "Geçersiz argüman! 'on' veya 'off' kullanmalısınız.");
            return false;
        }

        return true;
    }
}

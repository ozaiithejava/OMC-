package ozaii.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

public class HealCommand extends VanillaCommand {

    public HealCommand() {
        super("heal");
        this.setPermission("heal");  // Komutun kullanılabilmesi için izin
        this.setDescription("Heals the player to full health.");
        this.setUsage("/heal");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Komutun kullanılabilmesi için izin kontrolü
        if (!testPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok.");
            return false;
        }
        if (!testPermission(sender)) return true;

        // Komutun yalnızca oyuncular tarafından kullanılabilmesi için kontrol
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Oyuncunun sağlığını tamamen iyileştir
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20); // Oyuncunun açlık seviyesini tamamen iyileştir

            // Oyuncuya başarılı bir şekilde iyileştirildiğini bildiren mesaj
            player.sendMessage(ChatColor.GREEN + "§aSağlığınız tamamen iyileştirildi!");  // Yeşil renk ile başarılı mesajı

            // Eğer komut başka bir oyuncuya yapılacaksa, iyileştirilen oyuncunun ismini belirtme
            if (args.length > 0) {
                String targetName = args[0];
                if (targetName.equals(player.getName())) {
                    player.sendMessage(ChatColor.YELLOW + "Kendi sağlığınızı iyileştirdiniz.");
                } else {
                    // İyileştirilen oyuncuya bilgi mesajı
                    player.sendMessage(ChatColor.YELLOW + targetName + " sağlığı tamamen iyileştirildi.");
                }
            }

            return true;  // Komut başarılı bir şekilde işlendi
        } else {
            // Komut yalnızca oyuncular için geçerlidir, konsol için geçerli değil
            sender.sendMessage(ChatColor.RED + "§cBu komut yalnızca oyuncular için geçerlidir.");
            return false;
        }
    }
}

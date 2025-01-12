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
        if (!testPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok.");
            return false;
        }        if (sender instanceof Player) {
            Player player = (Player) sender;




            // Oyuncunun sağlığını tamamen iyileştir
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.sendMessage("§aSağlığınız tamamen iyileştirildi!");


            return true;  // Komut başarılı bir şekilde işlendi
        } else {
            // Komut yalnızca oyuncular için geçerli, konsol için geçerli değil
            sender.sendMessage("§cBu komut yalnızca oyuncular için geçerlidir.");
            return false;
        }
    }
}

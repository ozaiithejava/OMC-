package ozaii.commands.admin;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntitysCommand extends VanillaCommand {

    public EntitysCommand() {
        super("entitys");
        this.setDescription("Returns the number of entities within a specified radius.");
        this.setUsage("/entitys <radius>");
        this.setPermission("entitys.command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Bu komut yalnızca oyuncular tarafından kullanılabilir.");
            return false;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Lütfen bir yarıçap girin! Örnek: /entitys 10");
            return false;
        }

        Player player = (Player) sender;

        // Yarıçapı alalım
        int radius;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Geçersiz yarıçap değeri!");
            return false;
        }

        // Yarıçapın geçerli olup olmadığını kontrol edelim
        if (radius <= 0) {
            sender.sendMessage(ChatColor.RED + "Yarıçap pozitif bir sayı olmalıdır.");
            return false;
        }

        // Oyuncunun bulunduğu konumu alalım
        Location playerLocation = player.getLocation();

        // Bu alandaki tüm entity'leri sayalım
        int entityCount = 0;
        for (Entity entity : player.getWorld().getEntities()) {
            // Sadece belirtilen yarıçap içinde ve oyuncuya ait olmayan entity'leri sayalım
            if (entity.getLocation().distance(playerLocation) <= radius && entity != player) {
                entityCount++;
            }
        }

        // Sonucu oyuncuya bildir
        player.sendMessage(ChatColor.GREEN + "Etrafınızdaki " + radius + " blokluk alanda toplam " + entityCount + " entity bulunuyor.");

        return true;
    }
}

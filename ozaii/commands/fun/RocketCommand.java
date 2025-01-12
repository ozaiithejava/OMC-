package ozaii.commands.fun;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

public class RocketCommand extends VanillaCommand {

    public RocketCommand() {
        super("rocket");
        this.setPermission("rocket");  // Komutun kullanılabilmesi için izin
        this.setDescription("Shoots a fireball in the direction the player is looking.");
        this.setUsage("/rocket");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok.");
            return false;}        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Oyuncunun baktığı yönü al
            // Bu, oyuncunun bakış yönünde bir ateş topu fırlatmamızı sağlar
            Fireball fireball = player.launchProjectile(Fireball.class);


            // Yönlendirme işlemi otomatik olarak yapılır, çünkü fireball sınıfı, oyuncunun baktığı yönü kullanarak ateş topunu fırlatır.

            // Fırlatılan ateş topuna mesaj verebiliriz (isteğe bağlı)
            player.sendMessage("§aRoket fırlatıldı!");

            return true;  // Komut başarılı bir şekilde işlendi
        } else {
            // Komut yalnızca oyuncular için geçerlidir
            sender.sendMessage("§cBu komut yalnızca oyuncular için geçerlidir.");
            return false;
        }
    }
}

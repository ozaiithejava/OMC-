package ozaii.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

public class DuyuruCommand extends VanillaCommand {


    public DuyuruCommand( ) {
        super("duyuru");
        this.description = "Send a colored announcement to all players as a Title or ActionBar";
        this.usageMessage = "/duyuru <başlık> <içerik>";
        this.setPermission("duyuru.command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;

        if (sender instanceof Player) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Eksik argüman! Lütfen başlık ve içerik girin.");
                return false;
            }

            // Başlık ve içerik alınır
            String title = args[0];
            StringBuilder content = new StringBuilder();

            // İçerik birden fazla kelime olabilir, bu yüzden tüm args[i]'leri birleştiriyoruz
            for (int i = 1; i < args.length; i++) {
                content.append(args[i]).append(" ");
            }

            // Duyuru mesajını renklendirerek gönderme
            String coloredTitle = ChatColor.GOLD + title; // Başlık için altın rengi
            String coloredContent = ChatColor.AQUA + content.toString().trim(); // İçerik için mavi renk

            // Tüm oyunculara Duyuru göndermek
            for (Player player : sender.getServer().getOnlinePlayers()) {
                // Duyuruyu Title ve ActionBar olarak göndereceğiz
                sendTitle(player, coloredTitle, coloredContent);
                sendActionBar(player, coloredTitle, coloredContent);
                player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1.0f, 1.0f); // Ses efekti
            }

            // Komutu kullanan kişiye bilgi ver
            sender.sendMessage(ChatColor.GREEN + "Duyuru başarıyla gönderildi!");

        } else {
            // Eğer komut bir oyuncu tarafından kullanılmıyorsa, mesaj göster
            sender.sendMessage(ChatColor.RED + "Bu komut sadece oyuncular tarafından kullanılabilir.");
        }

        return true;
    }

    // Title mesajı gönderen metot
    private void sendTitle(Player player, String title, String message) {
        // Title mesajı göndermek
        player.sendTitle(ChatColor.BOLD + "" + ChatColor.ITALIC + title, message);
    }

    // ActionBar mesajı gönderen metot
    private void sendActionBar(Player player, String title, String message) {
        // ActionBar mesajını göndermek
        String actionBarMessage = ChatColor.RED + ">>> " + ChatColor.LIGHT_PURPLE + title + " <<<" + "\n" + ChatColor.GREEN + message;
        player.spigot().sendMessage(net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBarMessage));
    }
}

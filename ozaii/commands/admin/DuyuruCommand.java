package ozaii.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

public class DuyuruCommand extends VanillaCommand {

    public DuyuruCommand() {
        super("duyuru");
        this.description = "Send a colored announcement to all players as a Title and ActionBar.";
        this.usageMessage = "/duyuru <başlık> <içerik>";
        this.setPermission("duyuru.command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Check if the sender has the required permission
        if (!testPermission(sender)) return true;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Eksik argüman! Kullanım: " + getUsage());
            return false;
        }

        // Combine the arguments into a title and content
        String title = ChatColor.translateAlternateColorCodes('&', args[0]); // Başlık
        StringBuilder contentBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            contentBuilder.append(args[i]).append(" ");
        }
        String content = ChatColor.translateAlternateColorCodes('&', contentBuilder.toString().trim()); // İçerik

        // Send the announcement to all online players
        for (Player player : sender.getServer().getOnlinePlayers()) {
            sendTitle(player, title, content);
            sendActionBar(player, title, content);
            playSound(player);
        }

        // Confirm to the sender that the announcement was sent
        sender.sendMessage(ChatColor.GREEN + "Duyuru başarıyla gönderildi!");

        return true;
    }

    // Send a title message to a player
    private void sendTitle(Player player, String title, String subtitle) {
        String styledTitle = ChatColor.BOLD + "" + ChatColor.GOLD + title;
        String styledSubtitle = ChatColor.AQUA + subtitle;
        player.sendTitle(styledTitle, styledSubtitle);
    }

    // Send an ActionBar message to a player
    private void sendActionBar(Player player, String title, String content) {
        String actionBarMessage = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + title + ChatColor.DARK_PURPLE + "] "
                + ChatColor.GREEN + content;
        player.spigot().sendMessage(net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBarMessage));
    }

    // Play a sound for a player to enhance the announcement effect
    private void playSound(Player player) {
        player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 1.0f, 1.0f);
    }
}

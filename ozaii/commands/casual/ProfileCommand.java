package ozaii.commands.casual;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.apis.base.FactoryApi;
import ozaii.managers.ProfileManager;


public class ProfileCommand extends VanillaCommand {

    // FactoryApi örneği doğru bir şekilde başlatılıyor
    private static final FactoryApi api = new FactoryApi();

    public ProfileCommand() {
        super("profile");
        this.description = "See your or another player's profile (coins and level)";
        this.usageMessage = "/profile [player-name]";
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Komutu yalnızca oyuncular kullanabilir
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        String playerName = args.length > 0 ? args[0] : player.getName(); // Oyuncu adı alınır

        try {
            // Oyuncunun coin bilgileri alınır
            Double coins = api.getCoinManager().getCoins(playerName).join();
            if (coins == null) {
                player.sendMessage(ChatColor.RED + "Could not retrieve coin information for " + playerName + ".");
                return true;
            }

            // Oyuncunun seviye bilgileri alınır
            int level = api.getLevelManager().getLevelAsync(playerName).join();
            if (level == -1) {
                player.sendMessage(ChatColor.RED + "Could not retrieve level information for " + playerName + ".");
                return true;
            }

            // Oyuncunun profil bilgileri alınır
            ProfileManager.Profile profile = api.getProfileManager().getProfileAsync(playerName).join();
            if (profile == null) {
                player.sendMessage(ChatColor.RED + "Profile information for " + playerName + " could not be found.");
                return true;
            }

            // Profil bilgileri hazırlanır
            String lastLoginDate = profile.getLastLoginDate() != null ? profile.getLastLoginDate().toString() : "None";
            String lastLoginIp = profile.getLastLoginIp() != null ? profile.getLastLoginIp() : "None";

            // Tablo şeklinde ve renkli mesaj oluşturma
            player.sendMessage(ChatColor.GOLD + "\n===== " + ChatColor.AQUA + "Profile of " + playerName + ChatColor.GOLD + " =====\n");
            player.sendMessage(ChatColor.YELLOW + "Coins: " + ChatColor.GREEN + coins + ChatColor.YELLOW + " coins");
            player.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.GREEN + level);
            player.sendMessage(ChatColor.YELLOW + "Last Login Date: " + ChatColor.GREEN + lastLoginDate);
            player.sendMessage(ChatColor.YELLOW + "Last Login IP: " + ChatColor.GREEN + lastLoginIp);
            player.sendMessage(ChatColor.GOLD + "====================================\n");

        } catch (Exception e) {
            // Hata durumunda oyuncuya mesaj göster ve hata logunu yazdır
            player.sendMessage(ChatColor.RED + "An error occurred while retrieving profile information.");
            e.printStackTrace();
        }

        return true;
    }
}

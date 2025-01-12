package ozaii.commands.casual;


import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.apis.base.FactoryApi;
import ozaii.managers.ProfileManager;

public class ProfileCommand extends VanillaCommand {
    static FactoryApi api = new FactoryApi(); // api sınıfı üzerinden player bilgisi alınacak

    public ProfileCommand() {
        super("profile");
        this.description = "See your or another player's profile (coins and level)";
        this.usageMessage = "/profile [player-name]";
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Komutu sadece oyuncular kullanabilmeli
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName;

            // Eğer komutla oyuncu adı verilmişse, o oyuncunun profil bilgileri gösterilsin
            if (args.length > 0) {
                playerName = args[0];
            } else {
                // Eğer oyuncu adı verilmemişse, komutu yazan oyuncunun kendi profil bilgileri gösterilsin
                playerName = player.getName();
            }

            // Oyuncunun profil bilgilerini almak (coin ve level)
            Double coins = api.getCoinManager().getCoins(playerName).join();
            if (coins == null) {
                player.sendMessage("Coin bilgisi alınamadı.");
                return true;
            }

            int level = api.getLevelManager().getLevelAsync(playerName).join();
            if (level == -1) {
                player.sendMessage("Seviye bilgisi alınamadı.");
                return true;
            }

            ProfileManager.Profile profile = api.getProfileManager().getProfileAsync(playerName).join();
            if (profile == null) {
                player.sendMessage("Profil bilgisi bulunamadı.");
                return true;
            }
            String lastLoginDate = profile.getLastLoginDate() != null ? profile.getLastLoginDate().toString() : "Yok";
            String lastLoginIp = profile.getLastLoginIp() != null ? profile.getLastLoginIp() : "Yok";

            // Profil bilgilerini oyuncuya gösteriyoruz
            player.sendMessage("Profile of " + playerName + ":");
            player.sendMessage("Coins: " + coins + " coins");
            player.sendMessage("Level: " + level);
            player.sendMessage("LastLoginDate " + lastLoginDate);
            player.sendMessage("LastLoginIP " + lastLoginIp);

        } else {
            // Eğer komut bir oyuncu tarafından kullanılmıyorsa, mesaj göster
            sender.sendMessage("This command can only be used by players.");
        }

        return true;
    }
}

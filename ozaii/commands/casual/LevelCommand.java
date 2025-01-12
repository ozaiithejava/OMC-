package ozaii.commands.casual;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.apis.base.FactoryApi;

public class LevelCommand extends VanillaCommand {
    static FactoryApi api = new FactoryApi(); // api sınıfı üzerinden level bilgisi alınacak

    public LevelCommand() {
        super("level");
        this.description = "See your or another player's current level";
        this.usageMessage = "/level [player-name]";
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Komutu sadece oyuncular kullanabilmeli
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName;

            // Eğer komutla oyuncu adı verilmişse, o oyuncunun level'ı gösterilsin
            if (args.length > 0) {
                playerName = args[0];
            } else {
                // Eğer oyuncu adı verilmemişse, komutu yazan oyuncunun kendi level'ı gösterilsin
                playerName = player.getName();
            }

            // Oyuncunun level bilgisini almak
            int level = (int) api.getLevelManager().getLevelAsync(playerName).join();

            // Oyuncuya level bilgisini gösteriyoruz
            player.sendMessage(playerName + "'s current level: " + level);

        } else {
            // Eğer komut bir oyuncu tarafından kullanılmıyorsa, mesaj göster
            sender.sendMessage("This command can only be used by players.");
        }

        return true;
    }
}

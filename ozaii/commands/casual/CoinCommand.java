package ozaii.commands.casual;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.apis.base.FactoryApi;

public class CoinCommand extends VanillaCommand {
    static FactoryApi api = new FactoryApi(); // api sınıfı üzerinden coin bilgisi alınacak

    public CoinCommand() {
        super("coin");
        this.description = "See your or another player's current coin balance";
        this.usageMessage = "/coin [player-name]";
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Komutu sadece oyuncular kullanabilmeli
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName;

            // Eğer komutla oyuncu adı verilmişse, o oyuncunun coin miktarı gösterilsin
            if (args.length > 0) {
                playerName = args[0];
            } else {
                // Eğer oyuncu adı verilmemişse, komutu yazan oyuncunun kendi coin bilgisi gösterilsin
                playerName = player.getName();
            }

            // Oyuncunun coin miktarını almak
            int coins = api.getCoinManager().getCoins(playerName).join().intValue();

            // Oyuncuya coin miktarını gösteriyoruz
            player.sendMessage(playerName + "'s current coin balance: " + coins + " coins.");

        } else {
            // Eğer komut bir oyuncu tarafından kullanılmıyorsa, mesaj göster
            sender.sendMessage("This command can only be used by players.");
        }

        return true;
    }
}

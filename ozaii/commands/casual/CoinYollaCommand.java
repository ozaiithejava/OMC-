package ozaii.commands.casual;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.apis.base.FactoryApi;

public class CoinYollaCommand extends VanillaCommand {

    static FactoryApi api = new FactoryApi(); // API üzerinden coin işlemleri yapılacak

    public CoinYollaCommand() {
        super("coinyolla");
        this.description = "Send coins to another player or all players.";
        this.usageMessage = "/coinyolla <miktar> <hedef-oyuncu-adı>";
        this.setPermission("coinyolla.command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Komutu yalnızca adminlerin kullanabileceğini kontrol ediyoruz
        if (!testPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Eksik argüman! Kullanım: " + usageMessage);
            return false;
        }

        // İlk argüman: Miktar, ikinci argüman: Hedef oyuncunun adı
        String targetPlayerName = args[1];
        int amount;

        // Coin miktarını alalım
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Geçersiz coin miktarı!");
            return false;
        }

        // Coin miktarının geçerli olup olmadığını kontrol edelim
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Geçersiz miktar! Coin miktarı pozitif bir değer olmalıdır.");
            return false;
        }

        // Adminin coin miktarını kontrol et
        String adminName = sender.getName();


        // Eğer hedef oyuncu "all" yazılmışsa
        if (targetPlayerName.equalsIgnoreCase("all")) {
            // Tüm oyunculara coin gönder
            for (Player player : Bukkit.getOnlinePlayers()) {
                api.getCoinManager().deposit(player.getName(), amount);
                player.sendMessage(ChatColor.GREEN + sender.getName() + " sana " + amount + " coin gönderdi!");
            }

            // Admin'e bilgilendirme mesajı
            sender.sendMessage(ChatColor.GREEN + "Başarıyla tüm oyunculara " + amount + " coin gönderdiniz.");
        } else {
            // Belirli bir oyuncuya coin gönder
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

            // Eğer hedef oyuncu çevrimiçi değilse
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Hedef oyuncu çevrimiçi değil veya geçersiz bir oyuncu adı.");
                return false;
            }

            // Coin gönderme işlemi
            api.getCoinManager().deposit(targetPlayerName, amount);

            // Hedef oyuncuya bilgilendirme mesajı
            targetPlayer.sendMessage(ChatColor.GREEN + sender.getName() + " sana " + amount + " coin gönderdi!");

            // Admin'e bilgilendirme mesajı
            sender.sendMessage(ChatColor.GREEN + "Başarıyla " + targetPlayerName + " oyuncusuna " + amount + " coin gönderdiniz.");
        }

        return true;
    }
}

package ozaii.commands.admin;


import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.List;

public class GCommand extends VanillaCommand {

    public GCommand() {
        super("g");
        this.description = "Oyun modunu değiştiren komut.";
        this.usageMessage = "/g <c|s|sp>";
        this.setPermission("admin.command.g");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok.");
            return false;
        }        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Bu komut yalnızca oyuncular tarafından kullanılabilir.");
            return false;
        }

        Player player = (Player) sender;

        // Eğer komut hiç argümanla gelmediyse
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Eksik argüman! Kullanım: " + usageMessage);
            return false;
        }

        String subCommand = args[0].toLowerCase();

        // Alt komutlara göre işlem yapma
        switch (subCommand) {
            case "c":
                return setCreativeMode(player);
            case "s":
                return setSurvivalMode(player);
            case "sp":
                return setSpectatorMode(player);
            default:
                player.sendMessage(ChatColor.RED + "Geçersiz komut! Kullanım: " + usageMessage);
                return false;
        }
    }

    // /g c komutu - Creative mode'a geçiş
    private boolean setCreativeMode(Player player) {
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(ChatColor.GREEN + "Yaratıcı moda geçtiniz.");
        return true;
    }

    // /g s komutu - Survival mode'a geçiş
    private boolean setSurvivalMode(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(ChatColor.GREEN + "Hayatta kalma moda geçtiniz.");
        return true;
    }

    // /g sp komutu - Spectator mode'a geçiş
    private boolean setSpectatorMode(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(ChatColor.GREEN + "Gözlemci moda geçtiniz.");
        return true;
    }

    // Tab Tamamlama
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // İlk argüman için "c", "s" ve "sp" alt komutlarını tamamlayalım
        if (args.length == 1) {
            if ("c".startsWith(args[0].toLowerCase())) {
                completions.add("c");
            }
            if ("s".startsWith(args[0].toLowerCase())) {
                completions.add("s");
            }
            if ("sp".startsWith(args[0].toLowerCase())) {
                completions.add("sp");
            }
        }

        return completions.isEmpty() ? ImmutableList.of() : completions;
    }
}

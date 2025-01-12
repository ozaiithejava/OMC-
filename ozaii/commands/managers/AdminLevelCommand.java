package ozaii.commands.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.apis.base.FactoryApi;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AdminLevelCommand extends VanillaCommand {

    private FactoryApi api = new FactoryApi();

    public AdminLevelCommand() {
        super("ladmin");
        this.description = "Oyuncuların admin seviyelerini yönetmek için bir komut.";
        this.usageMessage = "/ladmin <set|get|reset> [oyuncu] [seviye]";
        this.setPermission("ozaii.adminlevel");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;


        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Kullanım: " + this.usageMessage);
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "set":
                return handleSet(sender, args);
            case "get":
                return handleGet(sender, args);
            case "reset":
                return handleReset(sender, args);
            default:
                sender.sendMessage(ChatColor.RED + "Geçersiz argüman! Kullanım: " + this.usageMessage);
                return true;
        }
    }

    private boolean handleSet(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Kullanım: /ladmin set <oyuncu> <seviye>");
            return true;
        }

        String targetPlayer = args[1];
        int level;

        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Seviye bir sayı olmalıdır!");
            return true;
        }

        api.getLevelManager().setLevelAsync(targetPlayer, level);
        sender.sendMessage(ChatColor.GREEN + targetPlayer + " oyuncusunun admin seviyesi " + level + " olarak ayarlandı.");
        return true;
    }

    private boolean handleGet(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Kullanım: /ladmin get <oyuncu>");
            return true;
        }

        String targetPlayer = args[1];

        // Seviyeyi asenkron olarak al
        CompletableFuture<Integer> levelFuture = api.getLevelManager().getLevelAsync(targetPlayer);

        // Asenkron işlem tamamlandığında işlemek için thenAccept kullan
        levelFuture.thenAccept(level -> {
            if (level == null) {
                sender.sendMessage(ChatColor.YELLOW + targetPlayer + " için admin seviyesi bulunamadı.");
            } else {
                sender.sendMessage(ChatColor.GREEN + targetPlayer + " oyuncusunun admin seviyesi: " + level);
            }
        }).exceptionally(ex -> {
            // Hata durumunda mesaj gönder
            sender.sendMessage(ChatColor.RED + "Bir hata oluştu: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });

        return true;
    }


    private boolean handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Kullanım: /ladmin reset <oyuncu>");
            return true;
        }

        String targetPlayer = args[1];

        api.getLevelManager().resetLevelAsync(targetPlayer);
        sender.sendMessage(ChatColor.GREEN + targetPlayer + " oyuncusunun admin seviyesi sıfırlandı.");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            // İlk argüman için öneriler
            List<String> suggestions = Arrays.asList("set", "get", "reset");
            List<String> result = new ArrayList<>();
            for (String suggestion : suggestions) {
                if (suggestion.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(suggestion);
                }
            }
            return result;
        } else if (args.length == 2) {
            // İkinci argüman için online oyuncuları listele
            List<String> result = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    result.add(player.getName());
                }
            }
            return result;
        }
        return super.tabComplete(sender, alias, args);
    }

}

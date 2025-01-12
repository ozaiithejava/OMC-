package ozaii.commands.managers;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import ozaii.apis.base.FactoryApi;

import java.util.ArrayList;
import java.util.List;

public class AdminCoinCommand extends VanillaCommand {
    private static final FactoryApi api = new FactoryApi();

    public AdminCoinCommand() {
        super("cadmin");
        this.description = "Adminler için coin komutları";
        this.usageMessage = "/cadmin <add|remove|reset> <player> [amount]";
        this.setPermission("admin.command.ozaii");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Permission kontrolü
        if (!testPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok.");
            return false;
        }

        // Argüman kontrolü
        if (args.length < 2) {
            sender.sendMessage("§cKullanım: /cadmin <add|remove|reset> <player> [amount]");
            return true;
        }

        String action = args[0].toLowerCase(); // add, remove veya reset
        String playerName = args[1];
        Player targetPlayer = Bukkit.getPlayer(playerName);

        // Oyuncu kontrolü
        if (targetPlayer == null) {
            sender.sendMessage("§cOyuncu bulunamadı: " + playerName);
            return true;
        }

        switch (action) {
            case "add":
                if (args.length < 3) {
                    sender.sendMessage("§cKullanım: /cadmin add <player> <amount>");
                    return true;
                }
                handleAddCoins(sender, targetPlayer, args[2]);
                break;

            case "remove":
                if (args.length < 3) {
                    sender.sendMessage("§cKullanım: /cadmin remove <player> <amount>");
                    return true;
                }
                handleRemoveCoins(sender, targetPlayer, args[2]);
                break;

            case "reset":
                handleResetCoins(sender, targetPlayer);
                break;

            default:
                sender.sendMessage("§cBilinmeyen işlem: " + action);
                sender.sendMessage("§cGeçerli işlemler: add, remove, reset");
                break;
        }

        return true;
    }

    private void handleAddCoins(CommandSender sender, Player player, String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                sender.sendMessage("§cPozitif bir miktar girin.");
                return;
            }
            // Deposit the amount
            api.getCoinManager().deposit(player.getName(), amount).join();

            // Get the new coin balance synchronously using join()
            Double newBalance = api.getCoinManager().getCoins(player.getName()).join();

            // Send success message
            sender.sendMessage("§a" + player.getName() + " isimli oyuncunun coini artırıldı: +" + amount);
            sender.sendMessage("§aYeni bakiye: " + newBalance);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cGeçersiz miktar: " + amountStr);
        }
    }

    private void handleRemoveCoins(CommandSender sender, Player player, String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                sender.sendMessage("§cPozitif bir miktar girin.");
                return;
            }
            // Remove the amount
            api.getCoinManager().remove(player.getName(), amount).join();

            // Get the new coin balance synchronously using join()
            Double newBalance = api.getCoinManager().getCoins(player.getName()).join();

            // Send success message
            sender.sendMessage("§a" + player.getName() + " isimli oyuncunun coini azaltıldı: -" + amount);
            sender.sendMessage("§aYeni bakiye: " + newBalance);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cGeçersiz miktar: " + amountStr);
        }
    }


    private void handleResetCoins(CommandSender sender, Player player) {
        // Reset the coins asynchronously
        api.getCoinManager().resetCoins(player.getName()).join();

        // Get the new coin balance asynchronously and wait for the result
        Double newBalance = api.getCoinManager().getCoins(player.getName()).join();

        // Send messages to the sender
        sender.sendMessage("§a" + player.getName() + " isimli oyuncunun coin bakiyesi sıfırlandı.");
        sender.sendMessage("§aYeni bakiye: " + newBalance);
    }
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        List<String> completions = new ArrayList<>();

        // First argument: "add", "remove", or "reset"
        if (args.length == 1) {
            if ("add".startsWith(args[0].toLowerCase())) {
                completions.add("add");
            }
            if ("remove".startsWith(args[0].toLowerCase())) {
                completions.add("remove");
            }
            if ("reset".startsWith(args[0].toLowerCase())) {
                completions.add("reset");
            }
        }

        // Second argument: Player names
        if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }

        // Third argument: Amount (only for add and remove actions)
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                // Suggest amounts (you can modify this as per your requirement, e.g., predefined amounts or ranges)
                completions.add("1");
                completions.add("10");
                completions.add("100");
                completions.add("1000");
            }
        }

        return completions.isEmpty() ? ImmutableList.of() : completions;
    }

}

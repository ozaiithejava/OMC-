package ozaii.commands.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;
import ozaii.apis.base.FactoryApi;
import ozaii.client.discord.factories.DiscordPairManager;
import ozaii.factory.DatabaseFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DiscordRemove extends VanillaCommand {

    private final DiscordPairManager discordPairManager;

    // Constructor
    public DiscordRemove() {
        super("discord-kaldır"); // Updated the command name to match the intended one
        this.description = "Minecraft hesabınızı Discord ile eşleştirmenizi kaldırır.";
        this.usageMessage = "/discord-kaldır";
        this.setPermission("admin.command.ozaii");

        // Initialize the discordPairManager here
        FactoryApi api = new FactoryApi();
        DatabaseFactory databaseFactory = api.getDatabaseFactory();
        this.discordPairManager = new DiscordPairManager(databaseFactory);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Check if the sender is a player and has the correct permission
        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komutu yalnızca oyuncular kullanabilir.");
            return false;
        }

        Player player = (Player) sender;

        // Check if the player has the permission to execute the command
        if (!player.hasPermission("admin.command.ozaii")) {
            player.sendMessage("Bu komutu kullanmak için gerekli izne sahip değilsiniz.");
            return false;
        }

        // Async check for Discord pairing
        CompletableFuture<Optional<String>> discordIdOptFuture = getDiscordIdAsync(player);

        discordIdOptFuture.thenAccept(discordIdOpt -> {
            // If the player isn't paired, send a friendly message
            if (!discordIdOpt.isPresent()) {
                sendMessage(player, "Discord hesabınız Minecraft hesabınızla eşleştirilmemiş.");
                return;
            }

            String discordId = discordIdOpt.get();
            String previousDiscordMessage = "Önceki Discord Hesabınız: " + discordId;

            // Remove the Discord pairing asynchronously
            removeDiscordPairAsync(player);

            // Send a structured message
            sendMessage(player, "Discord hesabınız başarıyla kaldırıldı.\n" + previousDiscordMessage);
        }).exceptionally(e -> {
            sendMessage(player, "Bir hata oluştu. Lütfen tekrar deneyin.");
            return null;
        });

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        // If no arguments are given, return the list of online players for tab completion
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                playerNames.add(onlinePlayer.getName());
            }
            return playerNames;
        }

        return Collections.emptyList();
    }

    /**
     * Helper method to send formatted messages to players.
     */
    private void sendMessage(Player player, String message) {
        // Creating a new TextComponent for better formatting
        TextComponent textComponent = new TextComponent(message);

        // Adding color and style
        textComponent.setColor(net.md_5.bungee.api.ChatColor.GREEN); // Color of the text
        textComponent.setBold(true); // Bold text for emphasis

        // Sending the message to the player
        player.spigot().sendMessage(textComponent);
    }

    // Async method to check if the player is paired with a Discord account
    private CompletableFuture<Optional<String>> getDiscordIdAsync(Player player) {
        // Directly return the existing CompletableFuture returned by discordPairManager
        return discordPairManager.getDiscordIdByMinecraftUuid(player.getUniqueId().toString());
    }


    // Async method to remove the Discord pairing
    private CompletableFuture<Void> removeDiscordPairAsync(Player player) {
        return CompletableFuture.runAsync(() -> {
            discordPairManager.removeDiscordPair(player.getUniqueId().toString());
        });
    }
}

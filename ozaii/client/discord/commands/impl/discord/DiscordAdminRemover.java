package ozaii.client.discord.commands.impl.discord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import ozaii.client.discord.commands.Command;
import ozaii.client.discord.factories.DiscordPairManager;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class DiscordAdminRemover implements Command {
    private final DiscordPairManager discordPairManager;

    public DiscordAdminRemover(DiscordPairManager discordPairManager) {
        this.discordPairManager = discordPairManager;
    }

    @Override
    public void execute(MessageCreateEvent event, String[] args) {
        // Ensure the command is used in a server context
        if (!event.isServerMessage()) {
            sendEmbedMessage(event, "Error", "This command can only be used in a server.", Color.RED);
            return;
        }

        Server server = event.getServer().orElse(null);
        if (server == null) {
            sendEmbedMessage(event, "Error", "Could not retrieve server information.", Color.RED);
            return;
        }

        // Check if the user is the server owner
        User author = event.getMessageAuthor().asUser().orElse(null);
        if (author == null || !server.isOwner(author)) {
            sendEmbedMessage(event, "Permission Denied", "You do not have permission to remove admins.", Color.RED);
            return;
        }

        // Validate arguments
        if (args.length < 1) {
            sendEmbedMessage(event, "Error", "Please provide a Minecraft player's username to remove.", Color.ORANGE);
            return;
        }

        String target = args[0];

        // Find the player by their username
        Player targetPlayer = Bukkit.getPlayerExact(target);
        if (targetPlayer == null) {
            sendEmbedMessage(event, "Error", "Player not found. Make sure the player is online.", Color.RED);
            return;
        }

        // Get the player's UUID and attempt to remove their Discord pairing
        String minecraftUuid = targetPlayer.getUniqueId().toString();
        CompletableFuture<Boolean> removed = discordPairManager.removeDiscordPair(minecraftUuid);

        removed.thenAccept(success -> {
            if (success) {
                sendEmbedMessage(event, "Success", "Successfully removed the Discord pairing for player: " + targetPlayer.getName(), Color.GREEN);
            } else {
                sendEmbedMessage(event, "Error", "Failed to remove Discord pairing for player: " + targetPlayer.getName(), Color.RED);
            }
        }).exceptionally(ex -> {
            sendEmbedMessage(event, "Error", "An unexpected error occurred: " + ex.getMessage(), Color.RED);
            return null; // Required because exceptionally expects a return value
        });

    }

    @Override
    public String getName() {
        return "removeadmin";
    }

    /**
     * Sends an embedded message to the channel.
     *
     * @param event       The message create event
     * @param title       The title of the embed
     * @param description The description of the embed
     * @param color       The color of the embed
     */
    private void sendEmbedMessage(MessageCreateEvent event, String title, String description, Color color) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(color)
                .setTimestampToNow(); // Adds the current timestamp

        event.getChannel().sendMessage(embed);
    }
}

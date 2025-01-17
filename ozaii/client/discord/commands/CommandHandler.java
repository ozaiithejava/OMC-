package ozaii.client.discord.commands;


import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import ozaii.client.discord.commands.Command;

import java.util.Arrays;

public class CommandHandler {
    private final DiscordApi api;
    private final String prefix;

    public CommandHandler(DiscordApi api, String prefix) {
        this.api = api;
        this.prefix = prefix;

        // Register the message listener
        registerMessageListener();
    }

    private void registerMessageListener() {
        api.addMessageCreateListener(event -> {
            String message = event.getMessageContent();

            // Check if the message starts with the bot's prefix
            if (!message.startsWith(prefix)) {
                return;
            }

            // Remove prefix and split into command and arguments
            String[] parts = message.substring(prefix.length()).split("\\s+");
            String commandName = parts[0].toLowerCase();
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            executeCommand(event, commandName, args);
        });
    }

    private void executeCommand(MessageCreateEvent event, String commandName, String[] args) {
        Command command = CommandRegistry.getCommand(commandName);

        if (command != null) {
            command.execute(event, args);
        } else {
            event.getChannel().sendMessage("Bilinmeyen komut: " + commandName);
        }
    }
}

package ozaii.client.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import ozaii.apis.base.FactoryApi;
import ozaii.client.discord.commands.CommandHandler;
import ozaii.client.discord.commands.CommandRegistry;
import ozaii.client.discord.commands.impl.*;
import ozaii.client.discord.commands.impl.discord.DiscordAdminRemover;
import ozaii.client.discord.commands.impl.discord.DiscordPair;
import ozaii.client.discord.commands.impl.discord.DiscordPairConfirm;
import ozaii.client.discord.factories.DiscordPairManager;

import java.util.logging.Logger;

public class DiscordClientFactory {
    private static final Logger logger = Logger.getLogger(DiscordClientFactory.class.getName());
    private static DiscordApi api;
    private static String PREFIX = "o!";

    public static boolean createBot(String token) {
        try {
            api = new DiscordApiBuilder()
                    .setToken(token)
                    .addIntents(Intent.MESSAGE_CONTENT)
                    .login()
                    .join();

            logger.info("Discord Bot successfully logged in.");
            logger.info("Bot Prefix: " + PREFIX);

            // Register commands
            CommandRegistry.registerCommand(new TpsCommand());
            CommandRegistry.registerCommand(new HelloCommand(new DiscordPairManager(new FactoryApi().getDatabaseFactory())));
            CommandRegistry.registerCommand(new DiscordPair());
            CommandRegistry.registerCommand(new HowManyPlayers());
            CommandRegistry.registerCommand(new DiscordPairConfirm());
            CommandRegistry.registerCommand(new DiscordAdminRemover(new DiscordPairManager(new FactoryApi().getDatabaseFactory())));

            // Initialize CommandHandler
            new CommandHandler(api, PREFIX);

            return true;
        } catch (Exception e) {
            logger.severe("Failed to start bot: " + e.getMessage());
            return false;
        }
    }

    public static DiscordApi getApi() {
        return api;
    }
    public static void shutdownBot() {
        if (api != null) {
            logger.info("Shutting down Discord Bot...");
            api.disconnect();
            logger.info("Discord Bot has been disconnected.");
        } else {
            logger.warning("Attempted to shut down a bot that was not running.");
        }
    }}

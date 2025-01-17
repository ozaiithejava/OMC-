package ozaii.client;

import ozaii.apis.base.FactoryApi;
import ozaii.client.discord.DiscordClientFactory;

import java.util.concurrent.CompletableFuture;

public class ClientController {

    private static DiscordClientFactory discord;
    private static FactoryApi factory;
    private static String TOKEN;

    public ClientController() {
        discord = new DiscordClientFactory();
        factory = new FactoryApi();
        TOKEN = factory.getSettingsFactory().get("DISCORD.TOKEN");
    }

    // Asynchronous method to start Discord client
    public static CompletableFuture<Void> startDiscordClientAsync() {
        return CompletableFuture.runAsync(() -> {
            discord.createBot(TOKEN);
        });
    }

    // Asynchronous method to stop Discord client
    public static CompletableFuture<Void> stopDiscordClientAsync() {
        return CompletableFuture.runAsync(() -> {
            discord.shutdownBot();
        });
    }
}

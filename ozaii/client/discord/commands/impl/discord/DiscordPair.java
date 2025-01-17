package ozaii.client.discord.commands.impl.discord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import ozaii.apis.base.FactoryApi;
import ozaii.client.discord.commands.Command;
import ozaii.client.discord.factories.DiscordPairManager;
import ozaii.factory.DatabaseFactory;
import ozaii.utils.bukkit.MessageUtils;
import ozaii.utils.TwoFactorAuthUtils;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class DiscordPair implements Command {
    TwoFactorAuthUtils codeGenerator = new TwoFactorAuthUtils();
    FactoryApi api = new FactoryApi();
    DatabaseFactory databaseFactory = api.getDatabaseFactory();
    DiscordPairManager pairManager = new DiscordPairManager(databaseFactory);

    @Override
    public void execute(MessageCreateEvent event, String[] args) {
        // Validate input arguments
        if (args.length < 1) {
            event.getChannel().sendMessage("Lütfen Minecraft oyuncu adını belirtin!");
            return;
        }

        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);

        if (player == null || !player.isOnline()) {
            event.getChannel().sendMessage("Belirtilen oyuncu çevrimdışı veya mevcut değil.");
            return;
        }

        // Check if the player is already paired asynchronously
        pairManager.getDiscordIdByMinecraftUuid(player.getUniqueId().toString())
                .thenAccept(discordIdOpt -> {
                    if (discordIdOpt.isPresent()) {
                        sendAlreadyPairedEmbed(event, player, discordIdOpt.get());
                    } else {
                        sendPlayerCodeAsync(player).thenAccept(code -> sendCodeSentEmbed(event, player));
                    }
                })
                .exceptionally(ex -> {
                    event.getChannel().sendMessage("Bir hata oluştu: " + ex.getMessage());
                    return null;
                });
    }

    private void sendAlreadyPairedEmbed(MessageCreateEvent event, Player player, String discordId) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Eşleşmiş Hesap")
                .setDescription("Bu Minecraft oyuncusu zaten Discord hesabıyla eşleşmiş!")
                .setColor(Color.RED)
                .addField("Oyuncu Adı", player.getName(), true)
                .addField("Eşleşmiş Discord ID", discordId, true)
                .setFooter("OMC Discord Botu", event.getApi().getYourself().getAvatar());

        event.getChannel().sendMessage(embed);
    }

    private CompletableFuture<String> sendPlayerCodeAsync(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            int codeLength = getCodeLength();
            long codeDuration = getCodeDuration();
            return codeGenerator.generateCode(player, codeLength, codeDuration);
        }).thenApply(code -> {
            sendFormattedMessage(player, code);
            return code;
        });
    }

    private void sendCodeSentEmbed(MessageCreateEvent event, Player player) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Kod Gönderildi")
                .setDescription("Minecraft oyuncusuna başarıyla doğrulama kodu gönderildi!")
                .setColor(Color.GREEN)
                .addField("Oyuncu Adı", player.getName(), true)
                .setThumbnail("https://cdn.discordapp.com/emojis/✅.png")
                .setFooter("OMC Discord Botu", event.getApi().getYourself().getAvatar());

        event.getChannel().sendMessage(embed);
    }

    private int getCodeLength() {
        String codeLengthStr = api.getSettingsFactory().get("DISCORD.CODE.LEN");
        return codeLengthStr != null ? Integer.parseInt(codeLengthStr) : 6;
    }

    private long getCodeDuration() {
        String codeTimeStr = api.getSettingsFactory().get("DISCORD.CODE.TIME");
        return codeTimeStr != null ? Long.parseLong(codeTimeStr) * 60 * 1000 : 5 * 60 * 1000;
    }

    public void sendFormattedMessage(Player player, String code) {
        String formattedMsg = api.getSettingsFactory().get("DISCORD.CODE.MSG");
        formattedMsg = formattedMsg.replaceAll("@code", code);
        MessageUtils.sendMessage(player, formattedMsg);
    }

    @Override
    public String getName() {
        return "discord-eşle";
    }
}

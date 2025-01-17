package ozaii.client.discord.commands.impl.discord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import ozaii.apis.base.FactoryApi;
import ozaii.client.discord.commands.Command;
import ozaii.client.discord.factories.DiscordPairManager;
import ozaii.factory.DatabaseFactory;
import ozaii.utils.TwoFactorAuthUtils;

import java.awt.Color;

public class DiscordPairConfirm implements Command {
    private final TwoFactorAuthUtils codeGenerator = new TwoFactorAuthUtils();
    private final FactoryApi api = new FactoryApi();
    private final DatabaseFactory databaseFactory = api.getDatabaseFactory();
    private final DiscordPairManager pairManager = new DiscordPairManager(databaseFactory);

    @Override
    public void execute(MessageCreateEvent event, String[] args) {
        // Kullanıcı bilgilerini al
        User user = event.getMessageAuthor().asUser().orElse(null);

        if (user == null) {
            sendEmbedMessage(event, "Geçersiz Kullanıcı", "Bu komutu yalnızca gerçek bir kullanıcı çalıştırabilir.", Color.RED);
            return;
        }

        if (args.length < 2) {
            sendEmbedMessage(event, "Eksik Argüman", "Lütfen bir Minecraft oyuncu adı ve doğrulama kodu girin! Örnek kullanım: `!discord-onay <MinecraftOyuncuAdı> <Kod>`", Color.YELLOW);
            return;
        }

        String minecraftUsername = args[0];
        String code = args[1];
        Player player = Bukkit.getPlayer(minecraftUsername);

        if (player == null) {
            sendEmbedMessage(event, "Geçersiz Minecraft Oyuncusu", "Minecraft oyuncu adı geçersiz veya oyuncu bulunamadı!", Color.RED);
            return;
        }

        String minecraftUuid = player.getUniqueId().toString();

        // Kodun geçerliliğini kontrol et
        if (codeGenerator.isCodeValidFromPlayer(player, code)) {
            // Pair player with Discord asynchronously
            pairManager.pairPlayerWithDiscord(minecraftUuid, player.getName(), String.valueOf(user.getId()))
                    .thenAccept(success -> {
                        if (success) {
                            sendEmbedMessage(event, "Eşleştirme Başarılı",
                                    "Minecraft oyuncusu **" + minecraftUsername + "** artık Discord kullanıcı **" + user.getDiscriminatedName() + "** ile eşleştirildi.",
                                    Color.GREEN);
                        } else {
                            sendEmbedMessage(event, "Hata", "Eşleştirme sırasında bir hata oluştu. Lütfen tekrar deneyin.", Color.RED);
                        }
                    })
                    .exceptionally(ex -> {
                        sendEmbedMessage(event, "Hata", "Eşleştirme sırasında bir hata oluştu: " + ex.getMessage(), Color.RED);
                        return null;
                    });
        } else {
            sendEmbedMessage(event, "Geçersiz Kod", "Girilen doğrulama kodu geçersiz veya süresi dolmuş.", Color.RED);
        }
    }

    @Override
    public String getName() {
        return "discord-onay";
    }

    /**
     * Helper method to send an embedded message to the channel.
     */
    private void sendEmbedMessage(MessageCreateEvent event, String title, String description, Color color) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(color) // Setting the color of the embed
                .setFooter("Eşleştirme Sistemi", event.getMessageAuthor().getAvatar())
                .setTimestampToNow(); // Add timestamp of when the message was sent

        event.getChannel().sendMessage(embed);
    }
}

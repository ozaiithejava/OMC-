package ozaii.client.discord.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.entity.user.User;
import ozaii.client.discord.commands.Command;
import ozaii.client.discord.factories.DiscordPairManager;

import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HelloCommand implements Command {
    private final DiscordPairManager discordPairManager;

    public HelloCommand(DiscordPairManager discordPairManager) {
        this.discordPairManager = discordPairManager;
    }
    @Override
    public void execute(MessageCreateEvent event, String[] args) {
        // Ensure event is not null
        if (event == null) {
            event.getChannel().sendMessage("Event is null!");
            return;
        }

        User user = event.getMessageAuthor().asUser().orElse(null);
        if (user == null) {
            event.getChannel().sendMessage("Kullanıcı bilgileri alınamadı!");
            return;
        }

        // Ensure server is present before accessing
        if (!event.getServer().isPresent()) {
            event.getChannel().sendMessage("Sunucu bilgileri alınamadı!");
            return;
        }

        String accountCreationDate = formatDate(user.getCreationTimestamp());
        String serverJoinDate = event.getServer()
                .flatMap(server -> user.getJoinedAtTimestamp(server))
                .map(this::formatDate)
                .orElse("Bilinmiyor");

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Kullanıcı Bilgileri")
                .setDescription("Merhaba, " + user.getDisplayName(event.getServer().get()) + "!")
                .setThumbnail(user.getAvatar())
                .setColor(Color.CYAN)
                .addField("Hesap Oluşturma Tarihi", accountCreationDate, true)
                .addField("Sunucuya Katılma Tarihi", serverJoinDate, true)
                .setFooter("Bilgiler, OMC Discord botu tarafından sağlandı", event.getApi().getYourself().getAvatar());

        event.getChannel().sendMessage(embed);
    }

    /**
     * Tarihi formatlamak için yardımcı metot.
     *
     * @param instant Instant türünde tarih/zaman
     * @return Formatlanmış tarih
     */
    private String formatDate(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    @Override
    public String getName() {
        return "hello";
    }
}

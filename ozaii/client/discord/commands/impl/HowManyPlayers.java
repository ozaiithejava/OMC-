package ozaii.client.discord.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import ozaii.client.discord.commands.Command;

import java.awt.*;
import java.util.stream.Collectors;

public class HowManyPlayers implements Command {
    @Override
    public void execute(MessageCreateEvent event, String[] args) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        // Çevrimiçi oyuncuları listele
        String playerList;
        if (onlinePlayers > 0) {
            playerList = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.joining("\n")); // Oyuncu isimlerini alt alta ekle
        } else {
            playerList = "Hiçbir oyuncu çevrimiçi değil.";
        }

        // Embed oluştur
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Minecraft Sunucu Durumu")
                .setDescription("Şu anda çevrimiçi olan oyuncu sayısı: " + onlinePlayers)
                .addField("Aktif Oyuncular", playerList)
                .setColor(onlinePlayers > 0 ? Color.GREEN : Color.RED) // Oyuncu varsa yeşil, yoksa kırmızı
                .setFooter("Oyuncu durumu bilgisi", event.getApi().getYourself().getAvatar());

        // Mesajı kanala gönder
        event.getChannel().sendMessage(embed);
    }

    @Override
    public String getName() {
        return "oyuncular";
    }
}

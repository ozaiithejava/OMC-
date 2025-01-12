package ozaii.draw;


import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Draw {

    private final int id;
    private final String prize;
    private final int winnerCount;
    private final long duration;
    private long endTime;
    private List<Player> participants;

    public Draw(int id, String prize, int winnerCount, long duration) {
        this.id = id;
        this.prize = prize;
        this.winnerCount = winnerCount;
        this.duration = duration;
        this.participants = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getPrize() {
        return prize;
    }

    public int getWinnerCount() {
        return winnerCount;
    }

    public long getDuration() {
        return duration;
    }

    public long getEndTime() {
        return endTime;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public void start() {
        this.endTime = System.currentTimeMillis() + (duration * 60000); // 'duration' dakika olarak verilmişti, milisaniyeye çeviriyoruz.
    }

    public void announceWinners() {
        if (participants.isEmpty()) {
            // Eğer hiç kimse katılmamışsa
            return;
        }

        // Kazananları rastgele seçiyoruz
        List<Player> winners = new ArrayList<>();
        for (int i = 0; i < winnerCount && winners.size() < participants.size(); i++) {
            Player winner = participants.get((int) (Math.random() * participants.size()));
            if (!winners.contains(winner)) {
                winners.add(winner);
            }
        }

        // Kazananlara mesaj gönder
        for (Player winner : winners) {
            winner.sendMessage("Tebrikler! " + prize + " hediyesini kazandınız!");
        }
    }

    public boolean addParticipant(Player player) {
        if (System.currentTimeMillis() > endTime) {
            player.sendMessage("Çekiliş sona erdi.");
            return false;
        }
        if (!participants.contains(player)) {
            participants.add(player);
            player.sendMessage("Çekilişe başarıyla katıldınız! Hediyeniz: " + prize);
            return true;
        }
        player.sendMessage("Bu çekilişe zaten katıldınız!");
        return false;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }
}

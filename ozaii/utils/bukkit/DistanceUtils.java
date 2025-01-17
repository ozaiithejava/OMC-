package ozaii.utils.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DistanceUtils {

    /**
     * İki oyuncu arasındaki mesafeyi ölçer.
     *
     * @param player1 İlk oyuncu
     * @param player2 İkinci oyuncu
     * @return İki oyuncu arasındaki mesafe
     */
    public static double getDistance(Player player1, Player player2) {
        // Her oyuncunun konumunu al
        Vector loc1 = player1.getLocation().toVector();
        Vector loc2 = player2.getLocation().toVector();

        // Mesafeyi hesapla
        return loc1.distance(loc2);
    }
}

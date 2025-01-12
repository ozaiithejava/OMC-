package ozaii.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.World;

public class WorldUtils {

    // Oyuncuyu belirtilen koordinatlara teleport et
    public static void teleportPlayer(Player player, World world, double x, double y, double z) {
        Location location = new Location(world, x, y, z);
        player.teleport(location);
    }

    // Oyuncuyu aynı dünyada belirtilen bloklara teleport et
    public static void teleportPlayerToBlock(Player player, World world, double x, double y, double z) {
        Location location = new Location(world, x, y, z);
        location.setY(world.getHighestBlockYAt((int) x, (int) z));  // Y'yi uygun yükseklikle güncelle
        player.teleport(location);
    }
}

package ozaii.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.block.Chest;
import org.bukkit.event.block.Action;
import org.bukkit.block.Block;

public class PlayerOpenChestEvent extends PlayerInteractEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Chest chest;
    private boolean cancelled;

    public PlayerOpenChestEvent(Player player, Block blockClicked, Action action) {
        super(player, action, null, null, null);  // PlayerInteractEvent constructor'ı

        // Burada Block'un bir Chest olup olmadığını kontrol ediyoruz
        if (blockClicked.getState() instanceof Chest) {
            this.chest = (Chest) blockClicked.getState(); // Eğer Chest ise, Chest'e çeviriyoruz
        } else {
            this.chest = null; // Chest değilse, null atıyoruz
        }

        this.cancelled = false; // Varsayılan olarak event iptal edilmemiştir
    }

    public Chest getChest() {
        return chest;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

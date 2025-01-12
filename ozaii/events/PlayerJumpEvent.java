package ozaii.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.bukkit.Location;

/**
 * Bu event, bir oyuncu zıpladığında tetiklenir.
 * Bu event, zıplama konumu, hızı ve yönü gibi bilgileri de içerir.
 */
public class PlayerJumpEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Location location;
    private final Vector velocity;
    private final Vector direction;
    private boolean cancelled;

    /**
     * Constructor
     *
     * @param player Zıplama işlemi gerçekleştiren oyuncu
     * @param location Zıplamanın gerçekleştiği konum
     * @param velocity Oyuncunun zıplarkenki hızı
     * @param direction Zıplamanın yönü
     */
    public PlayerJumpEvent(Player player, Location location, Vector velocity, Vector direction) {
        this.player = player;
        this.location = location;
        this.velocity = velocity;
        this.direction = direction;
        this.cancelled = false; // Default is not cancelled

    }

    /**
     * Zıplama yapan oyuncuyu alır.
     *
     * @return Zıplayan oyuncu
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Zıplama konumunu alır.
     *
     * @return Zıplama konumu
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Zıplama hızını alır.
     *
     * @return Zıplama hızı (Vector)
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * Zıplamanın yönünü alır.
     *
     * @return Zıplamanın yönü (Vector)
     */
    public Vector getDirection() {
        return direction;
    }

    /**
     * Event handler'larını almak için gereklidir.
     *
     * @return Event handler listesi
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Statik olarak event handler listesine erişmek için gereklidir.
     *
     * @return Event handler listesi
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return true if the event is cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Set whether the event should be cancelled.
     *
     * @param cancelled true to cancel the event, false to allow it
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

package ozaii.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityAirChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final LivingEntity entity;
    private int air;
    private boolean cancelled;

    public EntityAirChangeEvent(LivingEntity entity, int air) {
        this.entity = entity;
        this.air = air;
        this.cancelled = false;  // Varsayılan olarak event iptal edilmemiştir
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public int getAir() {
        return air;
    }

    public void setAir(int air) {
        this.air = air;
    }

    // Cancellable interface'inden gelen isCancelled metodu
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    // Cancellable interface'inden gelen setCancelled metodu
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

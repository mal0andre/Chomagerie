package tech.maloandre.chomagerie.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ItemPickupCallback {

    public static final Event<ItemPickupCallback.Pickup> EVENT = EventFactory.createArrayBacked(
            ItemPickupCallback.Pickup.class,
            (listeners) -> (player, stack) -> {
                for (Pickup listener : listeners) {
                    listener.onItemPickup(player, stack);
                }
            }
    );

    @FunctionalInterface
    public interface Pickup {
        /**
         * Called when a player picks up an item
         *
         * @param player The player who picked up the item
         * @param stack  The item stack that was picked up
         */
        void onItemPickup(PlayerEntity player, ItemStack stack);
    }
}


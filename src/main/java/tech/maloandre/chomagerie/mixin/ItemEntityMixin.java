package tech.maloandre.chomagerie.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.maloandre.chomagerie.Chomagerie;
import tech.maloandre.chomagerie.event.ItemPickupCallback;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getStack();

    @Unique
    private ItemStack capturedStack = ItemStack.EMPTY;

    @Unique
    private int originalCount = 0;

    /**
     * Capture the stack before attempting pickup
     */
    @Inject(
        method = "onPlayerCollision",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"
        )
    )
    private void beforeInsertStack(PlayerEntity player, CallbackInfo ci) {
        if (!player.getEntityWorld().isClient()) {
            ItemStack currentStack = getStack();
            if (!currentStack.isEmpty()) {
                capturedStack = currentStack.copy();
                originalCount = currentStack.getCount();
                Chomagerie.LOGGER.info("BEFORE INSERT: {} x{}",
                    currentStack.getItem().getName().getString(), currentStack.getCount());
            }
        }
    }

    /**
     * Check after insertStack if items were picked up
     */
    @Inject(
        method = "onPlayerCollision",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z",
            shift = At.Shift.AFTER
        )
    )
    private void afterInsertStack(PlayerEntity player, CallbackInfo ci) {
        if (!player.getEntityWorld().isClient() && !capturedStack.isEmpty()) {
            ItemStack currentStack = getStack();
            int currentCount = currentStack.isEmpty() ? 0 : currentStack.getCount();

            // If some items were picked up (stack reduced or empty)
            if (currentCount < originalCount) {
                int pickedCount = originalCount - currentCount;
                Chomagerie.LOGGER.info("AFTER INSERT: Items picked up! {} x{} (was {})",
                    capturedStack.getItem().getName().getString(), pickedCount, originalCount);

                // Create a stack with the picked amount
                ItemStack pickedStack = capturedStack.copy();
                pickedStack.setCount(pickedCount);

                // Trigger the pickup event
                ItemPickupCallback.EVENT.invoker().onItemPickup(player, pickedStack);
            }

            capturedStack = ItemStack.EMPTY;
            originalCount = 0;
        }
    }
}


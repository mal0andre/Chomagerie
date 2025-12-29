package tech.maloandre.chomagerie.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.maloandre.chomagerie.event.ItemStackDepletedCallback;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow
    @Final
    public PlayerEntity player;

    @Shadow
    public int getSelectedSlot() {
        return 0; // Stub, sera remplacé par Shadow
    }

    @Unique
    private Item chomagerie$lastSelectedItem = null;

    @Unique
    private int chomagerie$lastSelectedCount = 0;


    /**
     * Surveille quand un stack se vide par décrémentation dans le slot sélectionné uniquement
     */
    @Inject(method = "updateItems", at = @At("HEAD"))
    private void onUpdateItems(CallbackInfo ci) {
        if (player == null || player.getEntityWorld().isClient()) {
            return;
        }

        PlayerInventory inventory = (PlayerInventory) (Object) this;
        int currentSelectedSlot = getSelectedSlot();
        ItemStack currentStack = inventory.getStack(currentSelectedSlot);

        // Si on surveillait un item et que le slot est maintenant vide
        if (chomagerie$lastSelectedItem != null && currentStack.isEmpty()) {
            // Vérifier que c'était bien une décrémentation (le count était à 1 avant)
            // Si le count était > 1, c'est probablement un déplacement manuel
            if (chomagerie$lastSelectedCount == 1) {
                // Le stack s'est vidé par consommation, déclencher l'événement
                ItemStackDepletedCallback.EVENT.invoker().onItemStackDepleted(
                        player, currentSelectedSlot, chomagerie$lastSelectedItem, null
                );
            }
            chomagerie$lastSelectedItem = null;
            chomagerie$lastSelectedCount = 0;
        }
        // Si on a un item dans le slot sélectionné
        else if (!currentStack.isEmpty()) {
            // Si c'est le même item, mettre à jour le count
            if (chomagerie$lastSelectedItem == currentStack.getItem()) {
                chomagerie$lastSelectedCount = currentStack.getCount();
            }
            // Si l'item a changé, commencer à surveiller le nouveau
            else {
                chomagerie$lastSelectedItem = currentStack.getItem();
                chomagerie$lastSelectedCount = currentStack.getCount();
            }
        }
        // Si le slot est vide et qu'on ne surveillait rien
        else {
            chomagerie$lastSelectedItem = null;
            chomagerie$lastSelectedCount = 0;
        }
    }
}


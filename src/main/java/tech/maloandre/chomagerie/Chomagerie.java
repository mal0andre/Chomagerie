package tech.maloandre.chomagerie;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.maloandre.chomagerie.config.ServerConfig;
import tech.maloandre.chomagerie.event.ItemPickupCallback;
import tech.maloandre.chomagerie.event.ItemStackDepletedCallback;
import tech.maloandre.chomagerie.network.AutoPickupNotificationPayload;
import tech.maloandre.chomagerie.network.ConfigSyncPayload;
import tech.maloandre.chomagerie.network.RefillNotificationPayload;
import tech.maloandre.chomagerie.util.AutoPickupHandler;
import tech.maloandre.chomagerie.util.ShulkerRefillHandler;

public class Chomagerie implements ModInitializer {

    public static final String MOD_ID = "chomagerie";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Chomagerie - Automatic refill system enabled");

        // Initialize server configuration
        ServerConfig.getInstance();

        // Register network packet types
        PayloadTypeRegistry.playC2S().register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RefillNotificationPayload.ID, RefillNotificationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AutoPickupNotificationPayload.ID, AutoPickupNotificationPayload.CODEC);

        // Register server-side network handler
        ConfigSyncPayload.registerServerHandler();

        // Detect when players connect
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            // Do nothing here. If the player has the mod, they will send their config automatically
            // If after a few seconds they haven't sent a config, we assume they don't have the mod
            LOGGER.debug("Player {} connected, waiting for configuration...", handler.player.getName().getString());
        });

        // Register automatic refill event from shulker boxes
        ItemStackDepletedCallback.EVENT.register((player, slot, item, previousStack) -> {
            if (!player.getEntityWorld().isClient()) {
                // Check if the mod is enabled for this player on the server
                // This method now also checks if the player has the mod installed
                boolean isEnabled = ServerConfig.getInstance().isShulkerRefillEnabled(player.getUuid());

                if (isEnabled) {
                    // Get the filtering parameters for this player
                    ServerConfig config = ServerConfig.getInstance();
                    boolean filterByName = config.isFilterByNameEnabled(player.getUuid());
                    String nameFilter = config.getShulkerNameFilter(player.getUuid());

                    ShulkerRefillHandler.RefillResult result = ShulkerRefillHandler.tryRefillFromShulker(
                            player, slot, item, filterByName, nameFilter
                    );

                    // If refill succeeded, send notification to client
                    if (result.success() && player instanceof ServerPlayerEntity serverPlayer) {
                        ServerPlayNetworking.send(serverPlayer, new RefillNotificationPayload(result.itemName()));
                    }
                } else if (!ServerConfig.getInstance().playerHasMod(player.getUuid())) {
                    // Player doesn't have the mod, do nothing (silent)
                    LOGGER.debug("Refill ignored for {} - Mod not installed", player.getName().getString());
                }
            }
        });

        // Register automatic pickup event to store items in shulker boxes
        ItemPickupCallback.EVENT.register((player, stack) -> {
            if (!player.getEntityWorld().isClient()) {
                // Check if auto pickup is enabled for this player
                ServerConfig config = ServerConfig.getInstance();
                boolean isAutoPickupEnabled = config.isAutoPickupEnabled(player.getUuid());

                if (isAutoPickupEnabled && config.playerHasMod(player.getUuid())) {
                    // Get the filtering parameters for this player
                    boolean filterByName = config.isAutoPickupFilterByNameEnabled(player.getUuid());
                    String nameFilter = config.getAutoPickupShulkerNameFilter(player.getUuid());

                    AutoPickupHandler.PickupResult result = AutoPickupHandler.tryStoreInShulker(
                            player, stack, filterByName, nameFilter
                    );

                    // If storage succeeded, send notification to client if needed
                    if (result.success() && player instanceof ServerPlayerEntity serverPlayer) {
                        if (config.shouldShowAutoPickupMessages(player.getUuid())) {
                            ServerPlayNetworking.send(serverPlayer,
                                    new AutoPickupNotificationPayload(result.itemName(), result.storedCount()));
                        }
                        LOGGER.debug("Auto-stored {} x{} in shulker for {}",
                                result.itemName(), result.storedCount(), player.getName().getString());
                    }
                }
            }
        });
    }
}

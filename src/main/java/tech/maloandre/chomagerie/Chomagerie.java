package tech.maloandre.chomagerie;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.maloandre.chomagerie.config.ServerConfig;
import tech.maloandre.chomagerie.event.ItemStackDepletedCallback;
import tech.maloandre.chomagerie.network.ConfigSyncPayload;
import tech.maloandre.chomagerie.util.ShulkerRefillHandler;

public class Chomagerie implements ModInitializer {

	public static final String MOD_ID = "chomagerie";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initialisation de Chomagerie - Système de refill automatique activé");

		// Initialiser la configuration serveur
		ServerConfig.getInstance();

		// Enregistrer le type de paquet réseau
		PayloadTypeRegistry.playC2S().register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC);

		// Enregistrer le handler réseau côté serveur
		ConfigSyncPayload.registerServerHandler();

		// Enregistrer l'événement de refill automatique depuis les shulker boxes
		ItemStackDepletedCallback.EVENT.register((player, slot, item, previousStack) -> {
			if (!player.getEntityWorld().isClient()) {
				// Vérifier si le mod est activé pour ce joueur côté serveur
				boolean isEnabled = ServerConfig.getInstance().isShulkerRefillEnabled(player.getUuid());

				if (isEnabled) {
					ShulkerRefillHandler.tryRefillFromShulker(player, slot, item);
				}
			}
		});
	}
}

package tech.maloandre.chomagerie.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.maloandre.chomagerie.client.command.ChomagerieCommand;
import tech.maloandre.chomagerie.client.config.ChomagerieConfig;
import tech.maloandre.chomagerie.client.network.ClientNetworkHandler;
import tech.maloandre.chomagerie.config.ModState;

public class ChomagerieClient implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("chomagerie-client");

	@Override
	public void onInitializeClient() {
		// Charger la configuration
		ChomagerieConfig config = ChomagerieConfig.getInstance();

		// Mettre à jour l'état global avec la config ShulkerRefill
		ModState.setClientEnabled(config.shulkerRefill.isEnabled());

		if (config.shulkerRefill.isEnabled()) {
			LOGGER.info("Chomagerie - ShulkerRefill est activé côté client");
		} else {
			LOGGER.info("Chomagerie - ShulkerRefill est désactivé côté client");
		}

		// Initialiser le handler réseau
		ClientNetworkHandler.init();

		// Synchroniser la config au serveur lors de la connexion
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			ClientNetworkHandler.sendConfigToServer();
			LOGGER.info("Configuration envoyée au serveur");
		});

		// Enregistrer les commandes client
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			ChomagerieCommand.register(dispatcher);
		});
	}
}

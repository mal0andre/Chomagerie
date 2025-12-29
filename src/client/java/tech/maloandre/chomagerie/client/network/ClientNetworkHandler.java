package tech.maloandre.chomagerie.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import tech.maloandre.chomagerie.client.config.ChomagerieConfig;
import tech.maloandre.chomagerie.network.ConfigSyncPayload;

/**
 * Gestionnaire réseau côté client
 */
public class ClientNetworkHandler {

    /**
     * Envoie la configuration client au serveur
     */
    public static void sendConfigToServer() {
        if (!ClientPlayNetworking.canSend(ConfigSyncPayload.ID)) {
            return;
        }

        ChomagerieConfig config = ChomagerieConfig.getInstance();
        ConfigSyncPayload payload = new ConfigSyncPayload(
            config.shulkerRefill.isEnabled(),
            config.shulkerRefill.shouldShowRefillMessages()
        );

        ClientPlayNetworking.send(payload);
    }

    /**
     * Initialise les handlers réseau côté client
     */
    public static void init() {
        // On peut ajouter des handlers pour recevoir des paquets du serveur ici si nécessaire
    }
}


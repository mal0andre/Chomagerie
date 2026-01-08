package tech.maloandre.chomagerie.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import tech.maloandre.chomagerie.Chomagerie;

/**
 * Packet to send auto pickup notifications from server to client
 */
public record AutoPickupNotificationPayload(
        String itemName,
        int storedCount
) implements CustomPayload {

    public static final CustomPayload.Id<AutoPickupNotificationPayload> ID =
            new CustomPayload.Id<>(Identifier.of(Chomagerie.MOD_ID, "autopickup_notification"));

    public static final PacketCodec<RegistryByteBuf, AutoPickupNotificationPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeString(value.itemName);
                buf.writeInt(value.storedCount);
            },
            (buf) -> new AutoPickupNotificationPayload(
                    buf.readString(),
                    buf.readInt()
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}


package tech.maloandre.chomagerie.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import tech.maloandre.chomagerie.client.config.ChomagerieConfig;
import tech.maloandre.chomagerie.config.ModState;

public class ChomagerieCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("chomagerie")
                        .then(ClientCommandManager.literal("shulkerrefill")
                                .then(ClientCommandManager.literal("toggle")
                                        .executes(ChomagerieCommand::toggleShulkerRefill))
                                .then(ClientCommandManager.literal("enable")
                                        .executes(context -> setShulkerRefillEnabled(context, true)))
                                .then(ClientCommandManager.literal("disable")
                                        .executes(context -> setShulkerRefillEnabled(context, false)))
                                .then(ClientCommandManager.literal("status")
                                        .executes(ChomagerieCommand::showShulkerRefillStatus))
                        )
                        .then(ClientCommandManager.literal("autopickup")
                                .then(ClientCommandManager.literal("toggle")
                                        .executes(ChomagerieCommand::toggleAutoPickup))
                                .then(ClientCommandManager.literal("enable")
                                        .executes(context -> setAutoPickupEnabled(context, true)))
                                .then(ClientCommandManager.literal("disable")
                                        .executes(context -> setAutoPickupEnabled(context, false)))
                                .then(ClientCommandManager.literal("status")
                                        .executes(ChomagerieCommand::showAutoPickupStatus))
                        )
                        .then(ClientCommandManager.literal("status")
                                .executes(ChomagerieCommand::showStatus))
        );
    }

    private static int toggleShulkerRefill(CommandContext<FabricClientCommandSource> context) {
        ChomagerieConfig config = ChomagerieConfig.getInstance();
        boolean newState = !config.shulkerRefill.isEnabled();
        config.shulkerRefill.setEnabled(newState);
        ModState.setClientEnabled(newState);
        config.save();

        if (newState) {
            context.getSource().sendFeedback(Text.literal("§a[Chomagerie] ShulkerRefill enabled"));
        } else {
            context.getSource().sendFeedback(Text.literal("§c[Chomagerie] ShulkerRefill disabled"));
        }

        return 1;
    }

    private static int setShulkerRefillEnabled(CommandContext<FabricClientCommandSource> context, boolean enabled) {
        ChomagerieConfig config = ChomagerieConfig.getInstance();
        config.shulkerRefill.setEnabled(enabled);
        ModState.setClientEnabled(enabled);
        config.save();

        if (enabled) {
            context.getSource().sendFeedback(Text.literal("§a[Chomagerie] ShulkerRefill enabled"));
        } else {
            context.getSource().sendFeedback(Text.literal("§c[Chomagerie] ShulkerRefill disabled"));
        }

        return 1;
    }

    private static int showShulkerRefillStatus(CommandContext<FabricClientCommandSource> context) {
        ChomagerieConfig config = ChomagerieConfig.getInstance();
        String status = config.shulkerRefill.isEnabled() ? "§aenabled" : "§cdisabled";
        context.getSource().sendFeedback(Text.literal("§e[Chomagerie] ShulkerRefill is currently " + status));
        return 1;
    }

    private static int toggleAutoPickup(CommandContext<FabricClientCommandSource> context) {
        ChomagerieConfig config = ChomagerieConfig.getInstance();
        boolean newState = !config.autoPickup.isEnabled();
        config.autoPickup.setEnabled(newState);
        config.save();

        if (newState) {
            context.getSource().sendFeedback(Text.literal("§a[Chomagerie] AutoPickup enabled"));
        } else {
            context.getSource().sendFeedback(Text.literal("§c[Chomagerie] AutoPickup disabled"));
        }

        return 1;
    }

    private static int setAutoPickupEnabled(CommandContext<FabricClientCommandSource> context, boolean enabled) {
        ChomagerieConfig config = ChomagerieConfig.getInstance();
        config.autoPickup.setEnabled(enabled);
        config.save();

        if (enabled) {
            context.getSource().sendFeedback(Text.literal("§a[Chomagerie] AutoPickup enabled"));
        } else {
            context.getSource().sendFeedback(Text.literal("§c[Chomagerie] AutoPickup disabled"));
        }

        return 1;
    }

    private static int showAutoPickupStatus(CommandContext<FabricClientCommandSource> context) {
        ChomagerieConfig config = ChomagerieConfig.getInstance();
        String status = config.autoPickup.isEnabled() ? "§aenabled" : "§cdisabled";
        context.getSource().sendFeedback(Text.literal("§e[Chomagerie] AutoPickup is currently " + status));
        return 1;
    }

    private static int showStatus(CommandContext<FabricClientCommandSource> context) {
        ChomagerieConfig config = ChomagerieConfig.getInstance();
        String refillStatus = config.shulkerRefill.isEnabled() ? "§aenabled" : "§cdisabled";
        String pickupStatus = config.autoPickup.isEnabled() ? "§aenabled" : "§cdisabled";

        context.getSource().sendFeedback(Text.literal("§e[Chomagerie] Status:"));
        context.getSource().sendFeedback(Text.literal("  §7ShulkerRefill: " + refillStatus));
        context.getSource().sendFeedback(Text.literal("  §7AutoPickup: " + pickupStatus));
        return 1;
    }
}


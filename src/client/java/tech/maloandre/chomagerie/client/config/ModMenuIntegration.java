package tech.maloandre.chomagerie.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import tech.maloandre.chomagerie.config.ModState;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            // Reload configuration to ensure it's up to date
            ChomagerieConfig config = ChomagerieConfig.getInstance();
            config.reload();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("Chomagerie Configuration"));

            // ShulkerRefill category
            ConfigCategory shulkerRefillCategory = builder.getOrCreateCategory(Text.literal("ShulkerRefill"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // Option to enable/disable ShulkerRefill
            shulkerRefillCategory.addEntry(entryBuilder.startBooleanToggle(
                            Text.literal("Enable ShulkerRefill"),
                            config.shulkerRefill.isEnabled()
                    )
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Enables or disables the automatic refill system from shulker boxes"))
                    .setSaveConsumer(newValue -> {
                        config.shulkerRefill.setEnabled(newValue);
                        ModState.setClientEnabled(newValue);
                    })
                    .build());

            // Option to show refill messages
            shulkerRefillCategory.addEntry(entryBuilder.startBooleanToggle(
                            Text.literal("Show Messages"),
                            config.shulkerRefill.shouldShowRefillMessages()
                    )
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Displays a message when an item is refilled from a shulker box"))
                    .setSaveConsumer(config.shulkerRefill::setShowRefillMessages)
                    .build());

            // Option to play sounds during refill
            shulkerRefillCategory.addEntry(entryBuilder.startBooleanToggle(
                            Text.literal("Play Sounds"),
                            config.shulkerRefill.shouldPlaySounds()
                    )
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Plays a sound when an item is refilled from a shulker box"))
                    .setSaveConsumer(config.shulkerRefill::setPlaySounds)
                    .build());

            // Option to filter by shulker box name
            shulkerRefillCategory.addEntry(entryBuilder.startBooleanToggle(
                            Text.literal("Filter by Shulker Name"),
                            config.shulkerRefill.isFilterByNameEnabled()
                    )
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("Only uses shulker boxes with a specific name for refill"))
                    .setSaveConsumer(config.shulkerRefill::setFilterByName)
                    .build());

            // Option to set the name of shulker boxes to use
            shulkerRefillCategory.addEntry(entryBuilder.startStrField(
                            Text.literal("Shulker Box Name"),
                            config.shulkerRefill.getShulkerNameFilter()
                    )
                    .setDefaultValue("restock same")
                    .setTooltip(Text.literal("Only shulker boxes with this exact name will be used for refill"))
                    .setSaveConsumer(config.shulkerRefill::setShulkerNameFilter)
                    .build());


            // ============== AutoPickup Category ==============
            ConfigCategory autoPickupCategory = builder.getOrCreateCategory(Text.literal("AutoPickup"));

            // Option to enable/disable AutoPickup
            autoPickupCategory.addEntry(entryBuilder.startBooleanToggle(
                            Text.literal("Enable AutoPickup"),
                            config.autoPickup.isEnabled()
                    )
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("Automatically stores picked up items in shulker boxes that already contain that item type"))
                    .setSaveConsumer(config.autoPickup::setEnabled)
                    .build());

            // Option to show pickup messages
            autoPickupCategory.addEntry(entryBuilder.startBooleanToggle(
                            Text.literal("Show Messages"),
                            config.autoPickup.shouldShowPickupMessages()
                    )
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Displays a message when an item is stored in a shulker box"))
                    .setSaveConsumer(config.autoPickup::setShowPickupMessages)
                    .build());

            // Option to filter by shulker box name
            autoPickupCategory.addEntry(entryBuilder.startBooleanToggle(
                            Text.literal("Filter by Shulker Name"),
                            config.autoPickup.isFilterByNameEnabled()
                    )
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("Only uses shulker boxes with a specific name for auto pickup"))
                    .setSaveConsumer(config.autoPickup::setFilterByName)
                    .build());

            // Option to set the name of shulker boxes to use
            autoPickupCategory.addEntry(entryBuilder.startStrField(
                            Text.literal("Shulker Box Name"),
                            config.autoPickup.getShulkerNameFilter()
                    )
                    .setDefaultValue("storage")
                    .setTooltip(Text.literal("Only shulker boxes with this exact name will be used for auto pickup"))
                    .setSaveConsumer(config.autoPickup::setShulkerNameFilter)
                    .build());


            builder.setSavingRunnable(() -> {
                config.save();
                ModState.setClientEnabled(config.shulkerRefill.enabled);
            });

            return builder.build();
        };
    }
}


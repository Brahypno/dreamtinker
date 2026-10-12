package org.brahypno.dreamtinker.library.modifiers.modules.armor;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

import java.util.List;

/**
 * Makes the technical slot tracker available to data-driven armor formulas.
 */
public record ArmorLevelTrackerModule(ResourceLocation tracker) implements ModifierModule {
    public static final RecordLoadable<ArmorLevelTrackerModule> LOADER = RecordLoadable.create(
            Loadables.RESOURCE_LOCATION.requiredField("tracker", ArmorLevelTrackerModule::tracker),
            ArmorLevelTrackerModule::new);

    @Override
    public void addModules(ModuleHookMap.Builder builder) {
        builder.addModule(new SlotInChargeModule(TinkerDataKey.of(tracker)));
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return List.of();
    }

    @Override
    public RecordLoadable<ArmorLevelTrackerModule> getLoader() {
        return LOADER;
    }
}

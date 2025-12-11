package org.dreamtinker.dreamtinker.library.modifiers;

import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.tconstruct.library.module.HookProvider;

import javax.annotation.Nullable;

public interface DreamtinkerModifierModule extends GenericLoaderRegistry.IHaveLoader, HookProvider {
    GenericLoaderRegistry<slimeknights.tconstruct.library.modifiers.modules.ModifierModule> LOADER =
            new GenericLoaderRegistry<>("Dreamtinker Modifier Module", false);

    @Nullable
    default Integer getPriority() {
        return null;
    }
}

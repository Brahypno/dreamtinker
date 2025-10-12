package org.dreamtinker.dreamtinker.library.modifiers.modules.display;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RomanNumeralHelper;

import java.util.List;

public record DisplayModule(String key) implements ModifierModule, DisplayNameModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS =
            HookProvider.<DisplayModule>defaultHooks(ModifierHooks.DISPLAY_NAME);
    public static final RecordLoadable<DisplayModule> LOADER = new GenericLoaderRegistry<>("Modifier Display Module", false);

    @Override
    public @NotNull RecordLoadable<? extends GenericLoaderRegistry.IHaveLoader> getLoader() {
        return LOADER;
    }

    @Override
    public @NotNull Component getDisplayName(IToolStackView iToolStackView, ModifierEntry modifierEntry, Component component, @Nullable RegistryAccess registryAccess) {
        final String fulfill = modifierEntry.getModifier().getTranslationKey() + key;
        return Component.translatable(fulfill).append(" ").append(RomanNumeralHelper.getNumeral(modifierEntry.getLevel()))
                        .withStyle((style) -> style.withColor(ResourceColorManager.getTextColor(fulfill)));
    }

    @Override
    public @NotNull List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }
}

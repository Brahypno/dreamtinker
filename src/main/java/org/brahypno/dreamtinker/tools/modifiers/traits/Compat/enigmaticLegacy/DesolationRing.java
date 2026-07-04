package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.enigmaticLegacy;

import net.minecraft.network.chat.Component;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class DesolationRing extends Modifier implements ModifierRemovalHook, ValidateModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.REMOVE, ModifierHooks.VALIDATE);
        hookBuilder.addModule(new ModifierTraitModule(DreamtinkerModifiers.cursed_ring_bound.getId(), 1, true));
        super.registerHooks(hookBuilder);
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().putInt(CursedRingBound.TAG_DEEP_CURSE, Math.max(0, tool.getPersistentData().getInt(CursedRingBound.TAG_DEEP_CURSE) - 1));
        return null;
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        tool.getPersistentData().putInt(CursedRingBound.TAG_DEEP_CURSE, 1);
        return null;
    }
}

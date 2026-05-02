package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy;

import net.minecraft.network.chat.Component;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class DesolationRing extends ArmorModifier {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new ModifierTraitModule(DreamtinkerModifiers.cursed_ring_bound.getId(), 1, true));
        super.registerHooks(hookBuilder);
    }

    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(CursedRingBound.TAG_DEEP_CURSE);
        return null;
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        tool.getPersistentData().putBoolean(CursedRingBound.TAG_DEEP_CURSE, true);
        return null;
    }
}

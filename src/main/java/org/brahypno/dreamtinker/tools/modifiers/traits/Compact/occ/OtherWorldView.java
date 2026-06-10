package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.occ;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

public class OtherWorldView extends NoLevelsModifier implements ModifierRemovalHook, RawDataModifierHook {
    private static final String NBT_GOGGLES = "occultism:otherworld_goggles";

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.REMOVE, ModifierHooks.RAW_DATA);
    }


    @Override
    public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
        tag.putBoolean(NBT_GOGGLES, true);
    }

    @Override
    public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {
        tag.remove(NBT_GOGGLES);
    }

    @Override
    public @Nullable Component onRemoved(IToolStackView tool, Modifier modifier) {
        return null;
    }
}

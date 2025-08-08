package org.dreamtinker.dreamtinker.modifier.base.baseclass;

import net.minecraft.network.chat.Component;
import org.dreamtinker.dreamtinker.modifier.base.baseinterface.ArmorInterface;
import org.dreamtinker.dreamtinker.modifier.base.baseinterface.BasicInterface;
import org.dreamtinker.dreamtinker.modifier.base.baseinterface.InteractionInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;

public abstract class ArmorModifier extends Modifier implements ArmorInterface, BasicInterface, InteractionInterface {
    public ArmorModifier() {
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArmorInterfaceInit(hookBuilder);
        this.BasicInterfaceInit(hookBuilder);
        this.InteractionInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    public boolean isNoLevels(){return false;}
}

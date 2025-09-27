package org.dreamtinker.dreamtinker.library.modifiers.base.baseclass;

import net.minecraft.network.chat.Component;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.BasicInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.InteractionInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;

public abstract class BattleModifier extends Modifier implements ArrowInterface, MeleeInterface, InteractionInterface, BasicInterface {

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        this.MeleeInterfaceInit(hookBuilder);
        this.InteractionInterfaceInit(hookBuilder);
        this.BasicInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    public boolean isNoLevels() {return true;}
}

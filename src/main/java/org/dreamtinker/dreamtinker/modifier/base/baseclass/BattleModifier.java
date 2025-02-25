package org.dreamtinker.dreamtinker.modifier.base.baseclass;

import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.dreamtinker.dreamtinker.modifier.base.baseinterface.ArrowInterface;
import org.dreamtinker.dreamtinker.modifier.base.baseinterface.BasicInterface;
import org.dreamtinker.dreamtinker.modifier.base.baseinterface.InteractionInterface;
import org.dreamtinker.dreamtinker.modifier.base.baseinterface.MeleeInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;

public abstract class BattleModifier extends Modifier implements ArrowInterface, MeleeInterface, InteractionInterface, BasicInterface {
    public BattleModifier() {
        MinecraftForge.EVENT_BUS.addListener(this::LivingHurtEvent);
        MinecraftForge.EVENT_BUS.addListener(this::LivingAttackEvent);
        MinecraftForge.EVENT_BUS.addListener(this::LivingDamageEvent);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        this.MeleeInterfaceInit(hookBuilder);
        this.InteractionInterfaceInit(hookBuilder);
        this.BasicInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    private void LivingDamageEvent(Event event) {
    }

    private void LivingAttackEvent(Event event) {
    }

    private void LivingHurtEvent(Event event) {
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    public boolean isNoLevels(){return false;}
}

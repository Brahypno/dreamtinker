package org.dreamtinker.dreamtinker.modifier.base.baseclass;

import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.*;
import org.dreamtinker.dreamtinker.modifier.base.baseinterface.ArmorInterface;
import org.dreamtinker.dreamtinker.modifier.base.baseinterface.BasicInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;

public abstract class ArmorModifier extends Modifier implements ArmorInterface, BasicInterface {
    public ArmorModifier() {
        MinecraftForge.EVENT_BUS.addListener(this::LivingHurtEvent);
        MinecraftForge.EVENT_BUS.addListener(this::LivingAttackEvent);
        MinecraftForge.EVENT_BUS.addListener(this::LivingDamageEvent);
        MinecraftForge.EVENT_BUS.addListener(this::LivingHealEvent);
        MinecraftForge.EVENT_BUS.addListener(this::LivingUpdateEvent);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArmorInterfaceInit(hookBuilder);
        this.BasicInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    private void LivingDamageEvent(LivingDamageEvent event) {
    }

    private void LivingAttackEvent(LivingAttackEvent event) {
    }

    private void LivingHurtEvent(LivingHurtEvent event) {
    }
    private void LivingHealEvent(LivingHealEvent event) {
    }
    private void LivingUpdateEvent(LivingEvent event) {
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    public boolean isNoLevels(){return false;}
}

package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArmorInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class LightInDark extends Modifier implements ArrowInterface, MeleeInterface, ArmorInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        this.MeleeInterfaceInit(hookBuilder);
        this.ArmorInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * (1 + lightCurve(context.getAttacker()));
    }

    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(1 + lightCurve(shooter)));
    }

    public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
        return modifierValue * (1 + lightCurve(context.getEntity()));
    }

    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        return amount * (1 - lightCurve(context.getEntity()));
    }


    public static float lightCurve(LivingEntity entity) {
        BlockPos pos = entity.blockPosition();
        int skyDarken = entity.level().getSkyDarken();                  // 夜晚/天气导致的天光衰减
        int rawBrightness = entity.level().getRawBrightness(pos, skyDarken);
        // 可选：约束输入到 0..15
        if (rawBrightness < 0)
            rawBrightness = 0;
        if (rawBrightness > 15)
            rawBrightness = 15;
        if (rawBrightness <= 3){
            return 0.25f * (1.0f - rawBrightness / 3.0f); // 0.25 -> 0
        }else if (rawBrightness <= 11){
            double t = (rawBrightness - 4) / 7.0;                  // 0..1
            return (float) (-0.25f * (1.0f - 2.0f * Math.abs(t - 0.5f))); // 0 -> -0.25 -> 0
        }else { // 12..15
            return 0.25f * ((rawBrightness - 12) / 3.0f); // 0 -> 0.25
        }
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    public boolean isNoLevels() {return true;}

}

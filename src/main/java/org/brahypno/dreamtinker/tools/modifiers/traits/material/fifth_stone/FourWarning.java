package org.brahypno.dreamtinker.tools.modifiers.traits.material.fifth_stone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.ProjectileHurtHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.FirthMark;

public class FourWarning extends NoLevelsModifier implements ProjectileHurtHook {
    private static final String MARK_TAG = "dt_fifth_mark";

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, EsotericismTinkerHook.PROJECTILE_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyProjectileHurt(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile,
            DamageSource source, @Nullable LivingEntity attacker, LivingEntity target, float amount) {
        CompoundTag data = target.getPersistentData();
        int times = (data.getInt(MARK_TAG) + 1) % 5;
        float ratio = FirthMark.get().floatValue();
        data.putInt(MARK_TAG, times);
        return amount * (times == 0 ? 1 + ratio * 5 : 1 - ratio);
    }
}

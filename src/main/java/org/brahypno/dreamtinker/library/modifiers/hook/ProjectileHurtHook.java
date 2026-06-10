package org.brahypno.dreamtinker.library.modifiers.hook;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.Collection;

public interface ProjectileHurtHook {
    default float modifyProjectileHurt(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile,
            DamageSource source, @Nullable LivingEntity attacker, LivingEntity target, float amount) {
        return amount;
    }

    record AllMerger(Collection<ProjectileHurtHook> modules) implements ProjectileHurtHook {
        @Override
        public float modifyProjectileHurt(
                ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile,
                DamageSource source, @Nullable LivingEntity attacker, LivingEntity target, float amount) {
            for (ProjectileHurtHook module : this.modules) {
                amount = module.modifyProjectileHurt(modifiers, persistentData, modifier, projectile, source, attacker, target, amount);
            }
            return amount;
        }
    }
}

package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.brahypno.dreamtinker.utils.ProjectileHitMemory;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static org.brahypno.dreamtinker.config.DreamtinkerConfig.rangedHit;

public class RangedShoot extends NoLevelsModifier implements ProjectileHitModifierHook, ProjectileLaunchModifierHook {
    private static final String mark = "dreamtinker_ranged_shot";

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_LAUNCH);
        super.registerHooks(hookBuilder);
    }

    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (null != attacker && null != target && !ProjectileHitMemory.hasTriggered(mark, projectile, target.getUUID())){
            if (rangedHit.get() <= 1e-6)
                return false;
            double dis = attacker.level() == target.level() ? attacker.position().distanceTo(target.position()) : 3 * rangedHit.get();
            double ratio = Math.max(dis / rangedHit.get(), 0.25);
            Vec3 vel = projectile.getDeltaMovement().scale(ratio);
            projectile.setDeltaMovement(vel);
            if (projectile instanceof AbstractArrow arrow){
                arrow.setCritArrow(1 < ratio);
                arrow.setBaseDamage(arrow.getBaseDamage() * ratio);
            }
            ProjectileHitMemory.markTriggered(mark, projectile, target.getUUID());
            Vec3 dir = vel.lengthSqr() > 1e-6 ? vel : target.position().subtract(projectile.position());
            dir = dir.normalize();
            projectile.setPos(projectile.getX() + dir.x * 0.08,
                              projectile.getY() + dir.y * 0.08,
                              projectile.getZ() + dir.z * 0.08);
        }
        return false;
    }

    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (null != arrow)
            arrow.setPierceLevel((byte) ((arrow.getPierceLevel() + 1) * 2));
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(this.getTranslationKey() + ".description",
                                                    String.format("%s", rangedHit.get()))
                                      .withStyle(ChatFormatting.GRAY));
    }
}

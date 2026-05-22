package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileShootModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class acheron extends Modifier implements ModifierRemovalHook, MeleeHitModifierHook, ProjectileLaunchModifierHook, ProjectileShootModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.REMOVE, ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_LAUNCH,
                            ModifierHooks.PROJECTILE_SHOT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        return Component.translatable(this.getTranslationKey() + ".salvage");
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        Player attacker = context.getPlayerAttacker();
        if (attacker != null && !attacker.level().isClientSide){
            attacker.setAbsorptionAmount(Math.min(attacker.getAbsorptionAmount() + damageDealt, attacker.getMaxHealth() * 5));
        }
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter instanceof Player player && !shooter.level().isClientSide){
            player.setAbsorptionAmount(Math.min(player.getAbsorptionAmount() + player.getMaxHealth() * 0.1f, player.getMaxHealth() * 5));
        }
    }

    @Override
    public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter instanceof Player player && !shooter.level().isClientSide){
            player.setAbsorptionAmount(Math.min(player.getAbsorptionAmount() + player.getMaxHealth() * 0.1f, player.getMaxHealth() * 5));
        }
    }
}

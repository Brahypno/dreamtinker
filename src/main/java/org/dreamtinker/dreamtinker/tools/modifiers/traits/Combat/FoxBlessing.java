package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.Entity.AggressiveFox;
import org.dreamtinker.dreamtinker.Entity.DreamtinkerEntityTypes;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;

public class FoxBlessing extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, OnAttackedModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT,
                            ModifierHooks.ON_ATTACKED);
        super.registerHooks(hookBuilder);
    }

    private void SpawnWeaponFox(@NotNull LivingEntity spawner, LivingEntity target) {
        if (spawner.level().isClientSide)
            return;
        List<AggressiveFox> foxes = spawner.level().getEntitiesOfClass(AggressiveFox.class, spawner.getBoundingBox().inflate(5, 0.25D, 5));
        if (foxes.size() <= 3){
            AggressiveFox fox = DreamtinkerEntityTypes.AggressiveFOX.get().create(spawner.level());
            if (fox != null){
                fox.moveTo(spawner.blockPosition(), spawner.getYRot(), spawner.getXRot());
                if (null != target)
                    fox.setTarget(target);
                spawner.level().addFreshEntity(fox);
            }
        }
    }

    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        LivingEntity spawner = null != target ? target : attacker;
        if (null != spawner)
            SpawnWeaponFox(spawner, target);
        return false;
    }

    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        LivingEntity spawner = null != target ? target : context.getAttacker();
        SpawnWeaponFox(spawner, target);
        return knockback;
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        beforeMeleeHit(tool, modifier, context, damage, 0, 0);
    }

    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        LivingEntity target = source.getEntity() instanceof LivingEntity le ? le : null;
        LivingEntity spawner = null != target ? target : context.getEntity();
        SpawnWeaponFox(spawner, target);
    }

}

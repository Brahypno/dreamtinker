package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import io.netty.channel.unix.DomainSocketReadMode;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.dreamtinker.dreamtinker.Entity.AggressiveFox;
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
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;

public class FoxBlessing extends Modifier implements ArrowInterface, MeleeInterface, ArmorInterface {

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        this.MeleeInterfaceInit(hookBuilder);
        this.ArmorInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }
    private void SpawnWeaponFox(@NotNull LivingEntity spawner, LivingEntity target){
        if(spawner.level().isClientSide)
            return;
        List<AggressiveFox> foxes=spawner.level().getEntitiesOfClass(AggressiveFox.class, spawner.getBoundingBox().inflate(5, 0.25D, 5));
        if(foxes.size()<=3){
            AggressiveFox fox = DreamtinkerModule.AggressiveFOX.get().create(spawner.level());
            if (fox != null) {
                fox.moveTo(spawner.blockPosition(),spawner.getYRot(),spawner.getXRot());
                if(null!=target)
                    fox.setTarget(target);
                spawner.level().addFreshEntity(fox);
            }
        }
    }
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        LivingEntity spawner=null!=target?target:attacker;
        if(null!=spawner)
            SpawnWeaponFox(spawner,target);
        return false;
    }
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity spawner=null!=context.getLivingTarget()?context.getLivingTarget():context.getAttacker();
        if(null!=spawner)
            SpawnWeaponFox(spawner,context.getLivingTarget());
        return knockback;
    }
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        LivingEntity target= source.getEntity() instanceof LivingEntity le?le:null;
        LivingEntity spawner=null!=target?target: context.getEntity();
        if(null!=spawner)
            SpawnWeaponFox(spawner,target);
    }

}

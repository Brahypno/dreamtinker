package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.star_regulus;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.Entity.DreamtinkerEntityTypes;
import org.dreamtinker.dreamtinker.Entity.WiserLightBolt;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.UUID;

public class TwoHeadedSeven extends Modifier implements ProjectileHitModifierHook, ToolStatsModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.TOOL_STATS);
        hookBuilder.addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
        super.registerHooks(hookBuilder);
    }

    @Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
        if (null == attacker)
            return false;
        if (attacker.level().isClientSide)
            return false;
        ServerLevel level = (ServerLevel) attacker.level();
        summonWiserLight(attacker, level, hit.getBlockPos(), projectile);

        return false;
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (null != target && target.level() instanceof ServerLevel sl){

            AttributeModifier neg = new AttributeModifier(UUID.nameUUIDFromBytes(modifier.getId().toString().getBytes()), "def_suppress", Integer.MIN_VALUE,
                                                          AttributeModifier.Operation.ADDITION);
            AttributeInstance attr = target.getAttribute(Attributes.ARMOR);
            if (null != attr && attr.getModifier(UUID.nameUUIDFromBytes(modifier.getId().toString().getBytes())) == null)
                attr.addTransientModifier(neg);
            attr = target.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (null != attr && attr.getModifier(UUID.nameUUIDFromBytes(modifier.getId().toString().getBytes())) == null)
                attr.addTransientModifier(neg);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 10));
            summonWiserLight(attacker, sl, hit.getEntity().getOnPos(), projectile);
        }
        return false;
    }

    private void summonWiserLight(@Nullable LivingEntity attacker, ServerLevel level, BlockPos pos, Projectile projectile) {
        WiserLightBolt bolt = new WiserLightBolt(DreamtinkerEntityTypes.LIGHTNING_ENTITY.get(), level);

        bolt.setPos(pos.getX(), pos.getY(), pos.getZ());
        bolt.setOwner(attacker);
        bolt.setChainCount(7);
        bolt.setVisualOnly(false);
        bolt.setDamage(DTModifierCheck.getDamage(projectile));
        level.addFreshEntity(bolt);
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        float current_speed = builder.getStat(ToolStats.DRAW_SPEED);
        ToolStats.PROJECTILE_DAMAGE.add(builder, current_speed);
    }
}

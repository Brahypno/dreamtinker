package org.brahypno.dreamtinker.tools.modifiers.traits.material.deliverance;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.Entity.SlashOrbitEntity;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.utils.DTHelper;
import org.brahypno.dreamtinker.utils.DamageProbe;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class Signet extends Modifier implements MeleeHitModifierHook, MonsterMeleeHitModifierHook, TooltipModifierHook {
    private static final ResourceLocation SKY_FIRE_HEAT = Dreamtinker.getLocation("signet_skyfire_heat");
    private static final float MAX_HEAT = 100.0F;
    private static final double SKY_FIRE_SPAWN_MARGIN = 1.0D;
    private static final double SKY_FIRE_SLASH_SPEED = 0.5D;

    private static void releaseSkyFire(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageBasis) {
        if (!(context.getLevel() instanceof ServerLevel level)){
            return;
        }

        LivingEntity attacker = context.getAttacker();
        int levelValue = modifier.getLevel();
        float radius = 4.5F + levelValue;
        float slashDamage = Math.max(4.0F, damageBasis * (7.7F + 0.7F * levelValue));

        Vec3 slashDirection = attacker.getLookAngle().normalize();
        Vec3 slashOrigin = new Vec3(attacker.getX(), attacker.getY() + attacker.getBbHeight() * 0.5D, attacker.getZ())
                .add(slashDirection.scale(radius + SKY_FIRE_SPAWN_MARGIN));
        SlashOrbitEntity slash = SlashOrbitEntity.projectile(level, attacker, slashOrigin,
                                                             slashDirection.scale(SKY_FIRE_SLASH_SPEED),
                                                             radius, 12, 0.85F, 8, slashDamage, 0.95F, 18.0F, false);
        slash.setGradient(0xFFFF6A00, 0xFFFFD266, SlashOrbitEntity.GradMode.ANGULAR, true).setHueShift(0.015F);
        level.addFreshEntity(slash);

        DamageSource fireSource = DreamtinkerDamageTypes.source(level.registryAccess(), DamageTypes.IN_FIRE, null, attacker);
        Entity rootVehicle = attacker.getRootVehicle();
        AABB searchBox = attacker.getBoundingBox().inflate(radius, 1.0D, radius);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, searchBox,
                                                            target -> canHit(attacker, rootVehicle, target))) {
            target.setSecondsOnFire(4 + levelValue * 2);
            dealExtraDamage(target, fireSource, slashDamage);
        }

        level.sendParticles(ParticleTypes.FLAME, attacker.getX(), attacker.getY(0.5D), attacker.getZ(), 48, radius * 0.35D, 0.35D, radius * 0.35D, 0.08D);
        level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.2F, 0.75F);
        level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.FIRECHARGE_USE, attacker.getSoundSource(), 1.0F, 0.9F);

        consumeUserLife(attacker, 2.0F * levelValue);
        ToolDamageUtil.damageAnimated(tool, 8 + 4 * levelValue, attacker, context.getSlotType());
    }

    private static void dealExtraDamage(LivingEntity target, DamageSource source, float amount) {
        int invulnerableTime = target.invulnerableTime;
        try {
            target.invulnerableTime = 0;
            DamageProbe.damageHandler(target, source, amount);
        }
        finally {
            target.invulnerableTime = invulnerableTime;
        }
    }

    private static boolean canHit(LivingEntity attacker, Entity rootVehicle, LivingEntity target) {
        return target != attacker
               && target != rootVehicle
               && target.isAlive()
               && !target.isSpectator()
               && !attacker.isAlliedTo(target)
               && (!(target instanceof ArmorStand stand) || !stand.isMarker());
    }

    private static void consumeUserLife(LivingEntity attacker, float amount) {
        if (amount <= 0.0F || attacker instanceof Player player && player.isCreative()){
            return;
        }
        attacker.setHealth(Math.max(1.0F, attacker.getHealth() - amount));
    }

    private static float heatGain(ModifierEntry modifier, float damageBasis) {
        return Mth.clamp(8.0F + modifier.getLevel() * 4.0F + damageBasis, 8.0F, 30.0F);
    }

    private static float getHeat(ModDataNBT data) {
        return data.getFloat(SKY_FIRE_HEAT);
    }

    private static void addHeat(ModDataNBT data, float amount) {
        data.putFloat(SKY_FIRE_HEAT, Mth.clamp(getHeat(data) + amount, 0.0F, MAX_HEAT));
    }

    private static boolean isHeatFull(ModDataNBT data) {
        return getHeat(data) >= MAX_HEAT;
    }

    private static void consumeHeat(ModDataNBT data) {
        data.putFloat(SKY_FIRE_HEAT, 0.0F);
    }

    private static String formatHeat(float value) {
        return value == (int) value ? Integer.toString((int) value) : String.format(Locale.ROOT, "%.1f", value);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.getLevel().isClientSide || tool.isBroken()){
            return;
        }

        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        float damageBasis = Math.max(damageDealt, ETModifierCheck.getMeleeDamage(context.getAttacker(), target, tool, false));
        if (damageBasis <= 0.0F){
            damageBasis = context.getBaseDamage();
        }

        if (target != null && target.isAlive() && damageBasis > 0.0F){
            dealSignetDamage(modifier, context, target, damageBasis);
        }

        ModDataNBT data = tool.getPersistentData();
        if (isHeatFull(data)){
            consumeHeat(data);
            releaseSkyFire(tool, modifier, context, damageBasis);
            return;
        }

        addHeat(data, heatGain(modifier, damageBasis));
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        afterMeleeHit(tool, modifier, context, damageAttempted);
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }

    private void dealSignetDamage(ModifierEntry modifier, ToolAttackContext context, LivingEntity target, float damageBasis) {
        boolean highHealth = target.getHealth() + Math.max(damageBasis, 0.0F) > target.getMaxHealth() * 0.5F;
        DamageSource source = highHealth
                              ? DreamtinkerDamageTypes.source(context.getLevel().registryAccess(), DamageTypes.FREEZE,
                                                              context.makeDamageSource())
                              : DreamtinkerDamageTypes.source(context.getLevel().registryAccess(), DreamtinkerDamageTypes.solar_judgment,
                                                              context.makeDamageSource());
        float extraDamage = Math.max(7.7F, damageBasis * (7.7F + 0.7F * modifier.getLevel()));

        dealExtraDamage(target, source, extraDamage);
        if (highHealth){
            target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze(), target.getTicksFrozen() + 60 + 20 * modifier.getLevel()));
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        float heat = getHeat(tool.getPersistentData());
        tooltip.add(Component.translatable("modifier.dreamtinker.signet.heat", formatHeat(heat), formatHeat(MAX_HEAT)).withStyle(ChatFormatting.GOLD));
    }
}

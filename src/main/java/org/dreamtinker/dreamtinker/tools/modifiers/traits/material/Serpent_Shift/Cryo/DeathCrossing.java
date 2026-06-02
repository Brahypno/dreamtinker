package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.Serpent_Shift.Cryo;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.utils.DTDamageUtils;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class DeathCrossing extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, GeneralInteractionModifierHook, UsingToolModifierHook,
        AttributesModifierHook, KeybindInteractModifierHook, TooltipModifierHook {
    private static final ResourceLocation TAG_CHARGE = Dreamtinker.getLocation("death_crossing_charge");
    private static final ResourceLocation TAG_COOLDOWN_UNTIL = Dreamtinker.getLocation("death_crossing_cooldown_until");
    private static final int BASE_MAX_CHARGE = 100;
    private static final int MAX_CHARGE_PER_LEVEL = 50;
    private static final int BASE_CHARGE_GAIN = 45;
    private static final int CHARGE_GAIN_PER_LEVEL = 15;
    private static final int COOLDOWN_TICKS = 20 * 5;
    private static final int MIN_RELEASE_TICKS = 20;
    private static final int RELEASE_MIN_CHARGE = 50;
    private static final int RELEASE_EXTRA_CHARGE_CAP = 12;
    private static final int RELEASE_HITS = 5;
    private static final double CONE_HALF_ANGLE_COS = 0.5D;

    private static boolean tryGainCharge(IToolStackView tool, ModifierEntry modifier, Player player) {
        if (player.level().isClientSide){
            return true;
        }
        Tag cooldown = tool.getPersistentData().get(DeathCrossing.TAG_COOLDOWN_UNTIL);
        if (cooldown instanceof LongTag longTag && longTag.getAsLong() > player.level().getGameTime()){
            return false;
        }
        int maxCharge = BASE_MAX_CHARGE + Math.max(0, modifier.getLevel() - 1) * MAX_CHARGE_PER_LEVEL;
        if (getCharge(tool) >= maxCharge){
            return false;
        }
        setCharge(tool, Mth.clamp(getCharge(tool) + BASE_CHARGE_GAIN + Math.max(0, modifier.getLevel() - 1) * CHARGE_GAIN_PER_LEVEL, 0, maxCharge));
        tool.getPersistentData().put(DeathCrossing.TAG_COOLDOWN_UNTIL, LongTag.valueOf(player.level().getGameTime() + COOLDOWN_TICKS));
        return true;
    }

    private static int getCharge(IToolStackView tool) {
        return Math.max(0, tool.getPersistentData().getInt(TAG_CHARGE));
    }

    private static void setCharge(IToolStackView tool, int charge) {
        if (charge <= 0){
            tool.getPersistentData().remove(TAG_CHARGE);
        }else {
            tool.getPersistentData().putInt(TAG_CHARGE, charge);
        }
    }

    private static void releaseStoredCold(IToolStackView tool, ModifierEntry modifier, LivingEntity user, int charge) {
        if (!(user.level() instanceof ServerLevel level)){
            return;
        }

        double range = 4.0D + charge / 20.0D;
        float damage = tool.getStats().get(ToolStats.ATTACK_DAMAGE) *
                       (2.21F + Math.max(0, modifier.getLevel() - 1) * 0.12F +
                        Math.min(RELEASE_EXTRA_CHARGE_CAP, Math.max(0, charge - RELEASE_MIN_CHARGE)) *
                        (0.3671F + Math.max(0, modifier.getLevel() - 1) * 0.0194F));
        DamageSource source = DreamtinkerDamageTypes.source(level.registryAccess(), DamageTypes.FREEZE, null, user);
        Vec3 look = horizontal(user.getLookAngle());
        if (look.lengthSqr() <= 0.0D){
            look = new Vec3(0.0D, 0.0D, 1.0D);
        }

        Entity rootVehicle = user.getRootVehicle();
        AABB box = user.getBoundingBox().inflate(range, 2.0D, range);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, target -> canAffect(user, rootVehicle, target))) {
            Vec3 toTarget = horizontal(target.getEyePosition().subtract(user.getEyePosition()));
            if (toTarget.lengthSqr() <= 0.0D || toTarget.normalize().dot(look) < CONE_HALF_ANGLE_COS){
                continue;
            }
            if (user.distanceToSqr(target) > range * range){
                continue;
            }
            for (int i = 0; i < RELEASE_HITS; i++) {
                dealPowderSnowDamage(target, source, damage);
            }
            target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze(), target.getTicksFrozen() + 60 + 10 * modifier.getLevel()));
        }

        level.sendParticles(ParticleTypes.SNOWFLAKE, user.getX(), user.getY(0.5D), user.getZ(), 48 + charge / 2, range * 0.25D, 0.35D,
                            range * 0.25D, 0.05D);
        level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.POWDER_SNOW_BREAK, user.getSoundSource(), 1.1F, 0.7F);
        level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.GLASS_BREAK, user.getSoundSource(), 0.7F, 1.35F);
    }

    private static Vec3 horizontal(Vec3 vec) {
        return new Vec3(vec.x, 0.0D, vec.z).normalize();
    }

    private static boolean canAffect(LivingEntity user, Entity rootVehicle, LivingEntity target) {
        return target != user
               && target != rootVehicle
               && target.isAlive()
               && !target.isSpectator()
               && !user.isAlliedTo(target)
               && (!(target instanceof ArmorStand stand) || !stand.isMarker());
    }

    private static void dealPowderSnowDamage(LivingEntity target, DamageSource source, float amount) {
        int invulnerableTime = target.invulnerableTime;
        try {
            target.invulnerableTime = 0;
            DTDamageUtils.damageHandler(target, source, Math.max(0.0F, amount));
        }
        finally {
            target.invulnerableTime = invulnerableTime;
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING,
                            ModifierHooks.ATTRIBUTES, ModifierHooks.ARMOR_INTERACT, ModifierHooks.TOOLTIP);
        super.registerHooks(hookBuilder);
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
        return BlockingModifier.blockWhileCharging(tool, UseAnim.BOW);
    }

    @Override
    public @NotNull InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (tool.isBroken() || source != InteractionSource.RIGHT_CLICK || hand != InteractionHand.MAIN_HAND){
            return InteractionResult.PASS;
        }
        if (getCharge(tool) < RELEASE_MIN_CHARGE){
            return tryGainCharge(tool, modifier, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 1.0F);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
        if (entity.level().isClientSide){
            return;
        }

        int usedTicks = getUseDuration(tool, modifier) - timeLeft;
        int charge = getCharge(tool);
        if (usedTicks >= MIN_RELEASE_TICKS){
            if (charge >= RELEASE_MIN_CHARGE){
                releaseStoredCold(tool, modifier, entity, charge);
                setCharge(tool, 0);
            }
        }else if (entity instanceof Player player){
            tryGainCharge(tool, modifier, player);
        }
    }

    @Override
    public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
        if (player.level().isClientSide || tool.isBroken() || slot != EquipmentSlot.MAINHAND){
            return false;
        }
        return tryGainCharge(tool, modifier, player);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        damage = (tool.getMultiplier(ToolStats.ATTACK_SPEED) + 1) * damage;
        if (getCharge(tool) > 0){
            damage *= 1.5F + Math.max(0, modifier.getLevel() - 1) * 0.1F;
        }
        return damage;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        int charge = getCharge(tool);
        if (charge <= 0 || context.getLevel().isClientSide){
            return;
        }

        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        if (target == null || !target.isAlive()){
            return;
        }

        DamageSource source = DreamtinkerDamageTypes.source(context.getLevel().registryAccess(), DamageTypes.FREEZE, context.makeDamageSource());
        dealPowderSnowDamage(target, source, Math.max(1.0F, damageDealt * 0.2F + charge * (0.4F + 0.1F * modifier.getLevel())));
        target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze(), target.getTicksFrozen() + 20 + 10 * modifier.getLevel()));
        setCharge(tool, charge - 1);
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        afterMeleeHit(tool, modifier, context, damageAttempted);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken() && slot == EquipmentSlot.MAINHAND && getCharge(tool) > 0){
            consumer.accept(ForgeMod.ENTITY_REACH.get(),
                            new AttributeModifier(UUID.nameUUIDFromBytes(this.getId().toString().getBytes()),
                                                  this.getTranslationKey(),
                                                  modifier.getLevel(),
                                                  AttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey.isShiftOrUnknown())
            tooltip.add(Component.translatable("modifier.dreamtinker.ford_the_broken_crossing.tooltip.energy", getCharge(tool))
                                 .withStyle(this.getDisplayName().getStyle()));
    }
}

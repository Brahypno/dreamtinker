package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.deliverance;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.dreamtinker.dreamtinker.utils.DamageProbe;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.UUID;
import java.util.function.BiConsumer;

public class Metamorphosis extends Modifier implements DamageDealtModifierHook, AttributesModifierHook, InventoryTickModifierHook,
        DamageBlockModifierHook, OnAttackedModifierHook, ModifyDamageModifierHook, ModifierTraitHook {
    private static final ThreadLocal<Boolean> DAMAGE_DEALT_IN_PROGRESS = ThreadLocal.withInitial(() -> false);
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("metamorphosis"));
    private static final ResourceLocation COLLAPSED = Dreamtinker.getLocation("metamorphosis_collapsed");
    private static final ResourceLocation SUNLESS_TRAIT = Dreamtinker.getLocation("metamorphosis_sunless_trait");
    private static final String FROST_FIELD_TICK = Dreamtinker.getLocation("metamorphosis_frost_field_tick").toString();
    private static final String SUNLESS_ENTRY_COUNT = Dreamtinker.getLocation("metamorphosis_sunless_entry_count").toString();
    private static final String BELOW_SUNLESS_LINE = Dreamtinker.getLocation("metamorphosis_below_sunless_line").toString();
    private static final String SUNLESS_ACTIVE = Dreamtinker.getLocation("metamorphosis_sunless_active").toString();
    private static final UUID ATTACK_DAMAGE_ID = UUID.fromString("b3594501-c1f7-43bb-a287-ed6a7238c453");
    private static final UUID KNOCKBACK_RESISTANCE_ID = UUID.fromString("390ba9e0-5369-4dd6-b10b-d7ce05d4e37b");
    private static final float BASE_THRESHOLD = 0.35F;
    private static final int FIELD_TIME = 80;
    private static final float FIELD_DAMAGE_SCALE = 0.35F;

    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingHeal);
    }

    private static boolean isCollapsed(LivingEntity entity, ModifierEntry modifier) {
        return isCollapsed(entity, modifier.getLevel());
    }

    private static boolean isCollapsed(LivingEntity entity, int level) {
        if (entity == null || entity.getMaxHealth() <= 0.0F){
            return false;
        }
        return entity.getHealth() / entity.getMaxHealth() <= collapsedThreshold(level);
    }

    private static float collapsedThreshold(int level) {
        return Math.min(0.50F, BASE_THRESHOLD + 0.05F * Math.max(0, level - 1));
    }

    private static boolean isBelowSunlessLine(LivingEntity entity, int level) {
        return entity.getMaxHealth() > 0.0F
               && entity.getHealth() / entity.getMaxHealth() <= collapsedThreshold(level) * 0.5F;
    }

    private static void syncSunlessTrait(IToolStackView tool, ItemStack stack, boolean active) {
        if (tool.getPersistentData().getBoolean(SUNLESS_TRAIT) == active){
            return;
        }
        tool.getPersistentData().putBoolean(SUNLESS_TRAIT, active);
        if (tool instanceof ToolStack toolStack){
            toolStack.updateStack(stack);
        }else {
            ToolStack.from(stack).updateStack(stack);
        }
    }

    private static void applyCollapseImmunities(LivingEntity holder) {
        holder.clearFire();
        holder.setTicksFrozen(0);
        holder.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    private static void freezeBurst(
            LivingEntity attacker, Level world, LivingEntity center, int level, float damage, float radius, int freezeTicks,
            DamageSource source) {
        if (!(world instanceof ServerLevel serverLevel)){
            return;
        }

        DamageSource freezeSource = DreamtinkerDamageTypes.source(serverLevel.registryAccess(), DamageTypes.FREEZE, source);
        AABB box = center.getBoundingBox().inflate(radius, 2.0D, radius);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box, target -> canAffect(attacker, target))) {
            target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze(), target.getTicksFrozen() + freezeTicks));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, FIELD_TIME, Math.min(3, level), false, true));
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, FIELD_TIME, Math.min(2, level), false, true));
            if (target instanceof Player player){
                player.resetAttackStrengthTicker();
            }
            dealExtraDamage(target, freezeSource, Math.max(1.0F, damage));
        }
        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, center.getX(), center.getY(0.5D), center.getZ(), 32, radius * 0.25D, 0.25D,
                                  radius * 0.25D, 0.03D);
        serverLevel.playSound(null, center.getX(), center.getY(), center.getZ(), SoundEvents.GLASS_BREAK, center.getSoundSource(), 0.7F, 1.4F);
        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, center.getX(), center.getY(0.5D), center.getZ(), 64,
                                  radius * 0.35D, 0.25D, radius * 0.35D, 0.02D);
        serverLevel.playSound(null, center.getX(), center.getY(), center.getZ(), SoundEvents.POWDER_SNOW_STEP, center.getSoundSource(), 1.2F, 0.7F);
    }

    private static void fireBurst(LivingEntity attacker, Level world, LivingEntity center, float damage, float radius) {
        if (!(world instanceof ServerLevel serverLevel)){
            return;
        }

        DamageSource fireSource = DreamtinkerDamageTypes.source(serverLevel.registryAccess(), DamageTypes.IN_FIRE, null, attacker);
        AABB box = center.getBoundingBox().inflate(radius, 1.75D, radius);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box, target -> canAffect(attacker, target))) {
            target.setSecondsOnFire(3);
            dealExtraDamage(target, fireSource, damage);
        }
        serverLevel.sendParticles(ParticleTypes.FLAME, center.getX(), center.getY(0.5D), center.getZ(), 24, radius * 0.2D, 0.25D, radius * 0.2D, 0.05D);
        serverLevel.playSound(null, center.getX(), center.getY(), center.getZ(), SoundEvents.FIRECHARGE_USE, center.getSoundSource(), 0.8F, 1.2F);
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

    private static boolean canAffect(LivingEntity owner, LivingEntity target) {
        return target != owner
               && target.isAlive()
               && !target.isSpectator()
               && !owner.isAlliedTo(target)
               && (!(target instanceof ArmorStand stand) || !stand.isMarker());
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_DEALT, ModifierHooks.ATTRIBUTES, ModifierHooks.INVENTORY_TICK, ModifierHooks.DAMAGE_BLOCK,
                            ModifierHooks.ON_ATTACKED, ModifierHooks.MODIFY_HURT, ModifierHooks.MODIFIER_TRAITS);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void addTraits(IToolContext context, ModifierEntry self, ModifierTraitHook.TraitBuilder builder, boolean firstEncounter) {
        if (context.getPersistentData().getBoolean(SUNLESS_TRAIT)){
            builder.add(DreamtinkerModifiers.sunless.getId(), 1);
        }
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR && !tool.isBroken() && tool.getPersistentData().getBoolean(COLLAPSED)){
            consumer.accept(Attributes.ATTACK_DAMAGE,
                            new AttributeModifier(ATTACK_DAMAGE_ID, this.getTranslationKey(),
                                                  0.25D + 0.20D * modifier.getLevel(), AttributeModifier.Operation.MULTIPLY_TOTAL));
            consumer.accept(Attributes.KNOCKBACK_RESISTANCE,
                            new AttributeModifier(KNOCKBACK_RESISTANCE_ID, this.getTranslationKey(),
                                                  Math.min(1.0D, 0.35D + 0.15D * modifier.getLevel()),
                                                  AttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
        LivingEntity attacker = context.getEntity();
        int chargeLevel = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (slotType.getType() != EquipmentSlot.Type.ARMOR || context.getLevel().isClientSide || chargeLevel <= 0 || !isCollapsed(attacker, modifier)
            || Boolean.TRUE.equals(DAMAGE_DEALT_IN_PROGRESS.get())){
            return;
        }

        DAMAGE_DEALT_IN_PROGRESS.set(true);
        try {
            if (attacker.getRandom().nextBoolean()){
                freezeBurst(attacker, context.getLevel(), target, chargeLevel, Math.max(7.7F, amount * 0.7F * chargeLevel),
                            2.5F + 0.5F * chargeLevel, 80 + 20 * chargeLevel, source);
            }else {
                fireBurst(attacker, context.getLevel(), target, Math.max(7.7F, amount * 0.7F * chargeLevel), 2.5F + 0.5F * chargeLevel);
            }
        }
        finally {
            DAMAGE_DEALT_IN_PROGRESS.remove();
        }
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        if (!isCollapsed(context.getEntity(), modifier)){
            return false;
        }
        return source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.FREEZE);
    }

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        LivingEntity entity = context.getEntity();
        int chargeLevel = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (chargeLevel <= 0 || amount <= 0.0F || context.getLevel().isClientSide || !isCollapsed(entity, chargeLevel)){
            return;
        }
        long gameTime = context.getLevel().getGameTime();
        if (entity.getPersistentData().getLong(FROST_FIELD_TICK) == gameTime){
            return;
        }
        entity.getPersistentData().putLong(FROST_FIELD_TICK, gameTime);
        freezeBurst(entity, context.getLevel(), entity, chargeLevel,
                    Math.max(1.0F, Math.min(amount * FIELD_DAMAGE_SCALE, 2.0F + chargeLevel * 2.0F)), 4.0F + chargeLevel,
                    80 + 20 * chargeLevel, source);
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        boolean collapsed = isCorrectSlot && isCollapsed(holder, modifier);
        tool.getPersistentData().putBoolean(COLLAPSED, collapsed);

        if (holder.getHealth() >= holder.getMaxHealth()){
            holder.getPersistentData().remove(SUNLESS_ENTRY_COUNT);
            holder.getPersistentData().remove(BELOW_SUNLESS_LINE);
            holder.getPersistentData().remove(SUNLESS_ACTIVE);
        }else if (isCorrectSlot){
            boolean belowLine = isBelowSunlessLine(holder, modifier.getLevel());
            boolean wasBelowLine = holder.getPersistentData().getBoolean(BELOW_SUNLESS_LINE);
            if (belowLine && !wasBelowLine){
                int entries = holder.getPersistentData().getInt(SUNLESS_ENTRY_COUNT) + 1;
                holder.getPersistentData().putInt(SUNLESS_ENTRY_COUNT, entries);
                holder.getPersistentData().putBoolean(BELOW_SUNLESS_LINE, true);
                if (entries >= 3){
                    holder.getPersistentData().putBoolean(SUNLESS_ACTIVE, true);
                }
            }else if (!belowLine && wasBelowLine){
                holder.getPersistentData().remove(BELOW_SUNLESS_LINE);
            }
        }
        syncSunlessTrait(tool, stack, isCorrectSlot && holder.getPersistentData().getBoolean(SUNLESS_ACTIVE));

        if (!collapsed)
            return;

        applyCollapseImmunities(holder);

        if (world.getGameTime() % 20 == 0){
            if (holder instanceof ServerPlayer player)
                player.causeFoodExhaustion(2.0F);
        }
    }

    private void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        int level = DTModifierCheck.getEntityBodyModifierNum(entity, this.getId());
        if (level > 0 && isCollapsed(entity, level)){
            event.setAmount(event.getAmount() * Math.max(0.15F, 0.55F - 0.10F * level));
        }
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        return isCollapsed(context.getEntity(), modifier) ? Math.min(amount, context.getEntity().getMaxHealth()) : amount;
    }
}

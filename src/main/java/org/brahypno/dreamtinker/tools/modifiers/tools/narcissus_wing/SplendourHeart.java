package org.brahypno.dreamtinker.tools.modifiers.tools.narcissus_wing;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.TierSortingRegistry;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.tools.modifiers.events.AdvCountEvents;
import org.brahypno.dreamtinker.utils.DamageProbe;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.ProjectileHurtHook;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.*;
import slimeknights.tconstruct.library.tools.stat.*;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.logic.ModifierEvents;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.TheSplendourHeart;

public class SplendourHeart extends Modifier implements MeleeHitModifierHook, InventoryTickModifierHook,
        ToolDamageModifierHook, ModifierRemovalHook, TooltipModifierHook, ToolStatsModifierHook,
        AttributesModifierHook, VolatileDataModifierHook, ProjectileHurtHook {

    private static final ResourceLocation TAG_ADV_PERCENTAGE = Dreamtinker.getLocation("adv_percentage");

    private static final int ALLOWED_EXTRA_TIMES = 1;
    private static final ThreadLocal<Integer> EXTRA_ATTACK_DEPTH = ThreadLocal.withInitial(() -> 0);

    @Nullable
    private static SplendourData getSplendourData(IModDataView data) {
        if (!data.contains(TAG_ADV_PERCENTAGE)){
            return null;
        }

        float percentage = data.getFloat(TAG_ADV_PERCENTAGE);
        int level = splendourLevel(percentage);
        return new SplendourData(percentage, level, rangeToValue(percentage, level));
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_ADV_PERCENTAGE);
        return null;
    }

    private static int splendourLevel(float percentage) {
        List<? extends Double> ranges = TheSplendourHeart.get();

        int level = 0;
        while (level < ranges.size() && percentage >= ranges.get(level)) {
            level++;
        }

        return level;
    }

    private static String rangeToText(int level) {
        return switch (level) {
            case 0 -> "_entry";
            case 1 -> "_easy";
            case 2 -> "_normal";
            case 3 -> "_high";
            default -> "_excellent";
        };
    }

    private static float rangeToValue(float percentage, int level) {
        List<? extends Double> ranges = TheSplendourHeart.get();

        return switch (level) {
            case 0 -> (float) (((percentage - ranges.get(0)) / ranges.get(0) / 2.0D) + 1.0D);
            case 1 -> (float) (((percentage - ranges.get(0)) / (ranges.get(1) - ranges.get(0))) + 1.0D);
            case 2 -> (float) (3.0D + 1.5D * Math.pow((percentage - ranges.get(1)) / (ranges.get(2) - ranges.get(1)), 1.25D));
            case 3 -> (float) (4.5D + 2.0D * Math.pow((percentage - ranges.get(2)) / (ranges.get(3) - ranges.get(2)), 1.45D));
            default -> (float) (6.5D + 1.5D * Math.pow((percentage - ranges.get(3)) / (ranges.get(4) - ranges.get(3)), 1.75D));
        };
    }

    private static String percent(float value) {
        return String.format("%.2f", value * 100) + "%";
    }

    @Override
    public void onInventoryTick(
            IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder,
            int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide || !(holder instanceof ServerPlayer sp) || world.getGameTime() % 20 != 0){
            return;
        }

        var count = AdvCountEvents.AdvCountService.getCounts(sp);
        double total = count.total();
        float cur = total <= 0.0D ? 0.0F : (float) (count.done() / total);

        ModDataNBT data = tool.getPersistentData();
        if (!data.contains(TAG_ADV_PERCENTAGE) || data.getFloat(TAG_ADV_PERCENTAGE) != cur){
            data.putFloat(TAG_ADV_PERCENTAGE, cur);
        }
    }

    @Override
    public void addTooltip(
            IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player,
            List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        SplendourData splendour = getSplendourData(tool.getPersistentData());
        if (splendour == null){
            return;
        }

        tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.splendour_heart")
                             .append(percent(splendour.percentage()))
                             .withStyle(this.getDisplayName().getStyle()));

        tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.splendour_heart" + rangeToText(splendour.level()))
                             .append(": " + percent(splendour.value()))
                             .withStyle(this.getDisplayName().getStyle()));
    }

    @Override
    public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        SplendourData splendour = getSplendourData(tool.getPersistentData());
        if (splendour == null){
            return amount;
        }

        return (int) (amount / splendour.value());
    }

    @Override
    public void addAttributes(
            IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot,
            BiConsumer<Attribute, AttributeModifier> consumer) {
        SplendourData splendour = getSplendourData(tool.getPersistentData());
        if (tool.isBroken() || splendour == null){
            return;
        }

        consumer.accept(ForgeMod.ENTITY_REACH.get(),
                        new AttributeModifier(
                                UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes()),
                                this.getTranslationKey(),
                                splendour.level(),
                                AttributeModifier.Operation.ADDITION));
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        SplendourData splendour = getSplendourData(context.getPersistentData());
        if (splendour == null){
            return;
        }

        for (IToolStat<?> stat : ToolStats.getAllStats()) {
            if (!stat.supports(context.getItem())){
                continue;
            }

            if (stat instanceof CapacityStat capacityStat){
                capacityStat.multiply(builder, splendour.value());
            }else if (stat instanceof FloatToolStat floatToolStat){
                floatToolStat.multiply(builder, splendour.value());
            }
        }

        if (splendour.level() >= 1){
            Tier tier = builder.getStat(ToolStats.HARVEST_TIER);
            int oldIndex = TierSortingRegistry.getSortedTiers().indexOf(tier);
            int newIndex = Math.min(oldIndex + splendour.level() * 2, TierSortingRegistry.getSortedTiers().size() - 1);
            ToolStats.HARVEST_TIER.update(builder, TierSortingRegistry.getSortedTiers().get(newIndex));
        }
    }

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
        SplendourData splendour = getSplendourData(context.getPersistentData());
        if (splendour == null){
            return;
        }

        volatileData.putBoolean(ModifierEvents.SOULBOUND, true);
        volatileData.putBoolean(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY, true);

        if (splendour.level() <= 1){
            return;
        }

        for (SlotType slotType : SlotType.getAllSlotTypes()) {
            if (slotType != SlotType.ABILITY){
                volatileData.addSlots(slotType, splendour.level());
            }else {
                volatileData.addSlots(slotType, 2 * splendour.level());
            }
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        SplendourData splendour = getSplendourData(tool.getPersistentData());
        if (splendour == null){
            return;
        }

        applySplendourExtraDamage(tool, context, splendour);

        if (context.isExtraAttack()){
            return;
        }

        performSplendourSweep(tool, context);
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        afterMeleeHit(tool, modifier, context, damageAttempted);
    }

    @Override
    public float modifyProjectileHurt(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier,
            Projectile projectile, DamageSource source, @Nullable LivingEntity attacker,
            LivingEntity target, float amount) {
        if (!persistentData.contains(TAG_ADV_PERCENTAGE)){
            return amount;
        }

        if (attacker instanceof ServerPlayer player){
            int kills = player.getStats().getValue(Stats.ENTITY_KILLED.get(target.getType()));
            return amount * (1.0F + kills * 0.01F);
        }

        return amount;
    }

    private void applySplendourExtraDamage(IToolStackView tool, ToolAttackContext context, SplendourData splendour) {
        LivingEntity victim = ETHelper.getLivingTarget(context.getTarget());
        if (victim == null){
            return;
        }

        int depth = EXTRA_ATTACK_DEPTH.get();
        if (depth >= ALLOWED_EXTRA_TIMES){
            return;
        }

        try {
            float damage = ETModifierCheck.getMeleeDamage(context.getAttacker(), context.getTarget(), tool, splendour.level() > 2);
            ResourceKey<DamageType> damageType = damageTypeFor(context, splendour.level());
            DamageSource source = DreamtinkerDamageTypes.source(victim.level().registryAccess(), damageType, null, context.getAttacker());

            victim.invulnerableTime = 0;
            EXTRA_ATTACK_DEPTH.set(depth + 1);

            if (splendour.level() < 2){
                DamageProbe.damageHandler(victim, source, damage * splendour.value());
            }else {
                DamageProbe.finalDamageMethod(victim, source, damage * splendour.value());
            }
        }
        finally {
            EXTRA_ATTACK_DEPTH.set(depth);
        }
    }

    private void performSplendourSweep(IToolStackView tool, ToolAttackContext context) {
        double range = 3 + tool.getModifierLevel(TinkerModifiers.expanded.getId());
        if (range <= 0){
            return;
        }

        double rangeSq = range * range;
        LivingEntity attacker = context.getAttacker();
        Entity target = context.getTarget();
        Level level = attacker.level();

        for (LivingEntity aoeTarget : level.getEntitiesOfClass(
                LivingEntity.class,
                target.getBoundingBox().inflate(range, 0.25D, range))) {
            if (tool.isBroken()){
                break;
            }

            if (aoeTarget != attacker
                && aoeTarget != target
                && !attacker.isAlliedTo(aoeTarget)
                && ToolAttackUtil.isAttackable(attacker, aoeTarget)
                && !(aoeTarget instanceof ArmorStand stand && stand.isMarker())
                && target.distanceToSqr(aoeTarget) < rangeSq){
                ToolAttackUtil.performAttack(tool, context.withAOETarget(aoeTarget));
            }
        }

        level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
    }

    private ResourceKey<DamageType> damageTypeFor(ToolAttackContext context, int level) {
        return switch (level) {
            case 0 -> context.getAttacker() instanceof Player ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK;
            case 1 -> DreamtinkerDamageTypes.arcane_damage;
            case 2 -> dmtRandom(context.getLevel(), context.getLevel().random);
            default -> DreamtinkerDamageTypes.NULL_VOID;
        };
    }

    @NotNull
    private ResourceKey<DamageType> dmtRandom(Level level, RandomSource random) {
        ResourceKey<DamageType> damageType = DreamtinkerDamageTypes.getRandomDamageTypeWithTags(
                level,
                random,
                DamageTypeTags.BYPASSES_ARMOR,
                DamageTypeTags.BYPASSES_ENCHANTMENTS);

        return damageType == null ? DamageTypes.SONIC_BOOM : damageType;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOL_DAMAGE, ModifierHooks.REMOVE,
                            ModifierHooks.TOOLTIP, ModifierHooks.TOOL_STATS, ModifierHooks.ATTRIBUTES, ModifierHooks.VOLATILE_DATA,
                            EsotericismTinkerHook.PROJECTILE_HURT);

        super.registerHooks(hookBuilder);
    }

    private record SplendourData(float percentage, int level, float value) {}
}
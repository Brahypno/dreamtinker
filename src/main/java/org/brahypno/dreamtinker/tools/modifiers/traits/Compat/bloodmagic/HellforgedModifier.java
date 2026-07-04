package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.bloodmagic;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.utils.CompatUtils.bloodmagic.BloodMagicSoulNetworkReflect;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class HellforgedModifier extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, ModifyDamageModifierHook, BreakSpeedModifierHook, BlockBreakModifierHook, TooltipModifierHook {
    private static final ResourceKey<DamageType> BM_SACRIFICE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("bloodmagic", "sacrifice"));
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("blm_hell_forged_modifier"));

    private static final int LP_PER_DAMAGE = 100;

    private static final int EDGE_BASE_LP_CAP = 100;
    private static final int EDGE_LP_CAP_PER_TIER = 100;
    private static final int EDGE_LP_CAP_PER_LEVEL = 50;
    private static final float EDGE_EFFICIENCY_PER_LEVEL = 0.20F;

    private static final float WARD_RATIO_BASE = 0.12F;
    private static final float WARD_RATIO_PER_LEVEL = 0.06F;
    private static final float WARD_RATIO_MAX = 0.48F;
    private static final float WARD_EFFICIENCY_PER_LEVEL = 0.15F;
    private static final int WARD_BASE_LP_CAP = 100;
    private static final int WARD_LP_CAP_PER_TIER = 150;
    private static final int WARD_LP_CAP_PER_LEVEL = 50;

    private static final float MINING_TIER_SPEED_BASE = 0.10F;
    private static final float MINING_TIER_SPEED_PER_TIER = 0.08F;
    private static final float MINING_LP_SPEED_BASE = 0.15F;
    private static final float MINING_LP_SPEED_PER_LEVEL = 0.10F;
    private static final int MINING_BASE_LP_COST = 20;
    private static final int MINING_LP_COST_PER_TIER = 15;
    private static final int MINING_LP_COST_PER_LEVEL = 10;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT,
                            ModifierHooks.MODIFY_HURT, ModifierHooks.BREAK_SPEED, ModifierHooks.BLOCK_BREAK, ModifierHooks.TOOLTIP);
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        Player player = context.getPlayerAttacker();
        if (player == null){
            return damage;
        }

        int tier = BloodMagicSoulNetworkReflect.getOrbTier(player);
        if (tier <= 0){
            return damage;
        }

        return damage + tierPassiveDamage(modifier.getLevel(), tier);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = ETHelper.getLivingTarget(context.getTarget());
        LivingEntity attacker = context.getAttacker();

        if (!(attacker instanceof Player player) || target == null || player.level().isClientSide){
            return;
        }

        if (!target.isAlive() || target.isRemoved() || damageDealt <= 0){
            return;
        }

        int level = modifier.getLevel();
        int tier = BloodMagicSoulNetworkReflect.getOrbTier(player);
        if (tier <= 0){
            return;
        }

        int cost = edgeCost(level, tier);
        if (!BloodMagicSoulNetworkReflect.consumeLP(player, cost)){
            return;
        }

        target.hurt(context.makeDamageSource(), edgeDamage(level, cost));
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (!(context.getEntity() instanceof Player player) || player.level().isClientSide || level <= 0){
            return amount;
        }

        if (amount <= 0 || shouldNotWard(source)){
            return amount;
        }

        int tier = BloodMagicSoulNetworkReflect.getOrbTier(player);
        if (tier <= 0){
            return amount;
        }

        float targetPrevented = amount * wardRatio(level);
        float maxPreventedByTier = wardLpCap(level, tier) / (float) LP_PER_DAMAGE * wardEfficiency(level);
        float prevented = Math.min(targetPrevented, maxPreventedByTier);
        if (prevented <= 0){
            return amount;
        }

        int cost = wardCost(level, prevented);
        if (!BloodMagicSoulNetworkReflect.consumeLP(player, cost)){
            return amount;
        }

        return Math.max(0, amount - prevented);
    }

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, PlayerEvent.BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    }

    @Override
    public float modifyBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeedContext context, float speed) {
        Player player = context.event().getEntity();
        if (player == null || player.level().isClientSide || speed <= 0){
            return speed;
        }

        int tier = BloodMagicSoulNetworkReflect.getOrbTier(player);
        if (tier <= 0){
            return speed;
        }

        int level = modifier.getLevel();
        float multiplier = 1.0F + miningTierSpeed(level, tier);
        if (BloodMagicSoulNetworkReflect.hasLP(player, miningCost(level, tier))){
            multiplier += miningLpSpeed(level);
        }

        return speed * multiplier;
    }

    @Override
    public void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
        Player player = context.getPlayer();
        if (player == null || player.level().isClientSide){
            return;
        }

        int tier = BloodMagicSoulNetworkReflect.getOrbTier(player);
        if (tier <= 0){
            return;
        }

        BloodMagicSoulNetworkReflect.consumeLP(player, miningCost(modifier.getLevel(), tier));
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (!tooltipKey.isShiftOrUnknown()){
            return;
        }

        int level = modifier.getLevel();
        int tier = previewTier(player);

        tooltip.add(Component.translatable("modifier.dreamtinker.blm_hellforged.tooltip.rate", LP_PER_DAMAGE).withStyle(this.getDisplayName().getStyle()));
        tooltip.add(
                Component.translatable("modifier.dreamtinker.blm_hellforged.tooltip.edge", edgeCost(level, tier),
                                       trim(edgeDamage(level, edgeCost(level, tier))))
                         .withStyle(this.getDisplayName().getStyle()));
        tooltip.add(
                Component.translatable("modifier.dreamtinker.blm_hellforged.tooltip.ward", trim(wardRatio(level) * 100.0F),
                                       trim(wardEfficiency(level) * 100.0F))
                         .withStyle(this.getDisplayName().getStyle()));
        tooltip.add(
                Component.translatable("modifier.dreamtinker.blm_hellforged.tooltip.mining",
                                       trim((miningTierSpeed(level, tier) + miningLpSpeed(level)) * 100.0F),
                                       miningCost(level, tier)).withStyle(this.getDisplayName().getStyle()));

        if (player != null){
            int actualTier = BloodMagicSoulNetworkReflect.getOrbTier(player);
            tooltip.add(Component.translatable("modifier.dreamtinker.blm_hellforged.tooltip.tier_damage", actualTier,
                                               trim(actualTier > 0 ? tierPassiveDamage(level, actualTier) : 0)).withStyle(this.getDisplayName().getStyle()));
            tooltip.add(Component.translatable("modifier.dreamtinker.blm_hellforged.tooltip.lp", BloodMagicSoulNetworkReflect.getCurrentLP(player),
                                               BloodMagicSoulNetworkReflect.getMaxLP(player)).withStyle(this.getDisplayName().getStyle()));
        }
    }

    private static int previewTier(@Nullable Player player) {
        return player == null ? 1 : Math.max(1, BloodMagicSoulNetworkReflect.getOrbTier(player));
    }

    private static float tierPassiveDamage(int level, int tier) {
        return Math.max(1, level) * tier;
    }

    private static int edgeCost(int level, int tier) {
        return EDGE_BASE_LP_CAP + EDGE_LP_CAP_PER_TIER * Math.max(1, tier) + EDGE_LP_CAP_PER_LEVEL * Math.max(1, level);
    }

    private static float edgeDamage(int level, int consumedLP) {
        return consumedLP / (float) LP_PER_DAMAGE * edgeEfficiency(level);
    }

    private static float edgeEfficiency(int level) {
        return 1.0F + EDGE_EFFICIENCY_PER_LEVEL * Math.max(1, level);
    }

    private static float wardRatio(int level) {
        return Math.min(WARD_RATIO_MAX, WARD_RATIO_BASE + WARD_RATIO_PER_LEVEL * Math.max(1, level));
    }

    private static int wardLpCap(int level, int tier) {
        return WARD_BASE_LP_CAP + WARD_LP_CAP_PER_TIER * Math.max(1, tier) + WARD_LP_CAP_PER_LEVEL * Math.max(1, level);
    }

    private static int wardCost(int level, float preventedDamage) {
        return Math.max(1, (int) Math.ceil(preventedDamage * LP_PER_DAMAGE / wardEfficiency(level)));
    }

    private static float wardEfficiency(int level) {
        return 1.0F + WARD_EFFICIENCY_PER_LEVEL * Math.max(1, level);
    }

    private static float miningTierSpeed(int level, int tier) {
        return Math.max(1, level) * (MINING_TIER_SPEED_BASE + MINING_TIER_SPEED_PER_TIER * Math.max(1, tier));
    }

    private static float miningLpSpeed(int level) {
        return MINING_LP_SPEED_BASE + MINING_LP_SPEED_PER_LEVEL * Math.max(1, level);
    }

    private static int miningCost(int level, int tier) {
        return MINING_BASE_LP_COST + MINING_LP_COST_PER_TIER * Math.max(1, tier) + MINING_LP_COST_PER_LEVEL * Math.max(1, level);
    }

    private static boolean shouldNotWard(DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.is(BM_SACRIFICE);
    }

    private static String trim(float value) {
        if (value == (int) value){
            return Integer.toString((int) value);
        }

        return String.format(Locale.ROOT, "%.1f", value);
    }
}
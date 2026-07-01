package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.bloodmagic;

import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import org.brahypno.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.brahypno.dreamtinker.utils.CompactUtils.bloodmagic.BloodMagicWillReflect;
import org.brahypno.dreamtinker.utils.CompactUtils.bloodmagic.SentientWillState;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.LeftClickHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BiConsumer;

public class SentientWillModifier extends NoLevelsModifier implements ToolStatsModifierHook, MeleeHitModifierHook, TooltipModifierHook, LeftClickHook, ProcessLootModifierHook, AttributesModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS, ModifierHooks.MELEE_HIT, ModifierHooks.TOOLTIP, EsotericismTinkerHook.LEFT_CLICK,
                            ModifierHooks.PROCESS_LOOT, ModifierHooks.ATTRIBUTES);
    }

    @Override
    public @NotNull Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
        String type = SentientWillState.willType(tool);
        ChatFormatting color = SentientWillState.colorFor(type);
        return entry.getDisplayName().copy().withStyle(color);
    }

    @Override
    public List<Component> getDescriptionList(IToolStackView tool, ModifierEntry entry) {
        String type = SentientWillState.willType(tool);
        ChatFormatting color = SentientWillState.colorFor(type);
        return getDescriptionList(entry.getLevel()).stream().map(component -> (Component) component.copy().withStyle(color)).toList();
    }

    /**
     * BLM 原版 sentient mining tool 不会因为挖掘刷新缓存。
     * <p>
     * 这里同样只在 left click entity 刷新缓存。
     * addToolStats 只读取 persistent data 里已有的 delta。
     */
    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        if (!SentientWillState.hasState(context)){
            return;
        }

        float damage = SentientWillState.damageDelta(context);
        float attackSpeed = SentientWillState.attackSpeedDelta(context);
        float digSpeed = SentientWillState.digSpeedDelta(context);

        if (damage != 0){
            ToolStats.ATTACK_DAMAGE.add(builder, damage);
        }
        if (attackSpeed != 0){
            ToolStats.ATTACK_SPEED.add(builder, attackSpeed);
        }
        if (digSpeed != 0){
            ToolStats.MINING_SPEED.add(builder, digSpeed);
        }
    }

    /**
     * 刷新提前到 left click entity。
     * <p>
     * 这一步对应 BLM 的 onLeftClickEntity/recalculatePowers 语义：
     * - 读取玩家当前 will；
     * - 写入工具 persistent data 的统一 sentient_will compound；
     * - rebuild stats，让 ToolStats 立即吃到新的 damage/attackSpeed/miningSpeed delta。
     * <p>
     * 注意：
     * - 不在 left click block 刷新；
     * - 不在 mining speed 查询刷新；
     * - 不在 after block break 消耗 will。
     */
    @Override
    public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        if (level.isClientSide){
            return;
        }
        SentientWillState.consumeSwingDrain(tool, player);
        if (SentientWillState.refreshFromPlayer(tool, player)){
            updateStack(player.getItemBySlot(equipmentSlot));
        }

    }

    private static final List<MaterialVariantId> SENTIENT_MATERIALS = List.of(
            DreamtinkerMaterialIds.blm_sentient,
            DreamtinkerMaterialIds.blm_sentient_corrosive,
            DreamtinkerMaterialIds.blm_sentient_destructive,
            DreamtinkerMaterialIds.blm_sentient_vengeful,
            DreamtinkerMaterialIds.blm_sentient_steadfast
    );

    public static void updateStack(ItemStack stack) {
        if (stack.isEmpty()){
            return;
        }

        ToolStack tool = ToolStack.from(stack);
        if (!SentientWillState.hasState(tool)){
            return;
        }

        MaterialVariantId target = materialForWill(SentientWillState.willType(tool));
        MaterialNBT mats = tool.getMaterials();
        boolean changed = false;

        for (int i = 0; i < mats.size(); i++) {
            if (isSentientMaterial(mats.get(i).getVariant()) && !mats.get(i).sameVariant(target)){
                mats = mats.replaceMaterial(i, target);
                changed = true;
            }
        }

        if (!changed){
            return;
        }

        tool.setMaterials(mats);
        tool.rebuildStats();
        tool.updateStack(stack);
    }

    private static boolean isSentientMaterial(MaterialVariantId material) {
        for (MaterialVariantId sentient : SENTIENT_MATERIALS) {
            if (material.sameVariant(sentient)){
                return true;
            }
        }
        return false;
    }

    private static MaterialVariantId materialForWill(String type) {
        return switch (type) {
            case "corrosive" -> DreamtinkerMaterialIds.blm_sentient_corrosive;
            case "destructive" -> DreamtinkerMaterialIds.blm_sentient_destructive;
            case "vengeful" -> DreamtinkerMaterialIds.blm_sentient_vengeful;
            case "steadfast" -> DreamtinkerMaterialIds.blm_sentient_steadfast;
            case "default" -> DreamtinkerMaterialIds.blm_sentient;
            default -> DreamtinkerMaterialIds.blm_sentient;
        };
    }

    /**
     * 命中后只处理消耗与 will-type hit effect。
     * <p>
     * 不要在这里刷新 stats；刷新已经在 LeftClickHook#onLeftClickEntity 完成。
     */
    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity attacker = context.getAttacker();
        Entity target = context.getTarget();

        if (!(attacker instanceof Player player)){
            return;
        }

        if (target instanceof LivingEntity livingTarget){
            applyWillEffect(tool, livingTarget, player);
        }
    }

    /**
     * tooltip 只读缓存，不刷新。
     */
    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tooltipKey != TooltipKey.SHIFT){
            return;
        }
        if (!SentientWillState.hasState(tool)){
            tooltip.add(Component.translatable("modifier.dreamtinker.sentient_will.tooltip.unbound").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        String type = SentientWillState.willType(tool);
        int willLevel = SentientWillState.willLevel(tool);
        ChatFormatting color = SentientWillState.colorFor(type);
        Component typeName = Component.translatable("tooltip.bloodmagic.currentBaseType." + type).withStyle(color);

        tooltip.add(Component.translatable("tooltip.bloodmagic.sentientSword.desc").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("tooltip.bloodmagic.sentient.attuned", typeName).withStyle(ChatFormatting.GRAY));


        if (willLevel < 0 || "default".equals(type)){
            tooltip.add(Component.translatable("tooltip.bloodmagic.sentient.inactive").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        appendPlayerWillPool(player, type, willLevel, color, tooltip);
        appendCachedBonuses(tool, type, willLevel, color, tooltip);
    }

    private static void appendPlayerWillPool(@Nullable Player player, String type, int willLevel, ChatFormatting color, List<Component> tooltip) {
        if (player == null || !BloodMagicWillReflect.isLoaded()){
            return;
        }

        Object currentType = BloodMagicWillReflect.getLargestWillType(player);
        String currentTypeName = BloodMagicWillReflect.typeName(currentType).toLowerCase(Locale.ROOT);

        if (!type.equals(currentTypeName)){
            return;
        }

        double will = BloodMagicWillReflect.getTotalDemonWill(currentType, player);
        tooltip.add(Component.translatable("tooltip.bloodmagic.sentient.levelPool", willLevel + 1, formatNumber(will)).withStyle(color));
    }

    private static void appendCachedBonuses(IToolStackView tool, String type, int willLevel, ChatFormatting color, List<Component> tooltip) {
        float damage = SentientWillState.damageDelta(tool);
        if (damage > 0){
            tooltip.add(Component.translatable("tooltip.bloodmagic.sentient.bonusDamage", formatSigned(damage)).withStyle(ChatFormatting.GRAY));
        }

        appendTypedRider(tool, type, willLevel, color, tooltip);

        float digSpeed = SentientWillState.digSpeedDelta(tool);
        if (digSpeed > 0){
            tooltip.add(Component.translatable("tooltip.bloodmagic.sentient.rider.digspeed", formatSigned(digSpeed)).withStyle(ChatFormatting.GRAY));
        }
    }

    private static void appendTypedRider(IToolStackView tool, String type, int willLevel, ChatFormatting color, List<Component> tooltip) {
        switch (type) {
            case "corrosive" -> tooltip.add(Component.translatable(
                    "tooltip.bloodmagic.sentient.rider.corrosive",
                    formatNumber(SentientWillState.corrosiveTime(willLevel) / 20.0),
                    SentientWillState.corrosiveLevel(willLevel) + 1
            ).withStyle(color));
            case "steadfast" -> tooltip.add(Component.translatable(
                    "tooltip.bloodmagic.sentient.rider.steadfast",
                    formatNumber(SentientWillState.absorptionTime(willLevel) / 20.0)
            ).withStyle(color));
            case "vengeful" -> {
                float movement = SentientWillState.movementSpeedDelta(tool);
                if (movement > 0){
                    tooltip.add(Component.translatable("tooltip.bloodmagic.sentient.rider.vengeful", formatSigned(movement)).withStyle(color));
                }
            }
            default -> {}
        }
    }

    private static String formatNumber(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)){
            return String.format(Locale.ROOT, "%d", (long) value);
        }

        String string = String.format(Locale.ROOT, "%.2f", value);
        if (string.contains(".")){
            string = string.replaceAll("0+$", "");
            if (string.endsWith(".")){
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    private static String formatSigned(double value) {
        return (value >= 0 ? "+" : "") + formatNumber(value);
    }


    private static void applyWillEffect(IToolStackView tool, LivingEntity target, Player attacker) {
        if (!SentientWillState.hasState(tool)){
            return;
        }

        String type = SentientWillState.willType(tool);
        int level = SentientWillState.willLevel(tool);

        if (level < 0){
            return;
        }

        switch (type) {
            case "corrosive" -> applyCorrosive(level, target);
            case "steadfast" -> applySteadfast(level, target, attacker);
            case "destructive", "vengeful", "default" -> {
            }
            default -> {
            }
        }
    }

    private static void applyCorrosive(int level, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, SentientWillState.corrosiveTime(level), SentientWillState.corrosiveLevel(level)));
    }

    private static void applySteadfast(int level, LivingEntity target, Player attacker) {
        if (target.isAlive()){
            return;
        }

        float absorption = attacker.getAbsorptionAmount();
        float added = target.getMaxHealth() * 0.05f;
        float capped = Math.min(absorption + added, SentientWillState.maxAbsorptionHearts());

        attacker.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, SentientWillState.absorptionTime(level), 127, false, false));
        attacker.setAbsorptionAmount(capped);
    }

    @Override
    public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity killed)){
            return;
        }

        Entity killerEntity = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (!(killerEntity instanceof LivingEntity attacker)){
            return;
        }

        if (!(killed.level() instanceof ServerLevel)){
            return;
        }

        if (killed.level().getDifficulty() != Difficulty.PEACEFUL && !(killed instanceof Enemy)){
            return;
        }

        if (!SentientWillState.hasState(tool)){
            return;
        }

        String willType = SentientWillState.willType(tool);
        double soulDrop = SentientWillState.soulDrop(tool);
        double staticDrop = SentientWillState.staticDrop(tool);

        if (soulDrop <= 0 && staticDrop <= 0){
            return;
        }

        int looting = Math.max(0, context.getLootingModifier());
        RandomSource random = context.getRandom();
        double slimeModifier = killed instanceof Slime ? 0.67D : 1.0D;

        for (int i = 0; i <= looting; i++) {
            if (i != 0 && random.nextDouble() >= 0.4D){
                continue;
            }

            double amount = slimeModifier
                            * (soulDrop * random.nextDouble() + staticDrop)
                            * killed.getMaxHealth()
                            / 20.0D;

            if (amount <= 0){
                continue;
            }

            ItemStack willStack = BloodMagicWillReflect.createWillStack(willType, amount);
            BloodMagicWillReflect.addWillToEntityGemsOrLoot(attacker, willStack, generatedLoot);
        }
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!SentientWillState.hasState(tool)){
            return;
        }

        int level = modifier.getLevel();
        float movement = SentientWillState.movementSpeedDelta(tool) * level;
        float health = SentientWillState.healthBonusDelta(tool) * level;

        if (movement != 0){
            consumer.accept(Attributes.MOVEMENT_SPEED,
                            new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + Attributes.MOVEMENT_SPEED.getDescriptionId()).getBytes()),
                                                  "Sentient will movement speed", movement, AttributeModifier.Operation.ADDITION));
        }
        if (health != 0){
            consumer.accept(Attributes.MAX_HEALTH,
                            new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + Attributes.MAX_HEALTH.getDescriptionId()).getBytes()),
                                                  "Sentient will health bonus", health, AttributeModifier.Operation.ADDITION));
        }
    }
}
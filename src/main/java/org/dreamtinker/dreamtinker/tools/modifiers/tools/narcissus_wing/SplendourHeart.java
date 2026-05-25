package org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.TierSortingRegistry;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.tools.modifiers.events.AdvCountEvents;
import org.dreamtinker.dreamtinker.utils.DTDamageUtils;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
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
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.stat.*;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.TheSplendourHeart;

public class SplendourHeart extends Modifier implements MeleeHitModifierHook, InventoryTickModifierHook, ToolDamageModifierHook, ModifierRemovalHook, TooltipModifierHook, ToolStatsModifierHook, AttributesModifierHook, VolatileDataModifierHook {
    //According to PS5 achievement, 83 bronze, 36 silver 14 gold, 1 pla=>83 easy, 36 normal, 15 hard
    //According to myself, tinker`s construct 3 have 21 easy, 13 normal, 3 hard achievements.
    //In total 104 easy, 49 normal 18 hard=>60.8% easy, 28.6% normal,10.6% hard. so that
    // 2 choice, number or percentage, I choose percentage. <25% negative 25-45% normal 45%-65% high 65-80% super 81%-100% excellent
    private final ResourceLocation TAG_ADV_PERCENTAGE = Dreamtinker.getLocation("adv_percentage");

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_ADV_PERCENTAGE);
        return null;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && holder instanceof ServerPlayer sp && world.getGameTime() % 20 == 0){
            var count = AdvCountEvents.AdvCountService.getCounts(sp);
            float old = tool.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
            float cur = (float) count.done() / count.total();
            if (cur != old){
                tool.getPersistentData().putFloat(TAG_ADV_PERCENTAGE, (float) count.done() / count.total());
            }
        }
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool.getPersistentData().contains(TAG_ADV_PERCENTAGE)){
            float per = tool.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
            tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.splendour_heart").append(String.format("%.2f", per * 100) + "%")
                                 .withStyle(this.getDisplayName().getStyle()));
            tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.splendour_heart" + rangeToText(per))
                                 .append(": " + String.format("%.2f", rangeToValue(per) * 100) + "%")
                                 .withStyle(this.getDisplayName().getStyle()));
        }
    }

    @Override
    public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        float per = tool.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
        float value = rangeToValue(per);
        return (int) (amount / value);
    }

    private static final int allowed_extra_times = 1;
    private static final ThreadLocal<Integer> extra_attack_depth = ThreadLocal.withInitial(() -> 0);

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken()){
            float per = tool.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
            int level = java.util.Arrays.binarySearch(TheSplendourHeart.get().toArray(), (double) Math.nextUp(per));
            level = level <= 0 ? -(level) - 1 : level;
            consumer.accept(ForgeMod.ENTITY_REACH.get(),
                            new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes()),
                                                  this.getTranslationKey(),
                                                  level,
                                                  AttributeModifier.Operation.ADDITION));
        }

    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        afterMeleeHit(tool, modifier, context, damageAttempted);
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        if (context.getPersistentData().contains(TAG_ADV_PERCENTAGE)){
            float per = context.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
            float value = rangeToValue(per);
            int level = java.util.Arrays.binarySearch(TheSplendourHeart.get().toArray(), (double) Math.nextUp(per));
            level = level <= 0 ? -(level) - 1 : level;
            for (IToolStat<?> e : ToolStats.getAllStats()) {
                if (e.supports(context.getItem())){
                    if (e instanceof CapacityStat cps){
                        cps.multiply(builder, 1 + value);
                    }else if (e instanceof FloatToolStat fts){
                        fts.multiply(builder, value);
                    }
                }
            }
            if (1 <= level){
                Tier tier = builder.getStat(ToolStats.HARVEST_TIER);
                int idx = Math.min(TierSortingRegistry.getSortedTiers().indexOf(tier) + level * 2, TierSortingRegistry.getSortedTiers().size() - 1);
                Tier expected = TierSortingRegistry.getSortedTiers().get(idx);
                ToolStats.HARVEST_TIER.update(builder, expected);
            }
        }
    }

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
        if (context.getPersistentData().contains(TAG_ADV_PERCENTAGE)){
            float per = context.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
            int level = java.util.Arrays.binarySearch(TheSplendourHeart.get().toArray(), (double) Math.nextUp(per));
            level = level < 0 ? -level - 1 : level;
            if (1 < level){
                for (SlotType st : SlotType.getAllSlotTypes())
                    if (st != SlotType.ABILITY)
                        volatileData.addSlots(st, (int) Math.pow(level, 2));
                    else
                        volatileData.addSlots(st, (int) Math.pow(level, 1));
            }
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (tool.getPersistentData().contains(TAG_ADV_PERCENTAGE)){
            float per = tool.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
            int level = java.util.Arrays.binarySearch(TheSplendourHeart.get().toArray(), (double) Math.nextUp(per));
            level = level < 0 ? -level - 1 : level;
            LivingEntity victim = DTHelper.getLivingTarget(context.getTarget());
            float boost = rangeToValue(per);
            if (null != victim){
                int depth = extra_attack_depth.get();
                if (depth < allowed_extra_times){
                    try {
                        float damage = DTModifierCheck.getMeleeDamage(context.getAttacker(), context.getTarget(), tool, 2 < level);
                        ResourceKey<DamageType> dmt =
                                0 == level ? context.getAttacker() instanceof Player ? DamageTypes.PLAYER_ATTACK : DamageTypes.MOB_ATTACK :
                                1 == level ? DreamtinkerDamageTypes.arcane_damage :
                                2 == level ? DamageTypes.SONIC_BOOM : DreamtinkerDamageTypes.NULL_VOID;
                        int inv = victim.invulnerableTime;
                        victim.invulnerableTime = 0;
                        extra_attack_depth.set(depth + 1);
                        DTDamageUtils.damageHandler(victim, DreamtinkerDamageTypes.source(victim.level().registryAccess(), dmt, null, context.getAttacker()),
                                                    damage * (boost + 1));
                        victim.invulnerableTime = inv;
                    }
                    finally {
                        extra_attack_depth.set(depth);
                    }
                }
            }
        }
    }

    private String rangeToText(float d) {
        int level = java.util.Arrays.binarySearch(TheSplendourHeart.get().toArray(), (double) Math.nextUp(d));
        level = level <= 0 ? -(level) - 1 : level;

        switch (level) {
            case 0 -> {return "_entry";}
            case 1 -> {return "_easy";}
            case 2 -> {return "_normal";}
            case 3 -> {return "_high";}
            default -> {return "_excellent";}
        }
    }

    private float rangeToValue(float d) {
        int level = java.util.Arrays.binarySearch(TheSplendourHeart.get().toArray(), (double) Math.nextUp(d));
        level = level < 0 ? -level - 1 : level;
        switch (level) {
            case 0 -> {return (float) ((d - TheSplendourHeart.get().get(0)) / TheSplendourHeart.get().get(0) / 2) + 1;}//map to -50%-0
            case 1 -> {
                return (float) (d - (TheSplendourHeart.get().get(0)) / (TheSplendourHeart.get().get(1) - TheSplendourHeart.get().get(0)) + 1);
            }//map to 0-100%
            case 2 -> {
                return (float) (3.0D + 1.5D * Math.pow((d - TheSplendourHeart.get().get(1)) /
                                                       (TheSplendourHeart.get().get(2) - TheSplendourHeart.get().get(1)), 1.25D));
            } // map to 200%-350%

            case 3 -> {
                return (float) (4.5D + 2.0D * Math.pow((d - TheSplendourHeart.get().get(2)) /
                                                       (TheSplendourHeart.get().get(3) - TheSplendourHeart.get().get(2)), 1.45D));
            } // map to 350%-550%
            default -> {
                return (float) (6.5D + 1.5D * Math.pow((d - TheSplendourHeart.get().get(3)) /
                                                       (TheSplendourHeart.get().get(4) - TheSplendourHeart.get().get(3)), 1.75D));
            } // map to 550%-700%
            //highest tier, should show respect to that do such lots of advancements-----and this is not enough
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOL_DAMAGE,
                            ModifierHooks.REMOVE, ModifierHooks.TOOLTIP, ModifierHooks.TOOL_STATS, ModifierHooks.ATTRIBUTES, ModifierHooks.VOLATILE_DATA);
        super.registerHooks(hookBuilder);
    }
}

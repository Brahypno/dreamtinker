package org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
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
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.modifiers.events.AdvCountEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.TinkerAttributes;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.TheSplendourHeart;

public class SplendourHeart extends BattleModifier {
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
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(TAG_ADV_PERCENTAGE);
        return null;
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
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
    public int modifierDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        float per = tool.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
        float value = rangeToValue(per);
        return (int) (amount/value);
    }
    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken()){
            float per = tool.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
            int level = java.util.Arrays.binarySearch(TheSplendourHeart.get().toArray(), (double) Math.nextUp(per));
            level = level <= 0 ? -(level) - 1 : level;
            consumer.accept(ForgeMod.ENTITY_REACH.get(),
                    new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes()),
                            ForgeMod.ENTITY_REACH.get().getDescriptionId(),
                            level*2,
                            AttributeModifier.Operation.ADDITION));
        }

    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        if (context.getPersistentData().contains(TAG_ADV_PERCENTAGE)){
            float per = context.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
            float value = rangeToValue(per);
            int level = java.util.Arrays.binarySearch(TheSplendourHeart.get().toArray(), (double) Math.nextUp(per));
            level = level <= 0 ? -(level) - 1 : level;

            ToolStats.DURABILITY.multiply(builder, value);
            ToolStats.DRAW_SPEED.multiply(builder, value);
            ToolStats.PROJECTILE_DAMAGE.multiply(builder, value);
            ToolStats.VELOCITY.multiply(builder, value);
            ToolStats.ATTACK_DAMAGE.multiply(builder, value);
            ToolStats.ATTACK_SPEED.multiply(builder, value);

            if (1 <= level){
                Tier tier = builder.getStat(ToolStats.HARVEST_TIER);
                int idx = Math.min(TierSortingRegistry.getSortedTiers().indexOf(tier) + level, TierSortingRegistry.getSortedTiers().size() - 1);
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
            level = level <= 0 ? -(level) - 1 : level;
            if (1 < level){
                volatileData.addSlots(SlotType.UPGRADE, (int) Math.pow(level, 2));
                volatileData.addSlots(SlotType.SOUL, (int) Math.pow(level, 2));
            }
            if (2 < level){
                volatileData.addSlots(SlotType.ABILITY, (int) Math.pow(level, 1));
                volatileData.addSlots(SlotType.DEFENSE, (int) Math.pow(level, 1));
            }
        }
    }
    private static boolean in_progress = false;
    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (tool.getPersistentData().contains(TAG_ADV_PERCENTAGE)){
            float per = tool.getPersistentData().getFloat(TAG_ADV_PERCENTAGE);
            int level = java.util.Arrays.binarySearch(TheSplendourHeart.get().toArray(), (double) Math.nextUp(per));
            level = level <= 0 ? -(level) - 1 : level;
            if (null != context.getLivingTarget()&&1 <= level&&!in_progress){
                    in_progress=true;
                    float baseDamage = context.getBaseDamage();
                    float damage = baseDamage;
                    List<ModifierEntry> modifiers = tool.getModifierList();
                    for (ModifierEntry entry : modifiers) {
                        damage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, entry, context, baseDamage, damage);
                    }
                    context.getLivingTarget().setInvulnerable(false);
                    int invu=context.getLivingTarget().invulnerableTime;
                    context.getLivingTarget().invulnerableTime = 0;
                    ResourceKey<DamageType> dmt=1==level?context.getAttacker() instanceof Player?DamageTypes.PLAYER_ATTACK:DamageTypes.MOB_ATTACK:2==level?DreamtinkerDamageTypes.rain_bow:3==level?DamageTypes.SONIC_BOOM:DreamtinkerDamageTypes.NULL_VOID;
                    DamageSource dam = DreamtinkerDamageTypes.source(context.getAttacker().level().registryAccess(), dmt,null, context.getAttacker());
                    context.getLivingTarget().hurt(dam, damage);
                    context.getLivingTarget().invulnerableTime= (int) (invu/rangeToValue(per));
                    in_progress=false;
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
        level = level <= 0 ? -(level) - 1 : level;
        switch (level) {
            case 0 -> {return (float) ((d - TheSplendourHeart.get().get(0)) / TheSplendourHeart.get().get(0)) + 1;}//map to -100%-0
            case 1 -> {
                return (float) ((TheSplendourHeart.get().get(1) - d) / (TheSplendourHeart.get().get(1) - TheSplendourHeart.get().get(0)) + 1);
            }//map to 0-100%
            case 2 -> {
                return 1 + 2 * (float) ((TheSplendourHeart.get().get(2) - d) / (TheSplendourHeart.get().get(2) - TheSplendourHeart.get().get(1)) + 1);
            }//map to 100%-
            case 3 -> {return (float) (d - TheSplendourHeart.get().get(2)) * 100 + 1;}
            default -> {return d * 100 + 1.0f;}//highest tier, should show respect to that do such lots of advancements-----and this is not enough
        }
    }
}

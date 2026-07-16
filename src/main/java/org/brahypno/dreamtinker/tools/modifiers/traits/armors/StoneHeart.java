package org.brahypno.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.StoneHeartProjReduce;

public class StoneHeart extends Modifier
        implements ModifyDamageModifierHook,
        DamageBlockModifierHook,
        InventoryTickModifierHook,
        TooltipModifierHook {
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("stone_heart"));

    private static final int EATING_FOOD_PENALTY = 2;
    private static final float EATING_SATURATION_PENALTY = 2.0F;
    private static final float EXHAUSTION_PER_SECOND = 4.0F;
    private static final float HEALING_MULTIPLIER = 0.2F;
    private static final float MIN_PROJECTILE_DAMAGE_MULTIPLIER = 0.05F;

    public StoneHeart() {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingHeal);
        MinecraftForge.EVENT_BUS.addListener(this::onEat);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(
                this,
                ModifierHooks.MODIFY_HURT,
                ModifierHooks.DAMAGE_BLOCK,
                ModifierHooks.INVENTORY_TICK,
                ModifierHooks.TOOLTIP
        );
        super.registerHooks(hookBuilder);
    }

    private void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (ETModifierCheck.haveModifierIn(entity, this.getId())){
            event.setAmount(event.getAmount() * HEALING_MULTIPLIER);
        }
    }

    private void onEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)
            || player.level().isClientSide
            || !event.getItem().isEdible()
            || !ETModifierCheck.haveModifierIn(player, this.getId())){
            return;
        }

        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(Math.max(0, foodData.getFoodLevel() - EATING_FOOD_PENALTY));
        foodData.setSaturation(Math.min(
                foodData.getFoodLevel(),
                Math.max(0.0F, foodData.getSaturationLevel() - EATING_SATURATION_PENALTY)
        ));
    }

    @Override
    public boolean isDamageBlocked(
            IToolStackView tool,
            ModifierEntry modifier,
            EquipmentContext context,
            EquipmentSlot slotType,
            DamageSource source,
            float amount
    ) {
        if (source.getEntity() instanceof LivingEntity){
            return false;
        }

        return source.is(DamageTypes.DROWN)
               || source.is(DamageTypes.FALL)
               || source.is(DamageTypes.IN_FIRE)
               || source.is(DamageTypes.ON_FIRE)
               || source.is(DamageTypes.STARVE);
    }

    @Override
    public float modifyDamageTaken(
            IToolStackView tool,
            ModifierEntry modifier,
            EquipmentContext context,
            EquipmentSlot slotType,
            DamageSource source,
            float amount,
            boolean isDirectDamage
    ) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (level <= 0){
            return amount;
        }

        LivingEntity holder = context.getEntity();
        if (!holder.level().isClientSide && holder instanceof ServerPlayer player){
            restoreFoodFromDamage(player, amount);
        }

        if (isProjectileDamage(source)){
            float multiplier = (float) Math.max(
                    MIN_PROJECTILE_DAMAGE_MULTIPLIER,
                    1.0D - StoneHeartProjReduce.get() * level
            );
            amount *= multiplier;
        }

        return amount;
    }

    private static boolean isProjectileDamage(DamageSource source) {
        return source.is(DamageTypes.ARROW)
               || source.is(DamageTypes.FIREWORKS)
               || source.is(DamageTypes.FIREBALL)
               || source.is(TinkerTags.DamageTypes.PROJECTILE_PROTECTION)
               || source.getEntity() instanceof Projectile
               || source.getDirectEntity() instanceof Projectile;
    }

    private static void restoreFoodFromDamage(ServerPlayer player, float amount) {
        if (amount <= 0.0F){
            return;
        }

        FoodData foodData = player.getFoodData();
        int oldFood = foodData.getFoodLevel();
        float remaining = amount;

        int foodGain = Math.min(20 - oldFood, Math.max(0, (int) Math.floor(remaining)));
        if (foodGain > 0){
            foodData.setFoodLevel(oldFood + foodGain);
            remaining -= foodGain;
        }

        if (remaining > 0.0F){
            foodData.setSaturation(Math.min(
                    foodData.getFoodLevel(),
                    foodData.getSaturationLevel() + remaining
            ));
        }
    }

    @Override
    public void onInventoryTick(
            IToolStackView tool,
            ModifierEntry modifier,
            Level world,
            LivingEntity holder,
            int itemSlot,
            boolean isSelected,
            boolean isCorrectSlot,
            ItemStack stack
    ) {
        if ((isCorrectSlot || isSelected)
            && !world.isClientSide
            && world.getGameTime() % 20 == 0
            && holder instanceof ServerPlayer player){
            player.causeFoodExhaustion(EXHAUSTION_PER_SECOND);
        }
    }

    @Override
    public void addTooltip(
            IToolStackView tool,
            @NotNull ModifierEntry modifier,
            @Nullable Player player,
            List<Component> tooltip,
            TooltipKey tooltipKey,
            TooltipFlag tooltipFlag
    ) {
        if (!tooltipKey.isShiftOrUnknown()){
            return;
        }

        int level = modifier.getLevel();
        float damageMultiplier = (float) Math.max(
                MIN_PROJECTILE_DAMAGE_MULTIPLIER,
                1.0D - StoneHeartProjReduce.get() * level
        );
        float reductionPercent = (1.0F - damageMultiplier) * 100.0F;

        tooltip.add(Component.translatable(
                                     "modifier.dreamtinker.stone_heart.tooltip.projectile_reduction",
                                     String.format(Locale.ROOT, "%.1f", reductionPercent)
                             )
                             .withStyle(ChatFormatting.GREEN));

        tooltip.add(Component.translatable(
                                     "modifier.dreamtinker.stone_heart.tooltip.healing",
                                     Math.round(HEALING_MULTIPLIER * 100.0F)
                             )
                             .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable(
                                     "modifier.dreamtinker.stone_heart.tooltip.exhaustion",
                                     String.format(Locale.ROOT, "%.1f", EXHAUSTION_PER_SECOND)
                             )
                             .withStyle(ChatFormatting.RED));

        tooltip.add(Component.translatable(
                                     "modifier.dreamtinker.stone_heart.tooltip.eating_penalty",
                                     EATING_FOOD_PENALTY,
                                     String.format(Locale.ROOT, "%.1f", EATING_SATURATION_PENALTY)
                             )
                             .withStyle(ChatFormatting.GOLD));
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(
                Component.translatable(this.getTranslationKey() + ".flavor")
                         .withStyle(ChatFormatting.ITALIC),
                Component.translatable(
                                 this.getTranslationKey() + ".description",
                                 StoneHeartProjReduce.get() * 100
                         )
                         .withStyle(ChatFormatting.GRAY)
        );
    }
}

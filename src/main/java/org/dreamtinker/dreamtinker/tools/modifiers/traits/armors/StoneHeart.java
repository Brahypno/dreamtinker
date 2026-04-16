package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.StoneHeartProjReduce;

public class StoneHeart extends ArmorModifier {

    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("stone_heart"));

    {
        MinecraftForge.EVENT_BUS.addListener(this::LivingHealEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onEat);
    }

    private void LivingHealEvent(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (DTModifierCheck.haveModifierIn(entity, this.getId()))
            event.setAmount(event.getAmount() * 0.2F);
    }

    public void onEat(LivingEntityUseItemEvent.Finish e) {
        if (!(e.getEntity() instanceof Player p) || p.level().isClientSide || !DTModifierCheck.haveModifierIn(p, this.getId()))
            return;
        var fd = p.getFoodData();
        if (!fd.needsFood())
            return;
        fd.setFoodLevel(Math.min(20, Math.max(0, fd.getFoodLevel() - 2)));
        fd.setSaturation(Math.min(fd.getFoodLevel(), Math.max(0, fd.getSaturationLevel() - 2f)));
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        LivingEntity holder = context.getEntity();
        Level level = holder.level();
        if (!level.isClientSide() && !(source.getEntity() instanceof LivingEntity))
            return source.is(DamageTypes.DROWN) || source.is(DamageTypes.FALL) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) ||
                   source.is(DamageTypes.STARVE);

        return false;
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level)
            if (context.getEntity().level().isClientSide){
                if (context.getEntity() instanceof ServerPlayer player){
                    FoodData fd = player.getFoodData();
                    int newFood = (int) Math.min(20, amount + fd.getFoodLevel());
                    fd.setFoodLevel(newFood);
                    if (20 < amount + fd.getFoodLevel()){
                        float newSat = Math.min(fd.getSaturationLevel() + amount + fd.getFoodLevel() - 20, 20);
                        fd.setSaturation(newSat);
                    }
                }
                if (source.is(DamageTypes.ARROW) || source.is(DamageTypes.FIREWORKS) || source.is(DamageTypes.FIREBALL) ||
                    source.is(TinkerTags.DamageTypes.PROJECTILE_PROTECTION) ||
                    source.getEntity() instanceof Projectile || source.getDirectEntity() instanceof Projectile)
                    amount *= (float) Math.max(0.05f, 1 - StoneHeartProjReduce.get() * level);
            }
        return amount;
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if ((isCorrectSlot || isSelected) && !world.isClientSide && world.getGameTime() % 20 == 0)
            if (holder instanceof ServerPlayer player)
                player.causeFoodExhaustion(4 * 1.0F);

    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(this.getTranslationKey() + ".description", StoneHeartProjReduce.get() * 100)
                                      .withStyle(ChatFormatting.GRAY));
    }
}

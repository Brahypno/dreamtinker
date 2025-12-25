package org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.TinkerAttributes;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.*;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.AsOneA;

public class WeaponTransformation extends BattleModifier {
    @Override
    public int getPriority() {
        return -1000;
    }

    @Override
    public void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (tool.hasTag(Tags.Items.ARMORS_LEGGINGS)){
            float armor = tool.getStats().get(ToolStats.ARMOR);
            float toughness = tool.getStats().get(ToolStats.ARMOR_TOUGHNESS);
            float multi = valueExpSoftCap(armor, toughness);
            tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.weapon_transformation.legs").append(String.format("%.2f", multi * 100) + "%")
                                 .withStyle(this.getDisplayName().getStyle()));
        }
    }


    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (tool.isBroken())
            return;
        float armor = tool.getStats().get(ToolStats.ARMOR);
        float toughness = tool.getStats().get(ToolStats.ARMOR_TOUGHNESS);
        float multi = valueExpSoftCap(armor, toughness);
        UUID uuid = UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes());
        if (modifier.getLevel() > 0){
            switch (slot) {
                case CHEST -> {
                    consumer.accept(Attributes.LUCK,
                                    new AttributeModifier(uuid,
                                                          Attributes.LUCK.getDescriptionId(),
                                                          multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));

                    consumer.accept(TinkerAttributes.BAD_EFFECT_DURATION.get(),
                                    new AttributeModifier(uuid,
                                                          TinkerAttributes.BAD_EFFECT_DURATION.get().getDescriptionId(),
                                                          Math.min(0.4, multi / 4),
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(Attributes.ATTACK_SPEED,
                                    new AttributeModifier(uuid,
                                                          Attributes.ATTACK_SPEED.getDescriptionId(),
                                                          multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));

                }

                case LEGS -> {//see hurt event
                    consumer.accept(TinkerAttributes.GOOD_EFFECT_DURATION.get(),
                                    new AttributeModifier(uuid,
                                                          TinkerAttributes.BAD_EFFECT_DURATION.get().getDescriptionId(),
                                                          -Math.min(0.35, multi / 4),
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(TinkerAttributes.EXPERIENCE_MULTIPLIER.get(),
                                    new AttributeModifier(uuid,
                                                          TinkerAttributes.EXPERIENCE_MULTIPLIER.get().getDescriptionId(),
                                                          multi * 4,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case FEET -> {
                    consumer.accept(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(uuid,
                                                          Attributes.ATTACK_DAMAGE.getDescriptionId(),
                                                          multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));

                    consumer.accept(TinkerAttributes.MINING_SPEED_MULTIPLIER.get(),
                                    new AttributeModifier(uuid,
                                                          TinkerAttributes.MINING_SPEED_MULTIPLIER.get().getDescriptionId(),
                                                          multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(Attributes.MOVEMENT_SPEED,
                                    new AttributeModifier(uuid,
                                                          Attributes.MOVEMENT_SPEED.getDescriptionId(),
                                                          -Math.min(multi, 0.15),
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case HEAD -> {
                    consumer.accept(Attributes.MAX_HEALTH,
                                    new AttributeModifier(uuid,
                                                          Attributes.MAX_HEALTH.getDescriptionId(),
                                                          multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(Attributes.KNOCKBACK_RESISTANCE,
                                    new AttributeModifier(uuid,
                                                          Attributes.KNOCKBACK_RESISTANCE.getDescriptionId(),
                                                          -multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(ForgeMod.ENTITY_GRAVITY.get(),
                                    new AttributeModifier(uuid,
                                                          ForgeMod.ENTITY_GRAVITY.get().getDescriptionId(),
                                                          Math.min(.25f, multi),
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                default -> {}
            }
        }
    }

    @Override
    public void modifierOnInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack) {
        if (tool.isBroken())
            return;
        if (holder instanceof Player player && (isCorrectSlot || isSelected) && stack.is(Tags.Items.ARMORS_HELMETS) && stack.is(TinkerTags.Items.HELMETS)){
            if (player.getEffect(MobEffects.NIGHT_VISION) == null
                || Objects.requireNonNull(player.getEffect(MobEffects.NIGHT_VISION)).getDuration() <= 20 * 11){
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 21, AsOneA.get(), false, false));
            }
        }
    }

    public static float valueExpSoftCap(float armor, float toughness) {
        float U = Math.max(0f, UnderPlateBoostArmorFactor.get().floatValue() * armor +
                               UnderPlateBoostToughnessFactor.get().floatValue() * toughness); // 或自定义权重
        float K1 = 20f, p1 = 0.08f;  // 早期
        float K2 = 400f, p2 = 0.17f; // 中后期
        double g1 = Math.pow(1.0 + U / K1, -p1);
        double g2 = Math.pow(1.0 + U / K2, -p2);
        return (float) (UnderPlateBoostMax.get().floatValue() * (1.0 - g1 * g2)); // 返回“倍数”，乘100%即百分比
    }
}

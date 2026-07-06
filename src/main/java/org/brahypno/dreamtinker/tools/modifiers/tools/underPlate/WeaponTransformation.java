package org.brahypno.dreamtinker.tools.modifiers.tools.underPlate;

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
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.*;
import static org.brahypno.dreamtinker.config.DreamtinkerConfig.AsOneA;

public class WeaponTransformation extends Modifier implements InventoryTickModifierHook, TooltipModifierHook, AttributesModifierHook {
    @Override
    public int getPriority() {
        return -1000;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOLTIP, ModifierHooks.ATTRIBUTES);
        hookBuilder.addModule(
                new ModifierTraitModule(ModifierIds.thorns, 1, true, ModifierCondition.ANY_CONTEXT.with(ToolContextPredicate.tag(TinkerTags.Items.HELMETS))));
        super.registerHooks(hookBuilder);
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
        if (modifier.getLevel() > 0){
            switch (slot) {
                case CHEST -> {
                    Attribute attribute = Attributes.LUCK;
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            multi,
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));

                    attribute = TinkerAttributes.BAD_EFFECT_DURATION.get();
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            Math.min(0.4, multi / 4),
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                    attribute = Attributes.ATTACK_SPEED;
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            multi,
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));

                }

                case LEGS -> {//see hurt event
                    Attribute attribute = TinkerAttributes.GOOD_EFFECT_DURATION.get();
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            -Math.min(0.35, multi / 4),
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                    attribute = TinkerAttributes.EXPERIENCE_MULTIPLIER.get();
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            multi * 4,
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case FEET -> {
                    Attribute attribute = Attributes.ATTACK_DAMAGE;
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            multi,
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));

                    attribute = TinkerAttributes.MINING_SPEED_MULTIPLIER.get();
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            multi,
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                    attribute = Attributes.MOVEMENT_SPEED;
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            -Math.min(multi, 0.15),
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case HEAD -> {
                    Attribute attribute = Attributes.MAX_HEALTH;
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            multi,
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                    attribute = Attributes.KNOCKBACK_RESISTANCE;
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            -Math.min(.50f, multi),
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                    attribute = ForgeMod.ENTITY_GRAVITY.get();
                    consumer.accept(attribute,
                                    new AttributeModifier(
                                            UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                            this.getTranslationKey(),
                                            Math.min(.25f, multi),
                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                default -> {}
            }
        }
    }

    @Override
    public void onInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack) {
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

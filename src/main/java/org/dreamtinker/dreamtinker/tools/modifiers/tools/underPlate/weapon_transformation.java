package org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.AsOneA;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.UnderPlateBoostMutiply;

public class weapon_transformation extends BattleModifier {
    private final String tool_attribute_uuid = "facdf7e8-4b20-4e2d-9aba-5c1b408e7c9d";

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        float armor = tool.getStats().get(ToolStats.ARMOR);
        float toughness = tool.getStats().get(ToolStats.ARMOR_TOUGHNESS);
        float muti = armor * toughness * UnderPlateBoostMutiply.get().floatValue();
        if (modifier.getLevel() > 0){
            switch (slot) {
                case CHEST -> {
                    consumer.accept(Attributes.LUCK,
                                    new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                          Attributes.LUCK.getDescriptionId(),
                                                          muti,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case LEGS -> {//see hurt event
                }
                case FEET -> {
                    consumer.accept(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                          Attributes.ATTACK_DAMAGE.getDescriptionId(),
                                                          muti,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(Attributes.ATTACK_SPEED,
                                    new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                          Attributes.ATTACK_SPEED.getDescriptionId(),
                                                          muti,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(Attributes.MOVEMENT_SPEED,
                                    new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                          Attributes.MOVEMENT_SPEED.getDescriptionId(),
                                                          -Math.min(muti, 0.15),
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case HEAD -> {
                    consumer.accept(Attributes.MAX_HEALTH,
                                    new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                          Attributes.MAX_HEALTH.getDescriptionId(),
                                                          muti / 2,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(Attributes.KNOCKBACK_RESISTANCE,
                                    new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                          Attributes.KNOCKBACK_RESISTANCE.getDescriptionId(),
                                                          -muti,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                default -> {}
            }
        }
    }

    @Override
    public void modifierOnInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack) {
        if (holder instanceof Player player && (isCorrectSlot || isSelected) && stack.is(Tags.Items.ARMORS_HELMETS) && stack.is(TinkerTags.Items.HELMETS)){
            if (player.getEffect(MobEffects.NIGHT_VISION) == null
                || Objects.requireNonNull(player.getEffect(MobEffects.NIGHT_VISION)).getDuration() <= 20 * 11){
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 21, AsOneA.get(), false, false));
            }
        }
    }
}

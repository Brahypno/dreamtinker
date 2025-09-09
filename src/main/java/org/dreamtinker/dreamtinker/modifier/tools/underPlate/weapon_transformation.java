package org.dreamtinker.dreamtinker.modifier.tools.underPlate;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.UnderPlateBoostMutiply;

public class weapon_transformation extends BattleModifier {
    private final String tool_attribute_uuid = "facdf7e8-4b20-4e2d-9aba-5c1b408e7c9d";

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        float armor = tool.getStats().get(ToolStats.ARMOR);
        float toughness = tool.getStats().get(ToolStats.ARMOR_TOUGHNESS);
        if (modifier.getLevel() > 0){
            switch (slot) {
                case CHEST -> {
                    consumer.accept(Attributes.LUCK,
                                    new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                          Attributes.LUCK.getDescriptionId(),
                                                          armor * toughness * UnderPlateBoostMutiply.get(),
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case LEGS -> {
                }
                case FEET -> {
                    consumer.accept(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                          Attributes.ATTACK_DAMAGE.getDescriptionId(),
                                                          armor * toughness * UnderPlateBoostMutiply.get(),
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(Attributes.ATTACK_SPEED,
                                    new AttributeModifier(UUID.fromString(tool_attribute_uuid),
                                                          Attributes.ATTACK_SPEED.getDescriptionId(),
                                                          armor * toughness * UnderPlateBoostMutiply.get(),
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case HEAD -> {}
                default -> {}
            }
        }
    }
}

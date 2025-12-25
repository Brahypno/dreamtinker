package org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate;

import com.sammy.malum.registry.common.AttributeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate.WeaponTransformation.valueExpSoftCap;

public class SpiritualWeaponTransformation extends BattleModifier {
    private final Component errorMessage =
            Component.translatable("modifier.dreamtinker.spiritual_weapon_transformation.requirements");

    @Override
    public int getPriority() {
        return -1000;
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        if (0 < modifier.getLevel() && tool.getModifierLevel(DreamtinkerModifiers.weapon_transformation.getId()) < 1)
            return errorMessage;
        return null;
    }

    @Override
    public Component requirementsError(ModifierEntry entry) {
        return errorMessage;
    }

    @Override
    public @NotNull List<ModifierEntry> displayModifiers(ModifierEntry entry) {
        return List.of(new ModifierEntry(DreamtinkerModifiers.weapon_transformation.getId(), 1));
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
                    consumer.accept(LodestoneAttributeRegistry.MAGIC_DAMAGE.get(),
                                    new AttributeModifier(uuid,
                                                          LodestoneAttributeRegistry.MAGIC_DAMAGE.get().getDescriptionId(),
                                                          multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));

                    consumer.accept(LodestoneAttributeRegistry.MAGIC_RESISTANCE.get(),
                                    new AttributeModifier(uuid,
                                                          LodestoneAttributeRegistry.MAGIC_RESISTANCE.get().getDescriptionId(),
                                                          Math.min(0.4, multi / 4),
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }

                case LEGS -> {
                    consumer.accept(AttributeRegistry.ARCANE_RESONANCE.get(),
                                    new AttributeModifier(uuid,
                                                          AttributeRegistry.SOUL_WARD_RECOVERY_RATE.get().getDescriptionId(),
                                                          multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(AttributeRegistry.MALIGNANT_CONVERSION.get(),
                                    new AttributeModifier(uuid,
                                                          AttributeRegistry.MALIGNANT_CONVERSION.get().getDescriptionId(),
                                                          -multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case FEET -> {
                    consumer.accept(AttributeRegistry.SOUL_WARD_INTEGRITY.get(),
                                    new AttributeModifier(uuid,
                                                          AttributeRegistry.SOUL_WARD_INTEGRITY.get().getDescriptionId(),
                                                          multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(AttributeRegistry.SOUL_WARD_RECOVERY_RATE.get(),
                                    new AttributeModifier(uuid,
                                                          AttributeRegistry.SOUL_WARD_RECOVERY_RATE.get().getDescriptionId(),
                                                          -multi / 2,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                case HEAD -> {
                    consumer.accept(AttributeRegistry.SOUL_WARD_CAP.get(),
                                    new AttributeModifier(uuid,
                                                          AttributeRegistry.SOUL_WARD_RECOVERY_RATE.get().getDescriptionId(),
                                                          multi,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                    consumer.accept(AttributeRegistry.SCYTHE_PROFICIENCY.get(),
                                    new AttributeModifier(uuid,
                                                          AttributeRegistry.SCYTHE_PROFICIENCY.get().getDescriptionId(),
                                                          -multi / 2,
                                                          AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                default -> {}
            }
        }
    }
}

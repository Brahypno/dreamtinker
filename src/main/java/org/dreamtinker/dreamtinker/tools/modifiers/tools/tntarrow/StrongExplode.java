package org.dreamtinker.dreamtinker.tools.modifiers.tools.tntarrow;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.StrongExplodeDamageBoost;

public class StrongExplode extends BattleModifier {
    @Override
    public float onGetMeleeDamage(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull ToolAttackContext context, float baseDamage, float damage) {
        return damage * (tool.getModifierLevel(this) + 1) * StrongExplodeDamageBoost.get();
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (EquipmentSlot.MAINHAND == slot || EquipmentSlot.OFFHAND == slot)
            consumer.accept(Attributes.ATTACK_SPEED,
                            new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes()),
                                                  Attributes.ATTACK_SPEED.getDescriptionId(),
                                                  -1,
                                                  AttributeModifier.Operation.MULTIPLY_TOTAL));
    }


    @Override
    public int getPriority() {
        return Integer.MIN_VALUE / 2;
    }

    @Override
    public boolean isNoLevels() {return false;}
}

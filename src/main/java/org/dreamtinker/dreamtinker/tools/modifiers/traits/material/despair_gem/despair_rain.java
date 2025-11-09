package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerAttributes;

import java.util.UUID;
import java.util.function.BiConsumer;

public class despair_rain extends BattleModifier {
    @Override
    public boolean isNoLevels() {return false;}

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * (context.isCritical() ? 1 : 1.0f / modifier.getLevel());
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken())
            consumer.accept(TinkerAttributes.CRITICAL_DAMAGE.get(),
                            new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes()),
                                                  TinkerAttributes.CRITICAL_DAMAGE.get().getDescriptionId(),
                                                  4 + modifier.getLevel(),
                                                  AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity victim = context.getLivingTarget();
        if (null != victim){
            AttributeInstance attr_instance = victim.getAttribute(Attributes.MAX_HEALTH);
            if (null != attr_instance){
                UUID uuid = UUID.nameUUIDFromBytes((this.getId() + "." + Attributes.MAX_HEALTH.getDescriptionId()).getBytes());
                double debuff = 0;
                AttributeModifier cur = attr_instance.getModifier(uuid);
                if (null != cur)
                    debuff = cur.getAmount();
                attr_instance.removeModifier(uuid);
                attr_instance.addTransientModifier(
                        new AttributeModifier(uuid, Attributes.MAX_HEALTH.getDescriptionId(), debuff - damageDealt * 0.1f * modifier.getLevel(),
                                              AttributeModifier.Operation.ADDITION));
            }
        }
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (world.isClientSide)
            return;
        if ((isCorrectSlot || isSelected) && world.getGameTime() % 20 == 0)
            holder.setHealth(modifier.getLevel());

    }


}

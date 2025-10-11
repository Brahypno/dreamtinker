package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import com.sammy.malum.core.handlers.SpiritHarvestHandler;
import com.sammy.malum.registry.common.AttributeRegistry;
import com.sammy.malum.registry.common.item.EnchantmentRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.EnumMap;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.SpiritDefence;

public class malum_spirit_defense extends ArmorModifier {
    public static final EnumMap<EquipmentSlot, UUID> ARMOR_SLOT_UUIDS = new EnumMap<>(EquipmentSlot.class);

    static {
        ARMOR_SLOT_UUIDS.put(EquipmentSlot.HEAD, UUID.fromString("c3e9b4a2-7f1d-4a68-8e72-1b5f9a7c2d10"));
        ARMOR_SLOT_UUIDS.put(EquipmentSlot.CHEST, UUID.fromString("5a6d8f90-1234-4cde-9abc-0fedcba98765"));
        ARMOR_SLOT_UUIDS.put(EquipmentSlot.LEGS, UUID.fromString("8f21c6d9-b3a4-4d2e-8f7a-6c5b4a3920e1"));
        ARMOR_SLOT_UUIDS.put(EquipmentSlot.FEET, UUID.fromString("1d3f5a7b-9c2e-4f01-8a3b-7d6e5c4b2a19"));
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (source.getEntity() instanceof LivingEntity entity && !entity.level().isClientSide)
            amount -= SpiritHarvestHandler.getSpiritData(entity).map((d) -> d.totalSpirits).orElse(0) * modifier.getLevel();
        amount -= Mth.ceil(context.getEntity().getAttributeValue(AttributeRegistry.SPIRIT_SPOILS.get()));
        amount -= context.getEntity().getMainHandItem().getEnchantmentLevel(EnchantmentRegistry.SPIRIT_PLUNDER.get());
        return amount;
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (EquipmentSlot.MAINHAND == slot || EquipmentSlot.OFFHAND == slot)
            return;
        consumer.accept(AttributeRegistry.ARCANE_RESONANCE.get(),
                        new AttributeModifier(ARMOR_SLOT_UUIDS.get(slot),
                                              AttributeRegistry.ARCANE_RESONANCE.get().getDescriptionId(),
                                              SpiritDefence.get() * modifier.getLevel(),
                                              AttributeModifier.Operation.ADDITION));
        consumer.accept(AttributeRegistry.SOUL_WARD_CAP.get(),
                        new AttributeModifier(ARMOR_SLOT_UUIDS.get(slot),
                                              AttributeRegistry.SOUL_WARD_CAP.get().getDescriptionId(),
                                              SpiritDefence.get() * modifier.getLevel(),
                                              AttributeModifier.Operation.ADDITION));
        consumer.accept(AttributeRegistry.SOUL_WARD_INTEGRITY.get(),
                        new AttributeModifier(ARMOR_SLOT_UUIDS.get(slot),
                                              AttributeRegistry.SOUL_WARD_INTEGRITY.get().getDescriptionId(),
                                              SpiritDefence.get() * modifier.getLevel(),
                                              AttributeModifier.Operation.ADDITION));
    }

    @Override
    public boolean isNoLevels() {return false;}


}

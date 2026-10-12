package org.brahypno.dreamtinker.library.modifiers.variable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.variable.protection.ProtectionVariable;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Supplies the combined armor level only to the slot responsible for this modifier.
 */
public record SlotInChargeLevelVariable(ResourceLocation tracker) implements ProtectionVariable {
    public static final RecordLoadable<SlotInChargeLevelVariable> LOADER = RecordLoadable.create(
            Loadables.RESOURCE_LOCATION.requiredField("tracker", SlotInChargeLevelVariable::tracker),
            SlotInChargeLevelVariable::new);

    @Override
    public float getValue(IToolStackView tool, EquipmentContext context, LivingEntity entity, DamageSource source, EquipmentSlot slot) {
        return context == null || slot == null ? 0 :
               SlotInChargeModule.getLevel(context.getTinkerData(), TinkerDataKey.of(tracker), slot);
    }

    @Override
    public RecordLoadable<SlotInChargeLevelVariable> getLoader() {
        return LOADER;
    }
}

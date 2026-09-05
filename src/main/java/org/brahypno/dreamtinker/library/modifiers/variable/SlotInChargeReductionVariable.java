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

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.AbsorptionDefenseRate;

/**
 * Supplies the configured absorption reduction only for the armor slot currently in charge.
 */
public record SlotInChargeReductionVariable(ResourceLocation tracker) implements ProtectionVariable {
    public static final RecordLoadable<SlotInChargeReductionVariable> LOADER = RecordLoadable.create(
            Loadables.RESOURCE_LOCATION.requiredField("tracker", SlotInChargeReductionVariable::tracker),
            SlotInChargeReductionVariable::new);

    @Override
    public float getValue(
            IToolStackView tool, EquipmentContext context, LivingEntity entity,
            DamageSource source, EquipmentSlot slot) {
        if (context == null || slot == null){
            return 0;
        }
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), TinkerDataKey.of(tracker), slot);
        return level * AbsorptionDefenseRate.get().floatValue();
    }

    @Override
    public RecordLoadable<SlotInChargeReductionVariable> getLoader() {
        return LOADER;
    }
}

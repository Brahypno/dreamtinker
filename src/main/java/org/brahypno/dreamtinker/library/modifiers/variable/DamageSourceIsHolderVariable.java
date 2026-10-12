package org.brahypno.dreamtinker.library.modifiers.variable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.json.variable.protection.ProtectionVariable;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Returns 1 when the damage source owner is the entity wearing or holding the tool, otherwise 0.
 */
public enum DamageSourceIsHolderVariable implements ProtectionVariable {
    INSTANCE;

    public static final SingletonLoader<DamageSourceIsHolderVariable> LOADER = new SingletonLoader<>(INSTANCE);

    @Override
    public float getValue(
            IToolStackView tool, @Nullable EquipmentContext context, @Nullable LivingEntity holder,
            @Nullable DamageSource source, @Nullable EquipmentSlot slot) {
        return source != null && source.getEntity() != null && source.getEntity().equals(holder) ? 1 : 0;
    }

    @Override
    public SingletonLoader<DamageSourceIsHolderVariable> getLoader() {
        return LOADER;
    }
}

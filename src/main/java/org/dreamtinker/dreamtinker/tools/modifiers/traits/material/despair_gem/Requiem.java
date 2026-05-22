package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Requiem extends Modifier implements DamageBlockModifierHook, ModifyDamageModifierHook {
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("requiem"));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK, ModifierHooks.MODIFY_HURT);
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level){
            amount -= (long) context.getEntity().getActiveEffects().size() * level;
            if (source.getEntity() instanceof LivingEntity entity){
                amount -= (long) entity.getActiveEffects().size() * level;
            }
        }
        return amount;
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < amount && 0 < level){
            return null == source.getEntity() || !(source.getEntity() instanceof LivingEntity) || source.getEntity().equals(context.getEntity());
        }
        return false;
    }
}

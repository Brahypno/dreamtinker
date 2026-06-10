package org.brahypno.dreamtinker.tools.modifiers.traits.material.despair_gem;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class Ophelia extends Modifier implements ModifyDamageModifierHook {

    private static final ThreadLocal<Boolean> IN_PROGRESS = ThreadLocal.withInitial(() -> false);
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("ophelia"));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT);
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addModule(new RarityModule(Rarity.EPIC));
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level && !source.is(DreamtinkerDamageTypes.NULL_VOID)){
            if (Boolean.TRUE.equals(IN_PROGRESS.get()))
                return amount;
            IN_PROGRESS.set(true);
            try {
                amount /= (2.0f * level + 1.0f);
                LivingEntity victim = context.getEntity();
                int inv = victim.invulnerableTime;
                Level lev = context.getLevel();
                for (int i = 0; i < 2 * level + 1 && victim.isAlive() && !victim.isRemoved(); i++) {
                    DamageSource dms = DreamtinkerDamageTypes.randomSourceNotSame(lev.registryAccess(), source, lev.random);
                    victim.invulnerableTime = 0;
                    victim.hurtDuration = 0;
                    victim.hurt(dms, amount);
                }
                victim.invulnerableTime = inv;
            }
            finally {
                IN_PROGRESS.set(false);
            }
        }
        return amount;
    }
}

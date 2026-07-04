package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.faa;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.utils.CompatUtils.ForbiddenArcanusAurealCompat;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CorruptionDefense extends Modifier implements ModifyDamageModifierHook {

    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("aureal_defense"));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int con = context.getEntity() instanceof Player player ? ForbiddenArcanusAurealCompat.getCorruption(player) : 0;
        amount -= con * modifier.getLevel();
        return amount;
    }
}

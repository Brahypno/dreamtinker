package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.OathSteel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import org.dreamtinker.dreamtinker.Dreamtinker;
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

import static org.dreamtinker.dreamtinker.tools.modifiers.events.OathGuardPaleSteelEvents.oathBrokenSteelId;

public class LastBody extends Modifier implements ModifyDamageModifierHook {
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("last_body"));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level){
            if (!(context.getEntity() instanceof ServerPlayer player) || player.level().isClientSide() || amount <= 0.0F){
                return amount;
            }
            float healthRatio = player.getHealth() / player.getMaxHealth();
            if (healthRatio >= 0.5F){
                return amount;
            }

            float despair = tool.getPersistentData().getFloat(oathBrokenSteelId);
            float missingBelowHalf = (0.5F - healthRatio) / 0.5F;
            float cap = 0.14F + 0.08F * level + despair / 100.0F * 0.10F;
            float reduction = Math.min(missingBelowHalf * cap, cap);

            return amount * (1.0F - reduction);
        }
        return amount;
    }
}

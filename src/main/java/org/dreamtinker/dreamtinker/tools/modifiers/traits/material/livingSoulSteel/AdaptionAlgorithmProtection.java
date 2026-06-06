package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.livingSoulSteel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class AdaptionAlgorithmProtection extends NoLevelsModifier implements ProtectionModifierHook {
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("adaption_algorithm_protection"));
    private static final float PROTECTION_PER_DEATH = 1.0F;

    public static float killedByStatProtection(ServerPlayer player, EntityType<?> type) {
        int deaths = player.getStats().getValue(Stats.ENTITY_KILLED_BY.get(type));
        return deaths * PROTECTION_PER_DEATH;
    }

    private static EntityType<?> getSourceEntityType(DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker != null){
            return attacker.getType();
        }
        Entity direct = source.getDirectEntity();
        return direct == null ? null : direct.getType();
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(this, ModifierHooks.PROTECTION);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
        if (!(context.getEntity() instanceof ServerPlayer player) ||
            SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType) <= 0){
            return modifierValue;
        }
        EntityType<?> type = getSourceEntityType(source);
        return type == null ? modifierValue : modifierValue + killedByStatProtection(player, type);
    }
}

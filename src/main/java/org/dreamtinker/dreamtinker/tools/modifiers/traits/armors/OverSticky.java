package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class OverSticky extends ArmorModifier {
    private static final String HARDEN_UNTIL_KEY = "dreamtinker_harden_until";

    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("over_sticky"));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
        super.registerHooks(hookBuilder);
    }

    public boolean isNoLevels() {return false;}

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        OverslimeModule.INSTANCE.addAmount(tool, (int) Math.max(1, amount / 50));
        return false;
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level){
            if (source.is(TinkerTags.DamageTypes.FIRE_PROTECTION) || source.is(TinkerTags.DamageTypes.MAGIC_PROTECTION))
                return amount;

            LivingEntity entity = context.getEntity();
            long now = entity.level().getGameTime();
            CompoundTag tag = entity.getPersistentData();

            long hardenUntil = tag.getLong(HARDEN_UNTIL_KEY);

            // 持续时间：低级也能感受到，等级越高越更容易维持硬化
            int duration = 10 + level * 4;

            // 1. 若当前已在硬化中：本次减伤，并刷新持续时间
            if (now < hardenUntil){
                float maxReduction = 0.45F;
                float falloff = 0.78F;

                // 总等级越高，越接近上限，但不会无限膨胀
                float reduction = maxReduction * (1.0F - (float) Math.pow(falloff, level));
                amount *= (1.0F - reduction);

                // 刷新硬化持续时间：持续受击则维持板结
                tag.putLong(HARDEN_UNTIL_KEY, now + duration);
                return amount;
            }

            // 2. 若当前不在硬化中：本次不减伤，只开始硬化
            tag.putLong(HARDEN_UNTIL_KEY, now + duration);
        }
        return amount;
    }
}

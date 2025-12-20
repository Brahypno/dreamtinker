package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.eidolon;

import elucent.eidolon.network.LifestealEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.util.DamageTypeData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EidolonSapping extends BattleModifier {
    public boolean isNoLevels() {return false;}

    private final String sap_time = "eidolon_sapping";

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        if (null == target || target.level().isClientSide)
            return knockback;
        LivingEntity attacker = context.getAttacker();
        if (target.invulnerableTime > 0){
            target.invulnerableTime = 0;
            float before = target.getHealth();
            long cur_time = target.level().getGameTime();
            CompoundTag data = target.getPersistentData();
            if (data.getLong(sap_time) < cur_time){
                target.hurt(DamageTypeData.source(target.level(), DamageTypes.WITHER, attacker, null), 2.0f * modifier.getLevel());
                data.putLong(sap_time, cur_time);

                float healing = before - target.getHealth();
                if (healing > 0){
                    attacker.heal(healing);
                    if (!attacker.level().isClientSide)
                        Networking.sendToTracking(attacker.level(), attacker.blockPosition(),
                                                  new LifestealEffectPacket(target.blockPosition(), attacker.blockPosition(), 1.0f, 0.125f, 0.1875f));
                }
            }
        }
        return knockback;
    }
}

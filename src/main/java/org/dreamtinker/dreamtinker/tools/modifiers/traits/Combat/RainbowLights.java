package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.utils.DTDamageUtils;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.dreamtinker.dreamtinker.utils.model.RainbowTextUtil;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class RainbowLights extends BattleModifier {
    Component myName = null;

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * modifier.getEffectiveLevel() / 7.0f;
    }

    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        float Theoretical_damage = Math.max(0.5f, DTModifierCheck.getMeleeDamage(context.getAttacker(), context.getTarget(), tool, false));
        Theoretical_damage = Math.max(Theoretical_damage, damageDealt);
        Entity victim = context.getTarget();
        if (!victim.level().isClientSide){
            int inv = victim.invulnerableTime;
            for (int i = 0; i < 9 && victim.isAlive(); i++) {
                DamageSource source =
                        DreamtinkerDamageTypes.randomSourceNotSame(victim.level().registryAccess(), context.makeDamageSource(), victim.level().random);
                victim.invulnerableTime = 0;
                if (victim instanceof LivingEntity le)
                    le.hurtDuration = 0;
                DTDamageUtils.damageHandler(victim, source, Theoretical_damage);
            }
            victim.invulnerableTime = inv;
        }
    }

    @Override
    public @NotNull Component getDisplayName(int level) {
        if (myName == null){
            myName = RainbowTextUtil.rainbowKeyBand(getTranslationKey(), 0.05f, 0.70f);
        }
        return myName;
    }
}

package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.eidolon;

import elucent.eidolon.network.LifestealEffectPacket;
import elucent.eidolon.network.Networking;
import elucent.eidolon.util.DamageTypeData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.brahypno.dreamtinker.utils.DTHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EidolonSapping extends Modifier implements MeleeHitModifierHook, MonsterMeleeHitModifierHook {
    public boolean isNoLevels() {return false;}

    private final String sap_time = "eidolon_sapping";

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        sapping(context, modifier.getLevel());
        return knockback;
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        sapping(context, modifier.getLevel());
    }

    private void sapping(ToolAttackContext context, int level) {
        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        if (null == target || target.level().isClientSide)
            return;
        LivingEntity attacker = context.getAttacker();
        if (target.invulnerableTime > 0){
            target.invulnerableTime = 0;
            float before = target.getHealth();
            long cur_time = target.level().getGameTime();
            CompoundTag data = target.getPersistentData();
            if (data.getLong(sap_time) < cur_time){
                target.hurt(DamageTypeData.source(target.level(), DamageTypes.WITHER, attacker, null), 2.0f * level);
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
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        super.registerHooks(hookBuilder);
    }
}

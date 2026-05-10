package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class GoliathDamage extends NoLevelsModifier implements MeleeInterface {
    public static float goliathPercentage(LivingEntity attacker, Entity target) {
        double attacker_volume = DTHelper.getMultipartVolume(attacker);
        double target_volume = DTHelper.getMultipartVolume(target);
        double multi = 1.0 + 0.35D * Math.log((target_volume / attacker_volume));
        return (float) Math.max(multi, 0.70D);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.MeleeInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * goliathPercentage(context.getAttacker(), context.getTarget()) * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
    }

}

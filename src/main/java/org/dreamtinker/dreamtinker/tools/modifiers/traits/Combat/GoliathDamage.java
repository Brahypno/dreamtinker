package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class GoliathDamage extends NoLevelsModifier implements MeleeInterface {
    public static float goliathPercentage(LivingEntity attacker, Entity target) {
        AABB attacker_box = attacker.getBoundingBox();
        double attacker_volume = attacker_box.getXsize() * attacker_box.getYsize() * attacker_box.getZsize();
        AABB target_box = target.getBoundingBox();
        double target_volume = target_box.getXsize() * target_box.getYsize() * target_box.getZsize();
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

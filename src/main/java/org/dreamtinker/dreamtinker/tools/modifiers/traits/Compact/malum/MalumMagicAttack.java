package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry;

public class MalumMagicAttack extends Modifier implements MeleeInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.MeleeInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (null != context.getLivingTarget()){
            var magicResistance = context.getLivingTarget().getAttribute(LodestoneAttributeRegistry.MAGIC_RESISTANCE.get());
            if (magicResistance != null)
                damage /= (float) Math.max(magicResistance.getValue(), 0.01f);
        }
        var magicProficiency = context.getAttacker().getAttribute(LodestoneAttributeRegistry.MAGIC_PROFICIENCY.get());
        if (magicProficiency != null)
            damage *= (float) ((magicProficiency.getValue() - 1) * modifier.getLevel() + 1);
        return damage;
    }

    @Override
    public int getPriority() {
        return 200;
    }
}

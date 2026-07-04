package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.malum;

import net.minecraft.world.entity.LivingEntity;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry;

public class MalumMagicAttack extends Modifier implements MeleeDamageModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE);
        super.registerHooks(hookBuilder);
    }

    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        LivingEntity target = ETHelper.getLivingTarget(context.getTarget());
        if (null != target){
            var magicResistance = target.getAttribute(LodestoneAttributeRegistry.MAGIC_RESISTANCE.get());
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

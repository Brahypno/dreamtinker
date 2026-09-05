package org.brahypno.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.json.math.ModifierFormula;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.armor.AdjustDamageModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class LunarDurabilityDefense extends Modifier implements ToolDamageModifierHook {
    public boolean isNoLevels() {return false;}

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(AdjustDamageModule.builder().formula()
                                              .variable(ModifierFormula.VALUE)
                                              .variable(ModifierFormula.LEVEL).subtract()
                                              .build(),
                            ModifierHooks.MODIFY_HURT, ModifierHooks.TOOLTIP);
        hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE);
        super.registerHooks(hookBuilder);
    }

    @Override
    public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        amount *= (modifier.getLevel() + 1);
        return amount;
    }

}
